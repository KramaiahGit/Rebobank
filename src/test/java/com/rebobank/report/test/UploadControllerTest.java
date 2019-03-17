package com.rebobank.report.test;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import org.springframework.web.context.WebApplicationContext;

import com.rebobank.report.constants.FileUploadConstants;
import com.rebobank.report.fileutil.FileUtilService;


public class UploadControllerTest extends FileuploadApplicationTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private FileUtilService fileUtilService;
	@Autowired
	FileUploadConstants fileUploadConstants ;

	private MockMvc mockMvc;

	byte[] csvFile;
	byte[] xmlFile;
	byte[] htmlFile;
	static final String file1ContentType = "text/csv";
	static final String file2ContentType = "text/xml";
	static final String file3ContentType = "text/html";
	MockMultipartFile firstFile= null;
	MockMultipartFile secondFile =null;
	MockMultipartFile thirdFile =null;
	
	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		csvFile = Files.readAllBytes(Paths.get("records.csv"));
		xmlFile = Files.readAllBytes(Paths.get("records.xml"));
		htmlFile = Files.readAllBytes(Paths.get("records.xml"));
		firstFile = new MockMultipartFile("files", "records.csv", file1ContentType, csvFile);
		secondFile = new MockMultipartFile("files", "records.xml", file2ContentType, xmlFile);
		thirdFile = new MockMultipartFile("files", "instructions.html", file3ContentType, xmlFile);
	}
	
	@Test
	public void testUploadFile() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(firstFile).file(secondFile)
				.contentType(file2ContentType)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
   
	@Test
	@SuppressWarnings("static-access")
	public void readIvalidFiles() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(firstFile).file(thirdFile)).andExpect(
				model().attribute("message", fileUploadConstants.INVALID_FILE_SELECT_MESSAGE));

	}

	
	@Test
	@SuppressWarnings("static-access")
	public void readModalSuccessString() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(firstFile).file(secondFile))
				.andExpect(model().attribute("message", fileUploadConstants.FILE_UPLOAD_SUCCESS_MESSAGE));
	}
	
	@Test
	@SuppressWarnings("static-access")
	public void readOneFile() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(firstFile))
				.andExpect(model().attribute("message", fileUploadConstants.TWO_FILE_SELECTION_MESSAGE));
	}
	
	@Test
	public void readFileExtension() throws Exception {
		String filename = fileUtilService.getFileExtension("test.csv");
		assertEquals(filename, "csv");
		
	}
	
	@Test
	public void readInvalidFileExtension() throws Exception {
		String filename = fileUtilService.getFileExtension("test");
		assertEquals(filename, "");
		
	}
	
	
	

}
