package versioningSystem.bean;

import java.util.Date;

public class FinalBean {
	private String author;
	private Date startDate;
	private Date endDate;
	private String fileName;
	private Integer numberOfDevs;
	private Integer loc;
	private Long daysLength;
	private Integer commitsLength;
	private Boolean buggy;
	private String commitId;
	private String idBefore;
	
	public String getCommit2(){
		return commitId;
	}
	
	public void setCommit2(String id){
		this.commitId = id;
	}
	
	public String getCommit1(){
		return idBefore;
	}
	
	public void setCommit1(String id){
		this.idBefore = id;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date commBeforeDate) {
		this.startDate = commBeforeDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date commitEndDate) {
		this.endDate = commitEndDate;
	}
	
	public String getFile() {
		return fileName;
	}
	
	public void setFile(String file) {
		this.fileName = file;
	}
	
	public Integer getNoOfDevs() {
		return numberOfDevs;
	}
	
	public void setNoOfDevs(Integer devs) {
		this.numberOfDevs = devs;
	}

	public Integer getLoc() {
		return loc;
	}
	
	public void setLoc(Integer loc) {
		this.loc = loc;
	}
	
	public Long getDays() {
		return daysLength;
	}
	
	public void setDays(long l) {
		this.daysLength = l;
	}
	
	public Integer getCommits() {
		return commitsLength;
	}
	
	public void setCommits(Integer commits) {
		this.commitsLength = commits;
	}
	
	public Boolean getBuggy() {
		return buggy;
	}
	
	public void setBuggy(Boolean bug) {
		this.buggy = bug;
	}
}
