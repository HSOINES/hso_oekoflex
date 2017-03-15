package agents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import bid.BidBPM;
import bid.BidEOM;
import bid.BidType;
import loggers.BPMLogInfo;
import loggers.EOMLogInfo;
import loggers.LogWriterBPM;
import loggers.LogWriterEOM;
import markets.BalancingMarketOperator;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;
import structures.TupelMT;
import structures.TupelMTS;
import structures.TupelMTS.Status;
import util.TimeUtil;



/**
 * name	description	ChargePowerMax [MW]	DischargePowerMax [MW]	chargingEfficiency [%]	dischargingEfficiency [%]	capacityMin [MWh]	capacityMax [MWh]	variableCostsCharging [EUR/MWh]	variableCostsDischarging [EUR/MWh]
 */
public class Storage implements EOMTrader, BPMTrader{
	private PriceForwardCurve pfc;
	private EnergyOnlyMarketOperator eom;
	private BalancingMarketOperator bpm;
	
	/** name of this particular storage */
	private final String name;
	
	/** description of this particular storage */
    private final String description;
	
	/** ChargePowerMax [MW] */
	float chargePowerMax;
	
	/** DischargePowerMax [MW] */
	float dischargePowerMax;
	
	/** chargingEfficiency [%] */
	float chargingEfficiency;
	
	/** dischargingEfficiency [%] */
	float dischargingEfficiency;
	
	
	/** minimum Capacity of the storage in [MWh] */
	float capacityMin;
	
	/** maximum Capacity of the storage in [MWh] */
	float capacityMax;
	
	/** variableCostsCharging [EUR/MWh] */
	float variableCostsCharging;
	
	/** variableCostsDischarging [EUR/MWh] */
	float variableCostsDischarging;
	
	
	/** time values for hour/tick */
	private final float dt   = 0.25f;
	
	/** hours of BPM */
	private final float dtau = 4.0f;
	
	//
	
	int numberOfDischarge;
	int numberOfCharge;
	
	/** State of charge of the energy storage at the end of the last tick t-1 in [MWh] */
	private float stateOfCharge; 		
	
	private BidEOM currentAssignmentEOM;
	private BidEOM currentAssignmentEOMConfirmed; 		
	private BidBPM positiveCapacityConfirmed;
	private BidBPM negativeCapacityConfirmed;
	private BidBPM negativeWork;
	private BidBPM positiveWork;
	
	private LogWriterBPM bpmPosLogger;
	private LogWriterBPM bpmNegLogger;
	private LogWriterEOM eomLogger;
	
	
	/**
	 * name	description	ChargePowerMax [MW]	DischargePowerMax [MW]	chargingEfficiency [%]	dischargingEfficiency [%]	capacityMin [MWh]	capacityMax [MWh]	variableCostsCharging [EUR/MWh]	variableCostsDischarging [EUR/MWh]
	 */
	public Storage(String name, String description, float  chargePowerMax, float dischargePowerMax, float chargingEfficiency , float  dischargingEfficiency, float capacityMin, float capacityMax, float variableCostsCharging, float variableCostsDischarging, PriceForwardCurve pfc , EnergyOnlyMarketOperator eom , BalancingMarketOperator bpm ){
		this.name = name;
		this.description = description;			
		this.chargePowerMax = chargePowerMax;
		this.dischargePowerMax = dischargePowerMax;
		this.chargingEfficiency = chargingEfficiency;
		this.dischargingEfficiency = dischargingEfficiency;
		this.capacityMin = capacityMin;
		this.capacityMax = capacityMax;
		this.variableCostsCharging = variableCostsCharging;
		this.variableCostsDischarging = variableCostsDischarging;
		this.pfc = pfc;		
		this.eom = eom;
		this.bpm = bpm;
		
		stateOfCharge = ((capacityMax + capacityMin) /2.0f);
		
		currentAssignmentEOM = new BidEOM(0, 0, 0, BidType.ENERGY_DEMAND_CONFIRMED, this);
		currentAssignmentEOMConfirmed = new BidEOM(0, 0, 0, BidType.ENERGY_DEMAND_CONFIRMED, this);
		negativeCapacityConfirmed = new BidBPM(0, 0, 0, 0, BidType.POWER_NEGATIVE_CONFIRMED, this);
		positiveCapacityConfirmed = new BidBPM(0, 0, 0, 0, BidType.POWER_POSITIVE_CONFIRMED, this);
		negativeWork = new BidBPM(0, 0, 0, 0, BidType.POWER_NEGATIVE_CONFIRMED, this);
		positiveWork = new BidBPM(0, 0, 0, 0, BidType.POWER_NEGATIVE_CONFIRMED, this);
	}
	
	
	 public float getVariableCostsCharging() {
		return variableCostsCharging;
	}

