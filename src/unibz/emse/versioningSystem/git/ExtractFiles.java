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


	
	public static Vector<FinalBean> getDevs(Vector<CommitBean> commits) throws IOException, InterruptedException{
		
		HashSet<String> developers = new HashSet<String>();
		Vector<DevBean> devs = new Vector<DevBean>();
		Vector<FinalBean> partResult = new Vector<FinalBean>();
		//single final bean
		FinalBean singleFinal = new FinalBean();
		
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
		
		for(DevBean dev:devs){
//			System.out.println(dev.getCommits().size());
			
			//remove commits not containing 
			for(int i = 0; i<dev.getCommits().size(); i++){
				
				CommitBean commitAfter = dev.getCommits().get(i);
				
				for(int j = 0; j < dev.getCommits().size(); j++){
					
					CommitBean commitBefore = dev.getCommits().get(j);
					
					if(commitAfter.getModifiedFiles() != null && commitAfter.getModifiedFiles().size() > 0){
						for(String comBefore:commitAfter.getModifiedFiles()){
							if(commitBefore.getModifiedFiles() != null && commitBefore.getModifiedFiles().size() > 0 && !commitAfter.getCommitId().equals(commitBefore.getCommitId())){
								for(String fileName:commitBefore.getModifiedFiles()){
									Matcher matcherTestFile = patternTestFile.matcher(comBefore);
									if(comBefore.equals(fileName) && comBefore.endsWith(".java") && !matcherTestFile.find()){
										long difference = getDateDiff(commitAfter.getDate(), commitBefore.getDate(),TimeUnit.MINUTES);
										if(difference >= 0){
											//setting all the necessarry vars for the final data output
											singleFinal.setAuthor(commitBefore.getAuthor());
											singleFinal.setStartDate(commitAfter.getDate());
	//										System.out.println(commitAfter.getAuthor() + " " + commitBefore.getDate());
											singleFinal.setEndDate(commitBefore.getDate());
											singleFinal.setFile(fileName);
											singleFinal.setCommitId(commitAfter.getCommitId());
											singleFinal.setCommitBefore(commitBefore.getCommitId());
											singleFinal.setDays(TimeUnit.DAYS.convert(difference, TimeUnit.MINUTES));
											if(commitBefore.getBuggy()){
												singleFinal.setBuggy(true);
											} else {
												singleFinal.setBuggy(false);
											}
											
											//get number of developers who modified the same file between these 2 commits
											String filePath = "/home/vytautas/Desktop/commons-io/revlist.txt";
											int[] devsNumber = DevelopersNumber.noOfDevs(commits, commitAfter.getCommitId(), commitBefore.getCommitId(),fileName, commitBefore.getAuthor(), filePath);
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
			System.out.println("done one loop of commits");
		}
		
		return partResult;
	}
	
	public static Vector<FinalBean> findFiles(Vector<DevBean> origin) throws IOException, InterruptedException, ParseException {
		Vector<DevBean> modified = new Vector<DevBean>();
		Vector<FinalBean> allFiles = new Vector<FinalBean>();
		
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
								for(String fileName:commitAfter.getModifiedFiles()){
									Matcher matcherTestFile = patternTestFile.matcher(comBefore);
									if(comBefore.equals(fileName) && !comBefore.endsWith(".rdf") && !comBefore.endsWith(".txt") && !comBefore.endsWith(".xml") && !matcherTestFile.find()){
										//System.out.println(comBefore + " " + comAfter);
										//git diff
										
										GitSzz.getDiff("/home/vytautas/Desktop/commons-io", "/usr/bin/git", "/home/vytautas/Desktop/commons-io/diff.txt", "/home/vytautas/Desktop/", commitBefore.getCommitId(), commitAfter.getCommitId(), fileName);
										Vector<FinalBean> finalVector = GitRead.readDiff("/home/vytautas/Desktop/commons-io/diff.txt", commitAfter.getCommitId(), commitBefore.getCommitId(), commitAfter.getAuthor(), commitAfter.getDate(), commitBefore.getDate(), fileName);
//										for(DiffBean singleDiff:diffVector){
////											System.out.println(singleDiff.getCommitId() + " after commit date " + singleDiff.getDate() + " " + singleDiff.getAuthor() + " before commit date " + commitBefore.getDate());
////											System.out.println(singleDiff.getAddedNumber());
//										}
										num++;
										System.out.println(num);
										System.out.println();
										allFiles.addAll(finalVector);
									}
									
								}	
							}
						}	
					}
				}	
			}
		}
		
		return allFiles;
	}
}
