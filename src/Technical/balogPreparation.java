package Technical;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

class balogPreparation {
    
	//parameter
    private String lambda = "0.5";
    String dataSetName = "Java";
    //
    SkillArea SAObject;
    
    public static void main(String[] args) throws IOException, ParseException 
	{
    	balogPreparation bp = new balogPreparation();
    	String skillAreasPath = bp.getPath(bp.dataSetName, "Clustering");
    	String balogFolderPath = bp.getPath(bp.dataSetName, "Balog");
    	String answerIndexPath = bp.getPath(bp.dataSetName, "IndexAnswers");
    	bp.setFields(bp.lambda, skillAreasPath);
        bp.executeProbilityExpertGivenSAForAllSAs(bp.dataSetName, balogFolderPath, new File(answerIndexPath).toPath());
	}
    /*public balogPreparation(String dataSetName, String lambda, String answerIndexPathString, String skillAreasPath, String balogFolderPath) throws IOException, ParseException {
        setFields(lambda, skillAreasPath);
        executeProbilityExpertGivenSAForAllSAs(dataSetName, balogFolderPath, new File(answerIndexPathString).toPath());
    }*/

    public String getPath(String dataSetName, String fileName) {
        return Path.FilePaths.getFilePaths(dataSetName, fileName);
    }
    private void setFields(String lambda, String skillAreasPath) throws IOException {
        this.lambda = lambda;
        SAObject = new SkillArea(skillAreasPath);
    }

    private void executeProbilityExpertGivenSAForAllSAs(String dataSetName, String balogFolderPath, java.nio.file.Path AIndexPath) throws IOException, ParseException {
        for(Map.Entry<String, ArrayList<String>> item : SAObject.getSATagsLists().entrySet()){
            String SA = item.getKey();
            ArrayList<String> tags = item.getValue();
            Balog B = new Balog(AIndexPath, tags, dataSetName, findLambdaType(), findLambdaValue());
            LinkedHashMap<Integer,Double> UserId_Probility = B.getSortedUId_probilitySAGivenExpert();
            Map<Integer, Integer> userid_count = B.getUserid_count();
            printProbabilities(balogFolderPath, SA, UserId_Probility, userid_count);
            System.out.println("******* " + SA + " *******");
            System.gc();
        }
    }
    
    private String findLambdaType(){
        return (!lambda.equals(""))?"constant":"nonConstant";
    }
    
    private double findLambdaValue(){
        return (findLambdaType().equals("constant"))? (Double.parseDouble(lambda)):-1;
    }  

    private void printProbabilities(String balogFolderPath, String SA, LinkedHashMap<Integer, Double> UserId_Probility, Map<Integer, Integer> userid_count) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(balogFolderPath+SA+"Users.csv");
        writer.println("UserId, Probability");
        for(Map.Entry<Integer,Double> UserItem : UserId_Probility.entrySet()){
           int userId = UserItem.getKey();
           double probability = UserItem.getValue();
           if(userid_count.get(userId) <=1 )
        	   probability = 0;
           writer.println(userId+","+probability);
        }
        writer.close();
    }
}
