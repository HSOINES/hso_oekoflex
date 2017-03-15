package loggers;


import java.util.Comparator;

import bid.BidEOM;
import builder.OekoFlexContextBuilder;

public class EOMLogInfo {
	
	private BidEOM bid;
	private BidEOM bidconfirmed;
	
	public EOMLogInfo( BidEOM bid, BidEOM bidconfirmed) {
		this.bid = bid;
		this.bidconfirmed = bidconfirmed;
	}
	
	
	public String getDescription() {
		return this.bid.getMarketOperatorListener().getDescription();

	}
	@Override
	public String toString() {
		return    bid.getMarketOperatorListener().getName() 										+ ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(bid.getQuantity())				+ ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(bid.getPrice()) 				+ ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(bidconfirmed.getQuantity())		+ ";"
				+ OekoFlexContextBuilder.defaultNumberFormat.format(bidconfirmed.getPrice())		+ ";";
	}
	
	public String infoAbout(){
		return  this.bid.getTick() + ";" + bid.getMarketOperatorListener().getDescription() + ";"  ;
	}
	
	public static class ComparatorEOM implements Comparator<EOMLogInfo> {
		@Override
		public int compare(EOMLogInfo o1, EOMLogInfo o2) {
			final int compare = o1.bid.getMarketOperatorListener().getDescription().compareTo(o2.bid.getMarketOperatorListener().getDescription());
			if (compare == 0) {
				return  o1.bid.getMarketOperatorListener().getName().compareTo(o2.bid.getMarketOperatorListener().getName());
			}
			return compare;
		}
	}
}
