           package markets;



import agents.MarketOperatorListener;
import bid.BidBPM;
import bid.BidType;
import oekoflex.OekoflexAgent;
import util.TimeUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clears the balancing power market
 * <ul>
 * 	<li> gets bids as supplies or demands from the market traders
 * 	<li> determines the bids that are accepted 
 * 	<li> notifies the market traders that their bids are accepted or denied
 * </ul>
 * <p>
 * <p>
 * Furthermore has getter functions for:
 * <ul>
 * 	<li> JUnit tests and
 * 	<li> the diagram
 * </ul>
 */
public class BalancingMarketOperator implements IBalancingMarketOperator, OekoflexAgent {				// change all modifiers to protected for JUnit tests
    
    /** Name of this specific FPP */
    protected final String name;		
    
    /** List of all positive Supplies/Bids made within dtau */
    protected  List<BidBPM> positiveSupplies = new ArrayList<>();
    
    /** List of all negative Supplies/Bids made within dtau */
    protected  List<BidBPM> negativeSupplies = new ArrayList<>();

    protected float totalClearedPositiveQuantity;
    protected float totalClearedNegativeQuantity;

    protected float lastClearedPositiveMaxPrice;
    protected float lastClearedNegativeMaxPrice;
    
    /** Liste der qunatity für Leistungs Clearing */
    protected DemandBPM demandCapacity;
    
    /** Liste qunatity für Arbeitspreis Clearing */
    protected DemandBPM demandWork;
   
    /**
     * @param name				name of this balancing market operator
     * @param loggingActivated	is the logging activated true = yes , otherwise false
     * @param logDirName		name of directory for the logging
     * @param positiveDemandBPM	amount of positive demand of this balancing market
     * @param negativeDemandBPM amount of negative demand of this balancing market
     * @throws IOException
     */
    public BalancingMarketOperator(String name, final boolean loggingActivated, String logDirName) throws IOException {
        this.name = name;										// TODO add constructor injection 
    }
    
    public void addDemand(DemandBPM demandCapacity, DemandBPM demandWork ){
    	this.demandCapacity = demandCapacity; 
    	this.demandWork		= demandWork;
    }

	@Override
	public void addPositiveSupply(BidBPM supply) {
		if(supply.getQuantity() > 0.0001f){
			positiveSupplies.add(supply);
		}
	}

	@Override
	public void addNegativeSupply(BidBPM supply) {
		if(supply.getQuantity() > 0.0001f){
			negativeSupplies.add(supply);
		}
	}


	@Override
	public void clearMarketCapacityPrice() {
		
		ClearingData positiveClearingData = doClearMarketForCapacity(positiveSupplies, true); // positiveSupplies -> wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
        totalClearedPositiveQuantity = positiveClearingData.getClearedQuantity();
        lastClearedPositiveMaxPrice = positiveClearingData.getLastClearedMaxPrice();
        
        ClearingData negativeClearingData = doClearMarketForCapacity(negativeSupplies, false); // negativeSupplies -> wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
        totalClearedNegativeQuantity = negativeClearingData.getClearedQuantity();
        lastClearedNegativeMaxPrice = negativeClearingData.getLastClearedMaxPrice();
        
        positiveSupplies.clear();
        negativeSupplies.clear();
	}

	private ClearingData doClearMarketForCapacity(List<BidBPM> supplies, boolean pos) {
		
        supplies.sort(new BidBPM.SupplyComparatorCapacity()); // smaller prices before bigger ones, in case they are the same bigger quantities first 
        
        float totalClearedQuantity = 0;
        float lastClearedPrice = 0;
        float quantityDemand;
        
        if(demandCapacity == null || demandWork == null) throw new IllegalStateException("BPM demand files not there");
        quantityDemand = pos ? demandCapacity.getPositiveQuantity(TimeUtil.getCurrentTick()) : demandCapacity.getNegativeQuantity(TimeUtil.getCurrentTick());
        
        List<BidBPM> suplies2 = new ArrayList<>();		// List where all the fullfilled and partially fullfilled bids go
        
        long tick = TimeUtil.getCurrentTick();
        
        for (BidBPM bid : supplies) {
            MarketOperatorListener agent = bid.getMarketOperatorListener(); 
            
            if (totalClearedQuantity >= quantityDemand) {							// none fulfilled bids
                BidBPM bidAnswer = pos ? new BidBPM(0 , 0, 0,tick,BidType.POWER_POSITIVE_CONFIRMED, agent) : new BidBPM(0, 0, 0,tick, BidType.POWER_NEGATIVE_CONFIRMED, agent);
                suplies2.add(bidAnswer);
                doNotify(bidAnswer);	
            } else if (totalClearedQuantity + bid.getQuantity() < quantityDemand) {	// Completely fulfilled bids
            	
            	totalClearedQuantity += bid.getQuantity();						
                lastClearedPrice = bid.getPriceCapacity();
                BidBPM bidAnswer = pos ? new BidBPM(bid.getQuantity(), bid.getPriceCapacity(), bid.getPriceWork(),tick ,BidType.POWER_POSITIVE_CONFIRMED, agent) : new BidBPM(bid.getQuantity(), bid.getPriceCapacity(), bid.getPriceWork(),tick,BidType.POWER_NEGATIVE_CONFIRMED, agent);
                suplies2.add(bidAnswer);
                doNotify(bidAnswer);			

            } else {																// partially fulfilled bids
            	float newQuantity = quantityDemand - totalClearedQuantity;
                totalClearedQuantity = quantityDemand;
                lastClearedPrice = bid.getPriceCapacity();
                bid.setQuantity(newQuantity);					// Adjusting the quantity of the bid to the partially accepted amount
                
                
                
                BidBPM bidAnswer = pos ? new BidBPM(newQuantity, bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_POSITIVE_CONFIRMED, agent) : new BidBPM(bid.getQuantity(), bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_NEGATIVE_CONFIRMED, agent);
                suplies2.add(bidAnswer);
                doNotify(bidAnswer);                
            }
            
        }
        
        if(pos){	
        	positiveSupplies = suplies2;	// Liste anpassen: Nur noch völlständig oder teilweise erfüllte Gebote drin
        	// Hier wird geloggt
        }else{
        	 negativeSupplies = suplies2;	// Liste anpassen: Nur noch völlständig oder teilweise erfüllte Gebote drin
        	 // Hier wird geloggt
        }
        
        
        final float finalTotalClearedQuantity = totalClearedQuantity;
        final float finalLastClearedPrice = lastClearedPrice;
        
        
       
        return new ClearingData() {
            @Override
            public float getClearedQuantity() { return finalTotalClearedQuantity; }
            
            @Override
            public float getLastClearedMaxPrice() { return finalLastClearedPrice; }
        };
       
	}
	
