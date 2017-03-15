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
import oekoflex.Market;
import pfc.PriceForwardCurve;
import util.TimeUtil;

/**
 * texty text for class 
 * 
 */
public class FlexPowerplant implements EOMTrader, BPMTrader, MarketOperatorListener {

	/** name of the flexible power plant */
	private final String name;
	
	/** description of the flexible power plant */
	private final String description;
	
	/** maximum  amount of power for this flexible power plant in [MW] */
	private final float powerMax;
	
	/** minimum amount of power for this flexible power plant in [MW] */
	private final float powerMin;
	
	/** efficiency for this flexible power plant  */
	private final float efficiency;
	
	/** amount of power the plant can go up over the time of one tick*/
	private final float powerRampUp;
	
	/** amount of power the plant can go down over the time of one tick*/
	private final float powerRampDown;
	
	/** The variable costs of the power plant   */
	private float variableCosts;
	
	/** specific costs to start the flexible power plan, measured in [Euro/MW] used as euro per Nennleistung */
	private final float startupCosts;
	
	/** specific costs to shut down the flexible power plan, measured in [Euro/MW] used as euro per Nennleistung*/
	private final float shutdownCosts;
	
	/** price of last assignment by market operator as percentage  of the last bid */
	private float lastClearedPrice;				
	
	private float powerPreceding;
	
	/** marginal costs of this specific power plant, determined only once when constructed in [Euro/MWh]*/
	private final float marginalCosts;
	
	/** the price forward curve for this scenario */
	private final PriceForwardCurve priceForwardCurve;
	
	/** specific operator which represents the balancing power market */
	private BalancingMarketOperator balancingMarketOperator;
	
	/** specific operator which handles the energy only market */
	private EnergyOnlyMarketOperator eomMarketOperator;
	
	/** for BPM, trade per 5 minutes */
	static final float LATENCY = 3f;
	
	/** time values for hour/tick */
	private final float dt   = 0.25f;
	
	/** hours of BPM */
	private final float dtau = 4.0f;
	
	
	// Letzte von den Market operators zurückgemeldete Gebote
	
	/** Gebot Vorzuhaltende positive Regelleistung für den BPM in [MW] */
	private BidBPM positiveCapacity;
	
	/** Vorzuhaltende positive Regelleistung für den BPM in [MW] */
	private BidBPM positiveCapacityConfirmed;
	
	
	/** Gebot Vorzuhaltende negative Regelleistung für den BPM; the quantity in [MW], the capacity price in [Euro/MW], the work price in [Euro/MWh] */ 
	private BidBPM negtiveCapacity;
	
	/** Vorzuhaltende negative Regelleistung für den BPM; the quantity in [MW], the capacity price in [Euro/MW], the work price in [Euro/MWh] */ 
	private BidBPM negtiveCapacityConfirmed;
	
	
	/** Abgerufene positive Regelenrgie durch den BPM; the quantity in [MW], the capacity price in [Euro/MW], the work price in [Euro/MWh] */ 
	private BidBPM positiveWork;
	
	/** Abgerufene negative Regelenrgie durch den BPM, the quantity in [MW], the capacity price in [Euro/MW], the work price in [Euro/MWh] */ 
	private BidBPM negativeWork;
	
	
	/** letztes  MustRun Gebot an den EOM  */
	private BidEOM lastMustRun;
	
	/** letztes rückgemeldetes MustRun Gebot an den EOM  */
	private BidEOM lastMustRunConfirmed;
	
	
	/** letztes FlexibleRun Gebot an den EOM  */
	private BidEOM lastFlexibleRun;
	
	/** letztes rückgemeldetes FlexibleRun Gebot and den EOM  */
	private BidEOM lastFlexibleRunConfirmed;
	
	
	// Zustandsvariablen des Kraftwerkes: 
	
	/** Binäre Zustandsvariable der Anfahrtskostendeckung zu Beginn von dt */
	private boolean cc;
	@SuppressWarnings("unused")
	private boolean ccTminusOne;
	
	/** Individueller Deckungsbeitrag am EOM-Markt im Rahmen der Anfahrtskostendeckung */
	private float cm;
	
	/** List of number of ticks the power plant was running */
	private List<Integer> durations = new ArrayList<>();
	
