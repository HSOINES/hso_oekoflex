package bid;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class BidEOMTest {

	@SuppressWarnings("unused")
	private void printList(List<BidEOM> list){
		for(BidEOM b : list){
			System.out.println("( " + b.getQuantity() + " , " +b.getPrice() + " ),");
		}
		
	}
	
	// Tests if the list for the supply are in the right order
	// List should be sorted from small to big prices and if the prices are the same, the one with the bigger quantity should be first
	// List should look like this: ( 30 ; -10 ),( 20 ; -10 ),( 10 ; -10 ),( 30 ; 10 ),( 20 ; 10 ),( 10 ; 10 ),( 30 ; 20 ),( 20 ; 20 ),( 10 ; 20 ),( 30 ; 30 ),( 20 ; 30 ),( 10 ; 30 ),
	
	@Test
	public void testSupplyListComparator() {
		
		BidEOM b10_minus10 = new BidEOM(10, -10, 0, null, null);
		BidEOM b20_minus10 = new BidEOM(20, -10, 0, null, null);
		BidEOM b30_minus10 = new BidEOM(30, -10, 0, null, null);
		
		BidEOM b10_10 = new BidEOM(10, 10, 0, null, null);
		BidEOM b20_10 = new BidEOM(20, 10, 0, null, null);
		BidEOM b30_10 = new BidEOM(30, 10, 0, null, null);
		
		BidEOM b10_20 = new BidEOM(10, 20, 0, null, null);
		BidEOM b20_20 = new BidEOM(20, 20, 0, null, null);
		BidEOM b30_20 = new BidEOM(30, 20, 0, null, null);
		
		BidEOM b10_30 = new BidEOM(10, 30, 0, null, null);
		BidEOM b20_30 = new BidEOM(20, 30, 0, null, null);
		BidEOM b30_30 = new BidEOM(30, 30, 0, null, null);
		
		
		List<BidEOM> supplyList = new ArrayList<BidEOM>();
		
		supplyList.add(b10_minus10);
		supplyList.add(b20_minus10);
		supplyList.add(b30_minus10);
		
		supplyList.add(b10_10);
		supplyList.add(b20_10);
		supplyList.add(b30_10);
		
		supplyList.add(b10_20);
		supplyList.add(b20_20);
		supplyList.add(b30_20);
		
		supplyList.add(b10_30);
		supplyList.add(b20_30);
		supplyList.add(b30_30);
		
		supplyList.sort(new BidEOM.SupplyComparatorEOM());
		
		//printList(supplyList);

		assertEquals( b30_minus10  	, supplyList.get(0)  ); // should be		( 30.0 , -10.0 ),
		assertEquals( b20_minus10  	, supplyList.get(1)  ); // should be		( 20.0 , -10.0 ),
		assertEquals( b10_minus10  	, supplyList.get(2)  ); // should be		( 10.0 , -10.0 ),
		assertEquals( b30_10  		, supplyList.get(3)  ); // should be		( 30.0 , 10.0 ),
		assertEquals( b20_10  		, supplyList.get(4)  ); // should be		( 20.0 , 10.0 ),
		assertEquals( b10_10  		, supplyList.get(5)  ); // should be		( 10.0 , 10.0 ),
		assertEquals( b30_20  		, supplyList.get(6)  ); // should be		( 30.0 , 20.0 ),
		assertEquals( b20_20  		, supplyList.get(7)  ); // should be		( 20.0 , 20.0 ),
		assertEquals( b10_20  		, supplyList.get(8)  ); // should be		( 10.0 , 20.0 ),
		assertEquals( b30_30  		, supplyList.get(9)  ); // should be		( 30.0 , 30.0 ),
		assertEquals( b20_30  		, supplyList.get(10) ); // should be		( 20.0 , 30.0 ),
		assertEquals( b10_30  		, supplyList.get(11) ); // should be		( 10.0 , 30.0 ),
		
		
	}

	// Tests if the list for the supply are in the right order
	// List should be sorted from big to small prices and if the prices are the same, the one with the bigger quantity should be first
	// List should look like this: ,( 30 ; 30 ),( 20 ; 30 ),( 10 ; 30 ),( 30 ; 20 ),( 20 ; 20 ),( 10 ; 20 ),( 30 ; -10 ),( 30 ; 10 ),( 20 ; 10 ),( 10 ; 10 ),( 20 ; -10 ),( 10 ; -10 ),
	@Test
	public void testDemandListComparator() {
		
		BidEOM b10_minus10 = new BidEOM(10, -10, 0, null, null);
		BidEOM b20_minus10 = new BidEOM(20, -10, 0, null, null);
		BidEOM b30_minus10 = new BidEOM(30, -10, 0, null, null);
		
		BidEOM b10_10 = new BidEOM(10, 10, 0, null, null);
		BidEOM b20_10 = new BidEOM(20, 10, 0, null, null);
		BidEOM b30_10 = new BidEOM(30, 10, 0, null, null);
		
		BidEOM b10_20 = new BidEOM(10, 20, 0, null, null);
		BidEOM b20_20 = new BidEOM(20, 20, 0, null, null);
		BidEOM b30_20 = new BidEOM(30, 20, 0, null, null);
		
		BidEOM b10_30 = new BidEOM(10, 30, 0, null, null);
		BidEOM b20_30 = new BidEOM(20, 30, 0, null, null);
		BidEOM b30_30 = new BidEOM(30, 30, 0, null, null);
		
		
		List<BidEOM> demandList = new ArrayList<BidEOM>();
		
		demandList.add(b10_minus10);
		demandList.add(b20_minus10);
		demandList.add(b30_minus10);
		
		demandList.add(b10_10);
		demandList.add(b20_10);
		demandList.add(b30_10);
		
		demandList.add(b10_20);
		demandList.add(b20_20);
		demandList.add(b30_20);
		
		demandList.add(b10_30);
		demandList.add(b20_30);
		demandList.add(b30_30);
		
		demandList.sort(new BidEOM.DemandComparatorEOM());
		
		//printList(demandList);
		
		assertEquals( b30_30  		, demandList.get(0)  );	//	should be	    ( 30.0 , 30.0 ),
		assertEquals( b20_30  		, demandList.get(1)  );	//	should be		( 20.0 , 30.0 ),
		assertEquals( b10_30  		, demandList.get(2)  );	//	should be		( 10.0 , 30.0 ),
		assertEquals( b30_20  		, demandList.get(3)  );	//	should be		( 30.0 , 20.0 ),
		assertEquals( b20_20  		, demandList.get(4)  );	//	should be		( 20.0 , 20.0 ),
		assertEquals( b10_20  		, demandList.get(5)  );	//	should be		( 10.0 , 20.0 ),
		assertEquals( b30_10  		, demandList.get(6)  );	//	should be		( 30.0 , 10.0 ),
		assertEquals( b20_10  		, demandList.get(7)  );	//	should be		( 20.0 , 10.0 ),
		assertEquals( b10_10  		, demandList.get(8)  );	//	should be		( 10.0 , 10.0 ),
		assertEquals( b30_minus10  	, demandList.get(9)  );	//	should be		( 30.0 , -10.0 ),
		assertEquals( b20_minus10  	, demandList.get(10) );	//	should be		( 20.0 , -10.0 ),
		assertEquals( b10_minus10  	, demandList.get(11) );	//	should be		( 10.0 , -10.0 ),
		
	}
	
	
}
