package yt.sample.dto;

import java.util.List;

public class AddAllEmployeeRequest {

	private List<EmployeeDTO> employeeDTOList;

	public List<EmployeeDTO> getEmployeeDTOList() {
		return employeeDTOList;
	}

	public void setEmployeeDTOList(List<EmployeeDTO> employeeDTOList) {
		this.employeeDTOList = employeeDTOList;
	}
}