	public float getVariableCostsDischarging() {
		return variableCostsDischarging;
	}


	public float getStateOfCharge() {
		return stateOfCharge;
	}
	public boolean setStateOfCharge(float newStateOfCharge){
		if( (newStateOfCharge >= this.capacityMin) && (newStateOfCharge <= this.capacityMax) ){
			this.stateOfCharge = newStateOfCharge;
			return true;
		}else{
			return false;
		}
	}


	 /**
	  * Calculates the number of times the storage can either charge or discharge based on the current state of charge
	  * @return Total number of charges ( = numberOfDischarge + numberOfCharge)
	  */
	 protected Intervals calculateIntervals(float stateOfCharge, float capacityMin, float capacityMax, float dischargePowerMax, float chargePowerMax){
		 	int numberOfDischarge =  Math.max((int)Math.floor((stateOfCharge - capacityMin  )  / (dischargePowerMax * dt)), 0);		// Number of full  units  (that can be discharged before the storage is empty)
		 	int numberOfCharge    = (int)Math.floor((capacityMax   - stateOfCharge)  / (chargePowerMax * dt));						// Number of empty units (that can be charged before the storage is empty)
		 	return new Intervals(numberOfDischarge, numberOfCharge);
	 }

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void makeBidBalancingMarket() {
		makeBidBalancingMarket(TimeUtil.getCurrentTick());
	}

