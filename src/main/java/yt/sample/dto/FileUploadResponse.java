package yt.sample.dto;

public class FileUploadResponse {
	private String name;
	private long fileSize;
	private int employeeCount;
	
	public FileUploadResponse(String name, long fileSize, int employeeCount) {
		this.name = name;
		this.fileSize = fileSize;
		this.employeeCount = employeeCount;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public int getEmployeeCount() {
		return employeeCount;
	}
	
	public void setEmployeeCount(int employeeCount) {
		this.employeeCount = employeeCount;
	}
}
