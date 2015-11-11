package unibz.emse.versioningSystem.git;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibz.emse.versioningSystem.bean.BlameBean;
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
		
		FileInputStream fstream = new FileInputStream(logFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		String strLine;
		//line number count
		int lineNumber = 0;
		boolean startCount = false;
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			if(strLine.startsWith("diff --git")){
				
				//set id of commit to diffBean
				singleDiffBean.setCommitId(commitId);
				singleDiffBean.setDate(commitDate);
				
				singleDiffBean.setAuthor(author);
				if(singleDiffBean.getFile() != null){
					if(removedLines.size() > 0){
						singleDiffBean.setRemovedLines(removedLines);
					}
					resultDiff.addElement(singleDiffBean);
				}
				singleDiffBean = new DiffBean();
			}
			
			Matcher matcherLineNumbers = patternLineNumbers.matcher(strLine);
			Matcher matcherName = patternName.matcher(strLine);
			Matcher matcherLineRemoved = patternLineRemoved.matcher(strLine);
			Matcher matcherLineAdded = patternLineAdded.matcher(strLine);

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
				System.out.println(removedNumber + "," + removedQuantity);
				lineNumber = lineNumber -1;
			}

			//collect numbers of removed lines
			if(strLine.startsWith("- ")){
				int removedLine = lineNumber;					
				removedLines.add(removedLine);
				System.out.println(removedLine + " is a removed line number");				
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
}