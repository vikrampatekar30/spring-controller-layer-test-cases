package yt.sample.controller;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import yt.sample.dto.AddAllEmployeeRequest;
import yt.sample.dto.EmployeeDTO;
import yt.sample.dto.FileUploadResponse;
import yt.sample.model.Employee;
import yt.sample.service.EmployeeService;

@WebMvcTest(controllers = EmployeeController.class)
@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

	@Autowired
    MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
//    @Autowired
//    private CircuitBreakerRegistry circuitBreakerRegistry;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    public void init () {
        employee = getDummyEmployee();
        employeeDTO = getDummyEmployeeDTO(1);
//        circuitBreakerRegistry.circuitBreaker("EmployeeService").reset();
    }
    
    private EmployeeDTO getDummyEmployeeDTO(long id) {
    	EmployeeDTO employeeDTO = new EmployeeDTO();
    	employeeDTO.setEmpId(id);
    	employeeDTO.setFirstName("John");
    	employeeDTO.setLastName("Miller");
    	employeeDTO.setEmail("john.miller@gmail.com");
    	employeeDTO.setPhoneNo(9865412387L);
    	employeeDTO.setSalary(2300);
	    return employeeDTO;
	}

	private Employee getDummyEmployee() {
		Employee employee = new Employee();
		employee.setEmpId(1);
		employee.setFirstName("John");
		employee.setLastName("Miller");
		employee.setEmail("john.miller@gmail.com");
		employee.setPhoneNo(9865412387L);
		employee.setSalary(2300);
	    return employee;
	}

	@Test
    public void testAdd() throws JsonProcessingException, Exception {
    	// Mocking the service behavior
        given(employeeService.add(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        // Performing an HTTP POST request to create an employee
        ResultActions response = mockMvc.perform(post("/employee/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employeeDTO)));
            
        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.empId", CoreMatchers.is(Long.valueOf(employeeDTO.getEmpId()).intValue())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employeeDTO.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(employeeDTO.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(employeeDTO.getEmail())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNo", CoreMatchers.is(employeeDTO.getPhoneNo())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.salary", CoreMatchers.is(employeeDTO.getSalary())));
    }
	
	@Test
    public void testGet() throws JsonProcessingException, Exception {
    	// Mocking the service behavior
        given(employeeService.get(ArgumentMatchers.anyLong())).willReturn(employeeDTO);

        ResultActions response = mockMvc.perform(get("/employee/get/1")
            .contentType(MediaType.APPLICATION_JSON));
            
        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.empId", CoreMatchers.is(Long.valueOf(employeeDTO.getEmpId()).intValue())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employeeDTO.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(employeeDTO.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(employeeDTO.getEmail())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNo", CoreMatchers.is(employeeDTO.getPhoneNo())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.salary", CoreMatchers.is(employeeDTO.getSalary())));
    }
	
	@Test
    public void testUpdate() throws JsonProcessingException, Exception {
    	// Mocking the service behavior
		employeeDTO.setSalary(2000);
        given(employeeService.update(ArgumentMatchers.any())).willReturn(employeeDTO);

        ResultActions response = mockMvc.perform(put("/employee/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employeeDTO)));
            
        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.empId", CoreMatchers.is(Long.valueOf(employeeDTO.getEmpId()).intValue())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.salary", CoreMatchers.is(employeeDTO.getSalary())));
    }
	
	@Test
    public void testUpdateForErrorMessage() throws JsonProcessingException, Exception {
		employeeDTO.setSalary(2000);
        given(employeeService.update(ArgumentMatchers.any())).willThrow(new Exception("Error: Employee with id " + employeeDTO.getEmpId() + " does not exist."));
        
        Exception thrown = assertThrows(
        		Exception.class,
                () -> mockMvc.perform(put("/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO))),
                "Expected to throw, but it didn't"
         );

         assertTrue(thrown.getMessage().contains("Error: Employee with id 1 does not exist."));
    }
	
	@Test
    public void testDelete() throws JsonProcessingException, Exception {
    	// Mocking the service behavior
		employeeDTO.setSalary(2000);
        given(employeeService.delete(ArgumentMatchers.anyLong())).willReturn("Employee deleted");

        ResultActions response = mockMvc.perform(delete("/employee/delete/1")
            .contentType(MediaType.APPLICATION_JSON));
            
        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Employee deleted"));
    }
	
	@Test
    public void testGetAll() throws JsonProcessingException, Exception {
		List<EmployeeDTO> employeeDTOList = new ArrayList<>();
		employeeDTOList.add(employeeDTO);
		employeeDTOList.add(getDummyEmployeeDTO(2));
		employeeDTOList.add(getDummyEmployeeDTO(3));
    	// Mocking the service behavior
        given(employeeService.getAll()).willReturn(employeeDTOList);

        ResultActions response = mockMvc.perform(get("/employee/getall")
            .contentType(MediaType.APPLICATION_JSON));
            
        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
        	.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].empId", CoreMatchers.is(Long.valueOf(employeeDTO.getEmpId()).intValue())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName", CoreMatchers.is(employeeDTO.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName", CoreMatchers.is(employeeDTO.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", CoreMatchers.is(employeeDTO.getEmail())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].phoneNo", CoreMatchers.is(employeeDTO.getPhoneNo())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary", CoreMatchers.is(employeeDTO.getSalary())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].empId", CoreMatchers.is(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].empId", CoreMatchers.is(3)));
    }
	
	@Test
    public void testAddAll() throws JsonProcessingException, Exception {
		List<EmployeeDTO> employeeDTOList = new ArrayList<>();
		employeeDTOList.add(employeeDTO);
		employeeDTOList.add(getDummyEmployeeDTO(2));
		employeeDTOList.add(getDummyEmployeeDTO(3));
		AddAllEmployeeRequest request = new AddAllEmployeeRequest();
		request.setEmployeeDTOList(employeeDTOList);
		
    	// Mocking the service behavior
        given(employeeService.addAll(ArgumentMatchers.any())).willReturn("List of employee added");

        ResultActions response = mockMvc.perform(post("/employee/addall")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
            
        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
        	.andExpect(MockMvcResultMatchers.content().string("List of employee added"));
    }
    
    @Test
    public void testFileUpload() throws Exception {
        //given
        InputStream uploadStream = EmployeeControllerTest.class.getClassLoader().getResourceAsStream("employee-data.csv");
        MockMultipartFile file = new MockMultipartFile("file", uploadStream);
        FileUploadResponse fileUploadResponse = new FileUploadResponse("employee-data.csv", file.getSize(), 5);
        given(employeeService.fileUpload(ArgumentMatchers.any())).willReturn(fileUploadResponse);
        assertNotNull(uploadStream);
                
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart("/employee/fileupload").file(file).param("name", "employee-data.csv"));
        response.andExpect(MockMvcResultMatchers.status().isOk())
        	.andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("employee-data.csv")))
        	.andExpect(MockMvcResultMatchers.jsonPath("$.fileSize", CoreMatchers.is(Long.valueOf(file.getSize()).intValue())))
        	.andExpect(MockMvcResultMatchers.jsonPath("$.employeeCount", CoreMatchers.is(5)));
    }
    
    @Test
    public void testFileDownload() throws Exception {
    	String fileDownloadContent = "101,Donald,OConnell,DOCONNEL@gmail.com,6505079833,2600\r\n"
        		+ "102,Douglas,Grant,DGRANT@gmail.com,6505079844,3600\r\n"
        		+ "103,Jennifer,Whalen,JWHALEN@gmail.com,5151234444,4400\r\n"
        		+ "104,Michael,Hartstein,MHARTSTE@gmail.com,5151235555,13000\r\n"
        		+ "105,Jason,Mallin,JMALLIN@gmail.com,6501271934,3300";

    	InputStream uploadStream = EmployeeControllerTest.class.getClassLoader().getResourceAsStream("employee-data.csv");
        MockMultipartFile file = new MockMultipartFile("file", uploadStream);
        
        MvcResult result = mockMvc.perform(get("/employee/filedownload/employee-data.csv")
            .contentType(MediaType.APPLICATION_JSON)).andReturn();
            
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo("application/CSV");
        assertThat(response.getContentAsString().trim()).isEqualTo(fileDownloadContent.trim());
        assertThat(response.getContentAsByteArray()).isEqualTo(file.getBytes());
    }
    
    @Test
    public void testGetAllForNotFoundHttpStatus() throws JsonProcessingException, Exception {
    	// Mocking the service behavior
        given(employeeService.get(ArgumentMatchers.anyLong())).willReturn(employeeDTO);

        ResultActions response = mockMvc.perform(get("/employee/getaAll")
            .contentType(MediaType.APPLICATION_JSON));
            
        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
//	@Test
    public void testWithFallbackGet() throws JsonProcessingException, Exception {
    	// Mocking the service behavior
//        given(employeeService.get(ArgumentMatchers.anyLong())).willReturn(employeeDTO);

		CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("EmployeeService");
		String result = circuitBreaker.executeSupplier((Supplier<String>) mockMvc.perform(get("/employee/withfallback/get/1")
				            .contentType(MediaType.APPLICATION_JSON)));
		System.out.println("result : " + result);
//        ResultActions response = mockMvc.perform(get("/employee/withfallback/get/1")
//            .contentType(MediaType.APPLICATION_JSON));
        
//        verify(mockMvc).fallback(any(Exception.class));
//        assertThat(actualResult, is(expectedResult));
            
        // Asserting the response expectations
//        response.andExpect(MockMvcResultMatchers.status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.empId", CoreMatchers.is(1001)))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("FakeFirstName")))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("FakeLastName")))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("fake@gmail.com")))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNo", CoreMatchers.is(9876543210L)))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.salary", CoreMatchers.is(1001)));
    }
}
