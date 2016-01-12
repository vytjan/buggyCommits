package versioningSystem.general;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import issueTracker.IssueMining.*;
import issueTracker.bean.IssueBean;
import versioningSystem.bean.CommitBean;
import versioningSystem.bean.DiffBean;
import versioningSystem.bean.FinalBean;
import versioningSystem.git.GitRead;
import versioningSystem.git.GitSzz;
import versioningSystem.git.GitUtilities;
import versioningSystem.git.Csv;

public class General {

	public static void main(String[] args) throws Exception {
		
		//the local repository path
		String repoPath = "/home/vytautas/Desktop/commons-io";
//		String repoPath = "/home/vytautas/Desktop/ant-ivy";
//		String repoPath = "/home/vytautas/Desktop/activemq-apollo";
//		String repoPath = "/home/vytautas/Desktop/ddlutils";
//		String repoPath = "/home/vytautas/Desktop/karaf";
//		String repoPath = "/home/vytautas/Desktop/drill";
//		String repoPath = "/home/vytautas/Desktop/pivot";
		
		String gitCommand = "/usr/bin/git";
		String tempPath = "/home/vytautas/Desktop/";
		
		//partial results path
		String diffPath = "/diff.txt";
		String blamePath = "/blame.txt";
		String logFilePath = "/log.txt";
		String revisionPath = "/revlist.txt";
//		String branchName = "master";
		String branchName = "trunk";
		
		//final csv path
		String finalPath = "/commits.csv";
		
		//jira stuff
		String jiraUrl = "https://issues.apache.org/jira/";
		//code of the project in Jira issue tracker
		String jiraProject = "IO";
//		String jiraProject = "IVY";
//		String jiraProject = "AMQ";
//		String jiraProject = "DDLUTILS";
//		String jiraProject = "KARAF";
//		String jiraProject = "DRILL";
//		String jiraProject = "PIVOT";
		String issueType = "BUG";
		String issueStatus = "RESOLVED";
		
		
		//url's of projects
		String projectUrl = "https://github.com/apache/commons-io.git";
//		String projectUrl = "https://github.com/apache/ant-ivy.git";
//		String projectUrl = "https://github.com/apache/activemq-apollo.git";
//		String projectUrl = "https://github.com/apache/ddlutils.git";
//		String projectUrl = "https://github.com/apache/karaf.git";
//		String projectUrl = "https://github.com/apache/drill.git";
//		String projectUrl = "https://github.com/apache/pivot.git";
	
		
		try {
			
			GitUtilities.cloneGitRepositoryUnix(projectUrl, tempPath, gitCommand, tempPath);
			GitSzz.restore(repoPath, gitCommand, tempPath, branchName);
			GitUtilities.getLogFromGitRepository(repoPath, gitCommand, repoPath + logFilePath, tempPath);
			
			//get full log of commits
			Vector<CommitBean> commits = GitRead.readCommits(repoPath + logFilePath);
			
			

			/*Mine all the single project issues from Jira*/
			//@TODO change static vars to dynamic
			Vector<IssueBean> collectToReturn = JiraMining.mineIssues(jiraUrl, jiraProject, issueType, issueStatus, null, null, null);
			
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
					}
				}
			}
			
			
			//git diff on every file of commit:
			for(CommitBean singleBugFix:bugFixCommits){
				String singleCommitId = singleBugFix.getCommitId();

//				System.out.println(singleCommitId);
				String singleCommitAuthor = singleBugFix.getAuthor();
				Date singleCommitDate = singleBugFix.getDate();
				
				//Get diff for every commit
				GitSzz.getDiffBuggy(repoPath, gitCommand, repoPath + diffPath, tempPath, singleCommitId, branchName);
				
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
			Vector<FinalBean> devList = GitRead.getDevs(repoPath, gitCommand, repoPath + diffPath, tempPath, repoPath + revisionPath, commits);
		
			//get #LOC of every file
			for(FinalBean singleDev:devList){
				GitSzz.getDiff(repoPath, gitCommand, repoPath + diffPath, tempPath, singleDev.getCommit1(), singleDev.getCommit2(), singleDev.getFile());
				int loc = GitRead.readDiff( repoPath + diffPath);
				singleDev.setLoc(loc);
			}
			
			//put final data to csv file
			Csv.generateCsvFile(repoPath + finalPath, devList);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
