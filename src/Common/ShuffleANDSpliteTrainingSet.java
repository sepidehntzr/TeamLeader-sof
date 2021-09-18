package Common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShuffleANDSpliteTrainingSet //  70/30
{

	static // parameterss
	String dataSetName = "Java";
	static String fold = "1"; 
	static Boolean _switch = false;
	
	
	public static void main(String[] args) throws IOException {
		
		
		
		FileWriter writer1 = new FileWriter(
				Path.FilePaths.getFilePaths(dataSetName, "EK_trainingset_whole_new_shuffled_forcrossval"));

		FileWriter writer2 = new FileWriter(
				Path.FilePaths.getFilePaths(dataSetName, "EK_trainingset_whole_new_shuffled_forcrossval_reversed"));

		
		List<String> lines = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get(Path.FilePaths.getFilePaths(dataSetName, "EK_trainingset_whole_new")))) {
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) { e.printStackTrace(); }
		
		Collections.shuffle(lines);
		
		if(fold.equals("1") && _switch == false)
		{
			
			createTrainTest(lines, writer1, fold);
			_switch = true; fold = "2";
		}
		
		if(fold.equals("2") && _switch == true)
		{
			createTrainTest(lines, writer2, fold);
		}
		
		 
		writer1.close();
		writer2.close();
       System.out.print("Done");
	}


	private static void createTrainTest(List<String> lines, FileWriter writer, String _fold) throws IOException 
	{
		
		
		
		if(_fold.equals("1"))
		{
			for(int i = 0; i < lines.size(); i++)
			{
				String Line = lines.get(i);
				writer.write(Line);
				writer.write("\n");
				writer.flush();
			}
		}
		else if(_fold.equals("2"))
		{
			for(int i = lines.size()-1; i >= 0; i--)
			{
				String Line = lines.get(i);
				writer.write(Line);
				writer.write("\n");
				writer.flush();
			}
		}
		
		
		
		
	}

}
