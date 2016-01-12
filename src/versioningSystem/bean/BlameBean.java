package versioningSystem.bean;

import java.util.Date;
import java.util.Vector;

public class BlameBean {
	private String bugCommitId;
	private String preBugCommitId;
	private String authorName;
	private String fileName;
	private Vector<String> addedLines;
	private Vector<String> removedLines;
	private String addedNumber;
	private String removedNumber;
	private Date fixCommitDate;
	private Date bugCommitDate;
	
	public String getName() {
		return fileName;
	}
	public void setName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getAuthor() {
		return authorName;
	}
	public void setAuthor(String author) {
		this.authorName = author;
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
	public String getBugCommitId() {
		return bugCommitId;
	}
	public void setCommitId(String bugCommitId) {
		this.bugCommitId = bugCommitId;
	}
	
	public String getPreBugCommitId() {
		return preBugCommitId;
	}
	public void setPreBugCommitId(String preBugCommitId) {
		this.preBugCommitId = preBugCommitId;
	}
	
	public String getAddedNumber() {
		return addedNumber;
	}
	
	public void setAddedNumber(String addedNumber) {
		this.addedNumber = addedNumber;
	}
	
	public String getRemovedNumber() {
		return removedNumber;
	}
	
	public void setRemovedNumber(String removedNumber) {
		this.removedNumber = removedNumber;
	}
	
	public Date getFixDate() {
		return fixCommitDate;
	}
	public void setFixDate(Date fixDate) {
		this.fixCommitDate = fixDate;
	}
	public Date getBugDate() {
		return bugCommitDate;
	}
	public void setBugDate(Date bugDate) {
		this.bugCommitDate = bugDate;
	}
}
