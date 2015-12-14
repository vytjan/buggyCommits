package unibz.emse.versioningSystem.git;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibz.emse.versioningSystem.bean.BlameBean;
import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.bean.DevBean;
import unibz.emse.versioningSystem.bean.DiffBean;

public class GitRead {
	public static Vector<DiffBean> readDiff(String logFilePath, String commitId, String author, Date commitDate) throws IOException, ParseException{
		Vector<DiffBean> resultDiff = new Vector<DiffBean>();
		Vector<Integer> removedLines = new Vector<Integer>();
		DiffBean singleDiffBean = new DiffBean();
		
		
		//added lines
		String regexLineAdded = Pattern.quote("+ ") + Pattern.compile("(.*?)");
		Pattern patternLineAdded = Pattern.compile(regexLineAdded);
		
		//removed lines
		String regexLineRemoved = Pattern.quote("- ") + Pattern.compile("(.*?)");
		Pattern patternLineRemoved = Pattern.compile(regexLineRemoved);
		
		//number and position of lines removed:
		String regexLineNumbers = Pattern.quote("@@ -") + Pattern.compile("(.*?)") + Pattern.quote(",") + Pattern.compile("(.*?)") +
		Pattern.quote(" +") + Pattern.compile("(.*?)") + Pattern.quote(" @@");
		Pattern patternLineNumbers = Pattern.compile(regexLineNumbers);
		
		//name of file modified:
		String regexName = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)");
		Pattern patternName = Pattern.compile(regexName);
		
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
		//line number count
		int lineNumber = 0;
		boolean startCount = false;
		
		//Read File Line By Line
readFile:	while ((strLine = br.readLine()) != null)   {
			Matcher matcherLineNumbers = patternLineNumbers.matcher(strLine);
			Matcher matcherName = patternName.matcher(strLine);
//			Matcher matcherLineRemoved = patternLineRemoved.matcher(strLine);
			Matcher matcherLineAdded = patternLineAdded.matcher(strLine);
			Matcher matcherCommentStart = patternCommentStart.matcher(strLine);
			Matcher matcherCommentEnd = patternCommentEnd.matcher(strLine);
			Matcher matcherCommentLine = patternCommentLine.matcher(strLine);
			Matcher matcherCommentContinue = patternCommentContinue.matcher(strLine);
			Matcher matcherCommentContinue2 = patternCommentContinue2.matcher(strLine);
			
			
//			//if it's a single line comment
//			if(matcherCommentLine.find()){
////				System.out.println(strLine);
//				continue readFile;
//			}
//			//if line is a comment
//			if(matcherCommentStart.find()){
////				System.out.println(strLine);
//				continue readFile;
//			}
//			if(matcherCommentContinue.find() || matcherCommentContinue2.find()){
////				System.out.println(strLine);
//				continue readFile;
//			}
//			if(matcherCommentEnd.find()) {
////				System.out.println(strLine);
//				continue readFile;
//			}
			
//			System.out.println(strLine);
				if(strLine.startsWith("diff --git")){
					
					//set id of commit to diffBean
					singleDiffBean.setCommitId(commitId);
					singleDiffBean.setDate(commitDate);
					
					singleDiffBean.setAuthor(author);
					if(singleDiffBean.getFile() != null){
						if(removedLines.size() > 0){
							System.out.println("Removed lines are: " + removedLines );
							singleDiffBean.setRemovedLines(removedLines);
						}
						resultDiff.addElement(singleDiffBean);
					}
					singleDiffBean = new DiffBean();
				}
				
				//match file name
				if(matcherName.find()) {
					String fileName = matcherName.group(1);
					singleDiffBean.setFile(fileName);
				}
					
				if(matcherLineNumbers.find()){
					String removedNumber = matcherLineNumbers.group(1);
					String removedQuantity = matcherLineNumbers.group(2);
					//singleDiffBean.setRemovedNumber(removedNumber);
					lineNumber = Integer.parseInt(removedNumber);
					//System.out.println(removedNumber + "," + removedQuantity);
					lineNumber = lineNumber -1;
				}
		
				//collect numbers of removed lines
				if(strLine.startsWith("- ")){
					int removedLine = lineNumber;					
					removedLines.add(removedLine);
//					System.out.println(removedLine + " is a removed line number");				
				}
				
				if(strLine.startsWith("+ ")){
					lineNumber--;
				}
					lineNumber++;
		}
		resultDiff.addElement(singleDiffBean);
		//System.out.println(resultDiff.size() + " a size of resultDiff");
		//Close the input stream
		br.close();

