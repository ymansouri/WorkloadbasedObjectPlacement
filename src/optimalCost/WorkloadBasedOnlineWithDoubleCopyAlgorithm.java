package optimalCost;



import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import workload.workloadGenerator;

public class WorkloadBasedOnlineWithDoubleCopyAlgorithm {
	
	
	private int T;// total period time
	private long J;
	
	
	private bCostElements [][] bfCost;// This cost is corresponding to finalLocation.
	private int  [][] finalLocation;// This variable indicates the final place of object "j" in time slot "t", and cannot be changed.
	//private int [][] nLocation;// This variable indicates the new place of each object "j" at time slot "t", and can be changed.
	private int breakPoint;
	private double alpha;
	private finalCostCalculation fc; 
	
	
	BenchmarkAlgorithm benchAlgorithm;
	totalCostCalculation tCalculation;
 
 // 1 indicates hot-tier and 0 indicates cold-tier
 public void WorkloadBasedOnlineCostOptimizationWithDoubleCopy() {
		
	 	initialParameters();
		int inputObjectsNumber=0;
		int indexJ=0;
		
		int keepTime=0;// This variable indicates how long an object is stayed in the hot-tier
		int currentTime=0; // This variable indicates the passing time in the simulation
		
	 for (int slot = 0; slot < workloadGenerator.numberObjectsPerSlot.length; slot++) {
		inputObjectsNumber=inputObjectsNumber+workloadGenerator.numberObjectsPerSlot[slot];
		
		for (int j = indexJ; j < inputObjectsNumber; j++) {
			
			for (int t=0; t<T; t++ ){
				
				currentTime=t;
				if(existRequest(j, t)){
					breakPointEvaluation(j, t);
					alphaCalculation();
					
					if(finalLocation[j][t]==1){
					
					    for (keepTime = currentTime; keepTime < currentTime+alpha*(breakPoint); keepTime++) {
						    finalLocation[j][t]=1;
					     }
					}else if(residentialLatencyCost(j, t, 0).compareTo(residentialLatencyCost(j, t,1).add(totalCostCalculation.btotalMigrationCost[t][j][0][1]))==1){
						finalLocation[j][t]=1;
					}
				}// FIRST IF
			}   	
		}//For t
		indexJ=inputObjectsNumber;
	 }//slot
	 
	 /*
	 for (int obj = 0; obj < finalLocation.length; obj++) {
		    for (int time = 0; time < finalLocation[obj].length; time++) {
		     System.out.print(finalLocation[obj][time]+"  ");
		    
		    
		    }
	 }
	 */
	 
}

	
// This function sets storage, read, write and non-migration cost for each object "j" in time "t".
	public void bsetCostElements() {
			
			
		for (int obj = 0; obj < finalLocation.length; obj++) {
		    for (int time = 0; time < finalLocation[obj].length; time++) {
			
		       bfCost[obj][time].bstorageCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bstorageCost;
			   bfCost[obj][time].breadCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].breadCost;
			   bfCost[obj][time].bwriteCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bwriteCost;
			   bfCost[obj][time].btranCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].btranCost;
			   
			   //1. we calculate latency cost for two cases separately: when the object is in the hot  tier or cool tier.
			   if (finalLocation[obj][time]==0){//Object is in the cool tier
				   bfCost[obj][time].bdelayCost=totalCostCalculation.btotalResidentCost[time][obj][0].bdelayCost;
				   
			   }else{// object is in both tiers and, in consequence, the latency cost of writes is the maximum latency of writing in both tiers, and the read latency is the one in hot tier.
				   bfCost[obj][time].bedelCost=fc.bdelayMaxWriteReadCost(workloadGenerator.objectListRegion[obj], 1, obj, time);
			   }
			   
		       /* 2. Here we calculate storage cost and transaction cost to make consistent data. This requires just write transaction. Since in our cost 
               calculation make a computation for both read and writes.
		       */
		      
		       if(finalLocation[obj][time]==1){//NOTE: in above cost storage, we calculate the cost of object in either in hot or cold tier. 
		    	        //So here we calculate the storage cost in cold tier when the object is in hot tier.
		    	    bfCost[obj][time].bstorageCost=bfCost[obj][time].bstorageCost.add(totalCostCalculation.btotalResidentCost[time][obj][0].bstorageCost);
		            bfCost[obj][time].bconsisCost=totalCostCalculation.btotalResidentCost[time][obj][0].bconsisCost;
		       }
			   bfCost[obj][time].bnonMigrationCost=bfCost[obj][time].bstorageCost.add(bfCost[obj][time].breadCost).add( bfCost[obj][time].bwriteCost).
					                               add(bfCost[obj][time].btranCost).add( bfCost[obj][time].bconsisCost).
					                               add(bfCost[obj][time].bdelayCost);
					         //totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bnonMigrationCost.add(bfCost[obj][time].bconsisCost);
			   
			   //3. We need to calculate just transfer cost between cold to hot tier. From hot to cold tier does not make sense because we have a copy of data in cold tier.
		       if(breakPoint==1){//If breakPoint is ONE then for serving every  user, the object for each time slot is transfered from Cold to Hot tier.
		    	    
		        }
		        else if(time>0 && finalLocation[obj][time-1]==0 &&  finalLocation[obj][time]==1){
			          bfCost[obj][time].bmigrationCost=totalCostCalculation.btotalMigrationCost[time][obj][finalLocation[obj][time-1]][finalLocation[obj][time]];
				}
			}
		}		
	}
		


	
	
	