	/** counter for the ticks the fpp is currently running */
	private int durationRunning;
	
	/** Binäre Zustandsvariable der Kraftwerksabfahrt im aktuellen Zeitschritt dt */
	private boolean sd;
	@SuppressWarnings("unused")
	private boolean sdTminusOne;
	
	/** Binäre Zustandsvariable der Kraftwerkswiederanfahrt im aktuellen Zeitschritt dt */ 
	private boolean su;
	@SuppressWarnings("unused")
	private boolean suTminusOne;
	
	/** Time in ticks till the power plant has restarted ( from 0 to power minimum of fpp) */
	private long foresightFPPSU;
	
	/** */
	private boolean vorlauf;
	

	
	/** Theoretische Ausgangsleistung der Erzeugungseinheit während der Ab- und Wiederanfahrt [MW] */
	private float powerStandBy;
	
	/** Verfügbare positive Regelleistung für den Zeitraum dtau [MW] */
	private float powerPosAvailable;
	
	private float specificFuelPrice;
	private float co2certicicateprice;
	private float emmissionfactor;
	
	/** Anzahl der Ticks der Kraftwerksanfahrt */
	private int dFppSU;
	
	
	/** tolerance for floating point operations */
	private float epsilon = 0.000001f;
	
	private boolean abbruch;
	
	private LogWriterBPM bpmPosLogger;
	private LogWriterBPM bpmNegLogger;
	private LogWriterEOM eomLogger;
	
	/**
	 * name,	description,	powerMax [MW],	powerMin [MW],	efficiency [%],	rampUp [MW/15min],	rampDown [MW/15min],	variableCosts [EUR/MWh],	shutdownCosts [EUR/MW],	startupCosts  [EUR/MW]
	 * Constructor 2
	 */ 
	public FlexPowerplant(
			final String name, 			final String description, 
			final float powerMax, 		final float powerMin, 
			float efficiency, 			final float powerRampUp, 
			final float powerRampDown, 	final float variableCosts, 
			final float shutdownCosts , final float startupCosts,
			float specificFuelPrice, 	float co2certicicateprice, 
			float emmissionfactor, 
			BalancingMarketOperator balancingMarketOperator, 
			EnergyOnlyMarketOperator eomMarketOperator, 
			final PriceForwardCurve priceForwardCurve, long foresight) {
		
		this.name 					= name;
		this.description 			= description;
		this.powerMax 				= powerMax;
		this.powerMin 				= powerMin;
		this.efficiency				= efficiency;
		this.specificFuelPrice 		= specificFuelPrice;
		this.co2certicicateprice	= co2certicicateprice;
		this.emmissionfactor		= emmissionfactor;
		this.powerRampUp 			= powerRampUp;
		this.powerRampDown 			= powerRampDown;
		this.marginalCosts			= calculateMarginalCosts(specificFuelPrice, efficiency, co2certicicateprice, emmissionfactor, variableCosts);
		this.variableCosts 			= variableCosts;
		this.startupCosts			= startupCosts;
		this.shutdownCosts			= shutdownCosts;
		this.balancingMarketOperator = balancingMarketOperator;
		this.eomMarketOperator		= eomMarketOperator;
		this.priceForwardCurve 		= priceForwardCurve;
		
		this.powerPreceding = 0.0f;
		
		this.lastFlexibleRun			= new BidEOM(0, 0, 0, null, null);
		this.lastFlexibleRunConfirmed 	= new BidEOM(0, 0, 0, null, null);
		this.lastMustRun 				= new BidEOM(0, 0, 0, null, null);
		this.lastMustRunConfirmed 		= new BidEOM(0, 0, 0, null, null);
		
		this.positiveCapacityConfirmed 	= new BidBPM(0, 0, 0, 0, null, null);
		this.negtiveCapacityConfirmed 	= new BidBPM(0, 0, 0, 0, null, null);
		
		this.positiveWork 				= new BidBPM(0, 0, 0, 0, null, null);
		this.negativeWork 				= new BidBPM(0, 0, 0, 0, null, null);
		
		this.durationRunning = 0;
		this.cc = false;
		this.sd = false;
		this.su = false;
		this.dFppSU = ((int)(this.powerMin / this.powerRampUp)) +1;
		this.foresightFPPSU = foresight;
		
	}
	
