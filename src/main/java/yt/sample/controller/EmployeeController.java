package yt.sample.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletResponse;
import yt.sample.dto.AddAllEmployeeRequest;
import yt.sample.dto.EmployeeDTO;
import yt.sample.dto.FileUploadRequest;
import yt.sample.dto.FileUploadResponse;
import yt.sample.service.EmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService service; 
	
	@PostMapping("/add") 
	public ResponseEntity<EmployeeDTO> add(@RequestBody EmployeeDTO employeeDTO) {
		return ResponseEntity.ok(service.add(employeeDTO));
	}
	
	@GetMapping("/get/{id}") 
	public ResponseEntity<EmployeeDTO> get(@PathVariable long id) {
		return ResponseEntity.ok(service.get(id));
	}
	
	@PutMapping("/update") 
	public ResponseEntity<EmployeeDTO> update(@RequestBody EmployeeDTO employeeDTO) throws Exception {
		return ResponseEntity.ok(service.update(employeeDTO));
	}
	
	@DeleteMapping("/delete/{id}") 
	public ResponseEntity<String> delete(@PathVariable long id) {
		return ResponseEntity.ok(service.delete(id));
	}
	
	@GetMapping("/getall") 
	public ResponseEntity<List<EmployeeDTO>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}
	
	@PostMapping("/addall") 
	public ResponseEntity<String> addAll(@RequestBody AddAllEmployeeRequest addAllEmployeeRequest) {
		return ResponseEntity.ok(service.addAll(addAllEmployeeRequest));
	}
	
	@GetMapping("/withfallback/get/{id}") 
	@CircuitBreaker(name = "EmployeeService", fallbackMethod = "getFakeEmployee")
//	@HystrixCommand(fallbackMethod = "getFakeEmployee")
	public ResponseEntity<Object> getWithFallback(@PathVariable long id) throws Exception {
		throw new Exception();
	}
	
	@PostMapping("fileupload")
    public ResponseEntity<FileUploadResponse> fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        FileUploadRequest fileUploadRequest = new FileUploadRequest(name, file);
        return ResponseEntity.ok(service.fileUpload(fileUploadRequest));
    }
	
	@GetMapping("/filedownload/{fileName}")
	public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) {
	    try {
	    	InputStream is = EmployeeController.class.getClassLoader().getResourceAsStream(fileName);
	    	IOUtils.copy(is, response.getOutputStream());
	    	response.setContentType("application/CSV");
	      response.flushBuffer();
	    } catch (IOException ex) {
	    	throw new RuntimeException("IOError writing file to output stream");
	    }
	}
	
	public ResponseEntity<Object> getFakeEmployee(Exception exp) {
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setEmpId(1001);
		employeeDTO.setFirstName("FakeFirstName");
		employeeDTO.setLastName("FakeLastName");
		employeeDTO.setEmail("fake@gmail.com");
		employeeDTO.setPhoneNo(9876543210L);
		employeeDTO.setSalary(1001);
		return ResponseEntity.ok(employeeDTO);
	}
}