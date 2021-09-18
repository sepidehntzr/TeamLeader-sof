package Common;

import java.io.BufferedWriter;
import java.util.concurrent.atomic.AtomicLong;

public class ModelObj 
{
	
	//AtomicLong NDCG_avg_fmeasure_Model_10 = new AtomicLong(0);
	double NDCG_avg_mentorship_Model_10 ;
	double NDCG_avg_fmeasure_Model_10 ;
	double NDCG_avg_com_Model_10 ;
	double NDCG_avg_opt_Model_10 ;
	double NDCG_avg_mng_Model_10 ;
	
	double NDCG_avg_mentorship_Model_20 ;
	double NDCG_avg_fmeasure_Model_20 ;
	double NDCG_avg_com_Model_20 ;
	double NDCG_avg_opt_Model_20 ;
	double NDCG_avg_mng_Model_20 ;
	
	double GAP_avg_mentorship_Model_10 ;
	double GAP_avg_fmeasure_Model_10 ;
	double GAP_avg_com_Model_10 ;
	double GAP_avg_opt_Model_10 ;
	double GAP_avg_mng_Model_10 ;
	
	double GAP_avg_mentorship_Model_20 ;
	double GAP_avg_fmeasure_Model_20 ;
	double GAP_avg_com_Model_20 ;
	double GAP_avg_opt_Model_20 ;
	double GAP_avg_mng_Model_20 ;
	
	//double NDCG_avg_fmeasure_Model_5 ;
	//double NDCG_avg_fmeasure_Model_1 ;
	//double MRR_avg_fmeasure_Model ;
	
	BufferedWriter RankedMeasure_writer_Model;
	BufferedWriter BestPM_writer_Model;
	
	BufferedWriter besedOnCommunication_writer_Model;
	BufferedWriter besedOnOptimality_writer_Model;
	BufferedWriter besedOnManagerial_writer_Model;
	BufferedWriter besedOnMentorship_writer_Model;

	public double getNDCG_avg_fmeasure_Model_10() {
		return NDCG_avg_fmeasure_Model_10;
	}

	public void setNDCG_avg_fmeasure_Model_10(double nDCG_avg_fmeasure_Model_10) {
		NDCG_avg_fmeasure_Model_10 = nDCG_avg_fmeasure_Model_10;
	}

	
	public double getNDCG_avg_com_Model_10() {
		return NDCG_avg_com_Model_10;
	}

	public void setNDCG_avg_com_Model_10(double nDCG_avg_com_Model_10) {
		NDCG_avg_com_Model_10 = nDCG_avg_com_Model_10;
	}

	public double getNDCG_avg_opt_Model_10() {
		return NDCG_avg_opt_Model_10;
	}

	public void setNDCG_avg_opt_Model_10(double nDCG_avg_opt_Model_10) {
		NDCG_avg_opt_Model_10 = nDCG_avg_opt_Model_10;
	}

	public double getNDCG_avg_mng_Model_10() {
		return NDCG_avg_mng_Model_10;
	}

	public void setNDCG_avg_mng_Model_10(double nDCG_avg_mng_Model_10) {
		NDCG_avg_mng_Model_10 = nDCG_avg_mng_Model_10;
	}

	public double getNDCG_avg_fmeasure_Model_20() {
		return NDCG_avg_fmeasure_Model_20;
	}

	public void setNDCG_avg_fmeasure_Model_20(double nDCG_avg_fmeasure_Model_20) {
		NDCG_avg_fmeasure_Model_20 = nDCG_avg_fmeasure_Model_20;
	}

	public double getNDCG_avg_com_Model_20() {
		return NDCG_avg_com_Model_20;
	}

	public void setNDCG_avg_com_Model_20(double nDCG_avg_com_Model_20) {
		NDCG_avg_com_Model_20 = nDCG_avg_com_Model_20;
	}

	public double getNDCG_avg_opt_Model_20() {
		return NDCG_avg_opt_Model_20;
	}

	public void setNDCG_avg_opt_Model_20(double nDCG_avg_opt_Model_20) {
		NDCG_avg_opt_Model_20 = nDCG_avg_opt_Model_20;
	}

	public double getNDCG_avg_mng_Model_20() {
		return NDCG_avg_mng_Model_20;
	}

	public void setNDCG_avg_mng_Model_20(double nDCG_avg_mng_Model_20) {
		NDCG_avg_mng_Model_20 = nDCG_avg_mng_Model_20;
	}

	public double getGAP_avg_fmeasure_Model_10() {
		return GAP_avg_fmeasure_Model_10;
	}

	public void setGAP_avg_fmeasure_Model_10(double gAP_avg_fmeasure_Model_10) {
		GAP_avg_fmeasure_Model_10 = gAP_avg_fmeasure_Model_10;
	}

	public double getGAP_avg_com_Model_10() {
		return GAP_avg_com_Model_10;
	}

	public void setGAP_avg_com_Model_10(double gAP_avg_com_Model_10) {
		GAP_avg_com_Model_10 = gAP_avg_com_Model_10;
	}

