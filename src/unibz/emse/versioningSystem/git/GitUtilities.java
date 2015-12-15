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

//import unibz.emse.versioningSystem.bean.BlameBean;
import unibz.emse.versioningSystem.bean.CommitBean;


public class GitUtilities {

	/**
	 * This method clones a git repository to your local machine.
	 * 
	 * @param repositoryAddress
	 * @param destinationFolder
	 * @param gitCommand
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void cloneGitRepositoryUnix(String repositoryAddress, String destinationFolder, 
			String gitCommand, String tmpFolder) throws IOException, InterruptedException {
		
		//[START] Create a temporary file where we write the commands
		//that we want to execute from command line
		File commandsToExecute = new File(tmpFolder + "/commands.sh");
		commandsToExecute.createNewFile();
		//[END]
		
		//[START] We print the command that we want to execute
		PrintWriter pw = new PrintWriter(commandsToExecute);
		pw.println("cd " + destinationFolder);
		pw.println(gitCommand + " clone " + repositoryAddress);
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
		//in commands.sh (i.e., in this case, clones
		//the repository)
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
		//[END]
		
	}
	
	
	public static void cloneGitRepositoryWin(String repositoryAddress, String destinationFolder, 
			String gitCommand, String tmpFolder, String bashExePath) throws IOException, InterruptedException {
		
		//[START] Create a temporary file where we write the commands
		//that we want to execute from command line
		File commandsToExecute = new File(tmpFolder + "/commands.sh");
		commandsToExecute.createNewFile();
		//[END]
		
		//[START] We print the command that we want to execute
		PrintWriter pw = new PrintWriter(commandsToExecute);
		pw.println("#!/bin/sh" + "\n" + 
				"cd " + destinationFolder + "\n" + 
				gitCommand + " clone " + repositoryAddress);
		pw.close();
		//[END]
		
		
		//[START] Execute the command that we stored
		//in commands.sh (i.e., in this case, clones
		//the repository)
		Runtime rt = Runtime.getRuntime();
		
		String cmd = bashExePath + " " + tmpFolder + "/commands.sh";
		Process process = rt.exec(cmd);
		String line = null;
		
		BufferedReader stdoutReader = 
			      new BufferedReader (new InputStreamReader (process.getInputStream()));
		while ((line = stdoutReader.readLine()) != null) {
		    System.out.println(line);
		}
		
		BufferedReader stderrReader = new BufferedReader(
		         new InputStreamReader(process.getErrorStream()));
		while ((line = stderrReader.readLine()) != null) {
		    System.out.println(line);
		}
		process.waitFor();
		process.destroy();
		//[END]
		
	}
	
	
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
	public static void getLogFromGitRepository(String repositoryLocalPath,
			String gitCommand, String outputPath, String tmpFolder) throws IOException, InterruptedException {
			//[START] Create a temporary file where we write the commands
				//that we want to execute from command line
				File commandsToExecute = new File(tmpFolder + "/commands.sh");
				commandsToExecute.createNewFile();
				//[END]
				
				//[START] We print the command that we want to execute
				PrintWriter pw = new PrintWriter(commandsToExecute);
				pw.println("cd " + repositoryLocalPath);
				pw.println(gitCommand + " log " + "--first-parent --name-status --date=iso --stat HEAD --pretty=format:\"<commit-id>%h</commit-id><author-email>%ae</author-email><date>%ad</date><message>%s</message>\"" +
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
				//in commands.sh (i.e., in this case, clones
				//the repository)
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
		Vector modifiedFiles = new Vector();
		Vector addedFiles = new Vector();
		Vector deletedFiles = new Vector();
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
		int count = 0;
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
							//System.out.println(modifiedFiles.elementAt(0));
						} if(addedFiles.size() > 0) {
							singleCommit.setAddedFiles(addedFiles);
						} if(deletedFiles.size() > 0) {
							singleCommit.setDeletedFiles(deletedFiles);
						}
						result.addElement(singleCommit);
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
						DateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss Z", Locale.ENGLISH);
						Date date = format.parse(matcherCommitDate.group(1));
						singleCommit.setDate(date);
					} if(matcherCommitMessage.find()) {
						singleCommit.setCommitMessage(matcherCommitMessage.group(1));
					}
				}

				//Check if it is a modified file line
				// add M,A,D lines to different vectors and after that just to link them to the ones from commitBean
				if(strLine.startsWith("M	")) {
//					System.out.println(strLine.substring(2));
					modifiedFiles.addElement(strLine.substring(2));
				}
				if(strLine.startsWith("A	")) {
					addedFiles.addElement(strLine.substring(2));
				}
				if(strLine.startsWith("D	")) {
					deletedFiles.addElement(strLine.substring(2));
				}
			}
			if(singleCommit.getCommitId() != null) {
				//System.out.println(modifiedFiles.size() + " is a size of M files vector");
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
		}
		
		//Close the input stream
		br.close();
	//	System.out.println(count);

		return result;
	}
		
}