	@Override
	public void makeBidBalancingMarket(long currentTick) {
		
		// 1	
		List<TupelMT> chargePrices    = new ArrayList<>();
		List<TupelMT> dischargePrices = new ArrayList<>();
		List<TupelMT> restPrices      = new ArrayList<>();

		// 2 	Ermittlung aller Lade- und Entladezeitpunkte im Zeitraum dtau	gemÃ¤ÃŸ der Gebotslogik EOM â€“ rollierend Ã¼ber den gesamten Zeitraum dtau = 16 Ticks
		bpmRollingEomLogic(this.stateOfCharge, 16, currentTick, chargePrices, dischargePrices, restPrices);
		
		// 3   	Sortierung der zuvor ermittelten Entladungs- und Ladungsmarkpreise
		// 3.1 	Aufsteigende Sortierung voraussichtliche Entladungszeitpunkte
		dischargePrices.sort(new TupelMT.ascPriceAscTickComparator());
		// 3.2 	Absteigende Sortierung voraussichtliche Ladungszeitpunkte
		dischargePrices.sort(new TupelMT.dscPriceAscTickComparator());
		
		// 4	Ermittlung des Spreads (Ertrag) zwischen direkten Wertpaaren im Zeitraum dtau (â€žWorst-Case-Ertragâ€œ fÃ¼r tatsÃ¤chlich umgesetzte Energiemengen)
		// 4.1 	Gesamtzahl der Entladungsmarktpreise im Zeitraum dtau
		int dcs = dischargePrices.size();
		// 4.2 	Gesamtzahl der Ladungsmarktpreise im Zeitraum dtau
		int chs = chargePrices.size();
		// 4.3	Anzahl der Handelsintervalle tatsÃ¤chlich umgesetzter Energiemengen im Zeitraum
		int nReal = Math.min(dcs, chs);
		// 4.4 	Spezifischer Ertrag aus tatsÃ¤chlich umgesetzten Energiemengen am EOM im Zeitraum in dtau [â‚¬/MW]
		float revenueReal = calculateRevenue(nReal, chargePrices, dischargePrices);
		
		
		
		
		// 5.1	OHNE HANDEL 	SoC ohne HandelstÃ¤tigkeit am EOM innerhalb des Zeitraums dtau
		float stateOfChargeHold = this.stateOfCharge;
		
		// 6.1	OHNE HANDEL 	Berechnung der mÃ¶glichen Ladungs- und Entladungsintervalle 
		Intervals intervalsHold = calculateIntervals(stateOfChargeHold, capacityMin, capacityMax, dischargePowerMax, chargePowerMax);
		
		// 7.1	OHNE HANDEL 
		List<TupelMT> lowestTicksHold  = pfc.getSetWithLowestPrices(intervalsHold.chargeIntervals, currentTick + 16, intervalsHold.total());
		List<TupelMT> highestTicksHold = pfc.getSetWithHighestPrices(intervalsHold.dischargeIntervals, currentTick + 16, intervalsHold.total());
		
		// 7.2	OHNE HANDEL 
		List<TupelMTS> listLowHold = TupelMTS.convertToTupelMT_To_MTS(lowestTicksHold);
		List<TupelMTS> listHighHold = TupelMTS.convertToTupelMT_To_MTS(highestTicksHold);
		listLowHold.sort(new TupelMTS.ascPriceAscTickComparator());
		listHighHold.sort(new TupelMTS.descPriceAscTickComparator());
		
		// 7.3	OHNE HANDEL 
		List<TupelMTS> combinedHold = spreadBPM(listLowHold, listHighHold);
		
		// 7.4	OHNE HANDEL 
		List<TupelMTS> setDischargesHold  = new ArrayList<TupelMTS>();
		List<TupelMTS> setChargesHold = new ArrayList<TupelMTS>();
		
		@SuppressWarnings("unused")
		float newSoCHold = checkSoC(stateOfChargeHold, setChargesHold, setDischargesHold, combinedHold);
		setDischargesHold.sort(new TupelMTS.ascPriceAscTickComparator());
		setChargesHold.sort(new TupelMTS.descPriceAscTickComparator());
		
		
		int nHold = Math.min(setDischargesHold.size(), setChargesHold.size());
		
		float revHold = calculateRevenue(intervalsHold.total(), nHold, setChargesHold, setDischargesHold);
		
		
		
		// 5.1	MIT HANDEL	 	SoC mit entsprechender Handelstaeigkeit am EOM innerhalb des Zeitraums
		float stateOfChargeTrade = this.stateOfCharge - (dischargePowerMax * dischargePrices.size() *dt)  + (chargePowerMax * chargePrices.size()* dt);
		
		// 6.1	MIT HANDEL 		Berechnung der mÃ¶glichen Ladungs- und Entladungsintervalle 
		Intervals intervalsTrade = calculateIntervals(stateOfChargeTrade, capacityMin, capacityMax, dischargePowerMax, chargePowerMax);
		
		// 7.1	MIT HANDEL
		List<TupelMT> lowestTicksTrade  = pfc.getSetWithLowestPrices(intervalsTrade.chargeIntervals, currentTick + 16, intervalsTrade.total());
		List<TupelMT> highestTicksTrade = pfc.getSetWithHighestPrices(intervalsTrade.dischargeIntervals, currentTick + 16, intervalsTrade.total());
		
		// 7.2	MIT HANDEL
		List<TupelMTS> listLowTrade  = TupelMTS.convertToTupelMT_To_MTS(lowestTicksTrade);
		List<TupelMTS> listHighTrade = TupelMTS.convertToTupelMT_To_MTS(highestTicksTrade);
		listLowTrade.sort(new TupelMTS.ascPriceAscTickComparator());
		listHighTrade.sort(new TupelMTS.descPriceAscTickComparator());
		
		// 7.3	MIT HANDEL
		List<TupelMTS> combinedTrade = spreadBPM(listLowTrade, listHighTrade);
		
		// 7.4	MIT HANDEL
		List<TupelMTS> setDischargesTrade  = new ArrayList<TupelMTS>();
		List<TupelMTS> setChargesTrade = new ArrayList<TupelMTS>();
		
		@SuppressWarnings("unused")
		float newSoCTrade = checkSoC(stateOfChargeTrade, setChargesTrade, setDischargesTrade, combinedTrade);
		setDischargesTrade.sort(new TupelMTS.ascPriceAscTickComparator());
		setChargesTrade.sort(new TupelMTS.descPriceAscTickComparator());
		
		int nTrade = Math.min(setDischargesTrade.size(), setChargesTrade.size());
		
		float revTrade = calculateRevenue(intervalsTrade.total(), nTrade, setChargesTrade, setDischargesTrade);
		
		
		float revVariation = Math.abs( revHold- revTrade) * dtau;
		
		
		// Leistungspreis des Regelenergiemarktgebots (positiv & negativ) zum Zeitpunkt tau [€/MW]
		float leistungspreis = revenueReal + revVariation;
		
		
		// Arbeitspreisberechnung
		float arbeitspreisPos = 0;
		if (dcs > 0) {			
			float ret = 0;
			for (Iterator<TupelMT> iterator = dischargePrices.iterator(); iterator.hasNext();) {
				TupelMT tupelMT = (TupelMT) iterator.next();
				ret += tupelMT.price;
			} 
			arbeitspreisPos = (1.0f / dcs)  * ret;
		} else {
			float ret = 0;
			for (Iterator<TupelMTS> iterator = setDischargesTrade.iterator(); iterator.hasNext();) {
				TupelMTS tupelMT = (TupelMTS) iterator.next();
				ret += tupelMT.price;
			} 
			arbeitspreisPos = (1.0f / intervalsTrade.dischargeIntervals)  * ret;
		}

		
		float arbeitspreisNeg = 0;
		if (chs > 0) {			
			float ret = 0;
			for (Iterator<TupelMT> iterator = chargePrices.iterator(); iterator.hasNext();) {
				TupelMT tupelMT = (TupelMT) iterator.next();
				ret += tupelMT.price;
			} 
			arbeitspreisNeg =-1.0f *  (1.0f / dcs)  * ret;
		} else {
			float ret = 0;
			for (Iterator<TupelMTS> iterator = setChargesTrade.iterator(); iterator.hasNext();) {
				TupelMTS tupelMT = (TupelMTS) iterator.next();
				ret += tupelMT.price;
			} 
			arbeitspreisNeg =  -1.0f * (1.0f / intervalsTrade.chargeIntervals)  * ret;
		}
		
		// Zur Verfügung stehende Energiemenge für die positive Regelleistung
		float ePosRegLeistung = Math.min(((capacityMax - stateOfCharge) * dischargingEfficiency) , (dischargePowerMax * dtau * dischargingEfficiency));
		// Gebotsmenge am Regelenergiemarkt – positive Regelleistung [MW]
		float pPosLeistung = 0;
		
		if(ePosRegLeistung/dtau >= 5){
			pPosLeistung = ePosRegLeistung/dtau;
		}
		
		// Gebotsmenge am Regelenergiemarkt – negative Regelleistung [MW]	S188
		float eNegRegLeistung = Math.min(((stateOfCharge - capacityMin) * chargingEfficiency) , (chargePowerMax * dtau * chargingEfficiency));
		
		float pNegLeistung = 0;
		
		if(pNegLeistung/dtau >= 5){
			pNegLeistung = eNegRegLeistung/dtau;
		}	
		BidBPM pos = new BidBPM(pPosLeistung, leistungspreis, arbeitspreisPos, currentTick, BidType.POWER_POSITIVE, this);
		BidBPM neg = new BidBPM(pNegLeistung, leistungspreis, arbeitspreisNeg, currentTick, BidType.POWER_NEGATIVE, this);
		
		bpm.addPositiveSupply(pos);
		bpm.addNegativeSupply(neg);
				
	}

