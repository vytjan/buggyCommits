package unibz.emse.versioningSystem.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegularExpression {

	public static void main(String[] args) {
		
		String line = "<commit-id>9d4cd5e</commit-id><author-email>sebb@apache.org</author-email><date>2015-02-24 17:36:51 +0000</date><message>CGI must be svn:executable *</message>";
		
		String regexCommitId = Pattern.quote("<commit-id>") + Pattern.compile("(.*?)") + Pattern.quote("</commit-id>");
		Pattern patternCommitId = Pattern.compile(regexCommitId);
		Matcher matcherCommitId = patternCommitId.matcher(line);
		
		if(matcherCommitId.find())
			System.out.println(matcherCommitId.group(1));
		
		
		String regexAuthoEmail = Pattern.quote("<author-email>") + Pattern.compile("(.*?)") + Pattern.quote("</author-email>");
		Pattern patternAuthorEmail = Pattern.compile(regexAuthoEmail);
		Matcher matcherAuthorEmail = patternAuthorEmail.matcher(line);
		
		if(matcherAuthorEmail.find())
			System.out.println(matcherAuthorEmail.group(1));
		
	}

}
