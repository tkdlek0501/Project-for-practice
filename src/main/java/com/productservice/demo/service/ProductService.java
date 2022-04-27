package com.productservice.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.productservice.demo.controller.form.CreateOptionForm;
import com.productservice.demo.controller.form.CreateProductForm;
import com.productservice.demo.controller.form.UpdateOptionForm;
import com.productservice.demo.controller.form.UpdateProductForm;
import com.productservice.demo.domain.Category;
import com.productservice.demo.domain.Option;
import com.productservice.demo.domain.Order;
import com.productservice.demo.domain.Product;
import com.productservice.demo.domain.ProductImage;
import com.productservice.demo.domain.ProductOption;
import com.productservice.demo.dto.UploadFile;
import com.productservice.demo.repository.OptionRepository;
import com.productservice.demo.repository.ProductImageRepository;
import com.productservice.demo.repository.ProductRepository;
import com.productservice.demo.repository.spec.CategoryRepository;
import com.productservice.demo.util.upload.FileStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final FileStore fileStore;
	private final ProductImageRepository productImageRepository;
	private final OptionRepository optionRepository;
	
	// 상품 생성
	@Transactional
	public Long create(CreateProductForm form) throws IllegalStateException, IOException {
		
		// productOption 생성
		ProductOption productOption = ProductOption.createProductOption(form.getOptionItems());
		
		// 카테고리 엔티티 조회
		Category category = categoryRepository.findOne(form.getCategoryId());
		
		// 생성 메서드
		Product product = Product.createProduct(form.getName(), form.getPrice(), productOption, category);
		
		// 상품 저장
		productRepository.save(product);
		
		// 옵션 저장
		List<Option> options = createControlOption(form.getOption());
		for(int i = 0; i < options.size(); i++) {
			Option option = Option.addOption(options.get(i).getNames(), options.get(i).getStockQuantity(), product.getProductOption());
			optionRepository.save(option);
		}
		
		// 이미지 생성
		if(!form.getImage().get(0).isEmpty()) {
			List<ProductImage> productImages = null;
			if(form.getImage() != null) {
				productImages = controlImage(form.getImage());
			}
			
			for(int i = 0;i<form.getImage().size();i++) {
				ProductImage productImage = ProductImage.addProductImage(productImages.get(i).getOriginalName(), productImages.get(i).getStoreName(), product);
				productImageRepository.save(productImage);
			}
		}
		
		return product.getId();
	}

	// 상품 목록
	public List<Product> findProducts(){
		return productRepository.findAll();
	}
	
	// 노출 상태 상품 목록
	public List<Product> findShowProducts(){
		return productRepository.findShowAll();
	}
	
	// 상품 조회
	public Product findProduct(Long productId) {
		return productRepository.findOne(productId);
	}
	
	// 상품 수정
	@Transactional
	public Long modifyProduct(UpdateProductForm form) throws IllegalStateException, IOException {
		
		// 기존 상품 영속성 컨텍스트 등록
		Product findProduct = productRepository.findOne(form.getId());
		
		// 옵션 따로 등록
		List<Option> findOptions = optionRepository.findAllByPoId(findProduct.getProductOption().getId());
		
		try {
			
			// productOption 생성
			ProductOption productOption = ProductOption.createProductOption(form.getOptionItems());
			// 카테고리 엔티티 조회
			Category category = categoryRepository.findOne(form.getCategoryId());
			Product product = Product.updateProduct(form.getName(), form.getPrice(), form.getStatus(), productOption, category);
			// product 수정
			findProduct.modify(product);
			
			// 옵션 처리
			// option 생성
			List<Option> options = updateControlOption(form.getOption());
			List<Option> updateOptions = new ArrayList<>();
			int orgOptionsSize = findOptions.size();
			if(orgOptionsSize > 0) {
				updateOptions = options.subList(0, orgOptionsSize);
			}
			// option 수정
			for(int i = 0; i < updateOptions.size(); i++) {
				findOptions.get(i).modify(updateOptions.get(i));
			}
			
			
			// 추가 등록
			if(options.size() > orgOptionsSize) {
				List<Option> addOptions = options.subList(orgOptionsSize, options.size());
				for(Option addOption : addOptions) {
					Option option = Option.addOption(addOption.getNames(), addOption.getStockQuantity(), findProduct.getProductOption());
					optionRepository.save(option);
				}
			}
			
			// 이미지 처리
			// 삭제
			if(form.getDeleteImage().size() > 0) {
				for(int i = 0; i < form.getDeleteImage().size();i++) {
					ProductImage productImage = productImageRepository.findOne(form.getDeleteImage().get(i));
					productImageRepository.deleteOne(productImage);
				}
			}
			
			// 추가
			if(!form.getImage().get(0).isEmpty()) {
				List<ProductImage> productImages = controlImage(form.getImage());
				
				for(int i = 0;i<form.getImage().size();i++) {
					ProductImage productImage = ProductImage.addProductImage(productImages.get(i).getOriginalName(), productImages.get(i).getStoreName(), findProduct);
					productImageRepository.save(productImage);
				}
			}
		
		} catch (Exception e) {
			return null;
		}
		
		return findProduct.getId();
	}

	// 상품 삭제
	@Transactional
	public void deleteProduct(Long productId) {
		
		Product findProduct = productRepository.findOne(productId);
		productRepository.deleteOne(findProduct);
	}
	
	
	// 상품 이미지 처리
	private List<ProductImage> controlImage(List<MultipartFile> image) throws IOException {
		List<UploadFile> storeImageFiles = fileStore.storeFiles(image);
		List<ProductImage> productImages = new ArrayList<>();
		for (UploadFile storeImageFile : storeImageFiles) {
			ProductImage productImage = ProductImage.createProductImage(storeImageFile.getUploadFileName(), storeImageFile.getStoreFileName());
			productImages.add(productImage);
		}
		return productImages;
	}
	
	// 등록시 옵션 처리
	private List<Option> createControlOption(List<CreateOptionForm> list) {
		List<Option> options = new ArrayList<>();
		for(CreateOptionForm formOption : list) {
			if(formOption.getNames() != null && !formOption.getNames().isEmpty() && formOption.getStockQuantity() != null) {
				Option option = Option.createOption(formOption.getNames(), formOption.getStockQuantity());
				options.add(option);
			}
		}
		return options;
	}
	
	// 수정시 옵션 처리
	private List<Option> updateControlOption(List<UpdateOptionForm> list) {
		List<Option> options = new ArrayList<>();
		for(UpdateOptionForm formOption : list) {
			if(formOption.getNames() != null && !formOption.getNames().isEmpty() && formOption.getStockQuantity() != null) {
				Option option = Option.createOption(formOption.getNames(), formOption.getStockQuantity());
				options.add(option);
			}
		}
		return options;
	}
	
	
}
