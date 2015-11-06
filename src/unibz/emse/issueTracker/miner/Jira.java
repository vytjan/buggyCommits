package unibz.emse.issueTracker.miner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import unibz.emse.issueTracker.bean.CommentBean;
import unibz.emse.issueTracker.bean.IssueBean;
import unibz.emse.versioningSystem.bean.CommitBean;

public class Jira {

	
	/**
	 * This method downloads the list of issues present in an issue tracker provided
	 * as input. Different filtering mechanisms are used.
	 * 
	 * @param jiraAddress: the address of the issue tracker, e.g., https://issues.apache.org/jira/
	 * @param projectName: the name of the project to mine on the issue tracker (the same issue
	 * tracker can host information about multiple projects), e.g., LUCENE
	 * @param type: the issue type we are interested in, e.g., Bug
	 * @param status, e.g., Closed, Open, Resolved
	 * @param resolution, e.g., Fixed
	 * @return
	 * @throws Exception 
	 */
	public static Vector<IssueBean> getIssuesFromJiraIssueTracker(String jiraAddress, 
			String projectName, String type, String status, 
			String resolution, String dateStart, String dateEnd) throws Exception{
		Vector<IssueBean> result = new Vector<IssueBean>();
		CommentBean comments = new CommentBean();
		Vector<CommentBean> addComments = new Vector<CommentBean>();
		//no need of adding changedFiles at the moment
		
		//Every jira repository has a querying mechanism, we can access it at the following address  
		String queryingAddress = jiraAddress + "sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=";
		
		//[START] Compose the query on the basis of the parameters provided as input
		String query = "";
		
		if(projectName != null){
			query += "project+%3D+" + projectName;
		}
		 
		if(type != null){
			query += "+AND+issuetype+%3D+" + type;
		}
		
		if(status != null){
			query += "+AND+status+%3D+" + status;
		}
		
		if(resolution != null){
			query += "+AND+Resolution+%3D+" + resolution;
		}
		
		if(dateStart != null){
			query += "+AND+resolutiondate+%3E+" + dateStart;
		}
		
		if(dateEnd != null){
			query += "+AND+resolutiondate+%3C+" + dateEnd;
		}
		
		query += "&tempMax=100";
		//[END]
		
		
		
		//Read the webpage containing the issues we are interested in
		String webPageAddress = queryingAddress + query;
		URL jiraUrl = new URL(webPageAddress);
		String pageContent = readWebPage(jiraUrl);
		//System.out.println(jiraUrl);
		//System.out.println(pageContent);
		
		
		//Here you need to parse the page content and populate the result vector
		
		//each issue matcher
		String issueRegEx = Pattern.quote("<item>") + Pattern.compile("(.*?)") + Pattern.quote("</item>");
		Pattern issuePattern = Pattern.compile(issueRegEx, Pattern.DOTALL);
		
		//issue id pattern
		String issueId = Pattern.quote("<key id=") + Pattern.compile("(.*?)") + Pattern.quote(">") + Pattern.compile("(.*?)") + Pattern.quote("</key>");
		Pattern issueIdPattern = Pattern.compile(issueId);
		
		//issue type pattern
		String issueType = Pattern.quote("<type id=") + Pattern.compile("(.*?)") + Pattern.quote(">") + Pattern.compile("(.*?)") + Pattern.quote("</type>");
		Pattern issueTypePattern = Pattern.compile(issueType);
		
		//issue summary pattern
		String issueSummary = Pattern.quote("<summary>") + Pattern.compile("(.*?)") + Pattern.quote("</summary>");
		Pattern issueSummaryPattern = Pattern.compile(issueSummary);
		
		//issue description pattern
		String issueDescription = Pattern.quote("<description>") + Pattern.compile("(.*?)") + Pattern.quote("</description>");
		Pattern issueDescriptionPattern = Pattern.compile(issueDescription);
		
		//issue status pattern
		String issueStatus = Pattern.quote("<status id=") + Pattern.compile("(.*?)") + Pattern.quote(">") + Pattern.compile("(.*?)") + Pattern.quote("</status>");
		Pattern issueStatusPattern = Pattern.compile(issueStatus);
		
		//issue resolution pattern
		String issueResolution = Pattern.quote("<resolution id=") + Pattern.compile("(.*?)") + Pattern.quote(">") + Pattern.compile("(.*?)") + Pattern.quote("</resolution>");
		Pattern issueResolutionPattern = Pattern.compile(issueResolution);
		
		//issue link pattern
		String issueLink = Pattern.quote("<link>") + Pattern.compile("(.*?)") + Pattern.quote("</link>");
		Pattern issueLinkPattern = Pattern.compile(issueLink);
		
		//issue created pattern
		String created = Pattern.quote("<created>") + Pattern.compile("(.*?)") + Pattern.quote("</created>");
		Pattern issueCreatedPattern = Pattern.compile(created);
		
		//issue updated pattern
		String updated = Pattern.quote("<updated>") + Pattern.compile("(.*?)") + Pattern.quote("</updated>");
		Pattern issueUpdatedPattern = Pattern.compile(updated);
		
		//issue resolved pattern
		String resolved = Pattern.quote("<resolved>") + Pattern.compile("(.*?)") + Pattern.quote("</resolved>");
		Pattern issueResolvedPattern = Pattern.compile(resolved);
		
		//issue comments pattern
		String issueComments = Pattern.quote("<comments>") + Pattern.compile("(.*?)") + Pattern.quote("</comments>");
		Pattern issueCommentsPattern = Pattern.compile(issueComments, Pattern.DOTALL);
		//issue single comment pattern
		
		
		//Reading each issue
		Matcher matcherSingleIssue = issuePattern.matcher(pageContent);
		while(matcherSingleIssue.find()) {
			IssueBean singleIssue = new IssueBean();
			String singleIssueItem = matcherSingleIssue.group(1);
			Matcher matcherSingleIssueType = issueTypePattern.matcher(singleIssueItem);
			Matcher matcherSingleIssueId = issueIdPattern.matcher(singleIssueItem);
			Matcher matcherSingleIssueSummary = issueSummaryPattern.matcher(singleIssueItem);
			Matcher matcherSingleIssueDescription = issueDescriptionPattern.matcher(singleIssueItem);
			Matcher matcherSingleIssueStatus = issueStatusPattern.matcher(singleIssueItem);
			Matcher matcherSingleIssueResolution = issueResolutionPattern.matcher(singleIssueItem);
			Matcher matcherSingleIssueLink = issueLinkPattern.matcher(singleIssueItem);
			Matcher matcherCreated = issueCreatedPattern.matcher(singleIssueItem);
			Matcher matcherUpdated = issueUpdatedPattern.matcher(singleIssueItem);
			Matcher matcherResolved = issueResolvedPattern.matcher(singleIssueItem);
			Matcher matcherComments = issueCommentsPattern.matcher(singleIssueItem);
			
			if(matcherSingleIssueId.find()) {
				String newIssueId = matcherSingleIssueId.group(2);
				//System.out.println(matcherSingleIssueId.group(1));
				singleIssue.setIssueId(newIssueId);
			}
			if(matcherSingleIssueType.find()){
				singleIssue.setType(matcherSingleIssueType.group(1));
			}
			if(matcherSingleIssueSummary.find()){
				singleIssue.setSummary(matcherSingleIssueSummary.group(1));
			}
			if(matcherSingleIssueDescription.find()){
				singleIssue.setDescription(matcherSingleIssueDescription.group(1));
			}
			if(matcherSingleIssueStatus.find()){
				singleIssue.setStatus(matcherSingleIssueStatus.group(2));
			}
			if(matcherSingleIssueResolution.find()){
				singleIssue.setResolution(matcherSingleIssueResolution.group(2));
			}
			if(matcherSingleIssueLink.find()){
				singleIssue.setLink(matcherSingleIssueLink.group(1));
			}
			if(matcherCreated.find()){
				DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
				Date dateCreated = format.parse(matcherCreated.group(1));
				singleIssue.setCreated(dateCreated);
			}
			if(matcherUpdated.find()){
				DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
				Date dateUpdated = format.parse(matcherCreated.group(1));
				singleIssue.setCreated(dateUpdated);
			}
			if(matcherResolved.find()){
				DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
				Date dateResolved = format.parse(matcherResolved.group(1));
				singleIssue.setResolved(dateResolved);
			}
			//extracting comments
			if(matcherComments.find()){
				String allComments = matcherComments.group(1);
//				String commentInfo = Pattern.quote("<comment id=") + Pattern.compile("(.*?)") + 
//						Pattern.quote("author=\"") + 
//						Pattern.compile("(.*?)") + Pattern.quote("\"") + Pattern.compile("(.*?)") + 
//						//Pattern.quote("created=\"") + Pattern.compile("(.*?)") + Pattern.quote("\"") + 
//						//Pattern.compile("(.*?)") + Pattern.quote(">") +Pattern.quote("(.*?)") + 
//						Pattern.quote("</comment>");
				String commentInfo = Pattern.quote("<comment ") + Pattern.compile("(.*?)") + Pattern.quote("</comment>");
				Pattern commentInfoPattern = Pattern.compile(commentInfo, Pattern.DOTALL);
				Matcher matcherCommentInfo = commentInfoPattern.matcher(allComments);
				comments = new CommentBean();
				while(matcherCommentInfo.find()){
					String insideComment = matcherCommentInfo.group(0);
					
					//author of the comment
					String authorInfo = Pattern.quote("author=\"") + Pattern.compile("(.*?)") + Pattern.quote("\"") +
							Pattern.compile("(.*?)") + Pattern.quote(">")	;
					Pattern commentAuthorPattern = Pattern.compile(authorInfo, Pattern.DOTALL);
					Matcher matcherAuthorInfo = commentAuthorPattern.matcher(insideComment);
					
					//date of the comment
					String dateInfo = Pattern.quote("created=\"") + Pattern.compile("(.*?)") + Pattern.quote("\"  >");
					Pattern commentDatePattern = Pattern.compile(dateInfo, Pattern.DOTALL);
					Matcher matcherDateInfo = commentDatePattern.matcher(insideComment);
					
					//content of the comment
					String content = Pattern.quote("<comment ") + Pattern.compile("(.*?)") + Pattern.quote(">") +
					Pattern.compile("(.*?)") + Pattern.quote("</comment>");
					Pattern contentPattern = Pattern.compile(content, Pattern.DOTALL);
					Matcher matcherContent = contentPattern.matcher(insideComment);
					if(matcherAuthorInfo.find()){
						comments.setAuthor(matcherAuthorInfo.group(1));
					}
					if(matcherDateInfo.find()){
						DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
						Date dateComment = format.parse(matcherDateInfo.group(1));
						comments.setCommentDate(dateComment);
					}
					if(matcherContent.find()){
						comments.setText(matcherContent.group(2));
					}
				}
				addComments.add(comments);
				singleIssue.setComments(addComments);
			}
			//@TODO add recursion
			result.add(singleIssue);
		}	
		
		return result;
	}
	
	
	private static String readWebPage(URL url) throws Exception {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url.toURI());
        HttpResponse response = client.execute(request);

        Reader reader = null;
        try {
            reader = new InputStreamReader(response.getEntity().getContent());

            StringBuffer sb = new StringBuffer();
            {
                int read;
                char[] cbuf = new char[1024];
                while ((read = reader.read(cbuf)) != -1)
                    sb.append(cbuf, 0, read);
            }

            return sb.toString();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
}
