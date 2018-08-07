package optimalCost;

import java.math.BigInteger;

public class BenchmarkAlgorithm {
	
	finalCostCalculation finalCostCal=new finalCostCalculation();
	
	private BigInteger bHCost;
	private BigInteger bCCost;
	
	
	
//This function calculates the unoptimized cost for storage, read, write and delay. This is a benchmark in which an object when initially creates in a DC, it remains in that storage for all times.
public void bunoptimizedHotCost(int j) {
			//System.out.println("BENCHMARK COST=========>");
		    bHCost=new BigInteger("0");
			BigInteger bHStorageCost=new BigInteger("0");
			BigInteger bHReadCost=new BigInteger("0");
			BigInteger bHWriteCost=new BigInteger("0");
			BigInteger bHTransactionCost=new BigInteger("0");
			BigInteger bHDelayCost=new BigInteger("0");
			
			    for (int time = 0; time< optimizationCost.T ; time++) {
			       	
			       bHStorageCost=bHStorageCost.add(totalCostCalculation.btotalResidentCost[time][j][1].bstorageCost);
			       bHReadCost=bHReadCost.add(totalCostCalculation.btotalResidentCost[time][j][1].breadCost);
			       bHWriteCost=bHWriteCost.add(totalCostCalculation.btotalResidentCost[time][j][1].bwriteCost);
			       bHTransactionCost=bHTransactionCost.add(totalCostCalculation.btotalResidentCost[time][j][1].btranCost);
			       bHDelayCost=bHDelayCost.add(totalCostCalculation.btotalResidentCost[time][j][1].bdelayCost);
			       
			       //System.out.println("tCost"+"   "+totalCostCalculation.btotalResidentCost[time][j][1].btranCost+"   "+bHTransactionCost);
			       //System.out.println("STO==>"+bHStorageCost+"  "+"REA==>"+bHReadCost+"  "+"WRI==>"+bHWriteCost+"  "+"TRA==>"+bHTransactionCost);
			       
			       
			       //System.out.println("TotalCost"+"   "+bHCost);
			       
				}
			    bHCost=bHCost.add(bHStorageCost).add( bHReadCost).add(bHWriteCost).add(bHTransactionCost).add(bHDelayCost);
			   //System.out.println("bHCost===>"+bHCost);   
			
	}
	
	public void bunoptimizedColdCost(int j) {
		bCCost=new BigInteger("0");
		BigInteger bCStorageCost=new BigInteger("0");
		BigInteger bCReadCost=new BigInteger("0");
		BigInteger bCWriteCost=new BigInteger("0");
		BigInteger bCTransactionCost=new BigInteger("0");
		BigInteger bCDelayCost=new BigInteger("0");
			for (int time = 0; time< optimizationCost.T ; time++) {
				
				bCStorageCost=bCStorageCost.add(totalCostCalculation.btotalResidentCost[time][j][0].bstorageCost);
				bCReadCost=bCReadCost.add(totalCostCalculation.btotalResidentCost[time][j][0].breadCost);
				bCWriteCost=bCWriteCost.add(totalCostCalculation.btotalResidentCost[time][j][0].bwriteCost);
				bCTransactionCost=bCTransactionCost.add(totalCostCalculation.btotalResidentCost[time][j][0].btranCost);
				bCDelayCost=bCDelayCost.add(totalCostCalculation.btotalResidentCost[time][j][0].bdelayCost);
				
			}
			
			bCCost=bCCost.add(bCStorageCost).add( bCReadCost).add(bCWriteCost).add(bCTransactionCost).add(bCDelayCost);
		    //System.out.println("bCCost===>"+bCCost);
			//System.out.println("Cold out FOR"+"  "+"STO==>"+bCStorageCost+"  "+"REA==>"+bCReadCost+"  "+"WRI==>"+bCWriteCost+"  "+"TRA==>"+bCTransactionCost);
			
  }

	
	public BigInteger getBHCost() {
		return bHCost;
	}

	public void setBHCost(BigInteger bloCost) {
		this.bHCost = bloCost;
	}

	public BigInteger getBCCost() {
		return bCCost;
	}

	public void setBCCost(BigInteger bunloCost) {
		this.bCCost = bunloCost;
	}

	
}
