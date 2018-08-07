package optimalCost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;

import workload.workloadGenerator;

public class totalCostCalculation {
	
	
	private finalCostCalculation finalCostCal;
	private static int T;
	//private static boolean PCFlag;
	public static int BSize;// this variable is uses for the size of bucket, now for test.
	public static   bCostElements [][][] btotalResidentCost;
    public static BigInteger [][][][] btotalMigrationCost;
	public static int[] objectStartTime;
	private static int tierType;
	 
	
public void initializationOfDatacenterSpecification() throws FileNotFoundException {
		
	    objectDatacenterSpecification.readSpecificationObjectWorkload();
	    // read data center specification
		objectDatacenterSpecification.readAllDatacenter();
		// set delay between DCs
		
		// print data center specification
	    objectDatacenterSpecification.printDatacenterListSpecification();
		
	    objectDatacenterSpecification.readSpecificationObjectWorkload();
	    tierType=objectDatacenterSpecification.tierType;
	    T=workloadGenerator.periodTime;
	}
  

	public void intializationOfCostCalculation(double sf) throws FileNotFoundException {
		
		finalCostCal=new finalCostCalculation();
		finalCostCal.initializeCostCalculationParameters();
		btotalResidentCost=new bCostElements[T][workloadGenerator.numberObjects][tierType]; 
	    for (int t = 0; t < workloadGenerator.periodTime; t++) {
			for (int j = 0; j < workloadGenerator.numberObjects; j++) {
			     for (int tierT=0; tierT<tierType; tierT++){	
				  btotalResidentCost[t][j][tierT]=new bCostElements();
			     }			
			}
	    }
		btotalMigrationCost=new BigInteger[workloadGenerator.periodTime][workloadGenerator.numberObjects][tierType][tierType];
		    
	    finalCostCal.bucketSizeIncrement();
		finalCostCal.preciseCost(optimizationCost.pcFlag,sf);
		
	}
	
	
	
	// This function calculates btotal cost for each bucket in each time slot time "t". This cost includes storage, reading and writing cost.
	public void btotalResidentCostCalculationOfFinalCostCalculation() throws FileNotFoundException {
			
				for (int t = 0; t < workloadGenerator.periodTime; t++) {
					for (int j = 0; j < workloadGenerator.numberObjects; j++) {
						for (int tierT = 0; tierT < tierType ; tierT++) {
							
							//dcIndex: This parameter is set to the region of the object created by user. This value is "objectListRegion[j]" in workloadGenerator   
							btotalResidentCost[t][j][tierT].bstorageCost=finalCostCal.btotalResidentCostPermuDatacenter(workloadGenerator.objectListRegion[j],tierT,j, t ).bstorageCost;
							btotalResidentCost[t][j][tierT].breadCost=finalCostCal.btotalResidentCostPermuDatacenter(workloadGenerator.objectListRegion[j],tierT,j, t ).breadCost;
							btotalResidentCost[t][j][tierT].bwriteCost=finalCostCal.btotalResidentCostPermuDatacenter(workloadGenerator.objectListRegion[j],tierT,j, t ).bwriteCost;
							btotalResidentCost[t][j][tierT].btranCost=finalCostCal.btotalResidentCostPermuDatacenter(workloadGenerator.objectListRegion[j],tierT,j, t ).btranCost;
							btotalResidentCost[t][j][tierT].bconsisCost=finalCostCal.btotalResidentCostPermuDatacenter(workloadGenerator.objectListRegion[j],tierT,j, t ).bconsisCost;
							btotalResidentCost[t][j][tierT].bdelayCost=finalCostCal.btotalResidentCostPermuDatacenter(workloadGenerator.objectListRegion[j],tierT,j, t ).bdelayCost;
							
							btotalResidentCost[t][j][tierT].bnonMigrationCost=finalCostCal.btotalResidentCostPermuDatacenter(workloadGenerator.objectListRegion[j],tierT,j, t ).bnonMigrationCost;
							
						}
						
					}
					
				}
					
			}
	
	
 // This function calculate bmigration cost
 public void btotalMigrationCostOfFinalCostCalculation() {
				
	for (int t = 0; t < workloadGenerator.periodTime; t++) {
			for (int j = 0; j < workloadGenerator.numberObjects; j++) {// We need to calculate transfer Cost only for hot to cold and vice versa. 
				 				    btotalMigrationCost[t][j][0][1]=finalCostCal.bmigrationCostCost(workloadGenerator.objectListRegion[j],0,1, j,t);
				 				    btotalMigrationCost[t][j][1][0]=finalCostCal.bmigrationCostCost(workloadGenerator.objectListRegion[j],1,0, j,t);
		     }
	}
 } 
    	
	

 public void bwriteResidentalCostInFile(File fname,PrintWriter outFile1) throws FileNotFoundException {
		
	    outFile1.println(objectDatacenterSpecification.datacenterList.get(0).getName()+","+objectDatacenterSpecification.datacenterList.get(0).getName());
	    outFile1.print("obj#"+","+"object initialize size"+","+"sType"+","+"sCost"+","+"rCost"+","+"wCost"+","+"tCost"+","+"cCost"+","+"nonMCost");
		outFile1.println();
		 
		 for (int t = 0; t < T; t++) {
			
	    	 outFile1.println("t="+t+"  ");
	    	 for (int j = 0; j < workloadGenerator.numberObjects; j++) {
	    		 for (int tierT = 0; tierT < tierType; tierT++) {
					
	    			  outFile1.println("j="+j+","+finalCostCal.getNewBucketSize()[j][t]+","+tierT+","+btotalResidentCost[t][j][tierT].bstorageCost+","
	    			 +btotalResidentCost[t][j][tierT].breadCost+","+btotalResidentCost[t][j][tierT].bwriteCost+","+btotalResidentCost[t][j][tierT].btranCost+","
	    			 +btotalResidentCost[t][j][tierT].bconsisCost+","+btotalResidentCost[t][j][tierT].bnonMigrationCost);
	    			 
				}
			}
			outFile1.println();
		}
		
}

 public void bwriteMigrationCostInFile(File fname, PrintWriter outFile1) throws FileNotFoundException {
		
	    outFile1.println(objectDatacenterSpecification.datacenterList.get(0).getName()+","+objectDatacenterSpecification.datacenterList.get(0).getName()+","+optimizationCost.readToWrite);
		 outFile1.println();
		 for (int t = 0; t < T; t++) {
			
	    	 outFile1.println("t="+t+"  ");
	    	 for (int j = 0; j < workloadGenerator.numberObjects; j++) {
	    		 for (int s = 0; s < tierType; s++) {
					for (int d = 0; d < tierType; d++) {
				     	outFile1.println("j="+j+","+"s="+s+","+"d="+d+","+btotalMigrationCost[t][j][s][d]);
					}
				}
			}
			outFile1.println();
		}
}
 	
	
public int getBSize() {
		return BSize;
}

public void setBSize(int bSize) {
		BSize = bSize;
}

public static int[] getObjectStartTime() {
		return objectStartTime;
}

public static void setObjectStartTime(int[] objectStartTime) {
		totalCostCalculation.objectStartTime = objectStartTime;
}
}