		return resultDiff;
	}
	
	public static Vector<DiffBean> readDiffBuggy(String logFilePath, String commitId, String author, Date commitDate) throws IOException, ParseException{
		Vector<DiffBean> resultDiff = new Vector<DiffBean>();
		Vector<Integer> removedLines = new Vector<Integer>();
		DiffBean singleDiffBean = new DiffBean();
		
		//added lines
//		String regexLineAdded = Pattern.quote("+ ") + Pattern.compile("(.*?)");
//		Pattern patternLineAdded = Pattern.compile(regexLineAdded);
//		
//		//removed lines
//		String regexLineRemoved = Pattern.quote("- ") + Pattern.compile("(.*?)");
//		Pattern patternLineRemoved = Pattern.compile(regexLineRemoved);
		
		//number and position of lines removed:
		String regexLineNumbers = Pattern.quote("@@ -") + Pattern.compile("(.*?)") + Pattern.quote(",") + Pattern.compile("(.*?)") +
		Pattern.quote(" +") + Pattern.compile("(.*?)") + Pattern.quote(" @@");
		Pattern patternLineNumbers = Pattern.compile(regexLineNumbers);
		
		//name of file modified:
		String regexName = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)");
		Pattern patternName = Pattern.compile(regexName);
		
		//check if not test file:
		String regexTest = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote("Test") + Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)");
		Pattern patternTest = Pattern.compile(regexTest);
		
		//check if java file:
		String regexJava = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote(".java ") + Pattern.compile("(.*?)");
		Pattern patternJava = Pattern.compile(regexJava);
		
//		//if commented lines start
//		String regexCommentStart = Pattern.quote("-") + Pattern.compile("\\s+") + Pattern.compile("(.*?)") + Pattern.quote("/*") + Pattern.compile("(.*?)");
//		Pattern patternCommentStart = Pattern.compile(regexCommentStart);
//		
//		String regexCommentEnd = Pattern.compile("(.*?)") + Pattern.quote("*/") + Pattern.compile("(.*?)");
//		Pattern patternCommentEnd = Pattern.compile(regexCommentEnd);		
//		
//		//Check if it's a single line comment
//		String regexCommentLine = Pattern.quote("-") + Pattern.compile("\\s+") + Pattern.compile("(.*?)") + Pattern.quote("/*") + Pattern.compile("(.*?)") + Pattern.quote("*/") + Pattern.compile("(.*?)");
//		Pattern patternCommentLine = Pattern.compile(regexCommentLine);		
//		
//		//check if it's a * comment line
//		String regexCommentContinue = Pattern.quote("-") + Pattern.compile("\\s+") + Pattern.quote("*") + Pattern.compile("(.*?)");
//		Pattern patternCommentContinue = Pattern.compile(regexCommentContinue);
//		
//		String regexCommentContinue2 = Pattern.quote("+") + Pattern.compile("\\s+") + Pattern.quote("*") + Pattern.compile("(.*?)");
//		Pattern patternCommentContinue2 = Pattern.compile(regexCommentContinue2);
		
		
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
//			Matcher matcherLineRemoved = patternLineRemoved.matcher(strLine);
//			Matcher matcherLineAdded = patternLineAdded.matcher(strLine);
//			Matcher matcherCommentStart = patternCommentStart.matcher(strLine);
//			Matcher matcherCommentEnd = patternCommentEnd.matcher(strLine);
//			Matcher matcherCommentLine = patternCommentLine.matcher(strLine);
//			Matcher matcherCommentContinue = patternCommentContinue.matcher(strLine);
//			Matcher matcherCommentContinue2 = patternCommentContinue2.matcher(strLine);
			Matcher matcherTest = patternTest.matcher(strLine);
			Matcher matcherJava = patternJava.matcher(strLine);
			
			if(strLine.startsWith("diff --git") && singleDiffBean.getFile() != null && removedLines.size() > 0 ){
				System.out.println("Found diff --git line");
				//set id of commit to diffBean
				singleDiffBean.setCommitId(commitId);
				System.out.println(commitId);
				singleDiffBean.setDate(commitDate);
				singleDiffBean.setAuthor(author);
				
//				System.out.println("Removed lines are: " + removedLines );
				System.out.println(singleDiffBean.getFile() + " to bean");
				for(Integer singleLine:removedLines){
					System.out.println(singleLine);
				}
				singleDiffBean.setRemovedLines(removedLines);
				resultDiff.addElement(singleDiffBean);
				gotFile = false;
				singleDiffBean = new DiffBean();
			}
				
				//match file name
				if(matcherName.find() && matcherJava.find() && !matcherTest.find()) {
					gotFile = true;
					String fileName = matcherName.group(1);
					singleDiffBean.setFile(fileName);
					System.out.println(fileName + " found on diff");
				}
					
				if(matcherLineNumbers.find() && gotFile){
					String removedNumber = matcherLineNumbers.group(1);
					String removedQuantity = matcherLineNumbers.group(2);
					//singleDiffBean.setRemovedNumber(removedNumber);
					lineNumber = Integer.parseInt(removedNumber);
					//System.out.println(removedNumber + "," + removedQuantity);
					lineNumber = lineNumber -1;
				}
		
				//collect numbers of removed lines
				if(strLine.startsWith("- ") && gotFile){
					int removedLine = lineNumber;					
					removedLines.add(removedLine);
					//System.out.println(removedLine + " is a removed line number");				
				}
				
				//if line was added, -1 to line counter
				if(strLine.startsWith("+ ") && gotFile){
					lineNumber--;
				}
				
