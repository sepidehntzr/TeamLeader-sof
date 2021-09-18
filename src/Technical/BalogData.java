package Technical;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BalogData {
    public static LinkedHashMap<String,LinkedHashMap<Integer,Double>> readBalogProbabilitiesFromFiles(String skillAreasPath, String balogFolderPath) throws FileNotFoundException, IOException{
        SkillArea SAObject = new SkillArea(skillAreasPath);
        LinkedHashMap<String,LinkedHashMap<Integer,Double>> SA__userId_probability = new LinkedHashMap<>();
        for(String SA : SAObject.getSATagsLists().keySet()){
            LinkedHashMap<Integer,Double> userId_probability = new LinkedHashMap<>();
            LineNumberReader reader = new LineNumberReader(new FileReader(balogFolderPath+SA+"Users.csv"));
            String line = reader.readLine();
            while((line = reader.readLine()) != null){
                int userId = Integer.parseInt(line.split(",")[0]);
                double probability = Double.parseDouble(line.split(",")[1]);
                userId_probability.put(userId, probability);
            }
            reader.close();
            SA__userId_probability.put(SA, userId_probability);
        }
        return Sort.getSortedUserIdAndTheValuesListInAllSAs(SA__userId_probability, "DESC", (double) 1.2);
    }
    
    public static LinkedHashMap<String,Double> getMinProbabilityForEachSA(LinkedHashMap<String,LinkedHashMap<Integer,Double>> SA__userId_probability){
        LinkedHashMap<String,Double> SA_MinProbability = new LinkedHashMap<>();
        for(Map.Entry<String,LinkedHashMap<Integer,Double>> SAItem : SA__userId_probability.entrySet()){
            String SA = SAItem.getKey();
            double minProbability = Collections.min(SAItem.getValue().values());
            SA_MinProbability.put(SA, minProbability);
        }
        return SA_MinProbability;
    }
}