	protected List<TupelMTS> spreadBPM(List<TupelMTS> low , List<TupelMTS> high){
		int min = Math.min(low.size(), high.size());
		int max = Math.max(low.size(), high.size());
		
		List<TupelMTS> combined = new ArrayList<TupelMTS>();
		
		for(int i = 0; i < min ; i++){
			float lowPrice  = low.get(i).price;
			float highprice = high.get(i).price;
			
			if(calculateSpread(highprice, lowPrice)){
				high.get(i).status = Status.DISCHARGE;
				low.get(i).status  = Status.CHARGE;
			}else{
				high.get(i).status = Status.REST;
				low.get(i).status  = Status.REST;
			}
			
			combined.add(high.get(i));
			combined.add(low.get(i));
		}
		
		
		if(max == high.size()){
			for(int i = min; i < max ; i++){
				high.get(i).status = Status.DISCHARGE;
				combined.add(high.get(i));
			}
		}else{
			for(int i = min; i < max ; i++){
				low.get(i).status  = Status.CHARGE;
				combined.add(low.get(i));
			}
		}
		
		
		return combined;
	}
	
	
	protected boolean calculateSpread(float marketPricehigh, float marketPriceLow){
		float partHigh = marketPricehigh - (Math.abs(marketPricehigh) * ( 1 / dischargingEfficiency - 1) + variableCostsDischarging);
		float partLow  = marketPriceLow - (Math.abs(marketPriceLow) * ( 1 / chargingEfficiency - 1 ) +  variableCostsCharging);  
		float spread = partHigh - partLow; 
		return spread >= 0.0f;
	}
	
