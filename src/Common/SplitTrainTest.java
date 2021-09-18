package Common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SplitTrainTest //  70/30
{

	static // parameterss
	String dataSetName = "Java";
	
	
	public static void main(String[] args) throws IOException {
		
		
		FileInputStream fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "EK_trainingset_whole_new"));
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
		
		FileWriter writer_train = new FileWriter(
				Path.FilePaths.getFilePaths(dataSetName, "EK_trainset_new"));

		FileWriter writer_test = new FileWriter(
				Path.FilePaths.getFilePaths(dataSetName, "EK_testset_new"));

		String Line ; int i=0 , j=0;
		while ((Line = br.readLine()) != null)
		{
			if(i < 7)
			{
				writer_train.write(Line);
				writer_train.write("\n");
				writer_train.flush();
				i++;
			}
			else if(i>=7 && j < 3)
			{
				writer_test.write(Line);
				writer_test.write("\n");
				writer_test.flush();
				j++;
			}
			if(i>=7 && j>=3)
			{
				i=0;
				j=0;
			}
			
		}
		
		writer_train.close();
		writer_test.close();
       System.out.print("Done");
	}

}
