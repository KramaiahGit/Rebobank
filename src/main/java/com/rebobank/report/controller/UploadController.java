package com.rebobank.report.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rebobank.report.constants.FileUploadConstants;
import com.rebobank.report.fileutil.FileUtilService;
import com.rebobank.report.to.Record;

@Controller
public class UploadController {

	@Autowired
	FileUtilService fileUtilService;
	
	@Autowired
	FileUploadConstants fileUploadConstants ;


	@GetMapping("/")
	public String index() {
		return "upload";
	}

	@GetMapping("/backToupload")
	public String back() {
		return "upload";
	}

	@PostMapping("/upload") 
	@SuppressWarnings("static-access")
	public ModelAndView  multipleFileUpload(@RequestParam("files") MultipartFile[] files, RedirectAttributes redirectAttributes)
			throws Exception {
		String fileExtension1 = "";
		String fileExtension2 = "";
		List<Record> convertedXMLRecords = null;
		List<Record> convertedCSVRecords = null;
		
		ModelAndView model = new ModelAndView("uploadStatus");
		Map<String, MultipartFile> uploadedFileMap = new HashMap<>();

		if (null != files && files.length == 2) {
			
			MultipartFile file1 = files[0];
			MultipartFile file2 = files[1];

			fileExtension1 = fileUtilService.getFileExtension(file1.getOriginalFilename());
			fileExtension2 = fileUtilService.getFileExtension(file2.getOriginalFilename());
			
			if (!fileExtentionValidation(fileExtension1) || !fileExtentionValidation(fileExtension2)) {
				model.addObject("message",fileUploadConstants.INVALID_FILE_SELECT_MESSAGE);
				return model;
			}
		
			uploadedFileMap.put(fileExtension1, file1);
			uploadedFileMap.put(fileExtension2, file2);

			if (uploadedFileMap.containsKey("xml")) {
				File xmlFile = new File(uploadedFileMap.get("xml").getOriginalFilename());
				convertedXMLRecords = fileUtilService.parseRecordsXML(xmlFile); // xml parse
			}

			if (uploadedFileMap.containsKey("csv")) {
				convertedCSVRecords = fileUtilService
						.convertCsvToJava(uploadedFileMap.get("csv").getOriginalFilename()); // csv parse
			}

			Set<Record> newRecord = new HashSet<>();
			List<Record> duplicateList = new ArrayList<>();

			for (Record csv : convertedCSVRecords) {
				if (newRecord.add(csv)) {
				} else {
					duplicateList.add(csv);
				}

			}

			// Results
			for (Record xmlRecord : convertedXMLRecords) {
				if (newRecord.add(xmlRecord)) {
				} else {
					duplicateList.add(xmlRecord);
				}

			}
			/*redirectAttributes.addFlashAttribute("message",
					"You selected two files to upload " + "file1" + fileExtension1 + "file2" + fileExtension2);*/
			
			model.addObject("message",fileUploadConstants.FILE_UPLOAD_SUCCESS_MESSAGE);
			model.addObject("failedRecords", duplicateList);
			return model;
		} else {
			model.addObject("message",fileUploadConstants.TWO_FILE_SELECTION_MESSAGE);
			return model;
		}

	}

	private boolean fileExtentionValidation(String fileExtension1) {
		boolean isFileValid =false;
		if (null != "" && fileExtension1.equals("csv") || fileExtension1.equals("xml")) {
			isFileValid = true;
			return isFileValid;
		}
		return isFileValid;
	}

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }
    
    
}