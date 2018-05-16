package optimalCost;



import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import workload.workloadGenerator;

public class OnlineWithDoubleCopyAlgorithm {
	
	
	int T;// total period time
	long J;
	
	
	bCostElements [][] bfCost;// This cost is corresponding to finalLocation.
	int  [][] finalLocation;// This variable indicates the final place of object "j" in time slot "t", and cannot be changed.
	int [][] nLocation;// This variable indicates the new place of each object "j" at time slot "t", and can be changed.
	
	
	int breakPoint;
	int dcIndex;// This variable is set to determine which DC should be used now in our simulation.
	 
	
	
	BenchmarkAlgorithm benchAlgorithm;
	totalCostCalculation tCalculation;
 
 // 1 indicates hot-tier and 0 indicates cold-tier
 public void onlineCostOptimizationWithDoubleCopy() {
		
	 	initialParameters();
		int inputObjectsNumber=0;
		int indexJ=0;
		
		int keepTime=0;// This variable indicates how long an object is stayed in the hot-tier
		int currentTime=0; // This variable indicates the passing time in the simulation
		
	 for (int slot = 0; slot < workloadGenerator.numberObjectsPerSlot.length; slot++) {
		inputObjectsNumber=inputObjectsNumber+workloadGenerator.numberObjectsPerSlot[slot];
		
		for (int j = indexJ; j < inputObjectsNumber; j++) {
			
			breakPointEvaluation(workloadGenerator.objectListRegion[j]);// evaluation breakpoint
			for (int t=0; t<T; t++ ){
				
				currentTime=t;
				if(existRequest(j, t)){
					//System.out.println("j===>"+j+"  "+"t===>"+t);
					for (keepTime = currentTime; keepTime < currentTime+breakPoint; keepTime++) {
						finalLocation[j][t]=1;
					}
				}// FIRST IF
				
				else if(keepTime<=t || t==0 ||finalLocation[j][t-1]!=1){// transfer from hot tier to cold tier
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
			
			
		for (int obj = 0; obj < finalLocation.length; obj++) {
		    for (int time = 0; time < finalLocation[obj].length; time++) {
			
		       bfCost[obj][time].bstorageCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bstorageCost;
			   
		    // calculate storage cost for cold when object is in both tiers
		       
		       bfCost[obj][time].breadCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].breadCost;
			   
		       bfCost[obj][time].bwriteCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bwriteCost;
			   
		       bfCost[obj][time].btranCost=totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].btranCost;
			   
		       /* Here we calculate storage cost and transaction cost to make consistent data. This requires just write transaction. Since in our cost 
               calculation make a computation for both read and writes.
		       */
		      
		       if(finalLocation[obj][time]==1){//NOTE: in above cost storage, we calculate the cost of object in either in hot or cold tier. 
		    	        //So here we calculate the storage cost in cold tier when the object is in hot tier.
		    	    bfCost[obj][time].bstorageCost=bfCost[obj][time].bstorageCost.add(totalCostCalculation.btotalResidentCost[time][obj][0].bstorageCost);
		            bfCost[obj][time].bconsisCost=totalCostCalculation.btotalResidentCost[time][obj][0].bconsisCost;
		       }
			   bfCost[obj][time].bnonMigrationCost=bfCost[obj][time].bstorageCost.add(bfCost[obj][time].breadCost).add( bfCost[obj][time].bwriteCost).
					                               add(bfCost[obj][time].btranCost).add( bfCost[obj][time].bconsisCost);
					         //totalCostCalculation.btotalResidentCost[time][obj][finalLocation[obj][time]].bnonMigrationCost.add(bfCost[obj][time].bconsisCost);
			   
			   // We need to calculate just transfer cost between cold to hot tier. From hot to cold tier does not make sense because we have a copy of data in cold tier.
		       if(breakPoint==1){//If breakPoint is ONE then for serving every  user, the object for each time slot is transfered from Cold to Hot tier.
		    	     /* 
		    	     if (time>1 && finalLocation[obj][time]==1){
		    	           bfCost[obj][time].bmigrationCost=totalCostCalculation.btotalMigrationCost[time][obj][0][finalLocation[obj][time]];
		    	      }
		    	      */
		       }else if(time>0 && finalLocation[obj][time-1]==0 &&  finalLocation[obj][time]==1){
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
     
	 int n_c=(int) (objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCbandwidthCost()+
                    objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getCReadCost());
                   
	 int s_h=(int) objectDatacenterSpecification.getDatacenterListAll().get(dcIndex).getHStorageCost();
	 
	 breakPoint=(int)Math.ceil((double)(n_c)/(double)(s_h)) ;
	
	 //System.out.println("breakPoint for DoubleCopy===>"+breakPoint);
	 
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
	
	
	outFile1.print("obj#"+","+"nonMCost"+","+"sCost"+","+"rCost"+","+"wCost"+","+"cCost"+","+"tCost"+","+"mCost"+","+"location");
	
	 for (int t = 0; t < T; t++) {
		
    	 outFile1.println("t="+t+"  ");
    	 for (int j =0 ; j < J; j++) {
    		  if(bfCost[j][t].bmigrationCost==null)
    			  bfCost[j][t].bmigrationCost=new BigInteger("0");
    		 
    		 bwithMigSum[j]=bwithMigSum[j].add(bfCost[j][t].bnonMigrationCost).add(bfCost[j][t].bmigrationCost);
    		 bwithoutMigSum[j]=bwithoutMigSum[j].add(bfCost[j][t].bnonMigrationCost);
    		 
    		 bSCost[j]=bSCost[j].add(bfCost[j][t].bstorageCost);
    		 bRCost[j]=bRCost[j].add(bfCost[j][t].breadCost); 
    		 bWCost[j]=bWCost[j].add(bfCost[j][t].bwriteCost);
    		 bTCost[j]=bTCost[j].add(bfCost[j][t].btranCost);
    		 bCCost[j]=bCCost[j].add(bfCost[j][t].bconsisCost);
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
   	    	 BigDecimal bdCCost=new BigDecimal(bCCost[j]);
   	    	 BigDecimal bdMCost=new BigDecimal(bMCost[j]);
   	    	 
   	    	 //System.out.println("j====> MigrationCost"+j+"  "+bdMCost);
   	    	 
   	    	breakPointEvaluation(workloadGenerator.objectListRegion[j]);
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
                           
                            +bdCCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdMCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)
                            );
		 
			
	}		 
	
}

public int getDcIndex() {
	return dcIndex;
}



public void setDcIndex(int dcIndex) {
	this.dcIndex = dcIndex;
}

}
