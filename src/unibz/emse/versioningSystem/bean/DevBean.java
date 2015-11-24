package unibz.emse.versioningSystem.bean;

import java.util.Vector;

public class DevBean {
	private String developer;
	private Vector<CommitBean> commits;
	
	public String getDev() {
		return developer;
	}
	
	public void setDev(String dev) {
		this.developer = dev;
	}
	
	public void setCommits(Vector<CommitBean> comm) {
		this.commits = comm;
	}
	
	public Vector<CommitBean> getCommits(){
		return commits;
	}
	
}
