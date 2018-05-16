package optimalCost;

import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.ParameterException;




// This clas describe objects with a key(id) that is extended of file in cloud sim
public class NewObject extends File {
	
	private int idobject;
    private double objectAvailability;
    private NewDatacenter mapDatacenter[][];// this array show that each split and replication of object i in which datacenter is located
	
	/**
	 * @param file
	 * @param idobject
	 * @throws ParameterException
	 */
	public NewObject(String fileName, int fileSize, int idobject) throws ParameterException {
		super(fileName,fileSize);
		this.idobject = idobject;
	}

	
	
	public int getIdobject() {
		return idobject;
	}

	public void setIdobject(int idobject) {
		this.idobject = idobject;
	}
	public double getObjectAvailability() {
		return objectAvailability;
	}

	public void setObjectAvailability(double objectAvailability) {
		this.objectAvailability = objectAvailability;
	}

	public NewDatacenter[][] getMapDatacenter() {
		return mapDatacenter;
	}

	public void setMapDatacenter(NewDatacenter[][] mapDatacenter) {
		this.mapDatacenter = mapDatacenter;
	}


	
	
   
	@Override
	public String toString() {
		return "NewObject [idobject=" + idobject + ", toString()="
				+ super.toString() + "]";
	}
	
	
	
	
	
	
	

}
