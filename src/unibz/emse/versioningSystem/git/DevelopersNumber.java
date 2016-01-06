package unibz.emse.versioningSystem.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;

import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.bean.DiffBean;
import unibz.emse.versioningSystem.bean.FinalBean;

public class DevelopersNumber {
	
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
	
	
	public static int[] noOfDevs(Vector<CommitBean> commits, String commitAfter, String commitBefore, String fileName, String author, String logFilePath) throws IOException, InterruptedException{
		int[] anArray;
		anArray = new int[2];
		HashSet<String> devsArray = new HashSet<String>();
		
		Integer numberCommits;
		String repositoryPath = "/home/vytautas/Desktop/commons-io";
		String gitCommand = "/usr/bin/git";
		String outputPath = logFilePath;
		String tmpFolder = "/home/vytautas/Desktop/";
		
		//get list of revision between 2 commits
		getRevList(repositoryPath, gitCommand, outputPath, tmpFolder, commitAfter, commitBefore, fileName );
		
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
		//System.out.println(resultDiff.size() + " a size of resultDiff");
		//Close the input stream
		br.close();
		
		anArray[0] = numberCommits;
		anArray[1] = devsArray.size();
		
		return anArray;
	}
}
