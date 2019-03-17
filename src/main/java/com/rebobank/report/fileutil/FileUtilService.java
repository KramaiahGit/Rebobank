package com.rebobank.report.fileutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.rebobank.report.to.Record;



@Service
public class FileUtilService {
	
	public  List<Record> parseRecordsXML(File inputFile)
			throws ParserConfigurationException, SAXException, IOException {
		List<Record> records = new ArrayList<>();
		Record record = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inputFile);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("record");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				record = new Record();
				record.setReference(Long.parseLong(eElement.getAttribute("reference")));
				record.setAccountNumber(eElement.getElementsByTagName("accountNumber").item(0).getTextContent());
				record.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
				record.setEndBalance(
						new BigDecimal(eElement.getElementsByTagName("startBalance").item(0).getTextContent()));
				record.setMutation(new BigDecimal(eElement.getElementsByTagName("mutation").item(0).getTextContent()));
				record.setEndBalance(
						new BigDecimal(eElement.getElementsByTagName("endBalance").item(0).getTextContent()));
				records.add(record);
			}
		}
		return records;
	}

	
	public List<Record> convertCsvToJava(String fileName) {
		String line = "";
		String splitBy = ",";
		List<Record> recordList = new ArrayList<>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
			for (int i = 1; i < 2; i++) {
				br.readLine();
			}

			while ((line = br.readLine()) != null) {
				String[] recordRow = line.split(splitBy);
				Record record = new Record();
				// add values from csv to record object
				record.setReference(Long.parseLong(recordRow[0]));
				record.setAccountNumber(recordRow[1]);
				record.setDescription(recordRow[2]);
				record.setStartBalance(new BigDecimal(recordRow[3]));
				record.setMutation(new BigDecimal(recordRow[4]));
				record.setEndBalance(new BigDecimal(recordRow[5]));
				recordList.add(record);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return recordList;
	}
	
	public String getFileExtension(String fileName) {
		String fileExtension = "";
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (fileExtension != null && fileExtension.equals("csv") || fileExtension.equals("xml")) {
				return fileExtension;
			}
		}
		return fileExtension;
	}
	
}
