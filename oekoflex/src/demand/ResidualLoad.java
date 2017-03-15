package demand;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import agents.EOMTrader;
import agents.MarketOperatorListener;
import bid.BidBPM;
import bid.BidEOM;
import bid.BidType;
import markets.EnergyOnlyMarketOperator;
import util.TimeUtil;

// Wird zum "Akteur" gemacht, dass dieser immer sein Angebot abgeben kann ohne allzu stark die Implementierung zu verbiegen!!
/**
 * Blabla
 * Hat 2 innere Klassen: eine für die EE und eine für die TotalLoad/GridLoad
 */
public class ResidualLoad implements MarketOperatorListener, EOMTrader{	
	private GridLoad   gridload;
	private Renewables renewables;
	private EnergyOnlyMarketOperator eomOp;
	
	public ResidualLoad(String dirPath,String fileNameGridload, String filenameRenewables, EnergyOnlyMarketOperator eomOp){
		this.gridload   = new GridLoad(  dirPath,  "/" + fileNameGridload);
		this.renewables = new Renewables(dirPath,  "/" + filenameRenewables);
		this.eomOp = eomOp;
	}
	
	
	
	public ResidualLoad(String dirPath,String fileNameGridload, String filenameRenewables) {
		this.gridload   = new GridLoad(  dirPath,  "/" + fileNameGridload);
		this.renewables = new Renewables(dirPath,  "/" + filenameRenewables);
	}



	public float getResidualLoad(long tick){
		float resLoad = calculateResidualLoad(tick);
		return resLoad;
	}
	
	private float calculateResidualLoad(long tick){
			float gL = gridload.getValueForTick(tick);
			float rN = renewables.getValueForTick(tick);
			return gL - rN;
		
		
	}

	public float resLoad(long tick){
		return calculateResidualLoad(tick);
	}




	@Override
	public String getName() {
		return "Residual Load";
	}



	@Override
	public void notifyClearingDone(BidBPM bidAnswer) {
		// No need for Impl right now
	}



	@Override
	public void notifyClearingDone(BidEOM bidAnswer) {
		// No need for Impl right now
	}



	@Override
	public String getDescription() {
		return "Residual Load";
	}



	@Override
	public float getLastAssignments() {
		// No need for Impl right now
		return 0;
	}



	@Override
	public void setSpotMarketOperator(EnergyOnlyMarketOperator spotMarketOperator) {
		this.eomOp = spotMarketOperator;
		
	}



	@Override
	public float getLastClearedPrice() {
		// No need for Impl right now
		return 0;
	}



	@Override
	public void makeBidEOM() {
		this.makeBidEOM( TimeUtil.getCurrentTick());
	}



	@Override
	public void makeBidEOM(long currentTick) {
		float resLoad = calculateResidualLoad(currentTick);
		BidEOM bid = new BidEOM(resLoad, 3000, currentTick, BidType.ENERGY_DEMAND, this);
		eomOp.addDemand(bid);
	}



	@Override
	public float getCurrentPower() {
		// No need for Impl right now
		return 0;
	}



	@Override
	public void updatePowerPreceding() {
		// No need for Impl right now
		
	}



	@Override
	public void logEOM() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void logBPM() {
		// TODO Auto-generated method stub
		
	}



	
}




class Renewables {

	private List<Float> values = new LinkedList<Float>();

	public Renewables(String dir, String fileName) {

		String path = dir + fileName;
		try {
			init(path, fileName);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void init(String path, String filename) throws ParseException {
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] tickValue = line.split(";");
				NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
			    Number number = format.parse(tickValue[1]);
				float value = number.floatValue();
				values.add(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(values.size() < 35040){
			throw new IllegalStateException("File: " + filename +" has not enough data, should be 35040 entrys but is: " + values.size());
		}
	}

	public float getValueForTick(long tick) {
		if(tick<0){
			return  (float) values.get(values.size() +  (int)tick);
		}else{
			return (float) values.get((int) tick);
		}
	}

	public void printAllInformation() {
		for (int i = 0; i < values.size(); i++) {
			System.out.println("" + i + " , " + values.get(i));
		}
	}
}




class GridLoad {

	private List<Float> values = new LinkedList<Float>();

	public GridLoad(String dir, String fileName) {
		String path = dir + fileName;
		init(path, fileName);
	}

	public void init(String path, String filename) {
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] tickValue = line.split(";");
				float value = Float.parseFloat(tickValue[1]);
				values.add(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(values.size() < 35040){
			throw new IllegalStateException("File: " + filename +" has not enough data, should be 35040 entrys but is: " + values.size());
		}
	}

	public float getValueForTick(long tick) {
		if(tick<0){
			return  (float) values.get(values.size() +  (int)tick);
		}else{
			return (float) values.get((int) tick);
		}
	}

	public void printAllInformation() {
		for (int i = 0; i < values.size(); i++) {
			System.out.println("" + i + " , " + values.get(i));
		}
	}
}
