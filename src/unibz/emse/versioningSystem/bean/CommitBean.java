package unibz.emse.versioningSystem.bean;

import java.util.Date;
import java.util.Vector;

public class CommitBean {

	private String commitId;
	private String authorEmail;
	private Date date;
	private String commitMessage;
	private Vector<String> modifiedFiles;
	private Vector<String> addedFiles;
	private Vector<String> deletedFiles;
	private boolean buggy;
	
	
	public String getAuthor() {
		return authorEmail;
	}
	public void setAuthor(String author) {
		this.authorEmail = author;
	}
	public Vector<String> getModifiedFiles() {
		return modifiedFiles;
	}
	public void setModifiedFiles(Vector<String> modifiedFiles) {
		this.modifiedFiles = modifiedFiles;
	}
	public Vector<String> getAddedFiles() {
		return addedFiles;
	}
	public void setAddedFiles(Vector<String> addedFiles) {
		this.addedFiles = addedFiles;
	}
	public Vector<String> getDeletedFiles() {
		return deletedFiles;
	}
	public void setDeletedFiles(Vector<String> deletedFiles) {
		this.deletedFiles = deletedFiles;
	}
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
	public String getCommitMessage() {
		return commitMessage;
	}
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Boolean getBuggy() {
		return buggy;
	}
	public void setBuggy(Boolean buggy) {
		this.buggy = buggy;
	}
}
