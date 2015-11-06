package unibz.emse.versioningSystem.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.bean.DiffBean;
import unibz.emse.versioningSystem.git.GitSzz;
import unibz.emse.versioningSystem.git.GitUtilities;
import unibz.emse.issueTracker.IssueMining.*;
import unibz.emse.issueTracker.bean.IssueBean;
import unibz.emse.issueTracker.miner.Jira;

public class Test {

	public static void main(String[] args) throws Exception {
		
		
		try {
			//GitUtilities.cloneGitRepositoryUnix("https://github.com/apache/commons-io.git", 
			//		"/home/vytautas/Desktop", "/usr/bin/git", "/home/vytautas/Desktop/");
			
			GitUtilities.getLogFromGitRepository("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/log.txt", "/home/vytautas/Desktop/");
			
			Vector<CommitBean> commits = GitUtilities.readCommits("/home/vytautas/Desktop/commons-io/log.txt");
//			for(CommitBean commit:commits){
//				System.out.println(commit.getCommitMessage());
//			}
			//System.out.println(commits.size() + " is size of commits vector");
			
			Vector<IssueBean> collectToReturn = JiraMining.mineIssues("https://issues.apache.org/jira/", "IO", "BUG", "RESOLVED", null, null, null);
			
			//collect commits which fixed bugs
			Vector<CommitBean> bugFixCommits = new Vector<CommitBean>();
			
			for(IssueBean parseIssue:collectToReturn){
				String numberOfIssue = parseIssue.getIssueId();
				//issue id pattern
				String issueId = Pattern.quote(numberOfIssue);
				Pattern issueIdPattern = Pattern.compile(issueId);
				for(CommitBean parseCommit:commits){
					Matcher matcherSingleIssueId = issueIdPattern.matcher(parseCommit.getCommitMessage());
					if(matcherSingleIssueId.find()) {
						bugFixCommits.addElement(parseCommit);
						//System.out.println(parseCommit.getDate());
						//System.out.println(parseCommit.getModifiedFiles().size());
//						for(String singleModified:parseCommit.getModifiedFiles()){
//							System.out.println(singleModified);
//						}
					}
				}
			}
			
			System.out.println(bugFixCommits.size() + " is a size of bugfixed commits vector");
			
			//blaming files:
			for(CommitBean singleBugFix:bugFixCommits){
				String singleCommitId = singleBugFix.getCommitId();
				System.out.println(singleBugFix.getCommitId());
				GitSzz.getDiff("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/diff.txt", "/home/vytautas/Desktop/", singleCommitId);
				Vector<DiffBean> diffVector = GitSzz.readDiffs("/home/vytautas/Desktop/commons-io/diff.txt");
				System.out.println(diffVector.size());
//				Vector<String> modFilesInCommit = singleBugFix.getModifiedFiles();
//				for(String singleModFile:modFilesInCommit){
//					System.out.println(singleModFile);
				//	GitUtilities.getBlameHistory("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/blame.txt", "/home/vytautas/Desktop/", singleModFile);
				//	FileInputStream fileStream = new FileInputStream("/home/vytautas/Desktop/commons-io/blame.txt");
				//	BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
//
//					String singleLine;
//					
//					//Read File Line By Line
//					System.out.println(singleModFile);
//					while ((singleLine = br.readLine()) != null)   {
//						System.out.println(singleLine);
//					}
//				}
			}
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
