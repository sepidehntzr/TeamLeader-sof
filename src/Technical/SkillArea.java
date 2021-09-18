package Technical;


import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SkillArea {
    private TreeMap<String, ArrayList<String>> SA_Tags = new TreeMap<>();
    private LinkedHashMap<String, String> Tag_SA = new LinkedHashMap<>();
    private ArrayList<String> skills = new ArrayList<String>();
    
    public SkillArea(String SAPath) throws IOException{
        readSAsAndTheirTagsFromFile(SAPath);
        getTagsAndTheirSAs();
    }
    
    private void readSAsAndTheirTagsFromFile(String SAPath) throws IOException{
        LineNumberReader reader = new LineNumberReader(new FileReader(SAPath));
        String line = "";
        while((line = reader.readLine())!= null){
            if(!line.equals("SkillArea,Tag")){
                String SA = line.split(",")[0];
                String tag = line.split(",")[1];
                addSAsAndTheirTagsToList(SA, tag);
            }
        }
        reader.close();
    }

    private void addSAsAndTheirTagsToList(String SA, String tag) {
    	
    	if(!skills.contains(SA))
    	    skills.add(SA);
        if(SA_Tags.containsKey(SA)){
            ArrayList<String> tagsList = SA_Tags.get(SA);
            tagsList.add(tag);
            SA_Tags.replace(SA, tagsList);
        }else{
            ArrayList<String> tagsList = new ArrayList<>();
            tagsList.add(tag);
            SA_Tags.put(SA, tagsList);
        }
    }

    private void getTagsAndTheirSAs() {
        for(Map.Entry<String,ArrayList<String>> SAItem : SA_Tags.entrySet()){
            String SA = SAItem.getKey();
            ArrayList<String> tags = SAItem.getValue();
            for(String tag : tags){
                Tag_SA.put(tag, SA);
            }
        }
    }
    
    public TreeMap<String, ArrayList<String>> getSATagsLists(){
        return SA_Tags;
    }
        
    public LinkedHashMap<String,String> getTagSALists(){
        return Tag_SA;
    }

	public ArrayList<String> getSkills() {
		return skills;
	}

	
    
    
}
