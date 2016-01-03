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
			
			/*Mine all the single project issues from Jira*/
			Vector<IssueBean> collectToReturn = JiraMining.mineIssues("https://issues.apache.org/jira/", "IO", "BUG", "RESOLVED", null, null, null);
			
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
						//System.out.println(parseCommit.getDate());
						//System.out.println(parseCommit.getModifiedFiles().size());
//						for(String singleModified:parseCommit.getModifiedFiles()){
//							System.out.println(singleModified);
//						}
					}
				}
			}
			
//			System.out.println(bugFixCommits.size() + " is a size of bugfixed commits vector");
			
			//if "checkout to HEAD branch"
			//GitSzz.checkoutCommit("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/", null, reverse);
			
			//git diff on every file of commit:
			for(CommitBean singleBugFix:bugFixCommits){
				String singleCommitId = singleBugFix.getCommitId();
//				System.out.println();
//				System.out.println(singleCommitId);
				String singleCommitAuthor = singleBugFix.getAuthor();
				Date singleCommitDate = singleBugFix.getDate();
//				for(String singleFile:singleBugFix.getModifiedFiles()){
//					System.out.println(singleFile);
//				}
				
//				System.out.println(singleCommitId + " if is duplicated");
				//Get diff for every commit
				GitSzz.getDiffBuggy("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/diff.txt", "/home/vytautas/Desktop/", singleCommitId);
				
//				//Parse every diff command from diff.txt file
				Vector<DiffBean> diffVector = GitRead.readDiffBuggy("/home/vytautas/Desktop/commons-io/diff.txt", singleCommitId, singleCommitAuthor, singleCommitDate);
//				for(DiffBean singleDiff: diffVector){
//					System.out.println(singleDiff.getRemovedQuantity() + " is a quantity removed");
//					System.out.println(singleDiff.getFile() + " is a filename");
//						for(Integer singleNumber:singleDiff.getRemovedLines()){
//							System.out.println(singleNumber + " is a removed line");
//						}
//					System.out.println(singleDiff.getRemovedLines());
					
//				}
				//System.out.println(diffVector.size() + " is a size of diffVector");
				
				//checkout to every different commit version	
				
				//get the blame history of every file in checkouted commit
				for(DiffBean singleDiff:diffVector) {
					String fileNameToBlame = singleDiff.getFile();
					Vector<Integer> lineNumbers = singleDiff.getRemovedLines();
					String singleComId = singleDiff.getCommitId();
					GitSzz.checkoutCommit("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/", singleComId);
					
					//git blame for every file to .txt
					GitSzz.getBlameHistory("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/blame.txt", "/home/vytautas/Desktop/", fileNameToBlame);
//					
					//parse git blame
					Vector<String> blame = GitRead.readBlame("/home/vytautas/Desktop/commons-io/blame.txt", lineNumbers);
//					for(String singleBlame:blame){
//						System.out.println(singleBlame + " is a blame sssssssssssss");
//					}
					
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
				
//				ToCSV.generateCsvFile("/home/vytautas/Desktop/commons-io/commits.csv", commits);
				
//				for(CommitBean singleCommm:commits){
//					if(singleCommm.getBuggy()){
//						System.out.println(singleCommm.getCommitId());
//					}
//				}
				
				//get full list of developers
				Vector<FinalBean> devList = GitRead.getDevs(commits);
				ToCSV.generateCsvFile("/home/vytautas/Desktop/commons-io/commits.csv", devList);
//				for(DevBean singleDevs:devList){
//					System.out.println(singleDevs.getCommits().size() + "AAAA");
//				}
				
				//filter Commits only to those containing same file names:
//				Vector<FinalBean> filtered = GitRead.findFiles(devList);
//				
//				for(FinalBean singleDev:filtered){
//					System.out.println(singleDev.getCommits().size());
//				}
				
//				System.out.println(commits.size() + " is size of commits vector");
				
				
				
				
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