	protected float checkSoC(float soC, List<TupelMTS> charges, List<TupelMTS> discharges, List<TupelMTS> list){
		
		float stateOfCharge = soC;
		
		for(int i = 0; i < list.size() ; i++){
			TupelMTS currentTupel= list.get(i);
			
			if(currentTupel.status == Status.DISCHARGE && ((stateOfCharge - chargePowerMax * dt) >= capacityMin)){
				stateOfCharge = (stateOfCharge - chargePowerMax * dt);
				discharges.add(currentTupel);
				
			}else if(currentTupel.status == Status.CHARGE && ((stateOfCharge + dischargePowerMax * dt) <= capacityMax)){
				stateOfCharge = (stateOfCharge + dischargePowerMax * dt);
				charges.add(currentTupel);
				
			}else{
				// Do nothing because SoC is/would be full
			}
		}
		return stateOfCharge;
	}
	
	


	protected void bpmRollingEomLogic(float stateOfCharge,int period, long currentTick, List<TupelMT> chargePrices, List<TupelMT> dischargePrices, List<TupelMT> restPrices) {

		// hier mÃ¼ssen Listen mit 
		float currentSoC = stateOfCharge;		
		long tick = currentTick;
		for (; tick < currentTick + 16; tick++) {
			// Aufteilung der Marktpreise innerhalb der nÃ¤chsten 16 Ticks / 4 Stunden in die:	- x hohen und 				-> hohe Preise = discharge Punkte = setHigh - y niedrigen Marktpreise	-> niedrige Preise = discharge Punkte = setLow																		
			Intervals intervals   = calculateIntervals(currentSoC, this.capacityMin, this.capacityMax, this.dischargePowerMax, this.chargePowerMax);	
			List<TupelMT> highMT  = pfc.getSetWithHighestPrices((int)intervals.chargeIntervals, TimeUtil.getCurrentTick(), intervals.total());
			List<TupelMT> lowMT   = pfc.getSetWithLowestPrices((int)intervals.dischargeIntervals, TimeUtil.getCurrentTick(), intervals.total());

			// Sortierung der zuvor aufgeteilten: 	- hohen x (aufsteigend)- niedrigen y (absteigend) Marktpreise
			highMT.sort(new TupelMT.ascPriceAscTickComparator()); 
			lowMT.sort(new TupelMT.dscPriceAscTickComparator());
			
			
			int minIntervals = Math.min(lowMT.size(), highMT.size());		
			int targetIndex = 0;
			float matchHigh = 3000.0f;	
			float matchLow = -3000.0f;
			boolean bidDecision = false;	
			
			if(minIntervals > 0){		// Check if there is at least one high price low price combination to calculate a spread, by checking if the high and Low Price lists have at least one element
				for (; targetIndex < minIntervals-1; targetIndex++) {
					bidDecision = calculateSpread(highMT.get(targetIndex).price, lowMT.get(targetIndex).price);
					if (bidDecision) {
						break; 
					}
				}
			}else{ bidDecision = true; } // In this case the soc is either 0% or 100% so no spread calculation is possible, but we want to trade anyway
			
			if (highMT.size() > targetIndex ){ matchHigh = highMT.get(targetIndex).price;	}
			
			if (lowMT.size() > targetIndex){ matchLow = lowMT.get(targetIndex).price; }
			
			float curPrice = pfc.getPriceOnTick(tick);

			if( bidDecision && intervals.dischargeIntervals > 0 && curPrice >= matchHigh){						// Discharge so add a supply bid
				currentSoC = (currentSoC - dischargePowerMax * dt);
				dischargePrices.add(new TupelMT(tick, curPrice));
			}else if(bidDecision && intervals.chargeIntervals > 0 && curPrice <= matchLow){						// Charge so add a demand bid
				currentSoC = (currentSoC + chargePowerMax * dt);
				chargePrices.add(new TupelMT(tick, curPrice));
			}else{																								// Energiespeicher ruht
				restPrices.add(new TupelMT(tick, curPrice));
			}
		}

	}
	/**
	 * Errechnet den Spezifischer Ertrag aus tatsaechlich umgesetzten Energiemengen am EOM im Zeitraum dtau [Euro/MW]
	 * @param chargePrices	
	 * @param dischargePrices
	 * @return specific revenue within dtau in [EUR/MW]
	 */
	public float calculateRevenue(int n, List<TupelMT> chargePrices, List<TupelMT> dischargePrices){
		float revenue = 0.0f ;
		for (int i = 0; i < n; i++) {
			revenue += dischargePrices.get(i).price - chargePrices.get(i).price;		
		}
		revenue *= dt ;
		
		return revenue;
	}
	
