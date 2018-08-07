package optimalCost;



import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import workload.workloadGenerator;

public class WorkloadBasedOnlineWithSingleCopyAlgorithm {
	
	
	int T;// total period time
	long J;
	bCostElements [][] bfCost;// This cost is corresponding to finalLocation.
	int  [][] finalLocation;// This variable indicates the final place of object "j" in time slot "t", and cannot be changed.
	int breakPoint;
	//int dcIndex;// This variable is set to determine which DC should be used now in our simulation.
	BenchmarkAlgorithm benchAlgorithm;
	totalCostCalculation tCalculation;
	finalCostCalculation fc;
 
// 1 indicates hot-tier and 0 indicates cold-tier
 public void WorkloadBasedOnlineCostOptimizationWithSingleCopy() {
		
	 	initialParameters();
		int inputObjectsNumber=0;
		int indexJ=0;
		
		int keepTime=0;// This variable indicates how long an object is stored in the hot-tier
		int currentTime=0; // This variable indicates the passing time in the simulation
		int migrationTime=0;// This indicates the last migration time of the object.
		double alpha=0;
	 for (int slot = 0; slot < workloadGenerator.numberObjectsPerSlot.length; slot++) {
		inputObjectsNumber=inputObjectsNumber+workloadGenerator.numberObjectsPerSlot[slot];
		
		
		for (int j = indexJ; j < inputObjectsNumber; j++) {
			
		
			if(residentialLatencyCost(j, 0, 1).compareTo(residentialLatencyCost(j, 0, 0))==-1 ||
			   residentialLatencyCost(j, 0, 1).compareTo(residentialLatencyCost(j, 0, 0))==0){
				finalLocation[j][0]=1;
			}else{
				finalLocation[j][0]=0;
			}
			
			
			for (int t=1; t<T; t++ ){
				
				currentTime=t;
				
				if(existRequest(j, t)){
					breakPointEvaluation(j, t, migrationTime);// calculate break-even point
					alpha=alphaCalculation();// calculate alpha
					
					if(finalLocation[j][t-1]==1){
						for (keepTime = currentTime; keepTime < currentTime+alpha*(breakPoint); keepTime++) {
							finalLocation[j][t]=1;
						}	
					}else if(transferToCoolTier(j, t, migrationTime)){// Continue to be in the cool tier
						finalLocation[j][t]=0;
						
						
					}else{// migrate from cool to hot tier
						finalLocation[j][t]=1;
						
					}
				    
				}else if (t>keepTime){// migrate from hot to cool tier
					finalLocation[j][t]=0;
					migrationTime=t;
				}
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
			
		int migrationTimeToCool=0;	
		for (int obj = 0; obj < finalLocation.length; obj++) {
		    for (int time = 0; time < finalLocation[obj].length; time++) {
		    	
		       //System.out.println("LEN===>"+obj+"  "+time+"  "+finalLocation[obj][time]);
		       bfCost[obj][time].bstorageCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bstorageCost;
		       bfCost[obj][time].breadCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].breadCost;
			   
		       bfCost[obj][time].bwriteCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bwriteCost;
			   bfCost[obj][time].btranCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].btranCost;
			   bfCost[obj][time].bdelayCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bdelayCost;
			   
			   bfCost[obj][time].bnonMigrationCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bnonMigrationCost;
			   
			   
			   
			   if(breakPoint==1){// if breakpoint is ONE then one time the object is transfered from  cold to hot tier and one time in reverse direction.
				   
				   
			   }else if(time>0 && finalLocation[obj][time-1]!=finalLocation[obj][time]){
				    //System.out.println("MIGRATION====>"+ time+"   "+obj);
			    	bfCost[obj][time].bmigrationCost=totalCostCalculation.btotalMigrationCost[time][obj][finalLocation[obj][time-1]][finalLocation[obj][time]];
			    	
			    	// calculate the early deletion cost
			    	if (finalLocation[obj][time-1]==0 && finalLocation[obj][time]==1){//migration from the cool to hot tier
			    	     bfCost[obj][time].bedelCost=fc.earlyDeletionCost(workloadGenerator.objectListRegion[obj], time, migrationTimeToCool);
			    	}else{
			    		  migrationTimeToCool=time;
			    	}
			   }
			}
		}		
			
	}
		
/*
// This function determines the location of the objects in the initial time (t=0)
 public void initialPlacement(int j, int t) {
	// tierType 1: Hot, tiyerType 0:Cool
	BigInteger HResiCost= totalCostCalculation.btotalResidentCost[t][j][1].bnonMigrationCost;
	BigInteger CResiCost= totalCostCalculation.btotalResidentCost[t][j][0].bnonMigrationCost;
	
	//1: Hot tier,0:cool tier for making justify index, 0:read, 1:write 
	long HReadLatCost= fc.latencyWithinDatacenter(workloadGenerator.objectListRegion[j], 1, 0)*
	workloadGenerator.objectListWriteRate[j][t]*optimizationCost.readToWrite;
	long HWriteLatCost= fc.latencyWithinDatacenter(workloadGenerator.objectListRegion[j], 1, 1)*
			workloadGenerator.objectListWriteRate[j][t];
	
	long CReadLatCost= fc.latencyWithinDatacenter(workloadGenerator.objectListRegion[j], 0, 0)*
			workloadGenerator.objectListWriteRate[j][t]*optimizationCost.readToWrite;
	long CWriteLatCost= fc.latencyWithinDatacenter(workloadGenerator.objectListRegion[j], 0, 1)*
					workloadGenerator.objectListWriteRate[j][t];
	
	if (HResiCost.add(BigInteger.valueOf(HReadLatCost+HWriteLatCost)).compareTo(CResiCost.add(BigInteger.valueOf(CReadLatCost+CWriteLatCost)))==-1 ||
		HResiCost.add(BigInteger.valueOf(HReadLatCost+HWriteLatCost)).compareTo(CResiCost.add(BigInteger.valueOf(CReadLatCost+CWriteLatCost)))==0)
    {
	   finalLocation[j][t]=1; //Object in the Hot tier
	}else{
		finalLocation[j][t]=0;// Object in the Cool tier 
	}
}
*/
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
 
 
//This function calculates the break point for online algorithm
private void breakPointEvaluation(int j, int t, int tm) {
   
  int dcIndex=workloadGenerator.objectListRegion[j];
  int a_c= (int)(objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHReadCost()+
          objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHWriteCost());
  int l_c=0;//We use zero within a Datacenter and it can be more than zero when the object is moves across DCs.
	
  int e_d= fc.earlyDeletionCost(dcIndex, t, tm).intValue();
  int s_c=(int)(objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHStorageCost()-
  		objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCStorageCost())*
  		(int)finalCostCalculation.newBucketSize[j][t];
  
  breakPoint=(int)Math.ceil((double)(a_c+l_c+e_d)/(double)(s_c)) ;
	 
	 //System.out.println("breakPoint in SingleCopy===>"+breakPoint);
	 
}	
 
private double alphaCalculation() {
	
	double alpha=1;
	if (breakPoint<T) {
		alpha=breakPoint/T;
	}
	return alpha;
} 
 
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
 


// This function determines whether an object is transferred to the cool tier
private Boolean transferToCoolTier(int j, int t, int tm){
     boolean result=false;
     BigInteger mdeCost=totalCostCalculation.btotalMigrationCost[t][j][finalLocation[t-1][j]][finalLocation[t][j]].
     add(fc.earlyDeletionCost(workloadGenerator.objectListRegion[j], t, tm));
	
    if (residentialLatencyCost(j, t, 0).compareTo(residentialLatencyCost(j, t, 1).add(mdeCost))==-1){
		 result=true;
	 }
	
	return result;
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
	BigInteger bEDCost[]=new BigInteger [(int)J];//early delete
	BigInteger bMCost[]=new BigInteger [(int)J];
	
	for (int j =0 ; j < J; j++) {
		 bwithMigSum[j]=new BigInteger("0");
		 bwithoutMigSum[j] =new BigInteger("0");
		 bSCost[j]=new BigInteger("0");
		 bRCost[j]=new BigInteger("0");
		 bWCost[j]=new BigInteger("0");
		 bCCost[j]=new BigInteger("0");
		 bTCost[j]=new BigInteger("0");
		 bDCost[j]=new BigInteger("0");
		 bEDCost[j]=new BigInteger("0");
		 bMCost[j]=new BigInteger("0");
	  }
	
	long tSize[]=new long [(int)J];
	
	
	outFile1.print("obj#"+","+"nonMCost"+","+"sCost"+","+"rCost"+","+"wCost"+","+"tCost"+","+"cCost"+","+"mCost"+","+"location");
	
	 for (int t = 0; t < T; t++) {
		
    	 outFile1.println("t="+t+"  ");
    	 for (int j =0 ; j < J; j++) {
    		 if(bfCost[j][t].bmigrationCost==null)
   			      bfCost[j][t].bmigrationCost=new BigInteger("0");
    		 //System.out.println("j====> MigrationCost"+"  "+t+"  "+j+"  "+bMCost[j]);
    		 bwithMigSum[j]=bwithMigSum[j].add(bfCost[j][t].bnonMigrationCost).add(bfCost[j][t].bmigrationCost)
    				 .add(bfCost[j][t].bdelayCost.add(bfCost[j][t].bedelCost));
    		 bwithoutMigSum[j]=bwithoutMigSum[j].add(bfCost[j][t].bnonMigrationCost).add(bfCost[j][t].bdelayCost.add(bfCost[j][t].bedelCost));;
    		 
    		 bSCost[j]=bSCost[j].add(bfCost[j][t].bstorageCost);
    		 bRCost[j]=bRCost[j].add(bfCost[j][t].breadCost); 
    		 bWCost[j]=bWCost[j].add(bfCost[j][t].bwriteCost);
    		 bTCost[j]=bTCost[j].add(bfCost[j][t].btranCost);
    		 bCCost[j]=bCCost[j].add(bfCost[j][t].bconsisCost);
    		 bDCost[j]=bDCost[j].add(bfCost[j][t].bdelayCost);
    		 bEDCost[j]=bEDCost[j].add(bfCost[j][t].bedelCost);
    		 bMCost[j]=bMCost[j].add(bfCost[j][t].bmigrationCost);
    		 tSize[j]=tSize[j]+finalCostCalculation.newBucketSize[j][t]; 
    		 
    		 
    		 
    		 outFile1.println("j="+j+","+bfCost[j][t].bnonMigrationCost+","+bfCost[j][t].bstorageCost+","+bfCost[j][t].breadCost+","
    		 +bfCost[j][t].bwriteCost+","+bfCost[j][t].btranCost+","
			 +bfCost[j][t].bconsisCost+","+bfCost[j][t].bmigrationCost+","+finalLocation[j][t]);
    		 
    	   //}	
		}//j
		outFile1.println();
	}//t
  
	 for (int j = 0; j < J; j++) {
		 
		 benchAlgorithm=new BenchmarkAlgorithm();
		 benchAlgorithm.bunoptimizedHotCost(j);
		 
		 benchAlgorithm.bunoptimizedHotCost(j);
    	 BigDecimal bdHotCost=new BigDecimal(benchAlgorithm.getBHCost());
    	 
    	 benchAlgorithm.bunoptimizedColdCost(j);
    	 BigDecimal bdColdCost=new BigDecimal(benchAlgorithm.getBCCost()); 
    	 	 
   	     BigDecimal bdwithMigSum=new BigDecimal(bwithMigSum[j]);
   	     BigDecimal bdwithoutMigSum=new BigDecimal(bwithoutMigSum[j]);
   	     BigDecimal bdSCost=new BigDecimal(bSCost[j]);
   	     BigDecimal bdRCost=new BigDecimal(bRCost[j]);
   	     BigDecimal bdWCost=new BigDecimal(bWCost[j]);
   	     BigDecimal bdTCost=new BigDecimal(bTCost[j]);
   	     BigDecimal bdCCost=new BigDecimal(bCCost[j]);
   	     BigDecimal bdDCost=new BigDecimal(bCCost[j]);
   	     BigDecimal bdEDCost=new BigDecimal(bCCost[j]);
   	     
   	     
   	     BigDecimal bdMCost=new BigDecimal(bMCost[j]);
   	    	 
   	    	 
   	    	 // This should be revised
   	        //breakPointEvaluation(workloadGenerator.objectListRegion[j]);
		     outFile2.println("OWOutReplica"+","+workloadGenerator.experimentId+","+
		                     objectDatacenterSpecification.getDatacenterList().get(workloadGenerator.objectListRegion[j]).getName()+","
                 
	    		            + breakPoint+","
			           
			                +j+","+workloadGenerator.objectListRegion[j]+","+T+","+ optimizationCost.readToWrite+","
			           
			                +workloadGenerator.shape+","+workloadGenerator.scale+","+  +workloadGenerator.transactionNumberCoffecient+"," 
			             
                            +tSize[j]+","+finalCostCalculation.sizefactor+","+workloadGenerator.objectMinSize+"_"+workloadGenerator.objectMaxSize+","
                      
                            +bwithMigSum[j]+","+bdwithMigSum.divide(bdHotCost, 4, RoundingMode.CEILING)+"," +bdwithMigSum.divide(bdColdCost, 4, RoundingMode.CEILING)+","
                       
                            +bwithoutMigSum[j]+","+bdwithoutMigSum.divide(bdHotCost, 4, RoundingMode.CEILING)+","+ bdwithoutMigSum.divide(bdColdCost, 4, RoundingMode.CEILING)+","
                       
                            +bdSCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdRCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                            
                            +bdWCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdTCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                           
                            +bdCCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdDCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                           
                            +bdEDCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdMCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)
                            );
		// }
			
	}		 
	
}

public int getBreakPoint() {
	return breakPoint;
}


public void setBreakPoint(int breakPoint) {
	this.breakPoint = breakPoint;
}
   

}
