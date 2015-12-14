package unibz.emse.versioningSystem.test;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.bean.DevBean;
import unibz.emse.versioningSystem.bean.DiffBean;
import unibz.emse.versioningSystem.git.GitRead;
import unibz.emse.versioningSystem.git.GitSzz;
import unibz.emse.versioningSystem.git.GitUtilities;
import unibz.emse.issueTracker.IssueMining.*;
import unibz.emse.issueTracker.bean.IssueBean;
import unibz.emse.issueTracker.miner.Jira;

public class Test {

	public static void main(String[] args) throws Exception {
		
		String logFilePath = "/home/vytautas/Desktop/commons-io/log.txt";
		
		
		try {
			
			//GitUtilities.cloneGitRepositoryUnix("https://github.com/apache/commons-io.git", 
			//		"/home/vytautas/Desktop", "/usr/bin/git", "/home/vytautas/Desktop/");
			GitSzz.restore("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/");
			GitUtilities.getLogFromGitRepository("/home/vytautas/Desktop/commons-io", "/usr/bin/git", logFilePath, "/home/vytautas/Desktop/");
			
			//get full log of commits
			Vector<CommitBean> commits = GitUtilities.readCommits(logFilePath);
			
			//get full list of developers
//			Vector<DevBean> devList = GitRead.getDevs(commits);
			
			//filter Commits only to those containing same file names:
//			Vector<DevBean> filtered = GitRead.findFiles(devList);
			
			
			
			
			
//			for(CommitBean commit:commits){
//				System.out.println(commit.getCommitMessage());
//			}
//			System.out.println(commits.size() + " is size of commits vector");
			
			/*Mine all the single project issues from Jira*/
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
						for(String singleModified:parseCommit.getModifiedFiles()){
							System.out.println(singleModified);
						}
					}
				}
			}
			
			System.out.println(bugFixCommits.size() + " is a size of bugfixed commits vector");
			
			//if "checkout to HEAD branch"
			//GitSzz.checkoutCommit("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/", null, reverse);
			
			//git diff on every file of commit:
			for(CommitBean singleBugFix:bugFixCommits){
				String singleCommitId = singleBugFix.getCommitId();
				String singleCommitAuthor = singleBugFix.getAuthor();
				Date singleCommitDate = singleBugFix.getDate();
				//System.out.println(singleBugFix.getCommitId());
				System.out.println(singleCommitId);
				//Get diff for every commit
				GitSzz.getDiffBuggy("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/diff.txt", "/home/vytautas/Desktop/", singleCommitId);
				
				//Parse every diff command from diff.txt file
				Vector<DiffBean> diffVector = GitRead.readDiffBuggy("/home/vytautas/Desktop/commons-io/diff.txt", singleCommitId, singleCommitAuthor, singleCommitDate);
				
				//System.out.println(diffVector.size() + " is a size of diffVector");
				
				//checkout to every different commit version
//				GitSzz.checkoutCommit("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/", singleCommitId, noReverse);
				
				//get the blame history of every file in checkouted commit
				for(DiffBean singleDiff:diffVector) {
					String fileNameToBlame = singleDiff.getFile();
//					for(Integer singleNum:singleDiff.getRemovedLines()){
//						
//					}
					//System.out.println(fileNameToBlame);
//					System.out.println(singleDiff.getRemovedNumber());
					
					//if(singleDiff.get)
					//System.out.println(singleDiff.getAddedNumber());
					//System.out.println(singleDiff.getRemovedNumber());
//					if(singleDiff.getRemovedLines().size() > 0 ){
//						for(String rLine:singleDiff.getRemovedLines()){
//								System.out.println(rLine);
//						}
//					}
					//blame every file in checkout'ed commit:
//					GitSzz.getBlameHistory("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/blame.txt", "/home/vytautas/Desktop/", fileNameToBlame);
//					//GitRead.readBlame("/home/vytautas/Desktop/commons-io/blame.txt");
//					//get the author of every modified file LOC
//					
				}
				//go to the latest commit again
				//GitSzz.checkoutCommit("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/", null, reverse);
				
				
				
				
				//checkout to needed 
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
