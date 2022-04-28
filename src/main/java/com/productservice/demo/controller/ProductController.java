package com.productservice.demo.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.productservice.demo.controller.form.CreateCartForm;
import com.productservice.demo.controller.form.CreateOptionForm;
import com.productservice.demo.controller.form.CreateProductForm;
import com.productservice.demo.controller.form.UpdateOptionForm;
import com.productservice.demo.controller.form.UpdateProductForm;
import com.productservice.demo.domain.Category;
import com.productservice.demo.domain.Option;
import com.productservice.demo.domain.Product;
import com.productservice.demo.domain.ProductImage;
import com.productservice.demo.repository.OptionRepository;
import com.productservice.demo.repository.ProductImageRepository;
import com.productservice.demo.service.CategoryService;
import com.productservice.demo.service.OptionService;
import com.productservice.demo.service.ProductService;
import com.productservice.demo.util.upload.FileStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
@Slf4j
public class ProductController {
	
	private final ProductService productService;
	private final CategoryService categoryService;
	private final OptionService optionService;
	
	// 상품 목록
	@GetMapping("")
	public String products(Model model) {
		
		List<Product> products = productService.findProducts();
		model.addAttribute("products", products);
		return "product/products";
	}
	
	// 상품 등록 폼
	@GetMapping("/new")
	public String createProductForm(Model model) {
		
		// category 리스트
		List<Category> categories = categoryService.findCategories();
		
		model.addAttribute("categories", categories);
		model.addAttribute("form", new CreateProductForm());
		return "product/newProduct";
	}
	
	// 상품 등록
	@PostMapping("/new")
	public String createProduct(
			@Validated @ModelAttribute("form") CreateProductForm form,
			BindingResult bindingResult,
			Model model
			) throws IllegalStateException, IOException {
		
		log.info("상품 등록 form : {}", form);
		
		// category 리스트
		List<Category> categories = categoryService.findCategories();
		model.addAttribute("categories", categories);
		
		// 이미지 validation
		for(MultipartFile image : form.getImage()) {
			if(image.isEmpty()) { 
				bindingResult.rejectValue("image", null, "이미지를 등록해주세요");
				log.info("이미지 등록 안함");
			}
		}
		
		// option validation
		if(form.getOption().get(0).getNames() == null || 
				form.getOption().get(0).getNames().isEmpty() || 
				form.getOption().get(0).getStockQuantity() == null) {
			bindingResult.rejectValue("option", null, "최소한 첫번째 옵션은 입력해주세요.");
		}
		
		if(bindingResult.hasErrors()) {
			log.info("상품 등록 에러 : {}", bindingResult);
			bindingResult.reject("createProductError", null, "입력받은 값에 오류가 있습니다.");
			return "product/newProduct";
		}
		
		// 상품 등록
		Long id = productService.create(form);
		
		if(id == null) {
			log.info("상품 등록 실패");
			bindingResult.reject("createProductError", null, "상품 등록에 실패했습니다.");
			return "product/newProduct";
		}
	
		return "redirect:/admin/products";
	}
	
	// 상품 조회 (수정 폼)
	@GetMapping("{productId}")
	public String product(Model model, @PathVariable("productId") Long productId) {
		
		Product product = productService.findProduct(productId);
		log.info("조회한 상품 : {}", product);
		
		String status = String.valueOf(product.getStatus());
		
		List<Option> options = product.getProductOption().getOption();
		List<UpdateOptionForm> optionForms = new ArrayList<>();
		for (Option option : options) {
			UpdateOptionForm updateOptionForm = UpdateOptionForm.createOptionForm(option.getId(), option.getNames(), option.getStockQuantity());
			optionForms.add(updateOptionForm);
		}
		
		UpdateProductForm getProduct = UpdateProductForm.createUpdateProductForm(product.getId(), product.getName(), product.getPrice(), status, product.getProductImage(), product.getProductOption().getOptionItems(), optionForms, product.getCategory().getId());
		
		// category 리스트
		List<Category> categories = categoryService.findCategories();
		model.addAttribute("categories", categories);
		
		model.addAttribute("product", getProduct);
		return "product/product";
	}
	
	// 상품 수정
	@PostMapping("{productId}")
	public String updateProduct(
			@Validated @ModelAttribute("product") UpdateProductForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model
			) throws IllegalStateException, IOException {
		
		// 옵션 삭제
		for(int i = 0; i < form.getDeleteOption().size(); i++) {
			if(form.getDeleteOption().get(i) != null) {
				
				form.getOption().remove(i); // 받아온 form에서 i번쨰 삭제 - validation 에서 제외시킴
				
				Long pk = Long.valueOf(form.getDeleteOption().get(i));
				log.info("삭제하려는 옵션 pk : {}", pk);
				optionService.deleteOne(pk); // 실제 옵션에서 삭제
			}
		}
		
		// category 리스트
		List<Category> categories = categoryService.findCategories();
		model.addAttribute("categories", categories);
		
		// 수정
		Long id = productService.modifyProduct(form);
		
		// 실패시
		if(id == null) {
			bindingResult.reject("modifyProductFail", null, "상품 수정에 실패했습니다.");
		}
		
		// 이미지 validation
		if(form.getProductImage().isEmpty() || form.getProductImage() == null){ // 이미 등록된 이미지 없으면
			log.info("이미 등록된 이미지가 없어서 validation 중");
			for(MultipartFile image : form.getImage()) { // 새로운 이미지 있는지
				if(image.isEmpty()) { 
					bindingResult.rejectValue("image", null, "이미지를 등록해주세요");
					break;
				}
			}
		}
		
		// option validation
		if(form.getOption().get(0).getNames().isEmpty() || form.getOption().get(0).getStockQuantity() == null) {
			log.info("옵션 입력 제대로 안됨");
			bindingResult.rejectValue("option", null, "옵션을 입력해주세요.");
		}
		
		if(bindingResult.hasErrors()) {
			bindingResult.reject("createProductError", null, "입력받은 값에 오류가 있습니다.");
			
			// 이미지 다시 DB에서 조회
			Product product = productService.findProduct(form.getId());
			form.setProductImage(product.getProductImage());
			
			// 옵션 원래대로
			List<Option> options = product.getProductOption().getOption();
			List<UpdateOptionForm> optionForms = new ArrayList<>();
			for (Option option : options) {
				UpdateOptionForm updateOptionForm = UpdateOptionForm.createOptionForm(option.getId(), option.getNames(), option.getStockQuantity());
				optionForms.add(updateOptionForm);
			}
			form.setOption(optionForms);
			
			return "product/product";
		}
		
		// 성공시
		redirectAttributes.addAttribute("productId", form.getId());
		return "redirect:/admin/products/{productId}";
	}
	
	// 상품 삭제
	@GetMapping("/{id}/remove")
	public String deleteProduct(@PathVariable("id") Long id) {
		
		productService.deleteProduct(id);
		return "redirect:/admin/products";
	}
	
}
