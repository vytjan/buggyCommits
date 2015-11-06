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
	
	public static Vector<DiffBean> readDiffs(String logFilePath) throws IOException, ParseException{
		Vector<DiffBean> resultDiff = new Vector<DiffBean>();
		Vector<String> modifiedLines = new Vector<String>();
		Vector<DiffBean> singleDiffBean = new Vector<DiffBean>();
		Vector<String> modLinesVector = new Vector<String>();
		
		String regexLineAdded = Pattern.quote("+        ") + Pattern.compile("(.*?)");
		Pattern patternLineAdded = Pattern.compile(regexLineAdded);
		
		FileInputStream fstream = new FileInputStream(logFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		
		//Read File Line By Line
		int count = 0;
		while ((strLine = br.readLine()) != null)   {
			Matcher matcherLineAdded = patternLineAdded.matcher(strLine);
			if(matcherLineAdded.find()) {
				modLinesVector.addElement(matcherLineAdded.group(1));
			}
		}
		resultDiff.addAll(singleDiffBean);
		//Close the input stream
		br.close();
	//	System.out.println(count);

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
	
	
	/**
	 * 
	 * 
	 * @param bugFixCommits
	 * @return
	 */
	
	//parse git blame command
//	public static Vector<BlameBean> readBlame(String logFilePath) {
//		Vector<BlameBean> blame = new Vector<BlameBean>();
//		
//		
//		return blame;
//		
//	}

}
