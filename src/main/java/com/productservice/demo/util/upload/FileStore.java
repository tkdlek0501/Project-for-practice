package com.productservice.demo.util.upload;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.productservice.demo.dto.UploadFile;

import lombok.extern.slf4j.Slf4j;

// 파일 관련 유틸

@Component
@Slf4j
public class FileStore {
	
	// Upload
	
	@Value("${file.dir}") // file 업로드 경로 properties 에서 설정한 값 가져옴
	private String fileDir;
	
	// 파일 여러개 처리
	public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IllegalStateException, IOException{
		
		List<UploadFile> storeFileResult = new ArrayList<>();
		
		for (MultipartFile multipartFile : multipartFiles) {
			if(!multipartFile.isEmpty()) {
				storeFileResult.add(storeFile(multipartFile));
			}
		}
		return storeFileResult;
	}
	
	// 파일 저장
	private UploadFile storeFile (MultipartFile multipartFile) throws IllegalStateException, IOException{
		
		if(multipartFile.isEmpty()) return null;
		
		// 원래 파일 이름
		String originalFileName = multipartFile.getOriginalFilename();
		
		// 저장할 파일 이름
		String storeFileName = cretaeStoreFileName(originalFileName);
		
		multipartFile.transferTo(new File(getFullPath(storeFileName)));
		
		return new UploadFile(originalFileName, storeFileName);
	}
	
	// fullPath 가져오기
	public String getFullPath(String storeFileName) {
		return fileDir + storeFileName;
	}
	
	// 저장할 파일 이름 생성
	private String cretaeStoreFileName(String originalFileName) {
		String ext = extraExt(originalFileName);
		String uuid = UUID.randomUUID().toString();
		String storeFileName = uuid + "." + ext;
		log.info("저장할 파일 이름 {}", storeFileName);
		return storeFileName;
	}
	
	// 확장자명 가져오기
	private String extraExt(String originalFileName) {
		int pos = originalFileName.lastIndexOf(".");
		String ext = originalFileName.substring(pos + 1);
		return ext;
	}
	
}
