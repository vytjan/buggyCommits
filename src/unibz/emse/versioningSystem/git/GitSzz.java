package unibz.emse.versioningSystem.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.bean.DiffBean;

public class GitSzz {
	/**
	 * This method extract the change log from an already cloned git 
	 * repository and stores it in a textual file.
	 * 
	 * @param repositoryLocalPath
	 * @param scriptFolder
	 * @param gitCommand
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	public static void getBlameHistory(String repositoryLocalPath,
			String gitCommand, String outputPath, String tmpFolder, String fileNameToBlame) throws IOException, InterruptedException {
			//[START] Create a temporary file where we write the commands
				//that we want to execute from command line
				File commandsToExecute = new File(tmpFolder + "/commands.sh");
				commandsToExecute.createNewFile();
				//[END]
				
				//[START] We print the command that we want to execute
				PrintWriter pw = new PrintWriter(commandsToExecute);
				pw.println("cd " + repositoryLocalPath);
				pw.println(gitCommand + " blame " + " -w -c " + fileNameToBlame +
				" > " + outputPath);
				pw.close();
				//[END]
				
				
				//[START] Make the file executable (i.e., adds execution
				//permissions to the created file)
				Runtime rt = Runtime.getRuntime();
				String cmd = "chmod u+x " + tmpFolder + "/commands.sh";
				Process process = rt.exec(cmd);

				String line = null;

				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				while ((line = stdoutReader.readLine()) != null) {
					System.out.println(line);
				}

				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				while ((line = stderrReader.readLine()) != null) {
					System.out.println(line);
				}

				process.waitFor();
				//[END]

				//[START] Execute the command that we stored
				//in commands.sh (i.e., in this case, proceed git blame)
				cmd = tmpFolder + "./commands.sh";
				process = rt.exec(cmd);

				line = null;

				stdoutReader = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				while ((line = stdoutReader.readLine()) != null) {
					System.out.println(line);
				}

				stderrReader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				while ((line = stderrReader.readLine()) != null) {
					System.out.println(line);
				}

				process.waitFor();
				process.destroy();
		
		
	}
	
	public static void getDiff(String repositoryLocalPath,
			String gitCommand, String outputPath, String tmpFolder, String commitId) throws IOException, InterruptedException {
			//[START] Create a temporary file where we write the commands
				//that we want to execute from command line
				File commandsToExecute = new File(tmpFolder + "/commands.sh");
				commandsToExecute.createNewFile();
				//[END]
				
				//[START] We print the command that we want to execute
				PrintWriter pw = new PrintWriter(commandsToExecute);
				pw.println("cd " + repositoryLocalPath);
				pw.println(gitCommand + " diff " + commitId + "^ " + commitId + " > " + outputPath);
				pw.close();
				//[END]
				
				
				//[START] Make the file executable (i.e., adds execution
				//permissions to the created file)
				Runtime rt = Runtime.getRuntime();
				String cmd = "chmod u+x " + tmpFolder + "/commands.sh";
				Process process = rt.exec(cmd);

				String line = null;

				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				while ((line = stdoutReader.readLine()) != null) {
					//System.out.println(line);
				}

				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				while ((line = stderrReader.readLine()) != null) {
					System.out.println(line);
				}

				process.waitFor();
				//[END]

				//[START] Execute the command that we stored
				//in commands.sh (i.e., in this case, shows difference between 2 last commits)
				cmd = tmpFolder + "./commands.sh";
				process = rt.exec(cmd);

				line = null;

				stdoutReader = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				while ((line = stdoutReader.readLine()) != null) {
					System.out.println(line);
				}

				stderrReader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				while ((line = stderrReader.readLine()) != null) {
					System.out.println(line);
				}

				process.waitFor();
				process.destroy();
		
		
	}
	
	public static Vector<DiffBean> readDiff(String logFilePath, String commitId, String author, Date commitDate) throws IOException, ParseException{
		Vector<DiffBean> resultDiff = new Vector<DiffBean>();
		Vector<String> modifiedLines = new Vector<String>();
		DiffBean singleDiffBean = new DiffBean();
		
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
				singleDiffBean.setCommitId(commitId);
				singleDiffBean.setDate(commitDate);
				
				singleDiffBean.setAuthor(author);
				if(singleDiffBean.getFile() != null){
					if(addedLines.size() > 0){
						//System.out.println(addedLines.size() + " is a size of added lines");
						singleDiffBean.setAddedLines(addedLines);
					}
					if(removedLines.size() > 0){
						singleDiffBean.setRemovedLines(removedLines);
					}
					resultDiff.addElement(singleDiffBean);
				}
				singleDiffBean = new DiffBean();
			}
			
			Matcher matcherLineAdded = patternLineAdded.matcher(strLine);
			Matcher matcherAddedLines = patternAddedlines.matcher(strLine);
			Matcher matcherRemLines = patternRemlines.matcher(strLine);
			Matcher matcherName = patternName.matcher(strLine);
			Matcher matcherLineRemoved = patternLineRemoved.matcher(strLine);
			

			//match file name
			if(matcherName.find()) {
				String fileName = matcherName.group(1);
				singleDiffBean.setFile(fileName);
//				System.out.println(fileName);
			}
			
			//match added lines number
			if(matcherAddedLines.find()) {
				String added = matcherAddedLines.group(1);
//				System.out.println(added);
				singleDiffBean.setAddedNumber(added);
			}
			
			//match removed lines number
			if(matcherRemLines.find()){
				String removed = matcherRemLines.group(2);
//				System.out.println(removed);
				singleDiffBean.setRemovedNumber(removed);
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
		resultDiff.addElement(singleDiffBean);
		//System.out.println(resultDiff.size() + " a size of resultDiff");
		//Close the input stream
		br.close();

		return resultDiff;
	}
	
	
	
	public static void checkout(String repositoryLocalPath,
			String gitCommand, String outputPath, String tmpFolder, String commitId) throws IOException, InterruptedException {
			//[START] Create a temporary file where we write the commands
				//that we want to execute from command line
				File commandsToExecute = new File(tmpFolder + "/commands.sh");
				commandsToExecute.createNewFile();
				//[END]
				
				//[START] We print the command that we want to execute
				PrintWriter pw = new PrintWriter(commandsToExecute);
				pw.println("cd " + repositoryLocalPath);
				pw.println(gitCommand + " checkout " + commitId);
				pw.close();
				//[END]
				
				
				//[START] Make the file executable (i.e., adds execution
				//permissions to the created file)
				Runtime rt = Runtime.getRuntime();
				String cmd = "chmod u+x " + tmpFolder + "/commands.sh";
				Process process = rt.exec(cmd);

				String line = null;

				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				while ((line = stdoutReader.readLine()) != null) {
					System.out.println(line);
				}

				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				while ((line = stderrReader.readLine()) != null) {
					System.out.println(line);
				}

				process.waitFor();
				//[END]

				//[START] Execute the command that we stored
				//in commands.sh (i.e., in this case, checkout to needed commit version)
				cmd = tmpFolder + "./commands.sh";
				process = rt.exec(cmd);

				line = null;

				stdoutReader = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				while ((line = stdoutReader.readLine()) != null) {
					System.out.println(line);
				}

				stderrReader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				while ((line = stderrReader.readLine()) != null) {
					System.out.println(line);
				}

				process.waitFor();
				process.destroy();
	}
}
