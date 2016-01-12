package issueTracker.IssueMining;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import issueTracker.bean.IssueBean;
import issueTracker.miner.Jira;

public class JiraMining {

	public static Vector<IssueBean> mineIssues(String jiraAddress, String projectName, String type, String status, String resolution, String dateStart, String dateEnd)throws IOException, ParseException {
		Vector<IssueBean> collectToReturn = new Vector<IssueBean>();
		try {
			
			Vector<IssueBean> allIssues = new Vector<IssueBean>();
			Vector<IssueBean> jiraIssues = Jira.getIssuesFromJiraIssueTracker(jiraAddress, projectName, type, status, resolution, dateStart, dateEnd);
			Date lastCommit;
			
			/*parse all the issues from the project*/
			while(jiraIssues.size() == 100){
				lastCommit = jiraIssues.lastElement().getCreated();
				String DATE_FORMAT_NOW = "yyyy-MM-dd";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
				Calendar cal = Calendar.getInstance();
			    cal.setTime(lastCommit);
			    cal.add(Calendar.DATE, +1);
			    lastCommit.setTime(cal.getTime().getTime());
			    String lastCommitDateString = sdf.format(lastCommit);

			    allIssues.addAll(jiraIssues);
				jiraIssues = Jira.getIssuesFromJiraIssueTracker(jiraAddress, projectName, "BUG", "RESOLVED", null, null, lastCommitDateString);
			}
		    allIssues.addAll(jiraIssues);
		    for(IssueBean singleIssue:allIssues){
		    	if(!collectToReturn.contains(singleIssue)){
		    		collectToReturn.add(singleIssue);
		    	}
		    }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collectToReturn;

	}
	
}
