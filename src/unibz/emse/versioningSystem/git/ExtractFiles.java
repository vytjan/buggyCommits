package unibz.emse.versioningSystem.git;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibz.emse.versioningSystem.bean.CommitBean;
import unibz.emse.versioningSystem.bean.DevBean;
import unibz.emse.versioningSystem.bean.FinalBean;

public class ExtractFiles {
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
	//										System.out.println(commitAfter.getAuthor() + " " + commitBefore.getDate());
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
											
//											//get number of developers who modified the same file between these 2 commits
											int[] devsNumber = DevelopersNumber.noOfDevs(commits, singleCommit2.getCommitId(), singleCommit1.getCommitId(),fileName2, singleCommit2.getAuthor(), revisionPath);
											singleFinal.setNoOfDevs(devsNumber[1]);
											singleFinal.setCommits(devsNumber[0]);
											
											partResult.addElement(singleFinal);
											singleFinal = new FinalBean();
//											System.out.println(fileName + " " + commitBefore.getCommitId() + " " + commitAfter.getCommitId() + " " + comBefore);
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
	
}