//				//if it's a single line comment
//				if(matcherCommentLine.find()){
////					System.out.println(strLine);
//					continue readFile;
//				}
//				//if line is a comment
//				if(matcherCommentStart.find()){
////					System.out.println(strLine);
//					continue readFile;
//				}
//				if(matcherCommentContinue.find() || matcherCommentContinue2.find()){
////					System.out.println(strLine);
//					continue readFile;
//				}
//				if(matcherCommentEnd.find()) {
////					System.out.println(strLine);
//					continue readFile;
//				}
				
//				System.out.println(strLine);
			if(gotFile){
				lineNumber++;
			}
		}
		if(singleDiffBean.getFile() != null && removedLines.size() > 0){
			
			//set id of commit to diffBean
			singleDiffBean.setCommitId(commitId);
			singleDiffBean.setDate(commitDate);
			singleDiffBean.setAuthor(author);
			
//			System.out.println("Removed lines are: " + removedLines );
			singleDiffBean.setRemovedLines(removedLines);
			resultDiff.addElement(singleDiffBean);
			singleDiffBean = new DiffBean();
		}
		//System.out.println(resultDiff.size() + " a size of resultDiff");
		//Close the input stream
		br.close();

		return resultDiff;
	}
	
	public static Vector<BlameBean> readBlame(String logFilePath) throws IOException, ParseException{
		Vector<BlameBean> resultBlame = new Vector<BlameBean>();
		Vector<String> modifiedLines = new Vector<String>();
		BlameBean singleBlameBean = new BlameBean();
		
		//added lines
		String regexLineAdded = Pattern.quote("+        ") + Pattern.compile("(.*?)");
		Pattern patternLineAdded = Pattern.compile(regexLineAdded);
		
		//removed lines
		String regexLineRemoved = Pattern.quote("-        ") + Pattern.compile("(.*?)");
		Pattern patternLineRemoved = Pattern.compile(regexLineAdded);
		
		//number and position of lines added:
		String regexAddedlines = Pattern.quote("@@ ") + Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)") +
		Pattern.quote(" @@");
		Pattern patternAddedlines = Pattern.compile(regexAddedlines);
		
		//number and position of lines removed:
		String regexRemlines = Pattern.quote("@@ ") + Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)") +
		Pattern.quote(" @@");
		Pattern patternRemlines = Pattern.compile(regexRemlines);
		
		//name of file modified:
		String regexName = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote(" ") + Pattern.compile("(.*?)");
		Pattern patternName = Pattern.compile(regexName);
		
		FileInputStream fstream = new FileInputStream(logFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		String strLine;
		Vector<String> addedLines = new Vector<String>();
		Vector<String> removedLines = new Vector<String>();
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			if(strLine.startsWith("diff --git")){
				//set id of commit to diffBean
//				singleBlameBean.setCommitId(commitId);
//				singleBlameBean.setDate(commitDate);
//				
//				singleBlameBean.setAuthor(author);
				if(singleBlameBean.getName() != null){
					if(addedLines.size() > 0){
						//System.out.println(addedLines.size() + " is a size of added lines");
						singleBlameBean.setAddedLines(addedLines);
					}
					if(removedLines.size() > 0){
						singleBlameBean.setRemovedLines(removedLines);
						removedLines = new Vector<String>();
					}
					resultBlame.addElement(singleBlameBean);
				}
				singleBlameBean = new BlameBean();
			}
			
			Matcher matcherLineAdded = patternLineAdded.matcher(strLine);
			Matcher matcherAddedLines = patternAddedlines.matcher(strLine);
			Matcher matcherRemLines = patternRemlines.matcher(strLine);
			Matcher matcherName = patternName.matcher(strLine);
			Matcher matcherLineRemoved = patternLineRemoved.matcher(strLine);
			

			//match file name
			if(matcherName.find()) {
				String fileName = matcherName.group(1);
				singleBlameBean.setName(fileName);
//				System.out.println(fileName);
			}
			
			//match added lines number
			if(matcherAddedLines.find()) {
				String added = matcherAddedLines.group(1);
//				System.out.println(added);
				singleBlameBean.setAddedNumber(added);
			}
			
			//match removed lines number
			if(matcherRemLines.find()){
				String removed = matcherRemLines.group(2);
//				System.out.println(removed);
				singleBlameBean.setRemovedNumber(removed);
			}
			
			//collect added lines
			if(matcherLineAdded.find()){
				String addedLine = matcherLineAdded.group(1);
				addedLines.add(addedLine);
			}

			//collect removed lines
			if(matcherLineRemoved.find()){
				String removedLine = matcherLineRemoved.group(1);
				removedLines.add(removedLine);
			}
		}
		resultBlame.addElement(singleBlameBean);
		//System.out.println(resultDiff.size() + " a size of resultDiff");
		//Close the input stream
		br.close();

		return resultBlame;
	}

	
	public static Vector<DevBean> getDevs(Vector<CommitBean> commits){
		
		HashSet<String> developers = new HashSet<String>();
		Vector<DevBean> devs = new Vector<DevBean>();
		for(CommitBean commit:commits){
			developers.add(commit.getAuthor());
		}
		
		//set email of the dev on DevBean
		for(String dev:developers){
			DevBean singleDev = new DevBean();
			
			singleDev.setDev(dev);
			devs.add(singleDev);
		}
		
		//put commits of a developer to DevBean
		for(DevBean singleDev:devs){
			Vector<CommitBean> commitsVector = new Vector<CommitBean>();
			for(CommitBean commit:commits){
				if(commit.getAuthor().equals(singleDev.getDev())){
					commitsVector.add(commit);
//					System.out.println(commit.getCommitId());
				}
			}
			singleDev.setCommits(commitsVector);
		}
		
		return devs;
	}
	
	public static Vector<DevBean> findFiles(Vector<DevBean> origin) throws IOException, InterruptedException, ParseException {
		Vector<DevBean> modified = new Vector<DevBean>();
		
		//check if a test file:
		String regexTestFile = Pattern.compile("(.*?)") + Pattern.quote("Test") + Pattern.compile("(.*?)");
		Pattern patternTestFile = Pattern.compile(regexTestFile);
		
		
		//print the list of developers
		int num = 0;
		for(DevBean dev:origin){
//			System.out.println(dev.getCommits().size());
			
			//remove commits not containing 
			for(int i = 0; i<dev.getCommits().size(); i++){
				
				CommitBean commitBefore = dev.getCommits().get(i);
				
				for(int j = i+1; j < dev.getCommits().size(); j++){
					
					CommitBean commitAfter = dev.getCommits().get(j);
										
					if(commitBefore.getModifiedFiles() != null && commitBefore.getModifiedFiles().size() > 0){
						for(String comBefore:commitBefore.getModifiedFiles()){
							if(commitAfter.getModifiedFiles() != null && commitAfter.getModifiedFiles().size() > 0){
								for(String comAfter:commitAfter.getModifiedFiles()){
									Matcher matcherTestFile = patternTestFile.matcher(comBefore);
									if(comBefore.equals(comAfter) && !comBefore.endsWith(".txt") && !comBefore.endsWith(".xml") && !matcherTestFile.find()){
										//System.out.println(comBefore + " " + comAfter);
										//git diff
										
//										GitSzz.getDiff("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/diff.txt", "/home/vytautas/Desktop/", commitBefore.getCommitId(), commitAfter.getCommitId(), comAfter);
//										Vector<DiffBean> diffVector = GitRead.readDiff("/home/vytautas/Desktop/commons-io/diff.txt", commitAfter.getCommitId(), commitAfter.getAuthor(), commitAfter.getDate());
//										for(DiffBean singleDiff:diffVector){
										//	System.out.println(singleDiff.getRemovedNumber());
//										}
										num++;
										System.out.println(num);
									}
								}	
							}
						}	
					}
				}	
			}
		}
		
		return modified;
	}
}
