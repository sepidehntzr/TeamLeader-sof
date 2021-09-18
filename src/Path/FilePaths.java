package Path;

public class FilePaths {
    private static final String basePath = "E:\\1- Data, Datasets, project outputs\\proj-output\\Dataset-Thesis\\Results\\ProjectManager\\";
    
    public static String getFilePaths(String dataSetName, String fileName){
        switch (fileName) {
            case "Posts":
                return "E:\\DataSets\\SOF\\Post\\Posts.xml";
            case "IndexQuestions":
                return IndexPath(dataSetName)+"Questions";
            case "IndexAnswers":
                return IndexPath(dataSetName)+"Answers";
            case "Clustering":
                return GoldenPath(dataSetName)+dataSetName+"Cluster.csv";
            case "SkillShapesXML":
                return GoldenPath(dataSetName)+dataSetName+"SkillShapes.xml";
            case "QuestionDataSet":
                return GoldenPath(dataSetName)+dataSetName+"Questions.xml";
            case "AnswerDataSet":
                return GoldenPath(dataSetName)+dataSetName+"Answers.xml";
            case "userPerformance":
                return GoldenPath(dataSetName)+dataSetName+"UserPerformance.xml";
            case "skillAreasProperty":
                return GoldenPath(dataSetName)+dataSetName+"SkillArea.csv";
            case "AdvancedLevel":
                return GoldenPath(dataSetName)+dataSetName+"AdvancedLevel.csv";
            case "IntermediateLevel":
                return GoldenPath(dataSetName)+dataSetName+"IntermediateLevel.csv";
            case "BeginnerLevel":
                return GoldenPath(dataSetName)+dataSetName+"BeginnerLevel.csv";
            case "Shapes":
                return GoldenPath(dataSetName)+dataSetName+"Shapes.csv";
            case "topFrequntTags":
                return GoldenPath(dataSetName)+"topFrequntTags.txt";
            case "Similarity":
                return GoldenPath(dataSetName)+dataSetName+"Similarity.csv";
            case "SACategores":
                return GoldenPath(dataSetName)+dataSetName+"SACategores.csv";
            case "SAHightCategores":
                return GoldenPath(dataSetName)+dataSetName+"SAHighCategores.csv";
            case "SALevelName":
                return GoldenPath(dataSetName)+dataSetName+"SACategores_LevelName.csv";
            case "Technical_goldset":
                return GoldenPath(dataSetName)+"my" + dataSetName + "SkillShapes.xml";
            case "Managerial_goldset":
                return  basePath+"Golden\\PM\\myPMSkillShapes.xml";
            case "Approaches":
                return ResultPath(dataSetName);
            case "teamprojectlist_query_five":
            	return ResultPath(dataSetName)+"query\\recent_new\\"+dataSetName+"teamprojectlist_twoHsample.csv";
            case "teamprojectlist_five":
            	return ResultPath(dataSetName)+"query\\recent_new\\test_sample.csv";
               // return ResultPath(dataSetName)+dataSetName+"teamprojectlist_five_fiftysample.csv";
            	
            case "common_users":
            	return ResultPath(dataSetName)+"query\\recent_new\\"+dataSetName+"CommonUsers.csv";
            	
            	
            case "PMResult":
                return ResultPath(dataSetName)+dataSetName+"trainResult.csv";
                
            case "wholeSamples":
                return ResultPath(dataSetName)+"query\\recent_new\\whole_sample.csv";
            case "trainSamples":
                return ResultPath(dataSetName)+"query\\recent_new\\train_sample.csv";
            case "testSamples":
                return ResultPath(dataSetName)+"query\\recent_new\\test_sample.csv";
                
            case "EK_trainset":
                return ResultPath(dataSetName)+"trainingset\\EK\\EK_trainset.csv";
            case "EK_testset":
                return ResultPath(dataSetName)+"trainingset\\EK\\EK_testset.csv";
            case "EK_trainingset_whole":
                return ResultPath(dataSetName)+"trainingset\\EK\\EK_trainingset_whole.csv";
            case "EK_trainset_Letor":
                return ResultPath(dataSetName)+"trainingset\\EK\\EK_trainset_Letor.csv";
            case "EK_testset_Letor":
                return ResultPath(dataSetName)+"trainingset\\EK\\EK_testset_Letor.csv";
            case "EK_testset_Info":
                return ResultPath(dataSetName)+"trainingset\\EK\\EK_testset_Info.csv";
            case "EK_prediction_results":
                return ResultPath(dataSetName)+"trainingset\\EK\\PResult\\";
                
            case "Comm_trainset":
                return ResultPath(dataSetName)+"trainingset\\Comm\\Comm_trainset.csv";    
            case "Comm_testset":
                return ResultPath(dataSetName)+"trainingset\\Comm\\Comm_testset.csv";
		/*
		 * case "teamprojectlist_five": return
		 * ResultPath(dataSetName)+dataSetName+"teamprojectlist_five.csv";
		 */
                ////////////////////////models
            case "IKA_PolyKernel":
                return ResultPath(dataSetName)+"IKA_PolyKernel\\";
            case "Balog":
                return ResultPath(dataSetName)+"Balog\\SA_Users\\";
            case "EKA":
                return ResultPath(dataSetName)+"EKA\\";
            case "EKA_Tech":
                return ResultPath(dataSetName)+"EKA\\";
            case "EKA_Mng":
                return ResultPath(dataSetName)+"EKA\\";
            case "IKA_PolyKernel_Tech":
                return ResultPath(dataSetName)+"IKA_PolyKernel\\";
            case "IKA_PolyKernel_Mng":
                return ResultPath(dataSetName)+"IKA_PolyKernel\\";
            case "Balog_Tech":
                return ResultPath(dataSetName)+"Balog\\SA_Users\\";
            case "Balog_Mng":
                return ResultPath(dataSetName)+"Balog\\SA_Users\\";
                
            case "TM_Combine":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
                
            case "LCOM_MST":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_Diameter":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_SumShortestPath":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_BC":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_DC":
                return ResultPath(dataSetName)+"Communication\\";    
            case "LCOM_DC_MST":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_DC_SumShortestPath":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_MaximunDistance":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_Centrality":
                return ResultPath(dataSetName)+"Communication\\";
            case "LCOM_Voting":
                return ResultPath(dataSetName)+"Communication\\";
                
            case "TM_Combine_VSM":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
            case "TM_Combine_LM":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
            case "TM_Combine_ML_lr":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
            case "TM_Combine_ML_svm_linear":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
            case "TM_Combine_ML_svm_rbf":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
            case "TM_Combine_ML_ranknet":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
            case "TM_Combine_ML_svm_ranking":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
            case "TM_Combine_ML_lambdamart":
                return ResultPath(dataSetName)+"ExKnowdlage\\";
                
                
            default:
                return "";
        }
    }
    
    private static String GoldenPath(String dataSetName){
        return basePath+"Golden\\"+dataSetName+"\\";
    }
    
    private static String IndexPath(String dataSetName){
        return basePath+"Index\\"+dataSetName+"\\";
    }
    
    private static String ResultPath(String dataSetName){
        return basePath+"Result\\"+dataSetName+"\\";
    }
    
}
