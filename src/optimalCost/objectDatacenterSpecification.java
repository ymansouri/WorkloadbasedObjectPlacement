package optimalCost;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.intercloudsim.config.ICProperties;



public class objectDatacenterSpecification {

	public static List<NewDatacenter> datacenterList=new ArrayList<>();
	public static ArrayList<NewObject> objectList=new ArrayList<>();
	public static int [][] delayWithinDatacenter;
	public static int periodTime;
	public static int windowTime;
	public static int delayConstraint;
	public static long ninesNumber;
	
	public static int replicaNumber;
	public static double latencySencetive;
	public static String dataCenterSpecification;
	public static String datacenterDelay; 
	public static int tierType;
	public static int earlyDeletionDays;
	
   
	
	// create a datacenter
	public static NewDatacenter CreateDataCenter(String name,int reg,int id, double fail,
			long hSCost,long cSCost,long hBCost ,long cBCost,long HRCost, long CRCost, long HWCost, long CWCost) {
		
		// we first need some steps to create datacenter
		// 1.we need a list to store a list of our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. a machine contains one or more PEs or CPU/cores
		List<Pe> peList = new ArrayList<Pe>();
		int mips = 1000;
		// 3. create a PEs and add these into a list
		peList.add(new Pe(0, new PeProvisionerSimple(mips)));
		// 4. create a host with its id and list of PEs and add them to the list
		// of machines
		int hostid = 0;
		int ram = 2048;
		long storage = 100000;
		int bw = 10000;
		
		hostList.add(new Host(hostid, new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw), storage, peList,
				new VmSchedulerTimeShared(peList)));

		// this is a machine

		// specification of NewDatacenter
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located

		double costPerMem = 0; // the cost of using memory in this resource
		double costPerStorage = 24; // the cost of using storage per object
									// resource
		double costPerSec = 0;
		double costPerBw = 0.0; // the cost of using bw in this resource
		
		// we are not adding SAN devices by now
		LinkedList<Storage> storageList = new LinkedList<Storage>();
		// we added these fields
		int regionNumber=reg;
		int identification=id;
		double failure = fail;
		long HStorageCost = hSCost;
		long CStorageCost=cSCost;
		long HBandwidthCost=hBCost;
		long CBandwidthCost=cBCost;
		long HReadCost=HRCost;
		long CReadCost=CRCost;
		long HWriteCost=HWCost;
		long CWriteCost=CWCost;
		
		
		NewDatacenterCharactheristics charachtheristic = new NewDatacenterCharactheristics(
				arch, os, vmm, hostList, time_zone, costPerSec, costPerMem,
				costPerStorage, costPerBw,regionNumber,identification, failure, HStorageCost,CStorageCost,HBandwidthCost,CBandwidthCost,
				HReadCost, CReadCost, HWriteCost, CWriteCost);

