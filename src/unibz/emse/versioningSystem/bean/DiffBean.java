package unibz.emse.versioningSystem.bean;

import java.util.Date;
import java.util.Vector;

public class DiffBean {
	private String commitId;
	private String authorEmail;
	private String modifiedFile;
	private Date date;
	private String removedQuantity;
//	private Vector<String> modifiedLines;
	private Vector<Integer> removedLines;
	private Integer addedNumber;
	private Integer removedNumber;
//	private Vector<String> blame;
	
	public String getFile() {
		return modifiedFile;
	}
	public void setFile(String fileName) {
		this.modifiedFile = fileName;
	}
	
	public String getAuthor() {
		return authorEmail;
	}
	public void setAuthor(String author) {
		this.authorEmail = author;
	}

	public Vector<Integer> getRemovedLines() {
		return removedLines;
	}
	public void setRemovedLines(Vector<Integer> removedLines) {
		this.removedLines = removedLines;
	}

	public String getRemovedQuantity() {
		return removedQuantity;
	}
	public void setRemovedQuantity(String removedQuantity) {
		this.removedQuantity = removedQuantity;
	}
	
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String inCommitId) {
		this.commitId = inCommitId;
	}
	
	public Integer getAddedNumber() {
		return addedNumber;
	}
	
	public void setAddedNumber(Integer addedNumber) {
		this.addedNumber = addedNumber;
	}
	
	public Integer getRemovedNumber() {
		return removedNumber;
	}
	
	public void setRemovedNumber(Integer removedNumber) {
		this.removedNumber = removedNumber;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
