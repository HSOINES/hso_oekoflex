package loggers;

import java.util.Comparator;

import bid.BidBPM;
import builder.OekoFlexContextBuilder;

public class BPMLogInfo {
	
	private float amountCalled;
	private boolean abbruch;
	private BidBPM bid;

	public BPMLogInfo(BidBPM bid, float amountCalled, boolean abbruch) {
		
		this.amountCalled = amountCalled;
		this.abbruch = abbruch;
		this.bid = bid;
	}

	public String getDescription() {
		return this.bid.getMarketOperatorListener().getDescription();

	}
	
	@Override
	public String toString() {
		return    bid.getMarketOperatorListener().getName() 								+ ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(bid.getQuantity())		+ ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(bid.getPriceCapacity()) + ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(bid.getPriceWork()) 	+ ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(amountCalled) 			+ ";" 
				+ abbruch																	+ ";"  ;
	}
	
	public String infoAbout(){
		return  this.bid.getTick() + ";" + bid.getMarketOperatorListener().getDescription() + ";"  ;
	}
	
	public static class ComparatorBPM implements Comparator<BPMLogInfo> {
		@Override
		public int compare(BPMLogInfo o1, BPMLogInfo o2) {
			final int compare = o1.bid.getMarketOperatorListener().getDescription().compareTo(o2.bid.getMarketOperatorListener().getDescription());
			if (compare == 0) {
				return  o1.bid.getMarketOperatorListener().getName().compareTo(o2.bid.getMarketOperatorListener().getName());
			}
			return compare;
		}
	}
}