	/**
	 * Will be called by the BPM market-operator to invite the agents to  make bids.
	 * Calls makeBidBalancingMarket(long currentTick)
	 */ 
	@Override
	public void makeBidBalancingMarket() {					
		long currentTick = TimeUtil.getCurrentTick();
		checkAvailabilityForUse1(currentTick);
        makeBidBalancingMarket(currentTick);
	}
		/** Prüfung der Einsatzfähigkeit vor Abgabe eines Gebotes am BPM*/
	public void checkAvailabilityForUse1(long currentTick){

		// 
		this.foresightFPPSU = calculateForesight();
		
		// cm is needed for the calculation of cc
		this.cm = calculateCM(currentTick);
		
		// Berechnung und Update cc
		this.cc = calculateCC();
		
		// Berechnung suTi 
		this.su = calculateSU();
		
		// berechnung PstandBy
		this.powerStandBy = calculatePowerStandBy();
	}
	
	/**
	 * Will be called by the the repast scheduler to invite the agents to  make bids on the energy only market.
	 * <p>
	 * Calls makeBidEOM(long currentTick)
	 */ 
	@Override
	public void makeBidEOM() {
		  long currentTick = TimeUtil.getCurrentTick();
		  abbruch = false; 			// reset counter 
	      makeBidEOM(currentTick);
	}
	
	/**
	 * Prüfung der Einsatzfähigkeit auf Grundlage der zurückgemeldeten Gebote am EOM und
	 * die Rückmeldung an den BPM ob das vorher gemeldete Gebot noch zu erfüllen ist. 
	 * <p> 
	 * This Method is called by Repast Scheduler between the Ticks to acknowledge an agents bid to the BPM operator 
	 * in case something happen(e.g. Must Run Violation at EOM ) and the agent cannot fulfill his Bid on the Balancing 
	 * Power Market.
	 * <p>
	 * This Method will call the Balancing market operator and either acknowledge the previous made bid or send a "new" one
	 * with a quantity of zero. 
	 */
	// TODO add to repast scheduler
	@Override
	public void acknowledgeBidBPM(){
		if((Math.abs(lastMustRun.getQuantity() - lastMustRunConfirmed.getQuantity()) <= epsilon)){
			balancingMarketOperator.addPositiveSupply(positiveCapacityConfirmed);
			balancingMarketOperator.addNegativeSupply(negtiveCapacityConfirmed);
		}else{
			this.positiveCapacityConfirmed = new BidBPM(0, 0, 0, TimeUtil.getCurrentTick(), positiveCapacity.getBidType(), this);
			balancingMarketOperator.addPositiveSupply(positiveCapacityConfirmed);
			this.negtiveCapacityConfirmed = new BidBPM(0, 0, 0, TimeUtil.getCurrentTick(), negtiveCapacity.getBidType(), this);
			balancingMarketOperator.addNegativeSupply(negtiveCapacityConfirmed);
		}		
		this.calculateSD();
	}
	
