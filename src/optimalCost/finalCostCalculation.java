package optimalCost;



import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;

import workload.workloadGenerator;

public class finalCostCalculation {
	
	private int regNumber;
	private boolean flag;
	private ArrayList<NewDatacenter> datacenterList=new ArrayList<>();
	
	static long newBucketSize[][];// this variable changed its value based on writing size of object
	//public static ArrayList<NewDatacenter> combinationDatacenterList=new ArrayList<>();
	static long cons;//  If flag in data setting is true, then its value is 1 and somehow the price of transactions are ignored; Otherwise if flag is false, this value considered and its value is 100,000,000. 
	static double sizefactor; //If flag is true then its value is 1. In fact the size does not scale down. Otherwise, if the flag is false, then the size of object for read as well as for write can be scale down to arbitarey value (e.g., 0.1, 0.2)
	
	//NOTE: tierType with value 1 is hot tier and with value 0 is cold tier.
	
	
 //1. bStorage cost: 
  public BigInteger bstorageCost(int dcIndex, int tierType,int j,int t   ) {
	
	//System.out.println("dcIndex==>"+dcIndex);  
  	BigInteger CStorage=new BigInteger("0");
  	
  	if(tierType==1){
  		 
  		//System.out.println("size===>"+(newBucketSize[j][t]*sizefactor));
  		long v=(long)(datacenterList.get(dcIndex).getHStorageCost()*((double)(newBucketSize[j][t]*sizefactor)));
  		//System.out.println("storageCost in hot tier===>"+tierType+"  "+j+"  "+t+"  "+v);
  		
  		String sv=String.valueOf(v);
  		BigInteger bsv=new BigInteger(sv);
  		
  		String scons=String.valueOf(cons);
  		BigInteger bcons=new BigInteger(scons);
  		
  		CStorage=bsv.multiply(bcons);
  	}
  	else{
  		
  		
  		long v=(long)(datacenterList.get(dcIndex).getCStorageCost()*((double)(newBucketSize[j][t]*sizefactor)));
  		//System.out.println("storageCost in cold tier===>"+tierType+"  "+j+"  "+t+"  "+v);
  		String sv=String.valueOf(v);
  		BigInteger bsv=new BigInteger(sv);
  		
  		String scons=String.valueOf(cons);
  		BigInteger bcons=new BigInteger(scons);
  		
  		CStorage=bsv.multiply(bcons);
  		 
  	}
  	
  	return CStorage;
   }  
  

  
//2. bRead cost
 public BigInteger breadCost(int dcIndex, int tierType,  int j, int t) {
		
		BigInteger CRead=new BigInteger("0");
		int bPrice=0;
		if (tierType==1){
			bPrice=(int)datacenterList.get(dcIndex).getHbandwidthCost();
		}
		else{
			bPrice=(int)datacenterList.get(dcIndex).getCbandwidthCost();
		}
		
		if(workloadGenerator.objectListWriteRate[j][t]!=0){
			
			
			for (int reg = 0; reg < workloadGenerator.regioNumber; reg++) {
					
		  		if(workloadGenerator.objectListWriteRatePerRegion[j][t][reg]!=0){    
		  			
		  		    	  long v=optimizationCost.readToWrite*workloadGenerator.objectListSizePerRegion[j][t][reg]*bPrice;	
		  		    	  String sv=String.valueOf(v);
		  		  		  BigInteger bsv=new BigInteger(sv);
		  		    	
		  		  		  String scons=String.valueOf(cons);
		  		  		  BigInteger bcons=new BigInteger(scons);
		  		  		  
		  		  		  CRead=CRead.add(bsv.multiply(bcons));
		  		   }
		  		
		  	 }// reg
		}
		return CRead;
	}   
  


//3. bWrite Cost
public BigInteger bwriteCost( int indexPermu, int j, int t ) {
		 
	  BigInteger CWrite=new BigInteger("0");
	  int [] wRate=new int[regNumber];
	    
	if(workloadGenerator.objectListWriteRate[j][t]!=0){  
	  
		for (int reg = 0; reg < regNumber; reg++) {
			  wRate[reg]=workloadGenerator.objectListWriteRatePerRegion[j][t][reg];
		     
			  if(wRate[reg]!=0){  
		    	    
				        CWrite=BigInteger.valueOf(0);//CWrite+tCost(wRate[reg], combinationDatacenterList.get(des).getwriteCost(),flag); 
			      
		      }
		 }//reg
	}	  
	   return CWrite;
}
 
  
 
 
//4.1 bTransaction cost
public BigInteger btransactionCost(int dcIndex, int tierType, int j, int t ) {
	  BigInteger CTRW=new BigInteger("0");
	  BigInteger CTWrite=new BigInteger("0");
	  BigInteger CTRead=new BigInteger("0");
	  long [] wRate=new long[regNumber];
	  int rPrice=0;
      int wPrice=0;
	  if (tierType==1){
		  rPrice=(int)datacenterList.get(dcIndex).getHReadCost();
		  wPrice=(int)datacenterList.get(dcIndex).getHWriteCost();
	  }
	  else{
		  rPrice=(int)datacenterList.get(dcIndex).getCReadCost();
		  wPrice=(int)datacenterList.get(dcIndex).getCWriteCost();
	  }
   	  
	  
	  if(workloadGenerator.objectListWriteRate[j][t]!=0){  
	  
		for (int reg = 0; reg < regNumber; reg++) {
			  wRate[reg]=workloadGenerator.objectListWriteRatePerRegion[j][t][reg]*(workloadGenerator.transactionNumberCoffecient);
	          
		     
		      if(wRate[reg]!=0){  
		    	  		 CTWrite=CTWrite.add(btCost(wRate[reg], wPrice,flag)); // transaction cost of write
			    	     CTRead=CTRead.add(btCost(optimizationCost.readToWrite*wRate[reg], rPrice,flag));// transaction cost of read
			    	
		      }
		 }//reg
	  }
	   CTRW=CTWrite.add(CTRead);
	   return CTRW;
}



//4.2 It calculates Transaction Cost for each bunch of read and writes
public BigInteger btCost(long rate, long cost, boolean f) {
	
	BigInteger result=new BigInteger("0");
	BigInteger con=new BigInteger("100000000");// This number 10^8 is for converting the total price to real price. Because the value of "cost" is not for a operation. It is for 1000 or 10000 or 10^6 operations. 
	BigInteger r=  new BigInteger(Long.toString(rate));
    BigInteger c=  new BigInteger(Long.toString(cost));
   
	
   if (f==true) {
		result=(r.multiply(c)).divide(con);	
	}
	else{
		
		result=(r.multiply(c));	
	}
   return result;
	
} 

//5. bDelay Cost

public BigInteger bdelayCost(int dcIndex, int tierType ,int j, int t) {
	
	 BigInteger CDelay=new BigInteger("0");
	 
	 int RLatency=latencyWithinDatacenter(dcIndex, tierType, 0);
	 int WLatency=latencyWithinDatacenter(dcIndex, tierType, 1);
	 for (int reg = 0; reg < workloadGenerator.regioNumber; reg++) {
	  		if(workloadGenerator.objectListWriteRatePerRegion[j][t][reg]!=0){    
	  			int wRate=workloadGenerator.objectListWriteRatePerRegion[j][t][reg];
					  	
					 // It is considered for both read and write objects that transfered.
					 long v=((long)(objectDatacenterSpecification.latencySencetive*(wRate*WLatency+optimizationCost.readToWrite*wRate*RLatency)));
			  		
	  		    	  String sv=String.valueOf(v);
	  		  		  BigInteger bsv=new BigInteger(sv);
	  		    	
	  		  		  String scons=String.valueOf(cons);
	  		  		  BigInteger bcons=new BigInteger(scons);
	  		  		  
	  		  		  CDelay=CDelay.add(bsv.multiply(bcons));
	  		    	
	  		}
	  		
	  	 }// reg
	
  //System.out.println("index===>"+indexPermu+"  "+"J===>"+j+"  "+"t===>"+t+"  "+"CDelay===>"+CDelay);	
  return CDelay;
}  



//6. bMigration Cost
public BigInteger bmigrationCostCost(int dcIndex, int previousTier,int presentTier, int j, int t) {
  	   BigInteger CMigration=new BigInteger("0");
  	   
  	   if((previousTier==presentTier)|| previousTier==-1 || t< workloadGenerator.objectStartTime[j]){
			CMigration=BigInteger.valueOf(0);
		}
  	else{
		 	
  		 
  		if(presentTier-previousTier==-1){// Transfer from Hot to Cool tier 
				
				long v=datacenterList.get(dcIndex).getCWriteCost();
				String sv=String.valueOf(v);
		  		BigInteger bsv=new BigInteger(sv);
		    	
		  	    String scons=String.valueOf(cons);
		  		BigInteger bcons=new BigInteger(scons);
		  		
				CMigration=bsv.multiply(bcons);
				
			}
			else{// Transfer from Cool to Hot tier
				
				long v=(datacenterList.get(dcIndex).getCbandwidthCost()*(newBucketSize[j][t]))+
						datacenterList.get(dcIndex).getCReadCost();
				String sv=String.valueOf(v);
		  		BigInteger bsv=new BigInteger(sv);
		    	
		  	    String scons=String.valueOf(cons);
		  		BigInteger bcons=new BigInteger(scons);
		  		
				CMigration=bsv.multiply(bcons);
			}
		}
  	return CMigration;
  	
	}  



//7. bConsistency Cost: This function calculates consistency cost. This calculates only write transaction because input data to both tiers are free.
public BigInteger bconsistencyCost(int dcIndex, int tierType, int j, int t ) {
	  
	  BigInteger CConsistency=new BigInteger("0");
	  
	  long [] wRate=new long[regNumber];
	  
      int wPrice=0;
	  if (tierType==1){
		  wPrice=(int)datacenterList.get(dcIndex).getHWriteCost();
	  }
	  else{
		  wPrice=(int)datacenterList.get(dcIndex).getCWriteCost();
	  }
 	  
	  if(workloadGenerator.objectListWriteRate[j][t]!=0){  
	  
		for (int reg = 0; reg < regNumber; reg++) {
			  
			  wRate[reg]=workloadGenerator.objectListWriteRatePerRegion[j][t][reg]*(workloadGenerator.transactionNumberCoffecient);
	
		     
		      if(wRate[reg]!=0){  
		    	  		 CConsistency=CConsistency.add(btCost(wRate[reg], wPrice,flag)); // transaction cost of write
			  }
		 }//reg
	  }
	   return CConsistency;
}

// 8. Early Deletion Cost : This cost is applied when the object is kept for less than a certain days and is  transfered from the cool tier to the hot.
public BigInteger earlyDeletionCost(int dcIndex, int t, int tm) {
	BigInteger earlyDeletionCost =new BigInteger("0");
	
	if(t-tm < objectDatacenterSpecification.earlyDeletionDays){
		long v =(objectDatacenterSpecification.earlyDeletionDays-(t-tm))*(datacenterList.get(dcIndex).getCStorageCost());
		String sv=String.valueOf(v);
  		BigInteger bsv=new BigInteger(sv);
    	
  	    String scons=String.valueOf(cons);
  		BigInteger bcons=new BigInteger(scons);
  		
		earlyDeletionCost=bsv.multiply(bcons);
	}
	
	return earlyDeletionCost;
}

