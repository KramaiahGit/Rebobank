
package com.rebobank.report.to;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "records")
@XmlAccessorType (XmlAccessType.FIELD)
public class Records {
	
	 @XmlElement(name = "record")
	    private List<Record> records = null;

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}
	 
	 
}