	/**
	 * Makes 0,1 or 2 bids in the Balancing Power market. 
	 * <p>
	 * <ul>
	 * 		<li> One bid for positive balancing power
	 * 		<li> and one for negative balancing power.
	 * </ul>
	 * 
	 * This function is the result of the market operator calling the agents within in the market.
	 * 
	 * @param currentTick  tick in which the bid takes place
	 */ 
	@Override
    public void makeBidBalancingMarket(long currentTick) {
			
		float pfcCostsAverage = priceForwardCurve.avgPriceOverTicks(currentTick, Market.BALANCING_MARKET.getTicks()); // Market.BALANCING_MARKET.getTicks() is equivalent to 16
		
		float pRampUp   = (powerRampUp   / LATENCY);
		float pRampDown = (powerRampDown / LATENCY);
		
		
		// 1.1 Bestimmung Gebotsmenge am Regelenergiemarkt - positive Regelleistung
		float mengeRegelleistungPpos = 0;

		if ((!this.sd) && (!this.su)) {
			mengeRegelleistungPpos = Math.min(powerMax - powerPreceding, pRampUp);
		}
		
		if (mengeRegelleistungPpos < 5) {
			mengeRegelleistungPpos = 0;
		}
		
		float leistungspreisPpos = 0;
		float arbeitspreisPpos = 0;
		
		if (mengeRegelleistungPpos > 0) {
			
			// 1.2 Bestimmung Leistungspreis am Regelenergiemarkt - positive Regelleistung
			leistungspreisPpos = Math.max((pfcCostsAverage - calculateMariginalCostBPM(this.powerPreceding)/Market.BALANCING_MARKET.getTicks()) * dtau, 0)+ Math.abs(Math.min(((pfcCostsAverage - calculateMariginalCostBPM(this.powerPreceding)/Market.BALANCING_MARKET.getTicks()) * dtau * powerMin) / mengeRegelleistungPpos, 0));		// checked 20.12.2016
			arbeitspreisPpos   =  marginalCosts; // checked 20.12.2016
		}
		
		// 1.3 Angebotsabgabe der positiven Regelleistung
		BidBPM bidPosCap = new BidBPM(mengeRegelleistungPpos, leistungspreisPpos, arbeitspreisPpos, currentTick, BidType.POWER_POSITIVE, this);
		balancingMarketOperator.addPositiveSupply(bidPosCap);
		this.positiveCapacity = bidPosCap;
		
		
		// 2.1 Bestimmung Gebotsmenge am Regelenergiemarkt - negative Regelleistung
		float mengeRegelleistungPneg =  0.0f;
		
		if ((!this.sd) && (!this.su)) {
			mengeRegelleistungPneg = Math.min(powerPreceding - powerMin, pRampDown);
		}
		
		if (mengeRegelleistungPneg < 5) {
			mengeRegelleistungPneg = 0;
		}
		
		float leistungspreisPneg = 0;
		float arbeitspreisPneg = 0;
		
		if (mengeRegelleistungPneg > 0) {
			
			// 2.2 Bestimmung Leistungspreis am Regelenergiemarkt - negative Regelleistung
			leistungspreisPneg = Math.abs(Math.min(((pfcCostsAverage - calculateMariginalCostBPM(this.powerPreceding)/ Market.BALANCING_MARKET.getTicks()) * dtau * (powerMin + mengeRegelleistungPneg)) / mengeRegelleistungPneg, 0));	// checked 20.12.2016
			arbeitspreisPneg   = -marginalCosts;
		}	
		
		// 2.3 Angebotsabgabe der negativen Regelleistung
		BidBPM bidNegCap =new BidBPM(mengeRegelleistungPneg, leistungspreisPneg, arbeitspreisPneg, currentTick, BidType.POWER_NEGATIVE, this);
		balancingMarketOperator.addNegativeSupply(bidNegCap);
		this.negtiveCapacity = bidNegCap;

	}
	
	
	
	/**
	 * to calculate the none-specific marginal costs of the plant
	 *
	 * @return      marginal costs as [Euro/MWh]
	 */ 
	static float calculateMarginalCosts(float specificFuelPrice, float efficiency, float co2certicicateprice, float emmissionfactor, float variableCosts){
		float mc = (specificFuelPrice / efficiency) + (co2certicicateprice * ( emmissionfactor /  efficiency) ) + variableCosts;	// checked 20.12.2016
		return mc;
	}
	
	/** Teillastwirkungsgradabhängige Grenzkosten der Erzeugungseinheit i [Euro/MWh] */
	public float calculateMariginalCostBPM(float powerPreceding){
		float ratio = powerPreceding / powerMax;
		float etaLoss = this.calculateEffiencyLoss(ratio);
		float a = (specificFuelPrice)/(efficiency - etaLoss);
		float b = (co2certicicateprice * (emmissionfactor /(efficiency - etaLoss)));
		return a + b + variableCosts;
	}
	
	
	