		// this step DataCenter is created
		NewDatacenter datacenter = null;
		try {
			datacenter = new NewDatacenter(name, charachtheristic,
					new VmAllocationPolicySimple(hostList), storageList, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	
	// create objects
		public static NewObject createObject(String objName, int objSize, int idObj) {
		NewObject object=null;
		
		try {
			 object=new NewObject(objName,objSize, idObj);
			} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	   }
	
	
				
	//readAll Data center
	public static void readAllDatacenter() {
		
		ArrayList<String> strArrayList=new ArrayList<>();
		strArrayList.addAll(readInput(dataCenterSpecification));
		String[] results=null;		
		
		for (int i = 0; i < strArrayList.size(); i++) {
		
			String str=strArrayList.get(i);
		    results = str.split(",");
		    NewDatacenter datacenter=CreateDataCenter(results[0],Integer.parseInt(results[1]),Integer.parseInt(results[2]) ,Double.parseDouble(results[3]),
		    		                Long.parseLong(results[4]), Long.parseLong(results[5]),Long.parseLong(results[6]),Long.parseLong(results[7]),
		    		                Long.parseLong(results[8]),Long.parseLong(results[9]), Long.parseLong(results[10]), Long.parseLong(results[11]));
		   
		    datacenterList.add(datacenter); 
		   
		}
		
	}// Create all data centers
  
	
   	
	//This function reads latency within data centers with two tiers. The record for latency is classified based on tiers (hot and cool), requests (read and write), and object size (small, medium, and large).  
	public static void readDelayWithinDatacentersTiers() {
		
		delayWithinDatacenter=new int[datacenterList.size()][datacenterList.size()];
		
		ArrayList<String> strArrayList=new ArrayList<>();
		strArrayList.addAll(readInput(datacenterDelay));
		String[] results=null;		
		int firstIndex=0;
		for (int row = 0; row < delayWithinDatacenter.length; row++) {
			
			int index=1;
			for (int col = 0; col < delayWithinDatacenter[row].length; col++) {
				
				String str=strArrayList.get(firstIndex);
				//System.out.println("str====>"+str);
			    results = str.split(",");
                delayWithinDatacenter[row][col]=Integer.parseInt(results[index]);
			    index++;
			}
			firstIndex++;
			
		}	 
		
  }
    public static void readSpecificationObjectWorkload() {
		
	     dataCenterSpecification=ICProperties.IC_SIMULATION_DATACENTER_SPECIFICATION.getValue();
	     //datacenterDelay=ICProperties.IC_SIMULATION_DATACENTER_DELAY.getValue();
	     //delayConstraint=ICProperties.IC_SIMULATION_DELAY_CONSTRAINT.getValueAsInt();
	     latencySencetive=ICProperties.IC_SIMULATION_LATENCY_SENCETIVE.getValueAsDouble();
	     tierType=ICProperties.IC_SIMULATION_STORAGE_TIER_NUMBER.getValueAsInt();
	     
	      		
	}
	

	//This function read text from file
 public static ArrayList<String> readInput( String fileRead) {
		ArrayList<String> result=new ArrayList<>();
		try {
			Scanner inFile=new Scanner(new FileReader(fileRead));
			String str;
			System.out.println("read from file");
			str=inFile.next();
			
			result.add(str);
			
		    while (str!=null) {
				str=inFile.next();
				result.add(str);
			}
			inFile.close();
		} catch (Exception e) {
			if (e.getMessage()!=null) {
				System.out.println(e.getMessage());
			}
		}
		//System.out.println("str in read file===>"+result);
		return result;
	}// readInput

	// print NewDatacenterList
	public static void printDatacenterListSpecification() {

		System.out.println("specification datacenter in simulation:");
		System.out.println("number of datacenter:"+"   "+datacenterList.size());
		System.out.println("data center name"+"  "+"reg#"+"  "+"id"+"   "+"failure"+"  "+"hsCost"+"  "+"csCost"+"  "+"hbCost"+ "  "+"cbCost"+ "  "+"hRCost"+"  "+"cRCost"+"  "+"hwCost"+"   "+"cwCost");
		
		for (int i = 0; i < datacenterList.size(); i++) {
			System.out.println("data_center===>"+datacenterList.get(i).getName()+"  "+"  "+datacenterList.get(i).getRegionNumber()+"  "
		   +datacenterList.get(i).getIdentification()+"  " +datacenterList.get(i).getfailure()+"  "
		    +datacenterList.get(i).getHStorageCost()+"  "+datacenterList.get(i).getCStorageCost()+"   "
		    +datacenterList.get(i).getHbandwidthCost()+" "+datacenterList.get(i).getCbandwidthCost()
		    +"   "+datacenterList.get(i).getHReadCost()+"  "+datacenterList.get(i).getCReadCost()+
		    "  "+datacenterList.get(i).getHWriteCost()+"  "+datacenterList.get(i).getCWriteCost());
		}
		
	 }

	
	
	// This function return two DCs in regards to variable "rep"; while the below function return list of all DC in config File.
	public static List<NewDatacenter> getDatacenterList() {
		return datacenterList;
	}
	// This list contains all DCs in simulation we have: That is 22 DCs
	public static List<NewDatacenter> getDatacenterListAll() {
		return datacenterList;
	}
	
	public static ArrayList<NewObject> getObjectList() {
		return objectList;
	}
	public static void setObjectList(ArrayList<NewObject> objectList) {
		objectDatacenterSpecification.objectList = objectList;
	}
	
	public static int getReplicaNumber() {
		return replicaNumber;
	}

	public static void setReplicaNumber(int replicaNumber) {
		objectDatacenterSpecification.replicaNumber = replicaNumber;
	}
	public static int getPeriodTime() {
		return periodTime;
	}
	public static void setPeriodTime(int periodTime) {
		objectDatacenterSpecification.periodTime = periodTime;
	}
	public static int[][] getDelayBetweenDatacenterAll() {
		return delayWithinDatacenter;
	}
	
	
	public static void setDelayBetweenDatacenter(int[][] delayBetweenDatacenter) {
		objectDatacenterSpecification.delayWithinDatacenter = delayBetweenDatacenter;
	}
	public static int getDelayConstraint() {
		return delayConstraint;
	}
	public static void setDelayConstraint(int delayConstraint) {
		objectDatacenterSpecification.delayConstraint = delayConstraint;
	}
	public static long getNinesNumber() {
		return ninesNumber;
	}

	public static void setNinesNumber(long ninesNumber) {
		objectDatacenterSpecification.ninesNumber = ninesNumber;
	}
	public static int getWindowTime() {
		return windowTime;
	}
	public static void setWindowTime(int windowTime) {
		objectDatacenterSpecification.windowTime = windowTime;
	}
	
	public static int getEarlyDeletionDays() {
		return earlyDeletionDays;
	}

	public static void setEarlyDeletionDays(int earlyDeletionDays) {
		objectDatacenterSpecification.earlyDeletionDays = earlyDeletionDays;
	}

		
	}
