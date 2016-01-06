package unibz.emse.versioningSystem.git;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibz.emse.versioningSystem.bean.DiffBean;
import unibz.emse.versioningSystem.bean.FinalBean;

public class GitRead {
	public static Integer readDiff(String logFilePath) throws IOException, ParseException{
		Vector<Integer> addedLines = new Vector<Integer>();
		Integer result = 0;
		
		//added lines
		String regexLineAdded = Pattern.quote("+ ") + Pattern.compile("(.*?)");
		Pattern patternLineAdded = Pattern.compile(regexLineAdded);
		
		//removed lines
		String regexLineRemoved = Pattern.quote("- ") + Pattern.compile("(.*?)");
		Pattern patternLineRemoved = Pattern.compile(regexLineRemoved);
		
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
//				System.out.println(strLine);
				continue readFile;
			}
			//if line is a comment
			if(matcherCommentStart.find()){
//				System.out.println(strLine);
				continue readFile;
			}
			if(matcherCommentContinue.find() || matcherCommentContinue2.find()){
//				System.out.println(strLine);
				continue readFile;
			}
			if(matcherCommentEnd.find()) {
//				System.out.println(strLine);
				continue readFile;
			}
		
				//collect numbers of + lines
				if(strLine.startsWith("+ ")){				
					addedLines.add(1);
//					System.out.println(addedLine + " is a rem line number");				
				}
				
//				if(strLine.startsWith("- ")){
//					lineNumber--;
//				}
//					lineNumber++;
		}
		
		//Close the input stream
		br.close();

		if(addedLines.size() > 0){
//			System.out.println("Added lines are: " + addedLines );
			result = addedLines.size();
		}
		
		return result;
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
		String regexName = Pattern.quote("diff --git a/") + Pattern.compile("(.*?)") + Pattern.quote(".java ") + Pattern.compile("(.*?)");
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
//			if(removedLines.size() < 1){
//				gotFile = false;
//			}
			Matcher matcherLineNumbers = patternLineNumbers.matcher(strLine);
			Matcher matcherName = patternName.matcher(strLine);

			Matcher matcherTest = patternTest.matcher(strLine);
			Matcher matcherJava = patternJava.matcher(strLine);
			
			if(strLine.startsWith("diff --git") && singleDiffBean.getFile() != null){
//				System.out.println("Found diff --git line");
				if(removedLines.size() > 0 ){
					//set id of commit to diffBean
					singleDiffBean.setCommitId(commitId);
//					System.out.println(commitId);
//					System.out.println(singleDiffBean.getFile() + " is a fileName");
					singleDiffBean.setDate(commitDate);
					singleDiffBean.setAuthor(author);
					
	//				System.out.println("Removed lines are: " + removedLines );
	//				System.out.println(singleDiffBean.getFile() + " to bean");
//					for(Integer singleLine:removedLines){
//						System.out.println(singleLine);
//					}
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
//					System.out.println(fileName + " found on diff");
				}
					
				if(matcherLineNumbers.find() && gotFile){
					String removedNumber = matcherLineNumbers.group(1);
					String removedQuantity = matcherLineNumbers.group(2);
					singleDiffBean.setRemovedQuantity(removedQuantity);
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
					String codeLine = matcherRemLine.group(3);
					//exclude commented lines:
					if(!matcherCommentStart.find() && !matcherCommentEnd.find() && !matcherCommentLine.find() && !matcherCommentContinue.find() && !matcherEmpty.find()){
//						System.out.println(strLine);
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
	
}