	public double getGAP_avg_opt_Model_10() {
		return GAP_avg_opt_Model_10;
	}

	public void setGAP_avg_opt_Model_10(double gAP_avg_opt_Model_10) {
		GAP_avg_opt_Model_10 = gAP_avg_opt_Model_10;
	}

	public double getGAP_avg_mng_Model_10() {
		return GAP_avg_mng_Model_10;
	}

	public void setGAP_avg_mng_Model_10(double gAP_avg_mng_Model_10) {
		GAP_avg_mng_Model_10 = gAP_avg_mng_Model_10;
	}

	public double getGAP_avg_fmeasure_Model_20() {
		return GAP_avg_fmeasure_Model_20;
	}

	public void setGAP_avg_fmeasure_Model_20(double gAP_avg_fmeasure_Model_20) {
		GAP_avg_fmeasure_Model_20 = gAP_avg_fmeasure_Model_20;
	}

	public double getGAP_avg_com_Model_20() {
		return GAP_avg_com_Model_20;
	}

	public void setGAP_avg_com_Model_20(double gAP_avg_com_Model_20) {
		GAP_avg_com_Model_20 = gAP_avg_com_Model_20;
	}

	public double getGAP_avg_opt_Model_20() {
		return GAP_avg_opt_Model_20;
	}

	public void setGAP_avg_opt_Model_20(double gAP_avg_opt_Model_20) {
		GAP_avg_opt_Model_20 = gAP_avg_opt_Model_20;
	}

	public double getGAP_avg_mng_Model_20() {
		return GAP_avg_mng_Model_20;
	}

	public void setGAP_avg_mng_Model_20(double gAP_avg_mng_Model_20) {
		GAP_avg_mng_Model_20 = gAP_avg_mng_Model_20;
	}

	/////////////////////////////////////////////////////////////////
	public BufferedWriter getRankedMeasure_writer_Model() {
		return RankedMeasure_writer_Model;
	}

	public void setRankedMeasure_writer_Model(BufferedWriter rankedMeasure_writer_Model) {
		RankedMeasure_writer_Model = rankedMeasure_writer_Model;
	}

	public BufferedWriter getBestPM_writer_Model() {
		return BestPM_writer_Model;
	}

	public void setBestPM_writer_Model(BufferedWriter best_writer_Model) {
		BestPM_writer_Model = best_writer_Model;
	}

	public BufferedWriter getBesedOnCommunication_writer_Model() {
		return besedOnCommunication_writer_Model;
	}

	public void setBesedOnCommunication_writer_Model(BufferedWriter besedOnCommunication_writer_Model) {
		this.besedOnCommunication_writer_Model = besedOnCommunication_writer_Model;
	}

	public BufferedWriter getBesedOnOptimality_writer_Model() {
		return besedOnOptimality_writer_Model;
	}

	public void setBesedOnOptimality_writer_Model(BufferedWriter besedOnOptimality_writer_Model) {
		this.besedOnOptimality_writer_Model = besedOnOptimality_writer_Model;
	}

	public BufferedWriter getBesedOnManagerial_writer_Model() {
		return besedOnManagerial_writer_Model;
	}

	public void setBesedOnManagerial_writer_Model(BufferedWriter besedOnManagerial_writer_Model) {
		this.besedOnManagerial_writer_Model = besedOnManagerial_writer_Model;
	}

	public double getNDCG_avg_mentorship_Model_10() {
		return NDCG_avg_mentorship_Model_10;
	}

	public void setNDCG_avg_mentorship_Model_10(double nDCG_avg_mentorship_Model_10) {
		NDCG_avg_mentorship_Model_10 = nDCG_avg_mentorship_Model_10;
	}

	public double getNDCG_avg_mentorship_Model_20() {
		return NDCG_avg_mentorship_Model_20;
	}

	public void setNDCG_avg_mentorship_Model_20(double nDCG_avg_mentorship_Model_20) {
		NDCG_avg_mentorship_Model_20 = nDCG_avg_mentorship_Model_20;
	}

	public double getGAP_avg_mentorship_Model_10() {
		return GAP_avg_mentorship_Model_10;
	}

	public void setGAP_avg_mentorship_Model_10(double gAP_avg_mentorship_Model_10) {
		GAP_avg_mentorship_Model_10 = gAP_avg_mentorship_Model_10;
	}

	public double getGAP_avg_mentorship_Model_20() {
		return GAP_avg_mentorship_Model_20;
	}

	public void setGAP_avg_mentorship_Model_20(double gAP_avg_mentorship_Model_20) {
		GAP_avg_mentorship_Model_20 = gAP_avg_mentorship_Model_20;
	}

	public BufferedWriter getBesedOnMentorship_writer_Model() {
		return besedOnMentorship_writer_Model;
	}

	public void setBesedOnMentorship_writer_Model(BufferedWriter besedOnMentorship_writer_Model) {
		this.besedOnMentorship_writer_Model = besedOnMentorship_writer_Model;
	} 
	
	
	
}