	/**
	 * Makes bid in the bid Energy Only Market. 
	 * <p>
	 * This function is the result of the market operator calling the agents within in the market.
	 * 
	 * @param currentTick  tick in which the bid takes place
	 */ 
	@Override
	public void makeBidEOM(long currentTick) {
 
		float pConstRampUp   = powerRampUp   / 2;
		float pConstRampDown = powerRampDown / 2;
		
		// 1.1 Ermittlung Gebotsmenge des Must-Run-Gebots [MWh]
        float pMustRun = Math.max(powerPreceding - pConstRampDown  + negtiveCapacityConfirmed.getQuantity() , (powerMin + negtiveCapacityConfirmed.getQuantity())); // checked 20.12.2016
        float eMustRun = 0;
        float priceMustRun = Float.NEGATIVE_INFINITY;
        
		if (pMustRun * dt >= 1) {
			eMustRun = pMustRun * dt;	
			priceMustRun = -1.0f * ((((shutdownCosts + startupCosts) * powerMax) / calculateForesight() ) * dt) + calculateMariginalCostBPM(powerPreceding);		 // checked 20.12.2016

		}

		// 1.2 Ermittlung Gebotspreis des Must-Run-Gebots [Euro/MWh]

		// 1.3 Abgabe Must-Run-Gebot
		BidEOM mustRunSupply = new BidEOM(eMustRun, priceMustRun, currentTick, BidType.ENERGY_SUPPLY_MUSTRUN, this);
		eomMarketOperator.addSupply(mustRunSupply);
		this.lastMustRun = mustRunSupply;
		//System.out.println(mustRunSupply.toString());
      
        
		
		// 2.1 Ermittlung Gebotsmenge des Flexibilitätsgebots [MW]
	    float pFlex = Math.min((powerMax - positiveCapacityConfirmed.getQuantity() - pMustRun),(powerPreceding + pConstRampUp - positiveCapacityConfirmed.getQuantity() ) - pMustRun); // checked 20.12.2016
        float eFlex  = 0;
        float priceFlex = Float.NEGATIVE_INFINITY;
        		
		if (pFlex * dt >= 1) {
			eFlex = pFlex * dt;
			// 2.2 Ermittlung Gebotspreis des Flexibilitaetsangebots [Euro/MWh]
			priceFlex = calculateMariginalCostBPM(powerPreceding)	;	// checked 20.12.2016
		}
		
		// 2.3 Abgabe Flexibilitätsgebot
		BidEOM flexSupply = new BidEOM(eFlex,priceFlex,currentTick, BidType.ENERGY_SUPPLY, this);
		eomMarketOperator.addSupply(flexSupply);
        this.lastFlexibleRun = flexSupply;	
        //System.out.println(lastFlexibleRun.toString());
	}

