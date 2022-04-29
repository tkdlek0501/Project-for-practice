package com.productservice.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.productservice.demo.domain.Option;
import com.productservice.demo.repository.OptionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OptionService {
	
	private final OptionRepository optionRepository;
	
	// 옵션 조회
	public Option findOne(Long id) {
		return optionRepository.findOne(id);
	}
	
	// 옵션 삭제
	@Transactional
	public void deleteOne(Long optionId) {
		Option findOption = optionRepository.findOne(optionId);
		optionRepository.deleteOne(findOption);
	}
	
	// 옵션 목록
	public List<Option> findAll() {
		return optionRepository.findAll();
	}
	
	

	
}
