package unibz.emse.versioningSystem.bean;

import java.util.Date;
import java.util.Vector;

public class DiffBean {
	private String commitId;
	private String authorEmail;
	private String modifiedFile;
	private Date date;
	private Vector<String> modifiedLines;
//	private Vector<String> addedLines;
//	private Vector<String> deletedLines;
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
	public Vector<String> getModifiedLines() {
		return modifiedLines;
	}
	public void setModifiedLines(Vector<String> modLines) {
		this.modifiedLines = modLines;
	}
//	public Vector<String> getAddedLines() {
//		return addedFiles;
//	}
//	public void setAddedFiles(Vector<String> addedFiles) {
//		this.addedFiles = addedFiles;
//	}
//	public Vector<String> getDeletedFiles() {
//		return deletedFiles;
//	}
//	public void setDeletedFiles(Vector<String> deletedFiles) {
//		this.deletedFiles = deletedFiles;
//	}
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
