package unibz.emse.versioningSystem.api;

import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.git.GitUtilities;

public class Git {

	public static Vector<CommitBean> readCommitChangeHistory(String repositoryAddress, String destinationFolder, 
			String gitCommand, String tmpFolder) throws IOException, InterruptedException, ParseException{
		
		GitUtilities.cloneGitRepositoryUnix(repositoryAddress, destinationFolder, gitCommand, tmpFolder);
		GitUtilities.getLogFromGitRepository(destinationFolder, gitCommand, tmpFolder + "/log.txt", tmpFolder);
		Vector<CommitBean> result = GitUtilities.readCommits(tmpFolder + "/log.txt");
		
		return result;
	}
	
}
