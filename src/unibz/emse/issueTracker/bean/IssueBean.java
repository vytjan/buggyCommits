package unibz.emse.issueTracker.bean;

import java.util.Date;
import java.util.Vector;

public class IssueBean implements Comparable {

	private String issueId;
	private String type;
	private String summary;
	private String description;
	private String status;
	private String resolution;
	private Date created;
	private Date updated;
	private Date resolved;
	private String link;
	private Vector<String> changedFiles;
	private Vector<CommentBean> comments;
	
	
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String bugId) {
		this.issueId = bugId;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	@Override
	public String toString(){
		return this.issueId + " " + this.summary + " " + this.status + " " + 
				this.resolution + " " + this.created + " " + this.updated;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Date getResolved() {
		return resolved;
	}
	public void setResolved(Date resolved) {
		this.resolved = resolved;
	}
	public Vector<String> getChangedFiles() {
		return changedFiles;
	}
	public void setChangedFiles(Vector<String> changedFiles) {
		this.changedFiles = changedFiles;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Vector<CommentBean> getComments() {
		return comments;
	}
	public void setComments(Vector<CommentBean> comments) {
		this.comments = comments;
	}
	
	@Override
	public boolean equals(Object o){
		return this.issueId.equals(((IssueBean)o).getIssueId());
	}
	
	
	@Override
	public int compareTo(Object o) {
		return this.getResolved().compareTo(((IssueBean)o).getResolved());
	}
	
	
}
