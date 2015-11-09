package unibz.emse.versioningSystem.bean;

import java.util.Date;
import java.util.Vector;

public class DiffBean {
	private String commitId;
	private String authorEmail;
	private String modifiedFile;
	private Date date;
//	private Vector<String> modifiedLines;
	private Vector<String> addedLines;
	private Vector<String> removedLines;
	private String addedNumber;
	private String removedNumber;
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
	
	public Vector<String> getAddedLines() {
		return addedLines;
	}
	
	public void setAddedLines(Vector<String> addedLines) {
		this.addedLines = addedLines;
	}

	public Vector<String> getRemovedLines() {
		return removedLines;
	}
	public void setRemovedLines(Vector<String> removedLines) {
		this.removedLines = removedLines;
	}
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String inCommitId) {
		this.commitId = inCommitId;
	}
	
	public String getAddedNumber() {
		return addedNumber;
	}
	
	public void setAddedNumber(String addedNumber) {
		this.addedNumber = commitId;
	}
	
	public String getRemovedNumber() {
		return removedNumber;
	}
	
	public void setRemovedNumber(String removedNumber) {
		this.removedNumber = removedNumber;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
