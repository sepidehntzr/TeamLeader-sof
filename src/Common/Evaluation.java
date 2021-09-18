package Common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Evaluation {

	public HashMap<Integer, Double> Mentorship(List<Integer> projectmanagers, ArrayList<String> project,
			List<User> team, Map<Integer, HashMap<Integer, Double>> Asker_Answerer) throws IOException {

		///////////////////////////////////

		HashMap<Integer, Double> uid_mentorshipScore = new HashMap<Integer, Double>();
		for (Integer pm_ranked : projectmanagers) {
			{
				double score = 0;
				double[] team_score = new double[team.size()];
				//
				int i = 0;
				for (User u : team) {
					double user_score = 0;

					if (Asker_Answerer.containsKey(Integer.parseInt(u.getUserId())) != false)
						if (Asker_Answerer.get(Integer.parseInt(u.getUserId())).containsKey(pm_ranked) != false)
							user_score = Asker_Answerer.get(Integer.parseInt(u.getUserId())).get(pm_ranked);
					if (Double.isNaN(user_score) || Double.isInfinite(user_score))
						user_score = 0;
					team_score[i++] = user_score;
				}
				//
				double teamscore = 0;
				for (double s : team_score)
					teamscore += s;
				score = teamscore / team.size();

				if (Double.isNaN(score) || Double.isInfinite(score))
					score = 0;
				uid_mentorshipScore.put(pm_ranked, score);
			}

		} // for
		return uid_mentorshipScore;
	}

	
	

	public HashMap<Integer, Double> F_measure(List<Integer> projectmanagers,
			HashMap<Integer, Double> uid_communicationScore, HashMap<Integer, Double> uid_optimalityScore,
			HashMap<Integer, Double> uid_managerialScore, Map<String, User> technical_goldset) {

		HashMap<Integer, Double> uid_f_measureScore = new HashMap<Integer, Double>();
		for (Integer pm_ranked : projectmanagers) {
			{
				double score = 0;
				double optimalityScore = uid_optimalityScore.get(pm_ranked) != null ? uid_optimalityScore.get(pm_ranked)
						: 0;
				double managerialScore = uid_managerialScore.get(pm_ranked) != null ? uid_managerialScore.get(pm_ranked)
						: 0;

				double d1 = 2 * optimalityScore * managerialScore;
				double d2 = optimalityScore + managerialScore;
				score = d1 / d2;

				if (Double.isNaN(score) || Double.isInfinite(score))
					score = 0;

				uid_f_measureScore.put(pm_ranked, score);
			}

		}

		return uid_f_measureScore;
	}

	////////////////////////////////////////////////// IR Eval

	public double GAP_AtK(List<Integer> rankedUsers, LinkedHashMap<Integer, Double> UId_rel, int k,
			ArrayList<String> project) {

		ArrayList<Double> RankedRelList = new ArrayList<>();

		for (int UId : rankedUsers) {
			double rel = UId_rel.containsKey(UId) ? UId_rel.get(UId) : 0;
			RankedRelList.add(rel);
		}

		// k = RankedRelList.size();
		// double[] a = new double[k+2];
		// double[] b = new double[k+2];
		int i = 0;

		ArrayList<Double> RankedRelList_best = (ArrayList<Double>) RankedRelList.clone();

		Collections.sort(RankedRelList_best, Collections.reverseOrder());

		double best_value = 0;
		double sum_gain = 0;
		for (int n = 1; n <= k; n++) {
			double gain = 0;
			double gain_i_n = RankedRelList.get(n - 1);
			for (int m = 1; m <= n; m++) {
				double gain_i_m = RankedRelList.get(m - 1);
				if (gain_i_n < gain_i_m)
					gain += gain_i_n;
				else
					gain += gain_i_m;
			}

			gain /= (double) (n);
			sum_gain += gain;
			// a[i++]= gain;

			best_value += RankedRelList_best.get(n - 1);
		}
		// ArrayList<Double> RankedRelList2 = (ArrayList<Double>) RankedRelList.clone();

		// Collections.sort(RankedRelList, Collections.reverseOrder());
		// i=0;
		// for(int n = 1 ; n<=k ; n++)
		// {
		// best_value += RankedRelList.get(n-1);
		// b[i++]= RankedRelList.get(n-1);
		// }

		// double j =(double) sum_gain/best_value;
		// if(j>1)
		// System.out.print("");
		return (double) sum_gain / best_value;
	}

	public double MRR(List<Integer> rankedUsers, LinkedHashMap<Integer, Double> UId_rel, ArrayList<String> project,
			double mrr_gain) {
		ArrayList<Double> RankedRelList = new ArrayList<>();

		double g_max = mrr_gain;
		for (int UId : rankedUsers) {
			double rel = UId_rel.containsKey(UId) ? UId_rel.get(UId) : 0;
			RankedRelList.add(rel);
			g_max = rel > g_max ? rel : g_max;
		}
		double MRR = calculateMRR(RankedRelList, g_max);

		if (Double.isNaN(MRR) || Double.isInfinite(MRR))
			MRR = 0;

		return MRR;
	}

	private double calculateMRR(ArrayList<Double> RankedRelList, double g_max) {
		int count = 1;
		double MRR = 0;
		for (double scoreItem : RankedRelList) {
			if (scoreItem == g_max) {
				MRR = 1.0 / (double) count;
				break;
			}
			count++;
		}
		return MRR;
	}

	public double NDCG_AtK(List<Integer> rankedUsers, LinkedHashMap<Integer, Double> UId_rel, int k,
			ArrayList<String> project) {
		ArrayList<Double> RankedRelList = new ArrayList<>();
		ArrayList<Double> BestRelList = new ArrayList<>();

		for (int UId : rankedUsers) {
			double rel = UId_rel.containsKey(UId) ? UId_rel.get(UId) : 0;
			RankedRelList.add(rel);
		}
		BestRelList = (ArrayList<Double>) RankedRelList.clone();
		sortLinkedHashSet(BestRelList);

		RankedRelList = CutListsBasedOnThreshold(k, RankedRelList);
		BestRelList = CutListsBasedOnThreshold(k, BestRelList);
		double NDCG = calculateNDCG(RankedRelList, BestRelList);

		if (Double.isNaN(NDCG) || Double.isInfinite(NDCG))
			NDCG = 0;

		return NDCG;
	}

	////////////////////////////////////////////////////

	private void sortLinkedHashSet(ArrayList<Double> relList) {
		Collections.sort(relList, Collections.reverseOrder());
	}

	private ArrayList<Double> CutListsBasedOnThreshold(int threshold, ArrayList<Double> List) {
		List = new ArrayList<>(cutTheListBasedOnThreshold(threshold, List));
		return List;
	}

	private ArrayList<Double> cutTheListBasedOnThreshold(int threshold, ArrayList<Double> RankedList) {
		int i = 1;
		ArrayList<Double> RankedRelTemp = new ArrayList<>();
		for (double rel : RankedList) {
			RankedRelTemp.add(rel);
			if (i >= threshold)
				break;
			i++;
		}
		return RankedRelTemp;
	}

	private double calculateNDCG(ArrayList<Double> RankedRelList, ArrayList<Double> BestRelList) {
		double DCG = CalculateDCG(RankedRelList);
		double IDCG = CalculateDCG(BestRelList);
		double NDCG = DCG / IDCG;

		return NDCG;
	}

	private double CalculateDCG(ArrayList<Double> RankedRelList) {
		double DCG = 0;
		int i = 1;
		for (double rel_i : RankedRelList) {
			DCG += CalculateDCGInner(i, rel_i);
			i++;
		}
		return DCG;
	}

	private double CalculateDCGInner(int i, double rel_i) {
		double oneDCG = (Math.pow(2, rel_i) - 1) / (Math.log10(i + 1) / Math.log10(2));
		return oneDCG;
	}

	///////////////////////////////////////////

}
