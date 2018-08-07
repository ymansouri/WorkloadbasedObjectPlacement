package optimalCost;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;


import workload.workloadGenerator;

public class _OptimalCostAlgorithm {
	
	private int T;
	private int MinT;
	private long J;
	
	
	private int tierType;
	private BenchmarkAlgorithm benchAlgorithm;
	//private totalCostCalculation tCalculation;
	
	// cost and Location of all objects for each permutationComb
	//private long [][][] pCost;
	private BigInteger [][][] bpCost;
	private ArrayList<Integer> [][][] locationAllObjects;// This consider all possible(for combination size) location of All objects 
	
	// total cost and final location each objects for each time slot "t".
	//CostElements [][] Cost;
	private bCostElements [][] bCost;
	private ArrayList<Integer> [][] finalLocationAllObjects;
	private ArrayList<Integer> [][] tempLocationAllObjects;// This shows just only the best location of all objects that is calculated based on the variable above(locationAllObjects).

	
public void bOptimalCostCalculation() {
		
		int inputObjectsNumber=aggregateObjects(MinT);// This variable is the number of objects from t=0 to t=MinT
		
		
		for (int t = MinT; t < T; t++) {
			
			inputObjectsNumber=inputObjectsNumber+workloadGenerator.numberObjectsPerSlot[t];
			
			for (int j = 0; j < inputObjectsNumber; j++) {
			   
				for (int tierT = 0; tierT < tierType; tierT++) {
				    	  
					   if(t==MinT){
						  BigInteger costTier=totalCostCalculation.btotalResidentCost[t][j][tierT].bnonMigrationCost;
		                  bpCost[t][j][tierT]=costTier;
						  calculateLocationAllObjects(t, j, tierT, tierT);
						 }
					 
					   else{// t>0
						    int tempLocation=tierT;
						    // transfer cost between the same tiers is zero.
						    BigInteger mCost=new BigInteger("0");//totalCostCalculation.btotalMigrationCost[t-1][j][tierT][tierT];
						    BigInteger costComb=totalCostCalculation.btotalResidentCost[t][j][tierT].bnonMigrationCost;
						
						    bpCost[t][j][tierT]=bpCost[t][j][tierT].add(bpCost[t-1][j][tierT]).add(costComb).add(mCost);
				            calculateLocationAllObjects(t, j, tierT,tierT);
						
				            for (int combp = 0; combp < tierType; combp++) { 
						        if(combp!=tierT){
						             mCost=totalCostCalculation.btotalMigrationCost[t-1][j][combp][tierT];
							         BigInteger tempCost=new BigInteger("0");
						             tempCost=tempCost.add(bpCost[t-1][j][combp]).add(costComb).add(mCost);
						             if(tempCost.compareTo(bpCost[t][j][tierT])==-1 || tempCost.compareTo(bpCost[t][j][tierT])==0){
						    	         tempLocation=combp;
							             bpCost[t][j][tierT]=tempCost;
							             calculateLocationAllObjects(t, j, tierT,tempLocation);
						         }
						    }
						}
				  }// else
			}//comb			 
	   } //j 
    }//t
		// Calculate location each object in each time slot
		   bcalculateLocationEachObject();
}
	
	

// This function updates location of all objects. That is, it calculates locationAllObjects[t][j][comb]
public void calculateLocationAllObjects(int t, int j, int comb, int combp ) {
	
	
	if(t==0){
		 ArrayList<Integer> temp=new ArrayList<>();
		 temp.add(comb);
		 locationAllObjects[t][j][comb]=temp;
	}
	else{
		   ArrayList<Integer> temp=new ArrayList<>();
	       
		   temp.addAll(locationAllObjects[t-1][j][combp]);
		   temp.add(comb);
	       locationAllObjects[t][j][comb]=temp;
		
	}// else
}


public void bcalculateLocationEachObject() {
	
	 int inputObjectsNumber=0;
	 inputObjectsNumber=workloadGenerator.numberObjectsPerSlot[MinT]+aggregateObjects(MinT);
	 int indexJ=0;
	 int indexT=0; 
	
	 for (int slot = MinT; slot < T; slot++) {
		 
	       for (int j = indexJ; j < inputObjectsNumber; j++) {
	        	
	    	   //if(tCalculation.objectmemberInDC(j)){
	    		   
	    	        for (int t = slot; t < T; t++) {
				       BigInteger tempMinCost=bpCost[t][j][0];
				       
				       finalLocationAllObjects[j][t]=new ArrayList<>();
				       finalLocationAllObjects[j][t].addAll(locationAllObjects[t][j][0]);
				       
				       tempLocationAllObjects[j][t]=new ArrayList<>();
				       tempLocationAllObjects[j][t].addAll(locationAllObjects[t][j][0]);
				       
				       int tempIndex=0;
			     	   for (int comb = 1; comb < tierType; comb++) {
				           //if(bpCost[t][j][comb]<tempMinCost ){
				           if(bpCost[t][j][comb].compareTo(tempMinCost)==-1 ){
				    	      tempMinCost=bpCost[t][j][comb];
				    	      tempIndex=comb;
				    	      finalLocationAllObjects[j][t]=new ArrayList<>();
				    	      finalLocationAllObjects[j][t].addAll(locationAllObjects[t][j][comb]);
				    	   }
			     	    }
			     	 // this part of code shows the location of each object "j" until time "t". // It is for more test. It is not needy for result!!!
			     	  if(t>slot){
						    tempLocationAllObjects[j][t]=new ArrayList<>();
						    tempLocationAllObjects[j][t].addAll(tempLocationAllObjects[j][t-1]);
						    tempLocationAllObjects[j][t].add(tempIndex);
					      	}
			     	  if(t==slot){
			     		      tempLocationAllObjects[j][t]=new ArrayList<>();
			     		      tempLocationAllObjects[j][t].add(tempIndex);
			     	     }
			     	  
			    }//t
	        //}
	    }//j
	       
	    indexJ=inputObjectsNumber;
   	    indexT=slot+1;
   	    if(indexT!=T){
   	        inputObjectsNumber=inputObjectsNumber+workloadGenerator.numberObjectsPerSlot[indexT];
   	        }
	    }//slot	
	    
	 /*
	 if((T-MinT)==workloadGenerator.periodTime){
		 bcalculateCostEachObject();
	 }
	 */
	  
 }// end opt



// This function calculates cost for optimal algorithm. 
public void bcalculateCostEachObject() {
	
	for (int j = 0; j < J; j++) {
		
         // 2. Some objects is entered in system in time t>0. So before that the location of object is "-1" just only for a time slot. So this value "-1" is removed in order to have the location of object "j" during period "t>0... T-1".
    	while(finalLocationAllObjects[j][T-1].size()>T-workloadGenerator.objectStartTime[j] ){
    		finalLocationAllObjects[j][T-1].remove(0);
    	 }
    	while(tempLocationAllObjects[j][T-1].size()>T-workloadGenerator.objectStartTime[j] ){
    		tempLocationAllObjects[j][T-1].remove(0);
    	 }
		
     }
 
    for (int j = 0; j < J; j++) {
         
    	 int startTimeObject=workloadGenerator.objectStartTime[j];
         for (int t =startTimeObject ; t < T; t++) {
        	  
	          bCost[j][t].bstorageCost=totalCostCalculation.btotalResidentCost[t][j][finalLocationAllObjects[j][T-1].get(t-startTimeObject)].bstorageCost;
	          //System.out.println("Cost===>"+j+" "+" "+bCost[j][t]);
	          
	          bCost[j][t].breadCost=totalCostCalculation.btotalResidentCost[t][j][finalLocationAllObjects[j][T-1].get(t-startTimeObject)].breadCost;
	          bCost[j][t].bwriteCost=totalCostCalculation.btotalResidentCost[t][j][finalLocationAllObjects[j][T-1].get(t-startTimeObject)].bwriteCost;
	          bCost[j][t].btranCost=totalCostCalculation.btotalResidentCost[t][j][finalLocationAllObjects[j][T-1].get(t-startTimeObject)].btranCost;
	          bCost[j][t].bdelayCost=totalCostCalculation.btotalResidentCost[t][j][finalLocationAllObjects[j][T-1].get(t-startTimeObject)].bdelayCost;
	          bCost[j][t].bnonMigrationCost=totalCostCalculation.btotalResidentCost[t][j][finalLocationAllObjects[j][T-1].get(t-startTimeObject)].bnonMigrationCost;
	          
	          if(t>startTimeObject && finalLocationAllObjects[j][T-1].get(t-startTimeObject-1)!=finalLocationAllObjects[j][T-1].get(t-startTimeObject)){
		             bCost[j][t].bmigrationCost=totalCostCalculation.btotalMigrationCost[t-1][j][finalLocationAllObjects[j][T-1].get(t-startTimeObject-1)][finalLocationAllObjects[j][T-1].get(t-startTimeObject)];
	              }
            }
    	
      }//j
	
}



// This function receives the start 
 public int aggregateObjects(int mint) {
	 
	 int totalNumber=0;
	 if(mint!=0){
		 for (int t = 0; t < mint; t++) {
			totalNumber=totalNumber+workloadGenerator.numberObjectsPerSlot[t];
		}
	 }
	 
	return totalNumber;
 }
 
 
 @SuppressWarnings("unchecked")public void initialParameters(int miT, int maT) {
	MinT=miT;
	T=maT;
	J=workloadGenerator.numberObjects;//objectDatacenterSpecification.getNumberObject();// for test
	tierType=objectDatacenterSpecification.tierType;
	//pCost=new long [T][(int) J][tierType];
	
	bpCost=new BigInteger [T][(int) J][tierType];
	for (int t = 0; t < T; t++) {
		for (int j = 0; j < J; j++) {
			for (int tierT = 0; tierT < tierType; tierT++) {
				bpCost[t][j][tierT]=new BigInteger("0");
			}
		}
	}
	
	benchAlgorithm=new BenchmarkAlgorithm();
	//tCalculation=new totalCostCalculation();
	
    locationAllObjects=new ArrayList [T][(int)J][tierType];
	for (int t = 0; t < T; t++) {
		for (int j = 0; j < J; j++) {
			for (int tierT = 0; tierT < tierType; tierT++) {
				
				locationAllObjects[t][j][tierT]=new ArrayList<>();
				locationAllObjects[t][j][tierT].add(-1);
			}
		}
	}
	
	finalLocationAllObjects=new ArrayList [(int)J][T];
	tempLocationAllObjects=new ArrayList [(int)J][T];
	
	bCost=new bCostElements [(int)J][T];
	for (int j = 0; j < J; j++) {
		for (int t = 0; t < T; t++) {
			bCost[j][t]=new bCostElements();
		}
	}
	 
}	

public  void bwriteInFile( PrintWriter outFile1, PrintWriter outFile2) throws FileNotFoundException {
	
	
	BigInteger bwithMigSum[] = new BigInteger [(int)J];
	BigInteger bwithoutMigSum[] = new BigInteger [(int)J];
	
	BigInteger bSCost[]=new BigInteger [(int)J];
	BigInteger bRCost[]=new BigInteger [(int)J];
	BigInteger bWCost[]=new BigInteger [(int)J];
	BigInteger bTCost[]=new BigInteger [(int)J];
	BigInteger bCCost[]=new BigInteger [(int)J];
	BigInteger bMCost[]=new BigInteger [(int)J];
	long tSize[]=new long [(int)J];
	
	for (int j =0 ; j < J; j++) {
		 bwithMigSum[j]=new BigInteger("0");
		 bwithoutMigSum[j] =new BigInteger("0");
		 bSCost[j]=new BigInteger("0");
		 bRCost[j]=new BigInteger("0");
		 bWCost[j]=new BigInteger("0");
		 bTCost[j]=new BigInteger("0");
		 bCCost[j]=new BigInteger("0");
		 bMCost[j]=new BigInteger("0");
	  }
	
	
	outFile1.print("obj#"+","+"nonMCost"+","+"sCost"+","+"rCost"+","+"wCost"+","+"dCost"+","+"MCost"+","+"finallocation"+","+"tempLocation");
	outFile1.println();
	 for (int t = 0; t < T-MinT; t++) {
		 outFile1.println("t="+t+"  ");
    	
		 for (int j = 0; j < J; j++) {
			 
    		    //1. calculating all costs for storage, read, write, delay and migration for an object during all time slots. 
				bwithMigSum[j]=bwithMigSum[j].add(bCost[j][t].bnonMigrationCost).add(bCost[j][t].bmigrationCost);
				bwithoutMigSum[j]=bwithoutMigSum[j].add(bCost[j][t].bnonMigrationCost);
				
				//System.out.println("<===>"+j+"  "+t+"  "+bCost[j][t].btranCost);
				bSCost[j]=bSCost[j].add(bCost[j][t].bstorageCost);
    		    bRCost[j]=bRCost[j].add(bCost[j][t].breadCost); 
    		    bWCost[j]=bWCost[j].add(bCost[j][t].bwriteCost);
    		    //System.out.println("===>"+j+"  "+t+"  "+bCost[j][t].btranCost);
    		    bTCost[j]=bTCost[j].add(bCost[j][t].btranCost);
    		    
    		    bMCost[j]=bMCost[j].add(bCost[j][t].bmigrationCost);
    		    tSize[j]=tSize[j]+finalCostCalculation.newBucketSize[j][t];
    		    
    		     outFile1.print("j="+j+","+bCost[j][t].bnonMigrationCost+","+bCost[j][t].bstorageCost+","+bCost[j][t].breadCost+","+bCost[j][t].bwriteCost+","+
			                    bCost[j][t].bconsisCost+","+bCost[j][t].btranCost+","+bCost[j][t].bmigrationCost+",");
			     
    		     if(workloadGenerator.objectStartTime[j]<=t){
			              outFile1.println(  finalLocationAllObjects[j][T-1].get(t-workloadGenerator.objectStartTime[j])+","+
			    		                     tempLocationAllObjects[j][T-1].get(t-workloadGenerator.objectStartTime[j]));  
			     }
			     
			      
		       	
		   }//j
		outFile1.println();
	}//t
	 
	 
	 for (int j = 0; j < J; j++) {
		 benchAlgorithm=new BenchmarkAlgorithm();
		 
		 benchAlgorithm.bunoptimizedHotCost(j);
    	 BigDecimal bdHotCost=new BigDecimal(benchAlgorithm.getBHCost());
    	 
    	 benchAlgorithm.bunoptimizedColdCost(j);
    	 BigDecimal bdColdCost=new BigDecimal(benchAlgorithm.getBCCost()); 
    	 	 
		 BigDecimal bdwithMigSum=new BigDecimal(bwithMigSum[j]);
   	     BigDecimal bdwithoutMigSum=new BigDecimal(bwithoutMigSum[j]);
   	     
   	     BigDecimal bdSCost=new BigDecimal(bSCost[j]);
   	     BigDecimal bdRCost=new BigDecimal(bRCost[j]);
   	     BigDecimal bdWCost=new BigDecimal(bWCost[j]);
   	     BigDecimal bdTranCost=new BigDecimal( bTCost[j]);
   	     BigDecimal bdCCost=new BigDecimal(bCCost[j]);
   	     BigDecimal bdMCost=new BigDecimal(bMCost[j]);
   	     //System.out.println("=======>"+j+"   "+workloadGenerator.objectListRegion[j]);	 
		 outFile2.println("Opt"+","+workloadGenerator.experimentId+","+
		                   objectDatacenterSpecification.getDatacenterList().get(workloadGenerator.objectListRegion[j]).getName()+","
		                       
		    		            + -1+","
				           
				                +j+","+workloadGenerator.objectListRegion[j]+","+T+","+ optimizationCost.readToWrite+","
				           
				                +workloadGenerator.shape+","+workloadGenerator.scale+","+  +workloadGenerator.transactionNumberCoffecient+"," 
				             
	                            +tSize[j]+","+finalCostCalculation.sizefactor+","+workloadGenerator.objectMinSize+"_"+workloadGenerator.objectMaxSize+","
	                       
	                            +bwithMigSum[j]+","+bdwithMigSum.divide(bdHotCost, 4, RoundingMode.CEILING)+"," +bdwithMigSum.divide(bdColdCost, 4, RoundingMode.CEILING)+","
                       
                                +bwithoutMigSum[j]+","+bdwithoutMigSum.divide(bdHotCost, 4, RoundingMode.CEILING)+","+ bdwithoutMigSum.divide(bdColdCost, 4, RoundingMode.CEILING)+","
                       
                                +bdSCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdRCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                            
                                +bdWCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdTranCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","
                           
                               +bdCCost.divide(bdwithMigSum, 4, RoundingMode.CEILING)+","+bdMCost.divide(bdwithMigSum, 4, RoundingMode.CEILING));
   	     
	}
 }


public ArrayList<Integer>[][] getFinalLocationAllObjects() {
	return finalLocationAllObjects;
}


public void setFinalLocationAllObjects(
		ArrayList<Integer>[][] finalLocationAllObjects) {
	this.tempLocationAllObjects = finalLocationAllObjects;
}

}
