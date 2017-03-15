package markets;

import java.io.IOException;
import java.util.List;
import bid.BidBPM;


public class BPMTestOP extends BalancingMarketOperator{

	public BPMTestOP(String name, boolean loggingActivated, String logDirName) throws IOException {
		super(name, loggingActivated, logDirName);
	}

	public List<BidBPM> getPositiveSupplies(){
		return this.positiveSupplies;
	}
	
	public List<BidBPM> getNegativeSupplies(){
		return this.negativeSupplies;
	}

	
	// Maybe just inherit form DemandBPM to create a dummy and then override members demandCapacity & demandWork ???
	
	// Später hierfür noch ne CSV mit Testwerten bauen
	public void setDemandCapacity (DemandBPM dBPMC){
		this.demandCapacity = dBPMC;
	}
	
	// Später hierfür noch ne CSV mit Testwerten bauen
	public void setDemandWork (DemandBPM dBPMW){
		this.demandWork = dBPMW;
	}
	
	public String getName(){
		return this.name;
	}
	
}
