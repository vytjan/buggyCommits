package csvWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import unibz.emse.versioningSystem.bean.FinalBean;

public class ToCSV
{  
   public static void generateCsvFile(String sFileName, Vector<FinalBean> result)
   {
	try
	{
		//initiate fileWriter
	    FileWriter writer = new FileWriter(sFileName);
		 
	    //write title for every column - maybe remove later
//	    writer.append("DEV_NAME");
//	    writer.append(',');
////	    writer.append("START");
////	    writer.append(",");
//	    writer.append("END");
//	    writer.append(",");
////	    writer.append("FILE_N");
////	    writer.append(",");
////	    writer.append("NUM_DEVS");
//	    writer.append("BUGGY");
//	    writer.append('\n');
	    
	    for(FinalBean singleResult:result){
	    	writer.append(singleResult.getAuthor());
	    	writer.append(",");
	    	writer.append(singleResult.getStartDate().toString());
	    	writer.append(",");
	    	writer.append(singleResult.getEndDate().toString());
	    	writer.append(",");
	    	writer.append(singleResult.getFile());
	    	writer.append(",");
	    	writer.append(singleResult.getDays().toString());
	    	writer.append(",");
	    	writer.append(singleResult.getCommits().toString());
	    	writer.append(",");
	    	writer.append(singleResult.getNoOfDevs().toString());
	    	writer.append(",");
	    	writer.append(singleResult.getCommitBefore());
	    	writer.append(",");
	    	writer.append(singleResult.getCommitId());
	    	writer.append(",");
	    	writer.append(singleResult.getBuggy().toString());
	    	writer.append('\n');
	    }	
			
	    //generate whatever data you want
			
	    writer.flush();
	    writer.close();
	}
	catch(IOException e)
	{
	     e.printStackTrace();
	} 
    }
}
