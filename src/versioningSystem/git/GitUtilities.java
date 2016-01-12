package versioningSystem.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


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


	public static void getRevList(String repositoryLocalPath,
			String gitCommand, String outputPath, String tmpFolder, String commit1, String commit2, String fileName) throws IOException, InterruptedException {
			//[START] Create a temporary file where we write the commands
				//that we want to execute from command line
				File commandsToExecute = new File(tmpFolder + "/commands.sh");
				commandsToExecute.createNewFile();
				//[END]
				
				//[START] We print the command that we want to execute
				PrintWriter pw = new PrintWriter(commandsToExecute);
				pw.println("cd " + repositoryLocalPath);
				pw.println(gitCommand + " rev-list " + commit1 + "..." + commit2 +  " > " + outputPath);
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
		
}
