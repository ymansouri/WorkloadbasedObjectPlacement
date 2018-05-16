package optimalCost;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class NewDatacenter extends Datacenter {

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @throws Exception
	 */

	private NewDatacenterCharactheristics chartrs;

	// constructor
	public NewDatacenter(String arg0, NewDatacenterCharactheristics arg1, VmAllocationPolicy arg2, List<Storage> arg3,
			double arg4) throws Exception {

		super(arg0, arg1, arg2, arg3, arg4);
		chartrs = arg1;

	}

	// This function get array list of id objects in each datacenter
	public ArrayList<Integer> getObjectsId() {
		ArrayList<NewObject> temp = new ArrayList<>();
		ArrayList<Integer> objectId = new ArrayList<>();
		temp = chartrs.getDatacenterObjects();
		if (temp.size() != 0) {
			for (int i = 0; i < temp.size(); i++) {
				objectId.add(temp.get(i).getIdobject());

			}
		}
		return objectId;

	}

	// This function get array list of name objects in each datacenter
	public ArrayList<String> getObjectsName() {
		ArrayList<NewObject> temp = new ArrayList<>();
		ArrayList<String> objectName = new ArrayList<>();
		temp = chartrs.getDatacenterObjects();
		if (temp.size() != 0) {
			for (int i = 0; i < temp.size(); i++) {
				// objectId.add(temp.get(i).getIdobject());
				objectName.add(temp.get(i).getName());
			}
		}
		return objectName;

	}

	public int getIdentification() {
		int identification;
		identification = chartrs.getIdentification();
		return identification;
	}

	public int getRegionNumber() {
		int regionNumber;
		regionNumber = chartrs.getRegionNumber();
		return regionNumber;
	}

	public long getHStorageCost() {
		long HStorageCost = 0;
		HStorageCost = chartrs.getHStorageCost();

		return HStorageCost;
	}

	public long getCStorageCost() {
		long CStorageCost = 0;
		CStorageCost = chartrs.getCStorageCost();
		return CStorageCost;
	}

	public long getHbandwidthCost() {
		long HBandwidthCost = 0;
		HBandwidthCost = chartrs.getHBandwidthCost();
		return HBandwidthCost;
	}

	public long getCbandwidthCost() {
		long CBandwidthRCost = 0;
		CBandwidthRCost = chartrs.getCBandwidthCost();
		return CBandwidthRCost;
	}

	public long getHReadCost() {
		long HReadCost = 0;
		HReadCost = chartrs.getHReadCost();
		return HReadCost;
	}

	public long getCReadCost() {
		long CReadCost = 0;
		CReadCost = chartrs.getCReadCost();
		return CReadCost;
	}

	public long getHWriteCost() {
		long HWriteCost = 0;
		HWriteCost = chartrs.getHWriteCost();
		return HWriteCost;
	}

	public long getCWriteCost() {
		long CWriteCost = 0;
		CWriteCost = chartrs.getCWriteCost();
		return CWriteCost;
	}

	public double getfailure() {

		double failure;
		failure = chartrs.getFailure();
		return failure;
	}

	public int getAvailability() {
		int availability = 0;
		availability = (int) -(Math.log10(chartrs.getFailure()));
		return availability;
	}

	@Override
	public String toString() {
		return "NewDatacenter [chartrs=" + chartrs + "]";
	}

	public NewDatacenterCharactheristics getChartrs() {
		return chartrs;
	}

	public void setChartrs(NewDatacenterCharactheristics chartrs) {
		this.chartrs = chartrs;
	}

	public void setDatacenterObjects(ArrayList<NewObject> datacenterObjects) {
		chartrs.setDatacenterObjects(datacenterObjects);
	}

	public void deleteObjectsInDatacenter() {
		chartrs.deleteDatacenterObject();
	}

	public void addDatacenterObject(NewObject obj) {
		chartrs.addDatacenterObject(obj);
	}

}