 public bCostElements btotalResidentCostPermuDatacenter( int dcIndex, int tier, int j,int t) throws FileNotFoundException {
     
     bCostElements result=new bCostElements();
	 BigInteger tCost=new BigInteger("0");
	 BigInteger sCost=new BigInteger("0");
	 BigInteger rCost=new BigInteger("0");
	 BigInteger wCost=new BigInteger("0");
	 BigInteger tranCost=new BigInteger("0");
	 BigInteger consisCost=new BigInteger("0");
	 BigInteger dCost=new BigInteger("0");
  	
  	if(t >= workloadGenerator.objectStartTime[j]){// 
  		   
  	 	   sCost=bstorageCost(dcIndex, tier,j, t);
  	 	   rCost=breadCost(dcIndex,tier, j, t);
  	 	   wCost=bwriteCost(dcIndex, j, t);
  	 	   tranCost=btransactionCost(dcIndex, tier, j, t);
  	 	   consisCost=bconsistencyCost(dcIndex, tier, j, t);
  	 	   tCost=tCost.add(sCost).add(rCost).add(wCost).add(tranCost);//.add(consisCost);
  	 	   dCost=bdelayCost(dcIndex, tier, j, t);
  	 	}
	    result.bstorageCost=sCost;
	    result.breadCost=rCost;
	    result.bwriteCost=wCost;
	    result.btranCost=tranCost;
	    result.bconsisCost=consisCost;
	    result.bdelayCost=dCost;
	   
	    result.bnonMigrationCost=tCost;
	    return result;
}

   
    // This function initialize parameters that we need in this class
	 public void initializeCostCalculationParameters() {
		
		  
		  regNumber=workloadGenerator.regioNumber;
		  datacenterList=new ArrayList<>();
		  datacenterList.addAll(objectDatacenterSpecification.getDatacenterList());
		  
		  
		  
		  newBucketSize=new long[workloadGenerator.numberObjects][workloadGenerator.periodTime];
		  
		  for (int j = 0; j < workloadGenerator.numberObjects; j++) {
			 for (int t = 0; t < workloadGenerator.periodTime; t++) {
			
				newBucketSize[j][t]=workloadGenerator.objectListSize[j][t];// This is the initial size of each bucket. This size is the number of Tweets per month multiples the size of each tweet (1,10,50 and 100)
			 }
		   }
		  
				
	   }
	
		
  public void bucketSizeIncrement() {
	    
	     for (int j = 0; j < workloadGenerator.numberObjects; j++) {
			 for (int t = 0; t < workloadGenerator.periodTime-1; t++) {
				long tempSize=0;
				for (int reg = 0; reg < regNumber; reg++) {
				   tempSize= workloadGenerator.objectListSizePerRegion[j][t][reg] *(optimizationCost.readToWrite)+tempSize;//(objectDatacenterSpecification.getObjectReadtoWriteRate()) );
				}
			    
				 newBucketSize[j][t+1]=newBucketSize[j][t]+tempSize;
		   }
	   }
	    /* 
	     System.out.println("bucket size");
	     for (int i = 0; i < newBucketSize.length; i++) {
		     for (int j = 0; j < newBucketSize[i].length; j++) {
			  System.out.print(newBucketSize[i][j]+"   ");
		 }
		 System.out.println();
	     }
	     */
     }
 
//This function retrieves the latency based on the tier type (Hot=0 and Cool=1, except here to make justify index), request type (Read=0 and Write=1), and object size (Small=1, medium=2, and large=3)
  public int latencyWithinDatacenter(int dcInde, int tierT, int requestType) {
	  
	  int latency=0;
	  latency=objectDatacenterSpecification.delayWithinDatacenter[dcInde][6*(1-tierT)+requestType+objectSizeType()];
	  return latency;
}   

