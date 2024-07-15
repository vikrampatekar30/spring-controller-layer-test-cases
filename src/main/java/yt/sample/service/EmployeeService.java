package yt.sample.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yt.sample.dto.AddAllEmployeeRequest;
import yt.sample.dto.EmployeeDTO;
import yt.sample.dto.FileUploadRequest;
import yt.sample.dto.FileUploadResponse;
import yt.sample.mapper.EmployeeMapper;
import yt.sample.model.Employee;
import yt.sample.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeMapper mapper;
	
	public EmployeeDTO add(EmployeeDTO employeeDTO) {
		Employee employee = mapper.mapToEntity(employeeDTO);
		employeeRepository.save(employee);
		return employeeDTO;
	}
	
	public EmployeeDTO get(long id) {
		Employee employee = employeeRepository.getReferenceById(id);
		return mapper.mapToDTO(employee);
	}
	
	public EmployeeDTO update(EmployeeDTO employeeDTO) throws Exception {
		if(employeeRepository.existsById(employeeDTO.getEmpId())) {
			Employee employee = mapper.mapToEntity(employeeDTO);
			employeeRepository.save(employee);
			return employeeDTO;
		} else {
			throw new Exception("Error: Employee with id " + employeeDTO.getEmpId() + " does not exist.");
		}
	}
	
	public String delete(long id) {
		employeeRepository.deleteById(id);
		return "Employee deleted";
	}
	
	public List<EmployeeDTO> getAll() {
		List<Employee> employeeList = employeeRepository.findAll();
		List<EmployeeDTO> employeeDTOList = new ArrayList<EmployeeDTO>();
		for(Employee employee : employeeList) {
			employeeDTOList.add(mapper.mapToDTO(employee));
		}
		return employeeDTOList;
	}
	
	public String addAll(AddAllEmployeeRequest addAllEmployeeRequest) {
		List<Employee> employeeList = new ArrayList<Employee>();
		for(EmployeeDTO employeeDTO : addAllEmployeeRequest.getEmployeeDTOList()) {
			employeeList.add(mapper.mapToEntity(employeeDTO));
		}
		employeeRepository.saveAll(employeeList);
		return "List of employee added";
	}
	
	public FileUploadResponse fileUpload(FileUploadRequest fileUploadRequest) {
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
			File file = new File(fileUploadRequest.getName());
			FileUtils.writeByteArrayToFile(file, fileUploadRequest.getFile().getBytes());
			Scanner scanner = new Scanner(file);  
			while (scanner.hasNext()) {  
				String[] stringArr = scanner.next().split(",");
				employeeList.add(mapper.mapToEntity(stringArr));
			}   
			employeeRepository.saveAll(employeeList);
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new FileUploadResponse(fileUploadRequest.getName(), fileUploadRequest.getFile().getSize(), employeeList.size());
	}
}