// This function calculates the break point for online algorithm
public void breakPointEvaluation(int j, int t) {
    
	int dcIndex=workloadGenerator.objectListRegion[j];
	finalCostCalculation fc= new finalCostCalculation();
	int a_c=workloadGenerator.objectListWriteRate[j][t]*optimizationCost.readToWrite+
			(int)((objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCbandwidthCost()*
					finalCostCalculation.newBucketSize[j][t])); 
	int l_c=(workloadGenerator.objectListWriteRate[j][t]*optimizationCost.readToWrite)*fc.latencyWithinDatacenter(dcIndex, 0, 0);//0:Cool tier, 0:read
	
	int a_h=workloadGenerator.objectListWriteRate[j][t]*(int)(objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHStorageCost()); 
	int s_h=(int)(objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHStorageCost()*finalCostCalculation.newBucketSize[j][t]);
	 
	 breakPoint=(int)Math.ceil((double)(a_c+l_c-a_h)/(double)(s_h)) ;
	
	 //System.out.println("breakPoint for DoubleCopy===>"+breakPoint);
	 
 }	
/*	
private Boolean transferCostWODeletionCost(int j, int t){
    boolean result=false;
    BigInteger mdeCost=totalCostCalculation.btotalMigrationCost[t][j][0][1];
	
   if (residentialLatencyCost(j, t, 0).compareTo(residentialLatencyCost(j, t, 1).add(mdeCost))==-1){
		 result=true;
	 }
	
	return result;
}
*/
//This function calculates the residential and latency cost 
private BigInteger residentialLatencyCost(int j, int t, int tierType) {
	// tierType 1: Hot, tiyerType 0:Cool
	BigInteger resiCost= totalCostCalculation.btotalResidentCost[t][j][tierType].bnonMigrationCost;
	
	//1: Hot tier, 0:cool tier for making justify index, 0:read, 1:write 
	long readLatCost= fc.latencyWithinDatacenter(workloadGenerator.objectListRegion[j], tierType, 0)*
	workloadGenerator.objectListWriteRate[j][t]*optimizationCost.readToWrite;
	
	long writeLatCost= fc.latencyWithinDatacenter(workloadGenerator.objectListRegion[j], tierType, 1)*
			workloadGenerator.objectListWriteRate[j][t];
	
	return resiCost.add(BigInteger.valueOf(readLatCost+writeLatCost));
} 