	// ein Gebot zurückgeben, 
	private void doNotify(BidBPM bid) {
		
			MarketOperatorListener agent = bid.getMarketOperatorListener();
			
	        agent.notifyClearingDone(bid);				
	      
	}

	
	
	@Override
	public void clearMarketEnergyPrice() {
        ClearingData positiveClearingData = doClearMarketForEnergy(positiveSupplies, true); 
        totalClearedPositiveQuantity = positiveClearingData.getClearedQuantity();
        lastClearedPositiveMaxPrice = positiveClearingData.getLastClearedMaxPrice();
        
        ClearingData negativeClearingData = doClearMarketForEnergy(negativeSupplies, false); 
        totalClearedNegativeQuantity = negativeClearingData.getClearedQuantity();
        lastClearedNegativeMaxPrice  = negativeClearingData.getLastClearedMaxPrice();
        
        positiveSupplies.clear();
        negativeSupplies.clear();
	}

	private ClearingData doClearMarketForEnergy(List<BidBPM> supplies, boolean pos) {
        supplies.sort(new BidBPM.SupplyComparatorWork()); // Sortierung für Arbeitspreis: smaller prices before bigger ones, in case they are the same bigger quantities first 
        
        float totalClearedQuantity = 0;
        float lastClearedPrice = 0;
        float quantityDemand;
        
        if(demandCapacity == null || demandWork == null) throw new IllegalStateException("BPM demand files not there");
        quantityDemand = pos ? demandWork.getPositiveQuantity(TimeUtil.getCurrentTick()) : demandWork.getNegativeQuantity(TimeUtil.getCurrentTick());
        
        long tick = TimeUtil.getCurrentTick();
        
        for (BidBPM bid : supplies) {
            MarketOperatorListener agent = bid.getMarketOperatorListener(); 
            
            if (totalClearedQuantity >= quantityDemand) {							// none fulfilled bids
            	BidBPM bidAnswer = pos ? new BidBPM(0.0f , bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_POSITIVE_CALL , agent) : new BidBPM(0.0f, bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_NEGATIVE_CALL , agent);
                doNotify(bidAnswer);	
                
            } else if (totalClearedQuantity + bid.getQuantity() < quantityDemand) {	// Completely fulfilled bids
            	
            	totalClearedQuantity += bid.getQuantity();						
                lastClearedPrice = bid.getPriceCapacity();
                
                BidBPM bidAnswer = pos ? new BidBPM(bid.getQuantity(), bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_POSITIVE_CALL , agent) : new BidBPM(bid.getQuantity(), bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_NEGATIVE_CALL , agent);
                doNotify(bidAnswer);

            } else {																// partially fulfilled bids
            	float newQuantity = quantityDemand - totalClearedQuantity;
                totalClearedQuantity = quantityDemand;
                lastClearedPrice = bid.getPriceCapacity();
                BidBPM bidAnswer = pos ? new BidBPM(newQuantity, bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_POSITIVE_CALL , agent) : new BidBPM(newQuantity, bid.getPriceCapacity(), bid.getPriceWork(),tick, BidType.POWER_NEGATIVE_CALL , agent);
                doNotify(bidAnswer);
            }
        }
                
        final float finalTotalClearedQuantity = totalClearedQuantity;
        final float finalLastClearedPrice = lastClearedPrice;
                
        return new ClearingData() {
            @Override
            public float getClearedQuantity() { return finalTotalClearedQuantity; }
            
            @Override
            public float getLastClearedMaxPrice() { return finalLastClearedPrice; }
        };
	}

	// getter functions for:Unit tests, and the diagram //
	@Override
	public float getTotalClearedPositiveQuantity() { return totalClearedPositiveQuantity; }		

	@Override
	public float getTotalClearedNegativeQuantity() { return totalClearedNegativeQuantity; }		

	@Override
	public float getLastClearedNegativeMaxPrice()  { return lastClearedNegativeMaxPrice;  }		

	@Override
	public float getLastClearedPositiveMaxPrice()  { return lastClearedPositiveMaxPrice;  }		
 
	private interface ClearingData { 
        float getClearedQuantity();
        float getLastClearedMaxPrice();
    }

	@Override
	public String getName() {
		return this.name;
	}



}
