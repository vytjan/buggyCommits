package versioningSystem.git;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import versioningSystem.bean.CommitBean;
import versioningSystem.bean.DevBean;
import versioningSystem.bean.DiffBean;
import versioningSystem.bean.FinalBean;

public class GitRead {
	public static Integer readDiff(String logFilePath) throws IOException, ParseException{
		Vector<Integer> addedLines = new Vector<Integer>();
		Integer result = 0;
		
		//if commented lines start
		String regexCommentStart = Pattern.compile("(.*?)") + Pattern.quote("/*") + Pattern.compile("(.*?)");
		Pattern patternCommentStart = Pattern.compile(regexCommentStart);
		
		String regexCommentEnd = Pattern.compile("(.*?)") + Pattern.quote("*/") + Pattern.compile("(.*?)");
		Pattern patternCommentEnd = Pattern.compile(regexCommentEnd);		
		
		//Check if it's a single line comment
		String regexCommentLine = Pattern.compile("(.*?)") + Pattern.quote("/*") + Pattern.compile("(.*?)") + Pattern.quote("*/") + Pattern.compile("(.*?)");
		Pattern patternCommentLine = Pattern.compile(regexCommentLine);		
		
		//check if it's a * comment line
		String regexCommentContinue = Pattern.quote("-") + Pattern.compile("\\s+") + Pattern.quote("*") + Pattern.compile("(.*?)");
		Pattern patternCommentContinue = Pattern.compile(regexCommentContinue);
		
		String regexCommentContinue2 = Pattern.quote("+") + Pattern.compile("\\s+") + Pattern.quote("*") + Pattern.compile("(.*?)");
		Pattern patternCommentContinue2 = Pattern.compile(regexCommentContinue2);
		// commented lines end
		
		FileInputStream fstream = new FileInputStream(logFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		String strLine;
		
		//Read File Line By Line
readFile:	while ((strLine = br.readLine()) != null)   {
			Matcher matcherCommentStart = patternCommentStart.matcher(strLine);
			Matcher matcherCommentEnd = patternCommentEnd.matcher(strLine);
			Matcher matcherCommentLine = patternCommentLine.matcher(strLine);
			Matcher matcherCommentContinue = patternCommentContinue.matcher(strLine);
			Matcher matcherCommentContinue2 = patternCommentContinue2.matcher(strLine);
			
			
			//if it's a single line comment
			if(matcherCommentLine.find()){
				continue readFile;
			}
			//if line is a comment
			if(matcherCommentStart.find()){
				continue readFile;
			}
			if(matcherCommentContinue.find() || matcherCommentContinue2.find()){
				continue readFile;
			}
			if(matcherCommentEnd.find()) {
				continue readFile;
			}
		
			//collect numbers of + lines
			if(strLine.startsWith("+ ")){				
				addedLines.add(1);				
			}
		}
		
		//Close the input stream
		br.close();

		if(addedLines.size() > 0){
			result = addedLines.size();
		}
		
		return result;
	}
	
	
	
	public static Vector<DiffBean> readDiffBuggy(String logFilePath, String commitId, String author, Date commitDate) throws IOException, ParseException{
		Vector<DiffBean> resultDiff = new Vector<DiffBean>();
		Vector<Integer> removedLines = new Vector<Integer>();
		DiffBean singleDiffBean = new DiffBean();

		//number and position of lines removed:
		String regexLineNumbers = Pattern.quote("@@ -") + Pattern.compile("(.*?)") + Pattern.quote(",") + Pattern.compile("(.*?)") +
		Pattern.quote(" +") + Pattern.compile("(.*?)") + Pattern.quote(" @@");
		Pattern patternLineNumbers = Pattern.compile(regexLineNumbers);
		
		//name of file modified:
		String regexName = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote(".java ") + Pattern.compile("(.*?)");
		Pattern patternName = Pattern.compile(regexName);
		
		//check if not test file:
		String regexTest = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote("Test") + Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)");
		Pattern patternTest = Pattern.compile(regexTest);
		
		FileInputStream fstream = new FileInputStream(logFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		String strLine;
		//line number count
		int lineNumber = 0;
		boolean gotFile = false;
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {

			Matcher matcherLineNumbers = patternLineNumbers.matcher(strLine);
			Matcher matcherName = patternName.matcher(strLine);

			Matcher matcherTest = patternTest.matcher(strLine);
			
			if(strLine.startsWith("diff --git") && singleDiffBean.getFile() != null){
				if(removedLines.size() > 0 ){
					//set id of commit to diffBean
					singleDiffBean.setCommitId(commitId);
					singleDiffBean.setDate(commitDate);
					singleDiffBean.setAuthor(author);
					
					singleDiffBean.setRemovedLines(removedLines);
					resultDiff.addElement(singleDiffBean);
				}
				removedLines = new Vector<Integer>();
				gotFile = false;
				singleDiffBean = new DiffBean();
			}
				
				//match file name
				if(matcherName.find() && !matcherTest.find()) {
					gotFile = true;
					String fileName = matcherName.group(1) + ".java";
					singleDiffBean.setFile(fileName);
				}
					
				if(matcherLineNumbers.find() && gotFile){
					String removedNumber = matcherLineNumbers.group(1);
					String removedQuantity = matcherLineNumbers.group(2);
					singleDiffBean.setRemovedQuantity(removedQuantity);
					lineNumber = Integer.parseInt(removedNumber);
					lineNumber = lineNumber -1;
				}
		
				//collect numbers of removed lines
				if(strLine.startsWith("- ") && gotFile){
					int removedLine = lineNumber;					
					removedLines.add(removedLine);			
				}
				
				//if line was added, -1 to line counter
				if(strLine.startsWith("+ ") && gotFile){
					lineNumber--;
				}
			if(gotFile){
				lineNumber++;
			}
		}
		if(singleDiffBean.getFile() != null && removedLines.size() > 0){
			
			//set id of commit to diffBean
			singleDiffBean.setCommitId(commitId);
			singleDiffBean.setDate(commitDate);
			singleDiffBean.setAuthor(author);
			
			singleDiffBean.setRemovedLines(removedLines);
			resultDiff.addElement(singleDiffBean);
			singleDiffBean = new DiffBean();
		}
		//Close the input stream
		br.close();

		return resultDiff;
	}
	
	public static Vector<String> readBlame(String logFilePath, Vector<Integer> lineNumbers) throws IOException, ParseException{
		HashSet<String> buggyCommit = new HashSet<String>();
		Vector<String> bugs = new Vector<String>();
		
		//if commented lines start
		String regexCommentStart = Pattern.compile("\\s+(.*?)") + Pattern.quote("/*") + Pattern.compile("(.*?)");
		Pattern patternCommentStart = Pattern.compile(regexCommentStart);
		
		String regexCommentEnd = Pattern.compile("(.*?)") + Pattern.quote("*/") + Pattern.compile("(.*?)");
		Pattern patternCommentEnd = Pattern.compile(regexCommentEnd);		
		
		//Check if it's a single line comment
		String regexCommentLine = Pattern.compile("\\s+(.*?)") + Pattern.quote("/*") + Pattern.compile("(.*?)") + Pattern.quote("*/") + Pattern.compile("(.*?)");
		Pattern patternCommentLine = Pattern.compile(regexCommentLine);		
		
		//check if it's a * comment line
		String regexCommentContinue = Pattern.compile("\\s+") + Pattern.quote("*") + Pattern.compile("(.*?)");
		Pattern patternCommentContinue = Pattern.compile(regexCommentContinue);
		
		//check if empty line (whitespaces only)
		Pattern patternEmpty = Pattern.compile("^\\s*$");
		
		FileInputStream fstream = new FileInputStream(logFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		String strLine;
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			for(Integer line:lineNumbers){
				
				//create matchers for comments
				Matcher matcherCommentStart = patternCommentStart.matcher(strLine);
				Matcher matcherCommentEnd = patternCommentEnd.matcher(strLine);
				Matcher matcherCommentLine = patternCommentLine.matcher(strLine);
				Matcher matcherCommentContinue = patternCommentContinue.matcher(strLine);
				Matcher matcherEmpty = patternEmpty.matcher(strLine);
				
				String regexRemline = Pattern.compile("(.*?)") + Pattern.quote("") + Pattern.compile("\\s+") + Pattern.quote("(") + Pattern.compile("(.*?)") + Pattern.quote(line.toString()) +  Pattern.quote(")") + Pattern.compile("(.*?)");
				Pattern patternRemLine = Pattern.compile(regexRemline);
				
				Matcher matcherRemLine = patternRemLine.matcher(strLine);
				//match file name
				if(matcherRemLine.find()) {
					//exclude commented lines:
					if(!matcherCommentStart.find() && !matcherCommentEnd.find() && !matcherCommentLine.find() && !matcherCommentContinue.find() && !matcherEmpty.find()){
						buggyCommit.add(matcherRemLine.group(1));
					}
				}
			}
		}
		//Close the input stream
		br.close();
		for(String set:buggyCommit){
			bugs.add(set);
		}

		return bugs;
	}



	/**
	 * This method reads a log file stored on your machine, parses it
	 * and encapsulates the results in a Vector of CommitBean objects. 
	 * 
	 * @param logFilePath
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static Vector<CommitBean> readCommits(String logFilePath) throws IOException, ParseException{
		Vector<CommitBean> result = new Vector<CommitBean>();
		Vector<String> modifiedFiles = new Vector<String>();
		Vector<String> addedFiles = new Vector<String>();
		Vector<String> deletedFiles = new Vector<String>();
		CommitBean singleCommit = new CommitBean();
		
		String regexCommitId = Pattern.quote("<commit-id>") + Pattern.compile("(.*?)") + Pattern.quote("</commit-id>");
		Pattern patternCommitId = Pattern.compile(regexCommitId);
		
		//Author email
		String regexAuthorEmail = Pattern.quote("<author-email>") + Pattern.compile("(.*?)") + Pattern.quote("</author-email>");
		Pattern patternAuthorEmail = Pattern.compile(regexAuthorEmail);
		
		//Date of commit
		String regexDate = Pattern.quote("<date>") + Pattern.compile("(.*?)") + Pattern.quote("</date>");
		Pattern patternCommitDate = Pattern.compile(regexDate);
		
		//Message of commit
		String regexMessage = Pattern.quote("<message>") + Pattern.compile("(.*?)") + Pattern.quote("</message>");
		Pattern patternCommitMessage = Pattern.compile(regexMessage);
		
		FileInputStream fstream = new FileInputStream(logFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
		String strLine;
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			Matcher matcherCommitId = patternCommitId.matcher(strLine);
			Matcher matcherAuthorEmail = patternAuthorEmail.matcher(strLine);
			Matcher matcherCommitDate = patternCommitDate.matcher(strLine);
			Matcher matcherCommitMessage = patternCommitMessage.matcher(strLine);
			
			if(!strLine.isEmpty()) {
				if(strLine.startsWith("<")) {
					if(singleCommit.getCommitId() != null) {
						if(modifiedFiles.size() > 0) {
							singleCommit.setModifiedFiles(modifiedFiles);
						} if(addedFiles.size() > 0) {
							singleCommit.setAddedFiles(addedFiles);
						} if(deletedFiles.size() > 0) {
							singleCommit.setDeletedFiles(deletedFiles);
						}
						
						result.addElement(singleCommit);
						modifiedFiles = new Vector<String>();
						addedFiles = new Vector<String>();
						deletedFiles = new Vector<String>();
						singleCommit = new CommitBean();
					}
	
					if(matcherAuthorEmail.find()) {
						String correct = Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)");
						Pattern patternCorrected = Pattern.compile(correct);
						Matcher matcherCorrect = patternCorrected.matcher(matcherAuthorEmail.group(1));
						if(matcherCorrect.find()){
							singleCommit.setAuthor(matcherCorrect.group(1));
						} else if(matcherAuthorEmail.group(1).equals("dev-null@apache.org")){
							continue;
						} else {
							singleCommit.setAuthor(matcherAuthorEmail.group(1));
						}
					} if(matcherCommitId.find()) {
						singleCommit.setBuggy(false);
						singleCommit.setCommitId(matcherCommitId.group(1));
					}  if(matcherCommitDate.find()) {
						DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.ENGLISH);
						Date date = format.parse(matcherCommitDate.group(1));
						singleCommit.setDate(date);
					} if(matcherCommitMessage.find()) {
						singleCommit.setCommitMessage(matcherCommitMessage.group(1));
					}
				}
	
				//Check if it is a modified file line
				// add M,A,D lines to different vectors and after that just to link them to the ones from commitBean
				if(strLine.startsWith("M	")) {
					modifiedFiles.addElement(strLine.substring(2));
				}
			}
		}
		
		//Close the input stream
		br.close();
	
		return result;
	}



	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}



	public static Vector<FinalBean> getDevs(String repoPath, String gitCommand, String diffPath, 
		String tempPath, String revisionPath, Vector<CommitBean> commits) throws IOException, InterruptedException, ParseException{
		
		HashSet<String> developers = new HashSet<String>();
		Vector<DevBean> devs = new Vector<DevBean>();
		Vector<FinalBean> partResult = new Vector<FinalBean>();
		//single final bean
		FinalBean singleFinal = new FinalBean();
		
		//Strings for exec git commands and reading files
		
		
		for(CommitBean commit:commits){
			developers.add(commit.getAuthor());
		}
		
		//check if a test file:
		String regexTestFile = Pattern.compile("(.*?)") + Pattern.quote("Test") + Pattern.compile("(.*?)");
		Pattern patternTestFile = Pattern.compile(regexTestFile);
		
		
		//set email of the dev on DevBean
		for(String dev:developers){
			DevBean singleDev = new DevBean();
			
			singleDev.setDev(dev);
			devs.add(singleDev);
		}
			
			//remove commits not containing 
			for(CommitBean singleCommit1:commits){
				
				for(CommitBean singleCommit2:commits){
					
					if(singleCommit1.getModifiedFiles() != null && singleCommit2.getModifiedFiles() != null &&  
							singleCommit2.getModifiedFiles().size() > 0 && singleCommit1.getAuthor().equals(singleCommit2.getAuthor())){
						for(String fileName1:singleCommit1.getModifiedFiles()){
							
							Matcher matcherTestFile = patternTestFile.matcher(fileName1);
							if(!singleCommit1.getCommitId().equals(singleCommit2.getCommitId()) && fileName1.endsWith(".java") && !matcherTestFile.find()){
								for(String fileName2:singleCommit2.getModifiedFiles()){
									if(fileName1.equals(fileName2)&& fileName2.endsWith(".java") && !matcherTestFile.find()){
										long difference = getDateDiff(singleCommit1.getDate(), singleCommit2.getDate(),TimeUnit.MINUTES);
										if(difference >= 0){
											//setting all the necessarry vars for the final data output
											singleFinal.setAuthor(singleCommit2.getAuthor());
											singleFinal.setEndDate(singleCommit2.getDate());
											singleFinal.setStartDate(singleCommit1.getDate());
											singleFinal.setFile(fileName2);
											singleFinal.setCommit1(singleCommit1.getCommitId());
											singleFinal.setCommit2(singleCommit2.getCommitId());
											singleFinal.setDays(TimeUnit.DAYS.convert(difference, TimeUnit.MINUTES));
											if(singleCommit2.getBuggy()){
												singleFinal.setBuggy(true);
											} else {
												singleFinal.setBuggy(false);
											}
											
											//get number of developers who modified the same file between these 2 commits
											int[] devsNumber = GitRead.noOfDevs(commits, singleCommit2.getCommitId(), singleCommit1.getCommitId(),fileName2, singleCommit2.getAuthor(), revisionPath, repoPath);
											singleFinal.setNoOfDevs(devsNumber[1]);
											singleFinal.setCommits(devsNumber[0]);
											
											partResult.addElement(singleFinal);
											singleFinal = new FinalBean();
										}
									}
									
								}	
							}
						}	
					}
				}	
			}
		
		return partResult;
	}



	public static int[] noOfDevs(Vector<CommitBean> commits, String commitAfter, String commitBefore, String fileName, String author, String logFilePath, String repoPath) throws IOException, InterruptedException{
		int[] anArray;
		anArray = new int[2];
		HashSet<String> devsArray = new HashSet<String>();
		
		Integer numberCommits;
		String repositoryPath = repoPath;
		String gitCommand = "/usr/bin/git";
		String outputPath = logFilePath;
		String tmpFolder = "/home/vytautas/Desktop/";
		
		//get list of revision between 2 commits
		GitUtilities.getRevList(repositoryPath, gitCommand, outputPath, tmpFolder, commitAfter, commitBefore, fileName );
		
		FileInputStream fstream = new FileInputStream(outputPath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		String strLine;
		numberCommits = 0;
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			numberCommits +=1;
			if(  strLine.length() >= 7){
				String commId = strLine.substring(0, 7);
				for(CommitBean singleCommit:commits){
					if(commId.equals(singleCommit.getCommitId()) && !author.equals(singleCommit.getAuthor())){
						devsArray.add(singleCommit.getAuthor());
					}
				}
			}
		}
		//Close the input stream
		br.close();
		
		anArray[0] = numberCommits;
		anArray[1] = devsArray.size();
		
		return anArray;
	}
	
}