	public float calculateRevenue(int foresight , int n, List<TupelMTS> chargePrices, List<TupelMTS> dischargePrices){
		float revenue = 0.0f ;
		for (int i = 0; i < n; i++) {
			revenue += dischargePrices.get(i).price - chargePrices.get(i).price;		
		}
		revenue *= dt ;
		
		return (float) ((1.0/ (float)foresight)  * revenue);
	}
	
	
	@Override
	public void makeBidEOM() {
        makeBidEOM(TimeUtil.getCurrentTick());
	}

	@Override
	public void makeBidEOM(long currentTick) {
		// Aufteilung der Marktpreise innerhalb der naechsten 16 Ticks / 4 Stunden in die:	- x hohen und 				-> hohe Preise = discharge Punkte = setHigh - y niedrigen Marktpreise	-> niedrige Preise = discharge Punkte = setLow																		
		Intervals intervals   = calculateIntervals(this.stateOfCharge, this.capacityMin, this.capacityMax, this.dischargePowerMax, this.chargePowerMax);	
		List<TupelMT> highMT = pfc.getSetWithHighestPrices((int)intervals.dischargeIntervals, TimeUtil.getCurrentTick(), intervals.total());
		List<TupelMT> lowMT = pfc.getSetWithLowestPrices((int)intervals.chargeIntervals , TimeUtil.getCurrentTick(), intervals.total());
//					printList("highMT unsorted", highMT);
//					printList("lowMT unsorted", lowMT);
		// Sortierung der zuvor aufgeteilten: 	- hohen x (aufsteigend)- niedrigen y (absteigend) Marktpreise
		highMT.sort(new TupelMT.ascPriceAscTickComparator()); 
		lowMT.sort(new TupelMT.dscPriceAscTickComparator());
//					printList("highMT sorted", highMT);
//					printList("lowMT  sorted", lowMT);
		
		int minIntervals = Math.min(lowMT.size(), highMT.size());		
		int targetIndex = 0;
		float matchHigh = 3000.0f;
		float matchLow = -3000.0f;
		boolean bidDecision = false;
		
		if(minIntervals > 0){		// Check if there is at least one high price low price combination to calculate a spread, by checking if the high and Low Price lists have at least one element
			for (; targetIndex < minIntervals-1; targetIndex++) {
				bidDecision = calculateSpread(highMT.get(targetIndex).price, lowMT.get(targetIndex).price);
				if (bidDecision) {
					break; 
				}
			}
		}else{ bidDecision = true; } // In this case the soc is either 0% or 100% so no spread calculation is possible, but we want to trade anyway
		
		if (highMT.size() > targetIndex ){ matchHigh = highMT.get(targetIndex).price;	}
		
		if (lowMT.size() > targetIndex){ matchLow = lowMT.get(targetIndex).price; }
		
		float curPrice = pfc.getPriceOnTick(currentTick);

		if( bidDecision && intervals.dischargeIntervals > 0 && curPrice >= matchHigh){						// Discharge so add a supply bid
			float arg1 = ((stateOfCharge - capacityMin - positiveCapacityConfirmed.getQuantity() * dt) * dischargingEfficiency);		
			float arg2 = (dischargePowerMax * dt * dischargingEfficiency);
			float quantity = Math.min(arg1, arg2);  
			eom.addSupply(new BidEOM(quantity, -3000, currentTick, BidType.ENERGY_SUPPLY, this));
			currentAssignmentEOM = new BidEOM(quantity, -3000, currentTick, BidType.ENERGY_SUPPLY, this);
		}else if(bidDecision && intervals.chargeIntervals > 0 && curPrice <= matchLow){						// Charge so add a demand bid
			float arg1 = (capacityMax - stateOfCharge - negativeCapacityConfirmed.getQuantity() * dt) / (chargingEfficiency);
			float arg2 = 0;
			float quantity = Math.min(arg1, arg2);
			eom.addDemand(new BidEOM(quantity, 3000, currentTick, BidType.ENERGY_DEMAND, this));
			currentAssignmentEOM = new BidEOM(quantity, 3000, currentTick, BidType.ENERGY_DEMAND, this);
		}else{																								// Energiespeicher ruht
			eom.addSupply(new BidEOM(0, Float.NEGATIVE_INFINITY	, currentTick, BidType.ENERGY_DEMAND, this));	
			currentAssignmentEOM = new BidEOM(0, 0, currentTick, BidType.ENERGY_DEMAND, this);
			
		}
		
		
	}
	
