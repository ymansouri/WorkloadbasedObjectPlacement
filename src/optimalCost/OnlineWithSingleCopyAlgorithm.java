package optimalCost;



import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import workload.workloadGenerator;

public class OnlineWithSingleCopyAlgorithm {
	
	
	int T;// total period time
	long J;
	bCostElements [][] bfCost;// This cost is corresponding to finalLocation.
	int  [][] finalLocation;// This variable indicates the final place of object "j" in time slot "t", and cannot be changed.
	int breakPoint;
	int dcIndex;// This variable is set to determine which DC should be used now in our simulation.
	BenchmarkAlgorithm benchAlgorithm;
	totalCostCalculation tCalculation;
	 
 // 1 indicates hot-tier and 0 indicates cold-tier
 public void onlineCostOptimizationWithSingleCopy() {
		
	 	initialParameters();
		int inputObjectsNumber=0;
		int indexJ=0;
		
		int keepTime=0;// This variable indicates how long an object is stored in the hot-tier
		int currentTime=0; // This variable indicates the passing time in the simulation
		
	 for (int slot = 0; slot < workloadGenerator.numberObjectsPerSlot.length; slot++) {
		inputObjectsNumber=inputObjectsNumber+workloadGenerator.numberObjectsPerSlot[slot];
		
		for (int j = indexJ; j < inputObjectsNumber; j++) {
			breakPointEvaluation(workloadGenerator.objectListRegion[j]);// evaluation breakpoint
			for (int t=0; t<T; t++ ){
				//System.out.println("t in first===>"+t+"   "+"j in first===>"+j);
				currentTime=t;
				if(existRequest(j, t)){
				    //System.out.println("j===>"+j+"  "+"t===>"+t+"  "+finalLocation[j][t-1]);
					for (keepTime = currentTime; keepTime < currentTime+breakPoint; keepTime++) {
						//System.out.println(j+"   "+currentTime+"   "+keepTime);
						finalLocation[j][t]=1;
					}
				}// FIRST IF
				
				else if(keepTime<=t || t==0 ||finalLocation[j][t-1]!=1){// transfer from hot tier to cool tier
					  /* 
					  if(t==0){
					       System.out.println("THIS IS A TEST");  
						   System.out.println("t====>"+t+"  "+"j====>"+j+"   "+finalLocation[j][t-1]);
					   }
					  */      
					   finalLocation[j][t]=0;
					   // calculate cost
				    }
				 else{
					  finalLocation[j][t]=1;
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
			
		//System.out.println("LEN===>"+finalLocation.length);	
		for (int obj = 0; obj < finalLocation.length; obj++) {
		    for (int time = 0; time < finalLocation[obj].length; time++) {
		       //System.out.println("LEN===>"+obj+"  "+time+"  "+finalLocation[obj][time]);
		       bfCost[obj][time].bstorageCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bstorageCost;
		       bfCost[obj][time].breadCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].breadCost;
			   
		       bfCost[obj][time].bwriteCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bwriteCost;
			   bfCost[obj][time].btranCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].btranCost;
			   
			   bfCost[obj][time].bnonMigrationCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bnonMigrationCost;
			   
			   if(breakPoint==1){// if breakpoint is ONE then one time the object is transfered from  cold to hot tier and one time in reverse direction.
				   
				   /*
				   if(finalLocation[obj][time]==1){
				         System.out.println("It is in IF"+time);
				         if(time==0)
					            bfCost[obj][time].bmigrationCost=totalCostCalculation.btotalMigrationCost[time][obj][1][0];
				         else{
				        	   bfCost[obj][time].bmigrationCost=totalCostCalculation.btotalMigrationCost[time][obj][0][1];
				        	   //System.out.println("It is in IF"+"  "+time+"   "+bfCost[obj][time].bmigrationCost);
						       bfCost[obj][time].bmigrationCost=bfCost[obj][time].bmigrationCost.add(totalCostCalculation.btotalMigrationCost[time][obj][1][0]);
					           //System.out.println("It is in IF"+"  "+time+"  "+bfCost[obj][time].bmigrationCost);
				         }
				   }
				   */
			   }else if(time>0 && finalLocation[obj][time-1]!=finalLocation[obj][time]){
				    //System.out.println("MIGRATION====>"+ time+"   "+obj);
			    	bfCost[obj][time].bmigrationCost=totalCostCalculation.btotalMigrationCost[time][obj][finalLocation[obj][time-1]][finalLocation[obj][time]];
			   }
			}
		}		
			
	}
		

// This function calculates the break point for online algorithm
	
 public int getBreakPoint() {
		return breakPoint;
	}


	public void setBreakPoint(int breakPoint) {
		this.breakPoint = breakPoint;
	}


public void breakPointEvaluation(int dcIndex) {
     
	 int n_h=(int) (objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHbandwidthCost()+
	                objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHReadCost()+
	                objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHWriteCost());
	 int n_c=(int) (objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCbandwidthCost()+
                    objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCReadCost()+
                    objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCWriteCost());
	 int s_h=(int) objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHStorageCost();
	 int s_c=(int) objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCStorageCost();
	 
	 breakPoint=(int)Math.ceil((double)(n_h+n_c)/(double)(s_h-s_c)) ;
	 
	 //System.out.println("breakPoint in SingleCopy===>"+breakPoint);
	 
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
 
public void initialParameters() {
	
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
	  
 }	
	   
	
public void bwriteInFile(PrintWriter outFile1, PrintWriter outFile2) throws FileNotFoundException {
	
	
	BigInteger bwithMigSum[] = new BigInteger [(int)J];
	BigInteger bwithoutMigSum[] = new BigInteger [(int)J];
	BigInteger bSCost[]=new BigInteger [(int)J];
	BigInteger bRCost[]=new BigInteger [(int)J];
	BigInteger bWCost[]=new BigInteger [(int)J];
	BigInteger bCCost[]=new BigInteger [(int)J];
	BigInteger bTCost[]=new BigInteger [(int)J];
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
	  }
	
	long tSize[]=new long [(int)J];
	
	
	outFile1.print("obj#"+","+"nonMCost"+","+"sCost"+","+"rCost"+","+"wCost"+","+"tCost"+","+"cCost"+","+"mCost"+","+"location");
	
	 for (int t = 0; t < T; t++) {
		
    	 outFile1.println("t="+t+"  ");
    	 for (int j =0 ; j < J; j++) {
    		 if(bfCost[j][t].bmigrationCost==null)
   			      bfCost[j][t].bmigrationCost=new BigInteger("0");
    		 //System.out.println("j====> MigrationCost"+"  "+t+"  "+j+"  "+bMCost[j]);
    		 bwithMigSum[j]=bwithMigSum[j].add(bfCost[j][t].bnonMigrationCost).add(bfCost[j][t].bmigrationCost);
    		 bwithoutMigSum[j]=bwithoutMigSum[j].add(bfCost[j][t].bnonMigrationCost);
    		 
    		 bSCost[j]=bSCost[j].add(bfCost[j][t].bstorageCost);
    		 bRCost[j]=bRCost[j].add(bfCost[j][t].breadCost); 
    		 bWCost[j]=bWCost[j].add(bfCost[j][t].bwriteCost);
    		 bTCost[j]=bTCost[j].add(bfCost[j][t].btranCost);
    		 bCCost[j]=bCCost[j].add(bfCost[j][t].bconsisCost);
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
   	     BigDecimal bdMCost=new BigDecimal(bMCost[j]);
   	    	 
   	    	 //System.out.println("j====>"+j+"  "+blCost+"   "+bunlCost);
   	    	 
   	        breakPointEvaluation(workloadGenerator.objectListRegion[j]);
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
                           
                            +bdCCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdMCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)
                            );
		// }
			
	}		 
	
}

   
public int getDcIndex() {
	return dcIndex;
}



public void setDcIndex(int dcIndex) {
	this.dcIndex = dcIndex;
}

}
