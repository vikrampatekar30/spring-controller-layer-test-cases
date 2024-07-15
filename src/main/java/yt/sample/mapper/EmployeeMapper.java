package yt.sample.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import yt.sample.dto.EmployeeDTO;
import yt.sample.model.Employee;

@Component
public class EmployeeMapper {
	public Employee mapToEntity(EmployeeDTO employeeDTO) {
	    ModelMapper modelMapper = new ModelMapper();
	    return modelMapper.map(employeeDTO, Employee.class);
	}
	
	public EmployeeDTO mapToDTO(Employee employee) {
		ModelMapper modelMapper = new ModelMapper();
	    return modelMapper.map(employee, EmployeeDTO.class);
	}
	
	public Employee mapToEntity(String[] stringArr) {
		Employee employee = new Employee();
		employee.setEmpId(Long.valueOf(stringArr[0]));
		employee.setFirstName(stringArr[1]);
		employee.setLastName(stringArr[2]);
		employee.setEmail(stringArr[3]);
		employee.setPhoneNo(Long.valueOf(stringArr[4]));
		employee.setSalary(Double.valueOf(stringArr[5]));
	    return employee;
	}
}
