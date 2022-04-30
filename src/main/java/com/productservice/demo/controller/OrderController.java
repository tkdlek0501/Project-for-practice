package com.productservice.demo.controller;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.productservice.demo.controller.form.CreateCartForm;
import com.productservice.demo.controller.form.CreateOrderForm;
import com.productservice.demo.controller.form.CreateOrdersForm;
import com.productservice.demo.domain.Cart;
import com.productservice.demo.domain.Member;
import com.productservice.demo.domain.Order;
import com.productservice.demo.domain.Product;
import com.productservice.demo.domain.ProductImage;
import com.productservice.demo.dto.OrderSearch;
import com.productservice.demo.repository.ProductImageRepository;
import com.productservice.demo.service.CartService;
import com.productservice.demo.service.OrderService;
import com.productservice.demo.service.ProductService;
import com.productservice.demo.util.upload.FileStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {
	
	private final ProductImageRepository productImageRepository;
	private final FileStore fileStore;
	private final ProductService productService;
	private final OrderService orderService;
	private final CartService cartService;
	
	// 주문 가능 상품 리스트
	@GetMapping("/products")
	public String products(Model model) {
		List<Product> products = productService.findShowProducts();
		model.addAttribute("products", products);
		return "products";
	}
	
	// 상품 주문 상세 페이지
	@GetMapping("/products/{productId}")
	public String orderPage(
			@PathVariable("productId") Long productId,
			Model model) {
		
		CreateOrderForm form = new CreateOrderForm();
		CreateCartForm cartForm = new CreateCartForm();
		Product product = productService.findProduct(productId);
		
		model.addAttribute("form", form);
		model.addAttribute("cartForm", cartForm);
		model.addAttribute("product", product);
		return "product";
	}
	
	// 상품 주문
	@PostMapping("/products/{productId}")
	public String order(
			@Validated @ModelAttribute("form") CreateOrderForm form,
			BindingResult bindingResult,
			@PathVariable("productId") Long productId,
			Model model,
			RedirectAttributes redirectAttributes,
			Authentication auth
			) {
		log.info("상품 구매 시 form : {}", form);
		if(auth == null) { return "redirect:/doLogout"; }

		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		form.addMemberId(memberId);
	
		// 화면에 기본적으로 있어야 하는 data
		Product product = productService.findProduct(productId);
		model.addAttribute("product", product);
		
		// validation
		// 입력 받은 값 오류
		if(bindingResult.hasErrors()) {
			bindingResult.reject("createOrderError", null, "입력받은 값에 오류가 있습니다.");
			return "product";
		}
		
		// 주문 등록
		Map<String, Object> result = orderService.order(form);
		
		if(result.get("error") == "NotEnoughStock") {
			bindingResult.reject("orderError", null, "주문하려는 상품 재고가 부족합니다.");
		}else if(result.get("error") == "CreateError") {
			bindingResult.reject("orderError", null, "주문 등록에 오류가 있습니다.");
		}
		
		// 최종
		if(bindingResult.hasErrors()) {
			model.addAttribute("form", form);
			return "product";
		}
		
		// 성공시
		redirectAttributes.addAttribute("productId", productId);
		return "redirect:/orders";
	}
	
	
	// 자신의 주문 목록
	@GetMapping("/orders")
	public String order(
			Model model,
			Authentication auth
			) {
		if(auth == null) { return "redirect:/doLogout"; }

		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		
		List<Order> orders = orderService.myAll(memberId);
		
		model.addAttribute("orders", orders);
		return "orders";
	}
	
	// 주문 취소
	@PostMapping("/orders/{orderId}/cancel")
	public String cancelOrder(
			@PathVariable("orderId") Long orderId
			) {
		orderService.cancelOrder(orderId);
		return "redirect:/orders";
	}
	
	// 자신의 주문 상세
	@GetMapping("/orders/{orderId}")
	public String order(
			@PathVariable("orderId") Long orderId,
			Model model,
			Authentication auth
			) {
		if(auth == null) { return "redirect:/doLogout"; }

		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		
		Order order = orderService.myOne(orderId, memberId);
		
		model.addAttribute("order", order);
		return "order/order";
	}
	
	// 상품(옵션) 장바구니 등록
	@PostMapping("/products/{productId}/cart")
	public String addCart(
			@PathVariable("productId") Long productId,
			@Validated @ModelAttribute("cartForm") CreateCartForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model,
			Authentication auth
			) {
		
		if(auth == null) { return "redirect:/doLogout"; }
		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		form.addMemberId(memberId);
		
		CreateOrderForm orderForm = new CreateOrderForm();
		Product product = productService.findProduct(productId);
		model.addAttribute("form", orderForm);
		model.addAttribute("product", product);
		
		// 값에 오류
		if(bindingResult.hasErrors()) {
			bindingResult.reject(null, "입력받은 값에 오류가 있습니다.");
			return "product";
		}
		
		// 장바구니 등록
		Map<String,Object> result = cartService.create(form);
		
		// 실패시
		if(result.get("error") != null) {
			
			if(result.get("error") == "NotEnoughStockException") {
				bindingResult.reject(null, "장바구니에 넣는 개수가 남아있는 재고보다 많습니다.");
			} else if(result.get("error") == "DuplicationException") {
				bindingResult.reject(null, "이미 장바구니에 같은 옵션의 상품이 등록돼있습니다. 장바구니를 확인해주세요.");
			} else if(result.get("error") == "Exception") {
				bindingResult.reject(null, "장바구니 등록에 오류가 있습니다.");
			} 
			
			return "product";
		}
		
		// 성공시
		redirectAttributes.addAttribute("productId", productId);
		return "redirect:/products/{productId}";
	}
	
	// 장바구니에서 주문페이지 
	@GetMapping("/orders/multiple")
	public String orderMultiplePage(
			@RequestParam("chkList") List<Long> chkList,
			Model model
			) {
		
		CreateOrdersForm form = new CreateOrdersForm();
		
		List<Cart> carts = new ArrayList<>();
		for(Long cartId : chkList) {
			carts.add(cartService.findOne(cartId));
		}
		
		model.addAttribute("form", form);
		model.addAttribute("carts", carts);
		return "order/multiOrder";
	}
	
	// 여러개 주문
	@PostMapping("/orders/multiple")
	public String orderMultiple(
			@RequestParam("chkList") List<Long> chkList,
			@Validated @ModelAttribute("form") CreateOrdersForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model,
			Authentication auth
			) {
		log.info("form : {}", form);
		
		// 화면에 기본적으로 있어야 하는 data
		List<Cart> carts = new ArrayList<>();
		for(Long cartId : chkList) {
			carts.add(cartService.findOne(cartId));
		}
		model.addAttribute("carts", carts);
		
		// validation
		// 입력 받은 값 오류
		if(bindingResult.hasErrors()) {
			bindingResult.reject(null, "입력받은 값에 오류가 있습니다.");
			return "order/multiOrder";
		}
		
		if(auth == null) { return "redirect:/doLogout"; }

		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		form.addMemberId(memberId);
		
		// 주문 등록
		Map<String, Object> result = orderService.multiOrder(form);
		
		if(result.get("error") == "NotEnoughStock") {
			bindingResult.reject("orderError", null, "주문하려는 상품 재고가 부족합니다.");
		}else if(result.get("error") == "CreateError") {
			bindingResult.reject("orderError", null, "주문 등록에 오류가 있습니다.");
		}
		
		// 최종
		if(bindingResult.hasErrors()) {
			model.addAttribute("form", form);
			return "order/multiOrder";
		}
		
		return "redirect:/orders";
	}
	

	
// 관리자	
	// 주문 목록
	@GetMapping("/admin/orders")
	public String orders(
			@ModelAttribute("orderSearch") OrderSearch orderSearch,
			Model model
			) {
		
		//List<Order> orders = orderService.findAll();
		List<Order> orders = orderService.searchAll(orderSearch);
		model.addAttribute("orders", orders);
		return "order/orders";
	}
	
	// 주문 상세
	@GetMapping("/admin/orders/{orderId}")
	public String adminOrder(
			@PathVariable("orderId") Long orderId,
			Model model
			) {
		
		Order order = orderService.findOne(orderId);
		
		model.addAttribute("order", order);
		return "order/order";
	}
	
	// 배송 완료
	@PostMapping("/orders/{orderId}/complete")
	public String completeDelivery(
			@PathVariable("orderId") Long orderId
			) {
		orderService.completeDelivery(orderId);
		return "redirect:/admin/orders";
	}
	
	
// 이미지	
	// 이미지 노출
	@ResponseBody
	@GetMapping("/images/{filename}")
	public Resource showImages(@PathVariable String filename) throws MalformedURLException {
		return new UrlResource("file:" + fileStore.getFullPath(filename));
	}
	
	// 파일 다운로드
	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadAttach(@PathVariable("id") Long id) throws MalformedURLException{
		
		ProductImage image = productImageRepository.findOne(id);
		String storeFileName = image.getStoreName();
		String orgFilename = image.getOriginalName();
		
		// 실제 다운받을 경로
		UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
		
		String encodeOrgFileName = UriUtils.encode(orgFilename, StandardCharsets.UTF_8);
		String contentDisposition = "attachment; filename=\"" + encodeOrgFileName + "\"";
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				.body(resource);
	}
}
