package optimalCost;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;

/**
 * @author yaser
 *
 */
public class NewDatacenterCharactheristics extends DatacenterCharacteristics {
	
	/**
	 * @param architecture
	 * @param os
	 * @param vmm
	 * @param hostList
	 * @param timeZone
	 * @param costPerSec
	 * @param costPerMem
	 * @param costPerStorage
	 * @param costPerBw
	 * @param failure
	 * @param availability
	 * @param HStorageCost
	 * @param datacenterObjects
	 * 
	 */
	
	//properties of NewDataCenter: H stands for Hot tier and  C stands for cold tier.
	private int regionNumber;
	private int identification;
    private double failure;
    private int availability;
    private long HStorageCost;
    private long CStorageCost;
    private long HBandwidthCost;
    private long CBandwidthCost;// This bandwidth cost is provides by some providers (for example Amazon)to transfer the data with the lower cost.  
    private long HReadCost;
    private long HWriteCost;
    private long CReadCost;
    private long CWriteCost;
    
    private ArrayList<NewObject> datacenterObjects;// This variable specifies which objects are in each datacenter 
	// constructor 
	public NewDatacenterCharactheristics(String architecture, String os,
			String vmm, List<? extends Host> hostList, double timeZone,
			double costPerSec, double costPerMem, double costPerStorage,
			double costPerBw,int regionNumber, int idenntification, double failure, long HStorageCost, long CStorageCost,long HbandwidthCost,long CBandwidthCost,
			long HReadCost,long CReadCost, long HWriteCost, long CWriteCost) {
		super(architecture, os, vmm, hostList, timeZone, costPerSec,
				costPerMem, costPerStorage, costPerBw);
		this.regionNumber=regionNumber;
		this.identification=idenntification;
		this.failure = failure;
		this.HStorageCost=HStorageCost;
		this.CStorageCost=CStorageCost;//Reduced storage with less storage cost
		this.HBandwidthCost=HbandwidthCost;
		this.CBandwidthCost=CBandwidthCost;
		this.HReadCost=HReadCost;
		this.HWriteCost=HWriteCost;
		this.CReadCost=CReadCost;
		this.CWriteCost=CWriteCost;
	}
	
	
  
    // getter and setter
    
	public long getHStorageCost() {
		return HStorageCost;
	}



	public void setHStorageCost(long hStorageCost) {
		HStorageCost = hStorageCost;
	}



	public long getCStorageCost() {
		return CStorageCost;
	}



	public void setCStorageCost(long cStorageCost) {
		CStorageCost = cStorageCost;
	}



	public long getHBandwidthCost() {
		return HBandwidthCost;
	}



	public void setHBandwidthCost(long hBandwidthCost) {
		HBandwidthCost = hBandwidthCost;
	}



	public long getCBandwidthCost() {
		return CBandwidthCost;
	}



	public void setCBandwidthCost(long cBandwidthCost) {
		CBandwidthCost = cBandwidthCost;
	}



	public long getHReadCost() {
		return HReadCost;
	}



	public void setHReadCost(long hReadCost) {
		HReadCost = hReadCost;
	}



	


	public long getCReadCost() {
		return CReadCost;
	}



	public void setCReadCost(long cReadCost) {
		CReadCost = cReadCost;
	}



	public long getCWriteCost() {
		return CWriteCost;
	}



	public void setCWriteCost(long cWriteCost) {
		CWriteCost = cWriteCost;
	}



	/**
	 * @return the failure
	 */
	public double getFailure() {
		return failure;
	}

	/**
	 * @param failure the failure to set
	 */
	public void setFailure(double failure) {
		this.failure = failure;
	}

	/**
	 * @return the availability
	 */
	
	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}


	public ArrayList<NewObject> getDatacenterObjects() {
		return datacenterObjects;
	}


	public void setDatacenterObjects(ArrayList<NewObject> datacenterObjects) {
		this.datacenterObjects = datacenterObjects;
	}

	
	public void addDatacenterObject(NewObject obj) {
		this.datacenterObjects.add(obj);
	}
	
   public void deleteDatacenterObject() {
    this.datacenterObjects.clear();
    
}

public int getRegionNumber() {
	return regionNumber;
}


public int getIdentification() {
	return identification;
}



public void setIdentification(int identification) {
	this.identification = identification;
}



public long getHWriteCost() {
	return HWriteCost;
}



public void setHWriteCost(long rWriteCost) {
	HWriteCost = rWriteCost;
}



public void setRegionNumber(int regionNumber) {
	this.regionNumber = regionNumber;
}
	
}
