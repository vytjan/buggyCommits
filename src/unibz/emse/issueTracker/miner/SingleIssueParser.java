package unibz.emse.issueTracker.miner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleIssueParser {
	public void parseIssue(String singleIssueItem) {
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
				
				
	}
}
