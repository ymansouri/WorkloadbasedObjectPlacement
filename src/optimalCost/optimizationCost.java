package optimalCost;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import makeConfig.configFileMaker;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.intercloudsim.config.InterCloudSimConfiguration;

import workload.workloadGenerator;

public class optimizationCost {

	public static int T;
	public static int readToWrite;
	public static int repeat;
	public static boolean pcFlag;
	static ArrayList<String> wlists = new ArrayList<String>();
	static File fName1;
	static PrintWriter outfile1;
	static File fName2;
	static PrintWriter outfile2;
	static File fName3;
	static PrintWriter outfile3;
	static File fName4;
	static PrintWriter outfile4;

	static File fName5;
	static PrintWriter outfile5;

	static File fName6;
	static PrintWriter outfile6;

	static File fName7;
	static PrintWriter outfile7;

	static File finalFName;
	static PrintWriter finalOutfile;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length > 0)
			InterCloudSimConfiguration.DEFAULT_PATH = args[0];

		else {
			System.out.println("There is no config file.");
			System.exit(0);
		}

		try {
			  int num_user = 1;
			  Calendar calendar = Calendar.getInstance();
			  boolean trace_flag = false;
			  CloudSim.init(num_user, calendar, trace_flag);

			// 1. read file setup (DCs and delay) and workload
			  
			  workloadGenerator wg1 = new workloadGenerator();

			  wlists = wg1.workkloadListsRead();
			  for (int i = 0; i < wlists.size(); i++) {

				workloadGenerator wg = new workloadGenerator();
				System.out.println(wlists.get(i));

				wg.workloadHash(wlists.get(i));
				wg.workloadParametersInitialization();
				totalCostCalculation tcc = new totalCostCalculation();
				if (i == 0) {// we read the specification of DCs and delay
								// between them just for one time
					tcc.initializationOfDatacenterSpecification();
				}
				wg.workloadRead();

				outputFileCreate(wg.parsName(wlists.get(i)));

				for (double fSize = 1; fSize < 1.2; fSize += 0.8) {
					//System.out.println("fSize===>" + fSize);

					for (int rw = 30; rw <31;/*
												 * workloadGenerator.
												 * maxObjectReadToWriteRate
												 */) {
						//System.out.println("readToWrite===>" + readToWrite);
						readToWrite = rw;

						// 3. calculate residential and delay costs
						tcc.intializationOfCostCalculation(fSize);

						tcc.btotalResidentCostCalculationOfFinalCostCalculation();
						tcc.bwriteResidentalCostInFile(fName2, outfile2);

						// 4. calculate migration cost
						tcc.btotalMigrationCostOfFinalCostCalculation();
						tcc.bwriteMigrationCostInFile(fName3, outfile3);

						for (int t =30; t <= workloadGenerator.periodTime; t = t + 10) {
							T = t;
							//System.out.println("breakPoint===>"+(T * 2) / 3);

							//for (int bp = 6; bp <11 ;bp++) {//(T * 2) / 3
							 System.out.println("fSize="+fSize+"  "+ "rw="+rw);	
							  /*
								BenchmarkAlgorithm bc=new BenchmarkAlgorithm();
								bc.bunoptimizedHotCost(0);
								bc.bunoptimizedColdCost(0);
							  */
								//System.out.println("********************* Optimal Cost Algorithm *********************");

								OptimalCostAlgorithm optCost = new OptimalCostAlgorithm();
								optCost.initialParameters(0, T);
								optCost.bOptimalCostCalculation();
								optCost.bcalculateCostEachObject();
								optCost.bwriteInFile(outfile5, finalOutfile);

								//System.out.println("***************** Online With Single Copy Algorithm ************");
								
								OnlineWithSingleCopyAlgorithm oSingle = new OnlineWithSingleCopyAlgorithm();
								oSingle.initialParameters();
								//oSingle.setBreakPoint(bp);
								oSingle.onlineCostOptimizationWithSingleCopy();
								oSingle.bsetCostElements();
								oSingle.bwriteInFile(outfile6, finalOutfile);
                                
								//System.out.println("**************** Online With Double Copy Algorithm *****************");
								OnlineWithDoubleCopyAlgorithm oDouble = new OnlineWithDoubleCopyAlgorithm();
								oDouble.initialParameters();
								//oDouble.setBreakPoint(bp);
								oDouble.onlineCostOptimizationWithDoubleCopy();
								oDouble.bsetCostElements();
								oDouble.bwriteInFile(outfile7, finalOutfile);
							
							   /*
								if(bp==1){
								   bp+=4;
							   }else{
								   bp+=5;
							   }
						   */
								
							//}

						} // T

						if (rw == 1) {
							rw += 9;
						} else {
							rw += 10;
						}

					} // rate

				} // iteration
				filesClose();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	public static void outputFileCreate(String wn) throws IOException {
		// wn is the name of workload and we used in this project only.
		fName1 = new File(configFileMaker.createFolder(workloadGenerator.experimentId) + "/workload/ObjectSpecification"
				+ "_" + wn + ".csv");
		outfile1 = new PrintWriter(fName1);
		outfile1.println("run" + "," + "obj" + "," + "reg#" + "," + "R/W" + "," + "totalRead" + "," + "totalReadSize"
				+ "," + "totalWriteSize");

		fName2 = new File(configFileMaker.createFolder(workloadGenerator.experimentId) + "/workload/NonMCost" + "_" + wn
				+ ".csv");
		outfile2 = new PrintWriter(fName2);

		fName3 = new File(
				configFileMaker.createFolder(workloadGenerator.experimentId) + "/workload/mCost" + "_" + wn + ".csv");
		outfile3 = new PrintWriter(fName3);

		fName4 = new File(configFileMaker.createFolder(workloadGenerator.experimentId) + "/result/benchmark" + "/"
				+ "benchmark" + "_" + wn + ".csv");
		outfile4 = new PrintWriter(fName4);

		fName5 = new File(configFileMaker.createFolder(workloadGenerator.experimentId) + "/result/Optimal" + "/" + "opt"
				+ "_" + wn + ".csv");
		outfile5 = new PrintWriter(fName5);

		fName6 = new File(configFileMaker.createFolder(workloadGenerator.experimentId) + "/result/singleCopy" + "/"
				+ "singleR" + "_" + wn + ".csv");
		outfile6 = new PrintWriter(fName6);

		fName7 = new File(configFileMaker.createFolder(workloadGenerator.experimentId) + "/result/doubleCopy" + "/"
				+ "doubleR" + "_" + wn + ".csv");
		outfile7 = new PrintWriter(fName7);

		finalFName = new File(
				/*
				 * configFileMaker.createFolder(workloadGenerator.experimentId)+
				 */"OSD" + workloadGenerator.experimentId + "_" + wn + ".csv");
		finalOutfile = new PrintWriter(finalFName);
		finalOutfile.println("AlgorithmName" + "," + "expID" + "," + "DCName1" + "," + "breakPonit" + "," + "obj#" + ","
				+ "objReg" + "," + "T" + "," + "R/W" + "," + "shape" + "," + "scale" + "," + "TNC" + ","
				+ "totalSize" + "," + "factorSize" + "," + "itemSize" + "," + "sumWM" + "," + "NHCost" + "," + "NCCost"
				+ "," + "sumWOM" + "," + "NHCostWOM" + "," + "NCCostWOM" + "," + "SCostP" + "," + "RCostP" + ","
				+ "WCostP" + "," + "TCostP" + "," + "CCostP" + "," + "MCostP");

	}

	public static void filesClose() {
		outfile1.close();
		outfile2.close();
		outfile3.close();
		outfile4.close();
		outfile5.close();
		outfile6.close();
		outfile7.close();

		finalOutfile.close();

	}
}
