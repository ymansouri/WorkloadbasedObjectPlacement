package workload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.cloudbus.intercloudsim.config.ICProperties;
import optimalCost.ZipfGenerator;



public class workloadGenerator {
	
	
	public static String experimentId;
	public static int experimentRun;
	public static int experimentRepetor;// the kth repeat of experiment. the maximum number is 30.
	public static int periodTime;
	public static File folderW= new File("./workload/");; 
	
	//public static long objectsTotalSize;
	public static double factorSize;
	public static int bucketSize;
	public static int objectMinSize;
	public static int objectMaxSize;
	public static long transactionNumberCoffecient;
	
	public static int numberObjects;
	public static int objectReadToWriteRate;
	public static ArrayList<StringBuffer> objectList;
	 
	
	public static int [][] objectListWriteRate;// It keeps number read rate for each object j in each time slot "t";
	public static int [][][] objectListWriteRatePerRegion;
	public static long [][] objectListSize;// It keeps size for each object j in each time slot "t"; Note: This size means the size of object that is read in time slot "t". It is different with size of object that is stored in storage. This size is specified by "bucket size" variable. 
	public static long [][][] objectListSizePerRegion;
	public static int [] objectListRegion;//It keeps the region of objects that is created in region "r".
	
	
	public static int regioNumber;
	public static double shape;
	public static double scale;
	
	private static int [] seed;
	public static int[] objectStartTime;
	public static int[] numberObjectsPerSlot;
	private  HashMap<String, ArrayList<String>> workloadInfo = new HashMap<String, ArrayList<String>>();
	
	
	
