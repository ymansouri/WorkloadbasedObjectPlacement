package optimalCost;

import java.math.BigInteger;

public class bCostElements {
	
	/**
	 * @param bstorageCost
	 * @param breadCost
	 * @param bwriteCost
	 * @param bconsisCost
	 * @param bnonMigrationCost
	 * @param bmigrationCost
	 * @param btranCost
	 * @param bdelayCost
	 * @param bedelCost
	 */
	public bCostElements(BigInteger bstorageCost, BigInteger breadCost,
			BigInteger bwriteCost, BigInteger bconsisCost,
			BigInteger bnonMigrationCost, BigInteger bmigrationCost,
			BigInteger btranCost, BigInteger bdelayCost, BigInteger bedelCost) {
		super();
		this.bstorageCost = bstorageCost;
		this.breadCost = breadCost;
		this.bwriteCost = bwriteCost;
		this.bconsisCost = bconsisCost;
		this.btranCost = btranCost;
		this.bdelayCost=bdelayCost;
		this.bedelCost=bedelCost;
		this.bnonMigrationCost = bnonMigrationCost;
		this.bmigrationCost = bmigrationCost;
		
	}
	
	/**
	 * @param storageCost
	 * @param readCost
	 * @param writeCost
	 * @param nonMigrationCost
	 */
	public bCostElements() {
	super();
	bstorageCost=new BigInteger("0");
	breadCost=new BigInteger("0");
	bwriteCost=new BigInteger("0");
	bconsisCost=new BigInteger("0");
	btranCost=new BigInteger("0");
	bedelCost=new BigInteger("0");
	bnonMigrationCost=new BigInteger("0");
	bmigrationCost=new BigInteger("0");
	}
	public BigInteger bstorageCost;
	public BigInteger breadCost;
	public BigInteger bwriteCost;
	public BigInteger bconsisCost;
	public BigInteger btranCost;
	public BigInteger bedelCost;
	public BigInteger bdelayCost;
	
	public BigInteger bnonMigrationCost;
	public BigInteger bmigrationCost;
	
	public BigInteger getBconsisCost() {
		return bconsisCost;
	}
	public void setBconsisCost(BigInteger bconsisCost) {
		this.bconsisCost = bconsisCost;
	}
	public BigInteger getBstorageCost() {
		return bstorageCost;
	}
	public void setBstorageCost(BigInteger bstorageCost) {
		this.bstorageCost = bstorageCost;
	}
	public BigInteger getBreadCost() {
		return breadCost;
	}
	public void setBreadCost(BigInteger breadCost) {
		this.breadCost = breadCost;
	}
	public BigInteger getBwriteCost() {
		return bwriteCost;
	}
	public void setBwriteCost(BigInteger bwriteCost) {
		this.bwriteCost = bwriteCost;
	}
	public BigInteger getBdelayCost() {
		return bconsisCost;
	}
	public void setBdelayCost(BigInteger bconsisCost) {
		this.bconsisCost = bconsisCost;
	}
	public BigInteger getBnonMigrationCost() {
		return bnonMigrationCost;
	}
	public void setBnonMigrationCost(BigInteger bnonMigrationCost) {
		this.bnonMigrationCost = bnonMigrationCost;
	}
	public BigInteger getBmigrationCost() {
		return bmigrationCost;
	}
	public void setBmigrationCost(BigInteger bmigrationCost) {
		this.bmigrationCost = bmigrationCost;
	}
	public BigInteger getBtranCost() {
		return btranCost;
	}
	public void setBtranCost(BigInteger btranCost) {
		this.btranCost = btranCost;
	}
	
	public BigInteger getBedelCost() {
		return bedelCost;
	}
	public void setBedelCost(BigInteger bedelCost) {
		this.bedelCost = bedelCost;
	}

}