 // This function determines the objectSizeType: 1:small object, 2:Medium object, and 3:large object 
 private int objectSizeType() {
	int sizeType=-1;
	int sizeRange=workloadGenerator.objectMaxSize-workloadGenerator.objectMinSize;
	if (sizeRange>=1 && sizeRange<=100){
		sizeType=1;
	}else if (sizeRange>100 && sizeRange<=1000){
		sizeType=2;
	}else{
		sizeType=3;
	}
	
	
	return sizeType;
}
 
 //This function calculates the maximum writing latency cost between two tiers and read latency cost in the hot tier.
 public BigInteger bdelayMaxWriteReadCost(int dcIndex, int tierType ,int j, int t) {
		
	 BigInteger CDelay=new BigInteger("0");
	 
	 int RLatency=latencyWithinDatacenter(dcIndex, tierType, 0);
	 //Calculate the maximum latency of writes between two tiers.
	 int WLatency=0;
	 if (latencyWithinDatacenter(dcIndex, 1, 1)<latencyWithinDatacenter(dcIndex, 0, 1)){
		WLatency=latencyWithinDatacenter(dcIndex, 1, 1);
	 }else{
		WLatency=latencyWithinDatacenter(dcIndex, 0, 1);
	 }
	 
	 for (int reg = 0; reg < workloadGenerator.regioNumber; reg++) {
	  		if(workloadGenerator.objectListWriteRatePerRegion[j][t][reg]!=0){    
	  			int wRate=workloadGenerator.objectListWriteRatePerRegion[j][t][reg];
					  	
					 // It is considered for both read and write objects that transfered.
					 long v=((long)(objectDatacenterSpecification.latencySencetive*(wRate*WLatency+optimizationCost.readToWrite*wRate*RLatency)));
			  		
	  		    	  String sv=String.valueOf(v);
	  		  		  BigInteger bsv=new BigInteger(sv);
	  		    	
	  		  		  String scons=String.valueOf(cons);
	  		  		  BigInteger bcons=new BigInteger(scons);
	  		  		  
	  		  		  CDelay=CDelay.add(bsv.multiply(bcons));
	  		    	
	  		}
	  		
	  	 }// reg
	
  //System.out.println("index===>"+indexPermu+"  "+"J===>"+j+"  "+"t===>"+t+"  "+"CDelay===>"+CDelay);	
  return CDelay;
}  

 
 
     
// This function determines whether to consider cost in very precise or not. If output is true, then we have all cost in dollars; otherwise we have cost in dollars multiplied 100,000,000.  
public void preciseCost(boolean f ,double sf) {
	
	if (f==true) {
		cons=1;
		flag=true;
		sizefactor=sf;
	}
	else{
		flag=false;
		cons=100000000;
		sizefactor=sf;//workloadGenerator.factorSize;//0.1;// This value can be changed. 
		
	}
	
}





public long[][] getNewBucketSize() {
	return newBucketSize;
}

public long getCons() {
	return cons;
}

public boolean isFlag() {
	return flag;
}



}