	/** @return the current Power the storage has in [MW]  */
	@Override
	public float getCurrentPower() {
		return stateOfCharge / dt;
	}

	/** @return the current Energy the storage has in [MWh] */
	public float getCurrentEnergy(){
		return stateOfCharge;
	}

	@Override
	public float getLastAssignments() {
		// TODO Was fur einen Nutzen soll diese Funktion haben?? -> Frueher wegen der Grafik.
		return 0;
	}

	@Override
	public void notifyClearingDone(BidBPM bidAnswer) {
		switch (bidAnswer.getBidType()) {
			// All the BPM bids coming back form market operator
			case POWER_POSITIVE_CONFIRMED :
				this.positiveCapacityConfirmed =  bidAnswer;
				break;
			case POWER_NEGATIVE_CONFIRMED :	
				this.negativeCapacityConfirmed = bidAnswer;
				break;
			case POWER_POSITIVE_CALL :
				this.positiveWork =  bidAnswer;
				break;
			case POWER_NEGATIVE_CALL :
				this.negativeWork =  bidAnswer;
				break;
				
			// Default in case some thing is wrong	
			default:
				throw new IllegalStateException("No matching Bidtype, BidType is: " + bidAnswer.getBidType());
		}
	}

	@Override
	public void notifyClearingDone(BidEOM bidAnswer) {
		this.currentAssignmentEOMConfirmed = bidAnswer;
	}

