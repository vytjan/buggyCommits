package unibz.emse.issueTracker.IssueMining;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import unibz.emse.issueTracker.bean.CommentBean;
import unibz.emse.issueTracker.bean.IssueBean;
import unibz.emse.issueTracker.miner.Jira;

public class JiraMining {

	public static Vector<IssueBean> mineIssues(String jiraAddress, String projectName, String type, String status, String resolution, String dateStart, String dateEnd)throws IOException, ParseException {
		Vector<IssueBean> collectToReturn = new Vector<IssueBean>();
		try {
			
			Vector<IssueBean> allIssues = new Vector<IssueBean>();
			Vector<IssueBean> jiraIssues = Jira.getIssuesFromJiraIssueTracker(jiraAddress, projectName, type, status, resolution, dateStart, dateEnd);
			Vector<IssueBean> tempJiraIssues = new Vector<IssueBean>();
			Date lastCommit;
			
			/*parse all the issues from the project*/
			while(jiraIssues.size() == 100){
				lastCommit = jiraIssues.lastElement().getCreated();
				Date today = new Date();
				String DATE_FORMAT_NOW = "yyyy-MM-dd";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
				Calendar cal = Calendar.getInstance();
			    cal.setTime(lastCommit);
			    cal.add(Calendar.DATE, +1);
			    lastCommit.setTime(cal.getTime().getTime());
			    String lastCommitDateString = sdf.format(lastCommit);

			    tempJiraIssues = new Vector<IssueBean>();
			    tempJiraIssues = (Vector)jiraIssues.clone();
			    allIssues.addAll(jiraIssues);
				jiraIssues = Jira.getIssuesFromJiraIssueTracker("https://issues.apache.org/jira/", "IO", "BUG", "RESOLVED", null, null, lastCommitDateString);
				//System.out.println("End of loop, size is: " + jiraIssues.size());
			}
		    allIssues.addAll(jiraIssues);
		  //  System.out.println(allIssues.size() + " is size of allIssues");
		    for(IssueBean singleIssue:allIssues){
		    	if(!collectToReturn.contains(singleIssue)){
		    		collectToReturn.add(singleIssue);
		    	}
		    }
			for(IssueBean issue:collectToReturn) {
				//System.out.println(issue.getResolved());
//				Vector<CommentBean> commentsOfIssue = issue.getComments();
//				for(CommentBean comment:commentsOfIssue){
//					System.out.println(comment.getText() + " is the text of the comment");
//					System.out.println(comment.getAuthor() + " is the author");
//				}
			//	System.out.println(issue.getIssueId());
			}
			System.out.println(collectToReturn.size() + " is size of non duplicated issues");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collectToReturn;

	}
	
}