//This function demonstrates whether there is R/W request for an object "j" in specific time "t"
public boolean existRequest(int j, int t) {
	 
	 boolean result=false;
	 for (int reg = 0; reg < workloadGenerator.regioNumber; reg++) {
		 
		 if(workloadGenerator.objectListWriteRatePerRegion[j][t][reg]>0){
			 result=true;
			 break;
		 }
	 }
	return result;
}

private double alphaCalculation() {
	
	double alpha=1;
	if (breakPoint<T) {
		alpha=breakPoint/T;
	}
	return alpha;
} 
 
 
private void initialParameters() {
	
	T=optimizationCost.T;
	J=workloadGenerator.numberObjects;// for test
	
	bfCost=new bCostElements [(int)J][T];
	for (int j = 0; j < J; j++) {
		for (int t = 0; t < T; t++) {
			bfCost[j][t]=new bCostElements();
			
		}
	}
	
	finalLocation=new int [(int)J][T];// Initialized with -1 that equivalent empty set
	for (int[] row: finalLocation)
	    Arrays.fill(row, -1);
	
	
	benchAlgorithm=new BenchmarkAlgorithm();
	tCalculation=new totalCostCalculation();
	fc=new finalCostCalculation();  
 }	
	   
	
public void bwriteInFile(PrintWriter outFile1, PrintWriter outFile2) throws FileNotFoundException {
	
	
	BigInteger bwithMigSum[] = new BigInteger [(int)J];
	BigInteger bwithoutMigSum[] = new BigInteger [(int)J];
	BigInteger bSCost[]=new BigInteger [(int)J];
	BigInteger bRCost[]=new BigInteger [(int)J];
	BigInteger bWCost[]=new BigInteger [(int)J];
	BigInteger bCCost[]=new BigInteger [(int)J];
	BigInteger bTCost[]=new BigInteger [(int)J];
	BigInteger bDCost[]=new BigInteger [(int)J];//delay
	BigInteger bMCost[]=new BigInteger [(int)J];
	for (int j =0 ; j < J; j++) {
		 bwithMigSum[j]=new BigInteger("0");
		 bwithoutMigSum[j] =new BigInteger("0");
		 bSCost[j]=new BigInteger("0");
		 bRCost[j]=new BigInteger("0");
		 bWCost[j]=new BigInteger("0");
		 bCCost[j]=new BigInteger("0");
		 bTCost[j]=new BigInteger("0");
		 bMCost[j]=new BigInteger("0");
		 bDCost[j]=new BigInteger("0");
	  }
	
	long tSize[]=new long [(int)J];
	
	
	outFile1.print("obj#"+","+"nonMCost"+","+"sCost"+","+"rCost"+","+"wCost"+","+"cCost"+","+"tCost"+","+"mCost"+","+"location");
	
	 for (int t = 0; t < T; t++) {
		
    	 outFile1.println("t="+t+"  ");
    	 for (int j =0 ; j < J; j++) {
    		  if(bfCost[j][t].bmigrationCost==null)
    			  bfCost[j][t].bmigrationCost=new BigInteger("0");
    		 
    		 bwithMigSum[j]=bwithMigSum[j].add(bfCost[j][t].bnonMigrationCost).add(bfCost[j][t].bmigrationCost).add(bfCost[j][t].bdelayCost);
    		 bwithoutMigSum[j]=bwithoutMigSum[j].add(bfCost[j][t].bnonMigrationCost).add(bfCost[j][t].bdelayCost);
    		 
    		 bSCost[j]=bSCost[j].add(bfCost[j][t].bstorageCost);
    		 bRCost[j]=bRCost[j].add(bfCost[j][t].breadCost); 
    		 bWCost[j]=bWCost[j].add(bfCost[j][t].bwriteCost);
    		 bTCost[j]=bTCost[j].add(bfCost[j][t].btranCost);
    		 bCCost[j]=bCCost[j].add(bfCost[j][t].bconsisCost);
    		 bDCost[j]=bDCost[j].add(bfCost[j][t].bdelayCost);
    		 bMCost[j]=bMCost[j].add(bfCost[j][t].bmigrationCost);
    		 
    		 //System.out.println("j====> MigrationCost"+"  "+t+"  "+j+"  "+bMCost[j]);
    		 tSize[j]=tSize[j]+finalCostCalculation.newBucketSize[j][t]; 
    		 
    		 
    		 outFile1.println("j="+j+","+bfCost[j][t].bnonMigrationCost+","+bfCost[j][t].bstorageCost+","+bfCost[j][t].breadCost+","
    		 +bfCost[j][t].bwriteCost+","+bfCost[j][t].bconsisCost+","
			 +bfCost[j][t].btranCost+","+bfCost[j][t].bmigrationCost+","+finalLocation[j][t]);
    		 
    	   //}	
		}//j
		outFile1.println();
	}//t
  
	 for (int j = 0; j < J; j++) {
		 
		 
		 
		 
		     benchAlgorithm=new BenchmarkAlgorithm();
		 	 benchAlgorithm.bunoptimizedHotCost(j);
	    	 BigDecimal bdHotCost=new BigDecimal(benchAlgorithm.getBHCost());
	    	
	    	 benchAlgorithm.bunoptimizedColdCost(j);
	    	 BigDecimal bdColdCost=new BigDecimal(benchAlgorithm.getBCCost()); 
	    	 	
		 
   	    	 // convert form bigInteger to bigDecimal
   	    	 //BigDecimal bdlCost=new BigDecimal("1");//new BigDecimal(benchAlgorithm.getBloCost());
   	    	 //BigDecimal bdunlCost=new BigDecimal("1");//new BigDecimal(benchAlgorithm.getBunloCost()); 
   	    	 
   	    	 BigDecimal bdwithMigSum=new BigDecimal(bwithMigSum[j]);
   	    	 BigDecimal bdwithoutMigSum=new BigDecimal(bwithoutMigSum[j]);
   	    	 BigDecimal bdSCost=new BigDecimal(bSCost[j]);
   	    	 BigDecimal bdRCost=new BigDecimal(bRCost[j]);
   	    	 BigDecimal bdWCost=new BigDecimal(bWCost[j]);
   	    	 BigDecimal bdTCost=new BigDecimal( bTCost[j]);
   	    	 BigDecimal bdDCost=new BigDecimal(bCCost[j]);
   	    	 BigDecimal bdCCost=new BigDecimal(bCCost[j]);
   	    	 BigDecimal bdMCost=new BigDecimal(bMCost[j]);
   	    	 
   	    	 //System.out.println("j====> MigrationCost"+j+"  "+bdMCost);
   	    	 // It should be revised
   	    	//breakPointEvaluation(workloadGenerator.objectListRegion[j]);
		     outFile2.println("OWReplica"+","+workloadGenerator.experimentId+","+
		                      objectDatacenterSpecification.getDatacenterList().get(workloadGenerator.objectListRegion[j]).getName()+","
                 
	    		            +breakPoint+","
			           
			                +j+","+workloadGenerator.objectListRegion[j]+","+T+","+ optimizationCost.readToWrite+","
			           
			                +workloadGenerator.shape+","+workloadGenerator.scale+","+  +workloadGenerator.transactionNumberCoffecient+"," 
			             
                            +tSize[j]+","+finalCostCalculation.sizefactor+","+workloadGenerator.objectMinSize+"_"+workloadGenerator.objectMaxSize+","
                      
                            +bwithMigSum[j]+","+bdwithMigSum.divide(bdHotCost, 4, RoundingMode.CEILING)+"," +bdwithMigSum.divide(bdColdCost, 4, RoundingMode.CEILING)+","
                       
                            +bwithoutMigSum[j]+","+bdwithoutMigSum.divide(bdHotCost, 4, RoundingMode.CEILING)+","+ bdwithoutMigSum.divide(bdColdCost, 4, RoundingMode.CEILING)+","
                       
                            +bdSCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdRCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                            
                            +bdWCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdTCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                            +bdDCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                           
                            +bdCCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdMCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)
                            );
		 
			
	}		 
	
}


public int getBreakPoint() {
	return breakPoint;
}


public void setBreakPoint(int breakPoint) {
	this.breakPoint = breakPoint;
}




}
