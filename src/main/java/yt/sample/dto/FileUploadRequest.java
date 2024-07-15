package yt.sample.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadRequest {

	private String name;
	private MultipartFile file;
	
	public FileUploadRequest(String name, MultipartFile file) {
		this.name = name;
		this.file = file;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public MultipartFile getFile() {
		return file;
	}
	
	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
