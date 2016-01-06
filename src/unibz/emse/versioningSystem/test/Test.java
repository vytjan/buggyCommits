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

import csvWriter.ToCSV;
import unibz.emse.versioningSystem.bean.BlameBean;
import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.bean.DevBean;
import unibz.emse.versioningSystem.bean.DiffBean;
import unibz.emse.versioningSystem.bean.FinalBean;
import unibz.emse.versioningSystem.git.DevelopersNumber;
import unibz.emse.versioningSystem.git.ExtractFiles;
import unibz.emse.versioningSystem.git.GitRead;
import unibz.emse.versioningSystem.git.GitSzz;
import unibz.emse.versioningSystem.git.GitUtilities;
import unibz.emse.issueTracker.IssueMining.*;
import unibz.emse.issueTracker.bean.IssueBean;
import unibz.emse.issueTracker.miner.Jira;

public class Test {

	public static void main(String[] args) throws Exception {
		
		//dynamic one
		String repoPath = "/home/vytautas/Desktop/commons-io";
		
		String gitCommand = "/usr/bin/git";
		String tempPath = "/home/vytautas/Desktop/";
		
		//partial results path
		String diffPath = "/diff.txt";
		String blamePath = "/blame.txt";
		String logFilePath = "/log.txt";
		String revisionPath = "/revlist.txt";
		
		//final csv path
		String finalPath = "/commits.csv";
		
		//jira stuff
		String jiraUrl = "https://issues.apache.org/jira/";
		String jiraProject = "IO";
		String issueType = "BUG";
		String issueStatus = "RESOLVED";
		
		//url's of projects
		String projectUrl = "https://github.com/apache/commons-io.git";
		
		try {
			
			//GitUtilities.cloneGitRepositoryUnix(projectUrl, tempPath, gitCommand, tempPath);
			GitSzz.restore(repoPath, gitCommand, tempPath);
			GitUtilities.getLogFromGitRepository(repoPath, gitCommand, repoPath + logFilePath, tempPath);
			
			//get full log of commits
			Vector<CommitBean> commits = GitUtilities.readCommits(repoPath + logFilePath);
			
			

			/*Mine all the single project issues from Jira*/
			//@TODO change static vars to dynamic
			Vector<IssueBean> collectToReturn = JiraMining.mineIssues(jiraUrl, jiraProject, issueType, issueStatus, null, null, null);
			
			//collect commits which fixed bugs
			Vector<CommitBean> bugFixCommits = new Vector<CommitBean>();
			
			for(IssueBean parseIssue:collectToReturn){
				
				String numberOfIssue = parseIssue.getIssueId();
//				System.out.println(numberOfIssue);
				
				//issue id pattern
				String issueId = Pattern.quote(numberOfIssue);
				Pattern issueIdPattern = Pattern.compile(issueId);
				for(CommitBean parseCommit:commits){
					Matcher matcherSingleIssueId = issueIdPattern.matcher(parseCommit.getCommitMessage());
					if(matcherSingleIssueId.find()) {
//						System.out.println(parseCommit.getCommitMessage());
//						System.out.println();
						bugFixCommits.addElement(parseCommit);
//						System.out.println(parseCommit.getCommitId());
//						for(String singleModified:parseCommit.getModifiedFiles()){
//							System.out.println(singleModified);
//						}
					}
				}
			}
			
			
			//git diff on every file of commit:
			for(CommitBean singleBugFix:bugFixCommits){
				String singleCommitId = singleBugFix.getCommitId();

//				System.out.println(singleCommitId);
				String singleCommitAuthor = singleBugFix.getAuthor();
				Date singleCommitDate = singleBugFix.getDate();
				
//				System.out.println(singleCommitId + " if is duplicated");
				//Get diff for every commit
				GitSzz.getDiffBuggy(repoPath, gitCommand, repoPath + diffPath, tempPath, singleCommitId);
				
//				//Parse every diff command from diff.txt file
				Vector<DiffBean> diffVector = GitRead.readDiffBuggy(repoPath + diffPath, singleCommitId, singleCommitAuthor, singleCommitDate);

				
				//checkout to every different commit version
				//get the blame history of every file in checkouted commit
				for(DiffBean singleDiff:diffVector) {
					String fileNameToBlame = singleDiff.getFile();
					Vector<Integer> lineNumbers = singleDiff.getRemovedLines();
					String singleComId = singleDiff.getCommitId();
					GitSzz.checkoutCommit(repoPath, gitCommand, tempPath, singleComId);
					
					//git blame for every file to .txt
					GitSzz.getBlameHistory(repoPath, gitCommand, repoPath + blamePath, tempPath, fileNameToBlame);
//					
					//parse git blame
					Vector<String> blame = GitRead.readBlame(repoPath + blamePath, lineNumbers);

					
					//detect the most recent commit -> the one which introduced a bug
					//need commits vector and hashset blame
					CommitBean laterCommit;
					CommitBean earlierCommit;
					if(blame.size() > 0){
						for(String primaryId:blame){
							for(CommitBean comm:commits){
								if(primaryId.substring(0, 7).equals(comm.getCommitId())){
									laterCommit = comm;
									for(String secondaryId:blame){
										for(CommitBean comm2:commits){
											if(comm2.getCommitId().equals(secondaryId.substring(0, 7)) && primaryId != secondaryId){
												//compare 2 dates
												earlierCommit = comm2;
												if(laterCommit.getDate().compareTo(earlierCommit.getDate()) < 0){
													laterCommit = earlierCommit;
												}
//												System.out.println(laterCommit.getDate() + "   " + earlierCommit.getDate());
//												System.out.println();
											}
										}
									}
									laterCommit.setBuggy(true);
									System.out.println(laterCommit.getDate() + " is the most recent date");
								}
							}
						}
					}
				}
			}
			
			//get full data in one final bean
			Vector<FinalBean> devList = ExtractFiles.getDevs(repoPath, gitCommand, repoPath + diffPath, tempPath, repoPath + revisionPath, commits);
			System.out.println(devList.size() + " is a size of devlist");
			//
		
			int numb = 0;
			//get #LOC of every file
			for(FinalBean singleDev:devList){
				GitSzz.getDiff(repoPath, gitCommand, repoPath + diffPath, tempPath, singleDev.getCommit1(), singleDev.getCommit2(), singleDev.getFile());
				int loc = GitRead.readDiff( repoPath + diffPath);
				singleDev.setLoc(loc);
				System.out.println(numb++);
			}
			
			//put final data to csv file
			ToCSV.generateCsvFile(repoPath + finalPath, devList);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