	public ArrayList<String> workkloadListsRead(){
		
		ArrayList<String> results=new ArrayList<String>();
		File[] listOfFiles = folderW.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			String stname=listOfFiles[i].getName();
			if(stname.equalsIgnoreCase(".DS_Store")){
				System.out.println("there is a unpropoer workload file"+ "  "+stname);
			}
			else{
			     results.add(listOfFiles[i].getPath());
			}
		}
		return results;
	}
	
	public  void workloadHash(String workloadAddress) {
		
		try {
			
			  BufferedReader inFile = new BufferedReader(new FileReader(workloadAddress));
			  String str;
		    int iteration=0;
			while ((str = inFile.readLine()) != null) {
				if(iteration == 0) {
				        iteration++;  
				        continue;
				}
				ArrayList<String> info=new ArrayList<String>();
				String[] words = str.split(",");
				for (int i = 1; i < words.length; i++) {
					     info.add(words[i]);
			     }
				    workloadInfo.put(words[0], info);
			}//while
			inFile.close();
			
		} 
		
		catch (Exception e) {
			if (e.getMessage()!=null) {
				System.out.println(e.getMessage());
			}
		}
		
		  
   }
  	//This function reads the specification of each object, while the below function reads the integrated workload. 
	public   void workloadRead() {
			
		  int objectWriteRate;
	      int [] tempWriteRate;
			
	      for (Map.Entry<String, ArrayList<String>> entry : workloadInfo.entrySet()) {	 
	              
	        	  int j=Integer.valueOf(entry.getKey());
	              tempWriteRate=new int[periodTime];
				  ArrayList<String> info=new ArrayList<String>();
				  info.addAll( entry.getValue());
				 
				 objectListRegion[j]=Integer.parseInt(info.get(0))-1;// user's region: minus one because the region number of each DC is one unit less than its ID#
				 
				 int tweetNumber=Integer.parseInt(info.get(2));// tweet number of each user in a month
				 
				// size of each object in each time slot
				 calculateSizeObjectInAllTimeslots(j,tweetNumber);
				 
				for (int reg = 0; reg < regioNumber; reg++) {// 
					
				   int fInReg=Integer.parseInt(info.get(reg+3));// the number of friends user in a region: We added 3, because the number of total requests starts from index 3 of "info" arraylist 
				   if(fInReg>0){
				       
					   //if we use workload based on userID not integrated workload
					  objectWriteRate=fInReg*tweetNumber;
					  System.arraycopy(objectReadrateInSlotTime(objectWriteRate), 0, tempWriteRate, 0, periodTime);
				       
			          for (int t = 0; t < periodTime; t++) {
			        	// write rate in each time slot
			    	     objectListWriteRatePerRegion[j][t][reg]=tempWriteRate[t];
			         	// size of each in each time slot
			    	   
			    	   if(tempWriteRate[t]==0){
			    		   
			    		   objectListSizePerRegion[j][t][reg]=0;
			    	    }
			        	else{
			    	         objectListSizePerRegion[j][t][reg]=calculateObjectSizeInEachTimeSlot(tempWriteRate[t]);
			        	}
			    	     
				       }//t
				    }
				}//reg
				
			}// key
	     }

	
  private  void calculateSizeObjectInAllTimeslots(int j, int tweetNumber) {
		
	  for (int t = 0; t < periodTime; t++) {
	    objectListSize[j][t]=calculateObjectSizeInEachTimeSlot( tweetNumber);
	    objectListWriteRate[j][t]=Integer.valueOf(tweetNumber);//tempReadRate[t]; It keeps number of tweets for each user. If this value is zero (e.g.., DC#"8" in workload file), then we do not consider to calculate storage, read and write costs.
	  }
	  
	}  


	
   // We write four functions for read distribution: weibull, Zip, Normal, and random.
	public  int[] objectReadrateInSlotTime(int rRate) {//Weibull Distribution
		
		int [] readRatePerSlotTime=new int[periodTime];
		RandomGenerator rng=new JDKRandomGenerator();
		rng.setSeed(seed[experimentRepetor]);
		WeibullDistribution wd=new WeibullDistribution(rng, shape, scale);
	    
		double t=0;// This variable shows in which time "t" the probability of distribution tends to zero.
		double cdf = 0;
		while (Math.abs(cdf-1)>0.00001) {
			cdf=wd.cumulativeProbability(t);
			t+=0.1;
		}
		
		for (int i = 0; i < rRate; i++) {
			double sample=wd.sample();
			if (sample>t) {
				i--;
				continue;
			}
			readRatePerSlotTime[(int)Math.floor(sample * ((periodTime-1)/(double) t ))]++;
		}
			return readRatePerSlotTime;
	}
	

    public  int[] zobjectReadrateInSlotTime(int rRate) {//ZipfBased
		
	     Random rng=new Random();
	    rng.setSeed(seed[experimentRepetor]);
		     
		    ZipfGenerator z=new ZipfGenerator(periodTime, shape, rng);// shape: mean
		    int [] readRatePerSlotTime=Arrays.copyOf(z.getRankArray(rRate), z.getRankArray(rRate).length);
		    int sum=0;
		    for (int i = 0; i < periodTime; i++) {
		    	sum=sum+readRatePerSlotTime[i];
				
			}
		    //System.out.println("sum===>"+sum);
		return readRatePerSlotTime;
	 }
	
	
 // This function generates read rate in each time slot to each object based on normal distribution. objectReadrateInSlotTimeNormalBased
     public  int[] nobjectReadrateInSlotTime(int rRate) {//normal distribution
		
    	System.out.println("it is in normal Distribution read access");
		int [] readRPT=new int[periodTime];
		ArrayList<Integer> readRPTemp=new ArrayList<Integer>();
		
		ArrayList<Integer> list=new ArrayList<Integer>();
		ArrayList<Integer> tempList=new ArrayList<Integer>();
		
		RandomGenerator rng=new JDKRandomGenerator();
		rng.setSeed(seed[experimentRepetor]);
		NormalDistribution wd=new NormalDistribution(rng, shape, scale); // shape:mean, scale: sd
	    
		
		double t=0;// This variable shows in which time "t" the probability of distribution tends to zero.
		double cdf = 0;
		while (Math.abs(cdf-1)>0.00001) {
			cdf=wd.cumulativeProbability(t);
			t+=0.1;
		}
		
		for (int i = 0; i < rRate; i++) {
			double sample=wd.sample();
			if (sample>t) {
				i--;
				continue;
			}
			
			int index=(int)Math.floor(sample * ((periodTime-1)/(double) t ));
			
			list.add(index);
		}
		 
		Collections.sort(list);
		
		for (int i = 0; i < list.size(); i++) {
			
			int temp=list.get(i);
			if(containElementInList(tempList, temp)==false){
			    readRPTemp.add(Collections.frequency(list, temp));
			    tempList.add(temp);
			}
		}
		
		//1.
		if(readRPTemp.size()>periodTime){
		    
			int additional=readRPTemp.size()-periodTime;
			if(additional%2==0){
				
				// the middle of arraylist
				int indexA=0;
				for (int i = additional/2; i < readRPTemp.size()-(additional/2); i++) {
					readRPT[indexA]=readRPTemp.get(i);
					indexA++;
				}
				
				//right side
				for (int i = 0; i < additional/2; i++) {
					readRPT[i]=readRPT[i]+readRPTemp.get(i);
				}
				
				// left side
				int indexls=0;
				for (int i = periodTime+(additional/2); i < readRPTemp.size(); i++) {
					readRPT[periodTime-(additional/2)+indexls]=readRPT[periodTime-(additional/2)+indexls]+readRPTemp.get(i);
					indexls++;
				}	
			}
			else{
				
				int indexA=0;
				for (int i = (additional/2); i < (additional/2)+ periodTime; i++) {
					readRPT[indexA]=readRPTemp.get(i);
					indexA++;
				}
				
				//left side
				for (int i = 0; i < (additional/2); i++) {
					readRPT[i]=readRPT[i]+readRPTemp.get(i);
				}
				
				// right side
				int indexr=0;
				for (int i = periodTime+((additional/2)); i < readRPTemp.size(); i++) {
					readRPT[(periodTime-1)-(additional/2)+indexr]=readRPT[(periodTime-1)-(additional/2)+indexr]+readRPTemp.get(i);
					indexr++;
				}
			}
		}
	   //2.	
       if(readRPTemp.size()<periodTime){// it is ok
    	    
			int additional=periodTime-readRPTemp.size();
			if(additional%2==0){
				for (int i = 0; i < readRPTemp.size(); i++) {
					readRPT[additional/2+i]=readRPTemp.get(i);
				}
			}
			else{
				for (int i = 0; i < readRPTemp.size(); i++) {
					readRPT[(additional/2)+i]=readRPTemp.get(i);
				}
			}
		}
		//3.
		if(readRPTemp.size()==periodTime){
			for (int i = 0; i < readRPTemp.size(); i++) {
			  readRPT[i]=readRPTemp.get(i);	
			}
		}
       
	  	return readRPT;
	}

   // This function shows that whether an element exists in list.	
	public  boolean containElementInList(ArrayList<Integer> list, int temp) {
		boolean result=false;
		//ArrayList<Integer> result=new ArrayList<Integer>();
		
		for (int i = 0; i < list.size(); i++) {
			
			if(list.get(i)==temp){
				result=true;
				break;
			}
			
		}
		
		return result;
		
	}
	
	
	
	// This function generates the random number of read on each object such the total number of read equals with total reads. objectReadrateInSlotTimeRandomBased
    public  int[] robjectReadrateInSlotTime(int rRate) {//Random
		
    	System.out.println("it is in random Distribution read access");
    	int [] readRPT=new int[periodTime];
	    int avg=(int)Math.ceil((double)rRate/periodTime);
	    int  sum = 0;
		RandomGenerator rng=new JDKRandomGenerator();
		rng.setSeed(seed[experimentRepetor]);
		for (int i = 0; i < readRPT.length; i++) {
        		  if (sum<=rRate) {
        		    readRPT[i]=rng.nextInt(2*avg);
        		    sum=sum+readRPT[i];
        		  }
    	}
	
	if(sum>rRate){
		
		int additional=sum-rRate;
		int index=0;
		while(additional!=0){
			  if(readRPT[index]>0){
				readRPT[index]=readRPT[index]-1;
				additional--;
				if(index<periodTime-1){
					index++;	
				}
				else{
					index=0;
				}
				
			  }	
			  else{
				 
				  if(index<periodTime-1){
						index++;	
					}
					else{
						index=0;
					}
				 
			  }
			
		}
		
	}
	
	if(sum<rRate){
		
		int additional=rRate-sum;
		int index=0;
		while(additional!=0){
			
				readRPT[index]=readRPT[index]+1;
				additional--;
				if(index<periodTime-1){
					index++;	
				}
				else{
					index=0;
				}
		}
		
	}
		return readRPT;
	}
	
  	
	
  // This function calculates the  size of object in each time slot. In this function, in each read operation, an object between 10 k -100k is read. 
	public  long calculateObjectSizeInEachTimeSlot(int rRate) {
		
		int minObjectSize=objectMinSize;
		int maxObjectSize=objectMaxSize;
		int tempSize=0;
		Random rd=new Random(seed[experimentRepetor]);
		for (int r = 0; r < rRate; r++) {
			if(maxObjectSize==minObjectSize){
			    tempSize=minObjectSize+tempSize;
			}
			else{
				tempSize=(rd.nextInt(maxObjectSize-minObjectSize) + minObjectSize)+tempSize;
			}
		}
		//System.out.println("j===>"+j+"  "+"t===>"+t+"  "+"rRate===>"+rRate+"  "+"size===>"+tempSize + "Seed " + seed[experimentRepetor] + "Temp " + tempSize);
		return tempSize;
	}
	
	
	// 
	public  void workloadParametersInitialization() {
		
		experimentId=ICProperties.IC_EXPERIMENT_ID.getValue();
		experimentRun=ICProperties.IC_EXPERIMENT_RUN.getValueAsInt();
		// We use the fixed number for the below variable, because we want to have the fixed workload across time for each 2-combination DCs. Otherwise, we have different workload for each 2-combination DCs.
		experimentRepetor=1;//optimizationCost.repeat;
		periodTime=ICProperties.IC_SIMULATION_PERIOD_TIME.getValueAsInt();
		bucketSize=ICProperties.IC_SIMULATION_BUCKET_SIZE.getValueAsInt();
		numberObjects=workloadInfo.size();// the object size is the records number in a file.
		
		numberObjectsPerSlot=new int[periodTime];
		for (int t = 1; t < periodTime; t++) {
			numberObjectsPerSlot[0]=numberObjects;
			numberObjectsPerSlot[t]=0;
		}
		
		objectMinSize=ICProperties.IC_SIMULATION_OBJECT_MIN_SIZE.getValueAsInt();
		objectMaxSize=ICProperties.IC_SIMULATION_OBJECT_MAX_SIZE.getValueAsInt();
		transactionNumberCoffecient=ICProperties.IC_SIMULATION_TRANSACTION_NUMBER_COEFFICIENT.getValueAsLong();
		shape=ICProperties.IC_SIMULATION_WEIBULL_SHAPE.getValueAsDouble();
		scale=ICProperties.IC_SIMULATION_WEIBULL_SCALE.getValueAsDouble();
		regioNumber=ICProperties.IC_SIMULATION_REGION_NUMBER.getValueAsInt();
		objectList=new ArrayList<>();
		objectListWriteRate=new int[numberObjects][periodTime];
		objectListWriteRatePerRegion=new int[numberObjects][periodTime][regioNumber];
		
		objectListSize=new long [numberObjects][periodTime];
		objectListSizePerRegion=new long[numberObjects][periodTime][regioNumber];
		objectListRegion=new int[numberObjects];
		objectStartTime=new int[numberObjects];
		seed=new int [experimentRun];
		seedInitialize();
		
	}
	
	
	public static void seedInitialize() {
		
		for (int i = 0; i < seed.length; i++) {
			seed[i]=i+1;
			
		}
		
	}
	
	// It separates the file with name (that is , e.g., 1.1 ) form its address of workload
	public String parsName(String address){
		
		String result="";
		// separate for "/"
		String sf[]= address.split("/");
		// separate for "."
		String ss[]=sf[2].split("[.]");
		result=ss[0];
		
		return result;
	}
	
		
}