	/** @param balancingMarketOperator  the operator to set for the balancing power market */ 	
	@Override
	public void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator) {
		this.balancingMarketOperator = balancingMarketOperator;
	}

	/** @param spotMarketOperator  the operator to set for the spotmarket / Energy only market*/ 
	@Override
	public void setSpotMarketOperator(EnergyOnlyMarketOperator spotMarketOperator) {
		this.eomMarketOperator = spotMarketOperator;
	}

	/**@return      last cleared price */ 	
	@Override
	public float getLastClearedPrice() {
		return lastClearedPrice;
	}

	/**@return      current Power as MWh per 15min */ 
	@Override
	public float getCurrentPower() {
		return this.powerPreceding;
	}
	

	/**
	 * This function is called by the marketoperator to inform every agent that is market is cleared and what has become their individual bids.
	 */
	@Override
	public void notifyClearingDone(BidBPM bid) {
		switch (bid.getBidType()) {
		
		// All the BPM bids coming back form market operator
		case POWER_POSITIVE_CONFIRMED :
			this.positiveCapacityConfirmed =  bid;
			break;
		case POWER_NEGATIVE_CONFIRMED :	
			this.negtiveCapacityConfirmed = bid;
			break;
		case POWER_POSITIVE_CALL :
			this.positiveWork =  bid;
			break;
		case POWER_NEGATIVE_CALL :
			this.negativeWork =  bid;
			break;
			
		// Default in case some thing is wrong	
		default:
			throw new IllegalStateException("No matching Bidtype, BidType is: " + bid.getBidType());
			
		}
		
	}

	/** This function is called by the marketoperator to inform every agent that is market is cleared and what has become their individual bids. */
	@Override
	public void notifyClearingDone(BidEOM bid) {
		
		switch (bid.getBidType()) {
		
		// All the EOM bids coming back form market operator
		case ENERGY_DEMAND_CONFIRMED :				// both cases of flexible run 
		case ENERGY_SUPPLY_CONFIRMED : 
			this.lastFlexibleRunConfirmed =  bid;	
			//System.out.println(bid.toString());
			break;
		case ENERGY_SUPPLY_MUSTRUN_CONFIRMED:
			this.lastMustRunConfirmed =  bid;
			//System.out.println(bid.toString());
			this.updateDuration(bid);
			break;
		default:// Default in case some thing is wrong	
			throw new IllegalStateException("No matching Bidtype, BidType is: " + bid.getBidType());
		}
	}
	

	
	
	public float calculateEffiencyLoss(float ratio){
		float verlust = 0.0f;
		switch(this.description){
			case "lignite":
			case "hard coal":
				verlust = (float) (0.095859 * Math.pow(ratio, 4.0d) - 0.356010 * Math.pow(ratio, 3.0d) + 0.532948 * Math.pow(ratio, 2.0d) - 0.447059 * ratio + 0.174262 ) ;
				break;
			case "closed cycle gas turbine":
				verlust = (float) (0.178749* Math.pow(ratio, 4.0d) - 0.653192* Math.pow(ratio, 3.0d) + 0.964704* Math.pow(ratio, 2.0d) - 0.805845*ratio +  0.315584);
				break;
			case "open cycle gas turbine":
				verlust = (float) (0.485049 * Math.pow(ratio, 4.0d) - 1.540723 * Math.pow(ratio, 3.0d) + 1.899607 * Math.pow(ratio, 2.0d) - 1.251502 * ratio + 0.407569);
				break;
			case "oil":
				break;
			default:
				throw new IllegalStateException("No matching description, Type of this FPP is: " + this.description);
		}
		
		if(verlust < 0.0) verlust = 0.0f;
		if(verlust > 1.0) verlust = 1.0f;
		return verlust;
	}

	/**@return       string containing description(type) of this particular flexible power plant */ 
	@Override
	public String getDescription() {
		return this.description;
	}

	/**@return      string containing name of this particular flexible power plant */ 
	@Override
	public String getName() {
		return this.name;
	}

	// Maybe delete in the future
	@Override
	public float getLastAssignments() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// TODO add to Interface and add other methods to update t minus one variables
	public void updatePowerPreceding(){
		if(lastMustRunConfirmed.getQuantity() > 0 ){
			this.powerPreceding = (lastMustRunConfirmed.getQuantity() / dt) + (lastFlexibleRun.getQuantity() / dt) - negativeWork.getQuantity() + positiveWork.getQuantity();
			//System.out.println(this.name +" PowerPreceding = " + this.powerPreceding);
		}else if( lastMustRun.getQuantity() > 0 && sd){
			// Nothing changes so no difference to last PowerPreceding
		}else if( powerPreceding + powerRampUp >= powerMin  && su){ 
			this.powerPreceding = powerMin;
			//System.out.println(this.name +" PowerPreceding = " + this.powerPreceding);
		}else{
			this.powerPreceding = powerStandBy;
			//System.out.println(this.name +" PowerPreceding = " + this.powerPreceding);
		}
		
		this.ccTminusOne = cc;
		this.sdTminusOne = sd;
		this.suTminusOne = su;
	}
	
	/**
	 * Preisvoraussicht der Erzeugungseinheit i zur Prüfung der Anfahrtskostendeckung [-]
	 * @return the number of foresight ticks 
	 */
	public int calculateForesight(){
		int foresight = durationRunning;
		if(isVorlauf()){
			return (int) foresightFPPSU;
		}
		
		if( durations.size() != 0){
			for (Iterator<Integer> iterator = durations.iterator(); iterator.hasNext();) {
				Integer integer = (Integer) iterator.next();
				foresight += integer;
			}
			foresight = foresight/durations.size();
		}else{
			foresight = (int) foresightFPPSU;
		}
		foresight = (int) Math.max(foresight, foresightFPPSU);
		return foresight;
	}
	
	
	public void updateDuration(BidEOM lastConfMustRun) {
		if(Math.abs(lastConfMustRun.getQuantity()) <= epsilon){ 		// means if it is 0
			durations.add(durationRunning);
			durationRunning = 0;
			abbruch = true;
		}else{
			durationRunning++;
		}
	}
	
	
	/** */
	public boolean calculateCC(){
		if(vorlauf){
			return true;
		}
		boolean cc = (cm - startupCosts > 0) && ( Math.abs(powerPreceding) >= epsilon) && (sd == false);
		return cc;
	}
	
	/** */
	public boolean calculateSU(){
		boolean su = ((cc == true) && (sd == false) && (powerPreceding == 0)) || ((cc == true) && (sd == false) && (lastMustRun.getQuantity() == 0) && (powerPreceding != powerMin)) ;
		return su;
	}
	
	
	/** */
	public void calculateSD(){
		this.sd = ( ( lastMustRunConfirmed.getQuantity() !=  lastMustRun.getQuantity() )|| ( lastMustRun.getQuantity() == 0  &&  su == false) );
	}
	

	public float calculatePowerStandBy(){
		if( (powerPreceding <  powerMin && !su) || sd ){
			return  Math.max( powerPreceding - powerRampDown, 0);
		}else if( powerPreceding < powerMin && su){
			 return  Math.min( powerPreceding + powerRampDown, powerMin);
		}else{
			return 0;
		}
	}
	
	
	/** */
	public float calculateCM( long currentTick){
		long s = (long) (currentTick + dFppSU);
		float cm = 0.0f;
		for(long i=s; i<= s + foresightFPPSU ; i++){
			cm = priceForwardCurve.getPriceOnTick(i) -marginalCosts;
		}
		return cm;
	}
	

	public boolean isVorlauf() {
		return vorlauf;
	}

	public void setVorlauf(boolean vorlauf) {
		this.vorlauf = vorlauf;
	}
	
	public float getVariableCosts() {
		return variableCosts;
	}

	public float getPowerPreceding() {
		return powerPreceding;
	}

	public EnergyOnlyMarketOperator getEomMarketOperator() {
		return eomMarketOperator;
	}

	public BidBPM getPositiveCapacity() {
		return positiveCapacity;
	}

	public BidBPM getPositiveCapacityConfirmed() {
		return positiveCapacityConfirmed;
	}

	public BidBPM getNegtiveCapacity() {
		return negtiveCapacity;
	}

	public BidBPM getNegtiveCapacityConfirmed() {
		return negtiveCapacityConfirmed;
	}

	public BidBPM getPositiveWork() {
		return positiveWork;
	}

	public BidBPM getNegativeWork() {
		return negativeWork;
	}

	public BidEOM getLastMustRun() {
		return lastMustRun;
	}

	public BidEOM getLastMustRunConfirmed() {
		return lastMustRunConfirmed;
	}

	public BidEOM getLastFlexibleRun() {
		return lastFlexibleRun;
	}

	public BidEOM getLastFlexibleRunConfirmed() {
		return lastFlexibleRunConfirmed;
	}

	public boolean isCc() {
		return cc;
	}

	public float getCm() {
		return cm;
	}

	public List<Integer> getDurations() {
		return durations;
	}

	public int getDurationRunning() {
		return durationRunning;
	}

	public boolean isSd() {
		return sd;
	}

	public boolean isSu() {
		return su;
	}

	public long getForesightFPPSU() {
		return foresightFPPSU;
	}

	public float getPowerStandBy() {
		return powerStandBy;
	}

	public float getPowerAvailable() {
		return powerPosAvailable;
	}

	public float getCo2certicicateprice() {
		return co2certicicateprice;
	}

	public float getEmmissionfactor() {
		return emmissionfactor;
	}
	
	public int getdFppSU(){
		return this.dFppSU;
	}
	
	public void setCurrentPower(float power){
		this.powerPreceding = power;
	}
	
	@Override
	public void logEOM() {
		
		eomLogger.addToList(new EOMLogInfo(lastMustRun, lastMustRunConfirmed));
		eomLogger.addToList(new EOMLogInfo(lastFlexibleRun, lastFlexibleRunConfirmed));
	}

	@Override
	public void logBPM() {
		bpmPosLogger.addToList(new BPMLogInfo(positiveCapacityConfirmed, positiveWork.getQuantity(), abbruch));
		bpmNegLogger.addToList(new BPMLogInfo(negtiveCapacityConfirmed,  negativeWork.getQuantity(), abbruch));
	}
	
	public void setLoggers(LogWriterBPM bpmPosLogger, LogWriterBPM bpmNegLogger, LogWriterEOM eomLogger	){
		this.bpmPosLogger = bpmPosLogger;
		this.bpmNegLogger = bpmNegLogger;
		this.eomLogger = eomLogger;
	}
	
	
}