	@Override
	public void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator) {
		this.bpm = balancingMarketOperator;	
	}

	@Override
	public void setSpotMarketOperator(EnergyOnlyMarketOperator spotMarketOperator) {
		this.eom = spotMarketOperator;
		
	}

	@Override
	public float getLastClearedPrice() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Class encapsulates the charge and discharge Intervals 
	 */
	public class Intervals{
		
		int dischargeIntervals;
		int chargeIntervals;
		
		Intervals(int dischargeIntervals, int chargeIntervals){
			this.dischargeIntervals = dischargeIntervals;
			this.chargeIntervals = chargeIntervals;
		}
		/** @return dischargeIntervals + chargeIntervals */
		int total(){
			return dischargeIntervals + chargeIntervals;
		}
	}

	@Override
	public void acknowledgeBidBPM() {
		bpm.addPositiveSupply(positiveCapacityConfirmed);
		bpm.addNegativeSupply(negativeCapacityConfirmed);	
	}
	
	public void updatePowerPreceding(){
		
		this.stateOfCharge +=  negativeWork.getQuantity()* dt - positiveWork.getQuantity()* dt;
		
		// In case of Supply/ Demand adjust the soC
		if(currentAssignmentEOMConfirmed.getBidType() == BidType.ENERGY_DEMAND_CONFIRMED || currentAssignmentEOMConfirmed.getBidType() == BidType.ENERGY_DEMAND){
			this.stateOfCharge += currentAssignmentEOMConfirmed.getQuantity();
		}else{
			this.stateOfCharge -= currentAssignmentEOMConfirmed.getQuantity();
		}
	}
	
	public void setLoggers(LogWriterBPM bpmPosLogger, LogWriterBPM bpmNegLogger, LogWriterEOM eomLogger	){
		this.bpmPosLogger = bpmPosLogger;
		this.bpmNegLogger = bpmNegLogger;
		this.eomLogger = eomLogger;
	}

	@Override
	public void logEOM() {
		eomLogger.addToList(new EOMLogInfo(currentAssignmentEOM, currentAssignmentEOMConfirmed));
		System.out.println(this.name +" : " + currentAssignmentEOM.toString() + " | " + currentAssignmentEOMConfirmed.toString());
	}


	@Override
	public void logBPM() {
		bpmPosLogger.addToList(new BPMLogInfo(positiveCapacityConfirmed, positiveWork.getQuantity(), false));
		bpmNegLogger.addToList(new BPMLogInfo(negativeCapacityConfirmed, negativeWork.getQuantity(), false));
	}
	
	// Only for debugging and convenience 
	@SuppressWarnings("unused")
	private void printList(String nameOfList, List<TupelMT> list ){
		System.out.println("\n----------------------------------");
		System.out.println(nameOfList + " for current tick:  " +  TimeUtil.getCurrentTick());
		for (Iterator<TupelMT> iterator = list.iterator(); iterator.hasNext();) {
			TupelMT tupelMT = (TupelMT) iterator.next();
			System.out.println(tupelMT.toString());
			
		}
		System.out.println("----------------------------------\n");	
	}
}