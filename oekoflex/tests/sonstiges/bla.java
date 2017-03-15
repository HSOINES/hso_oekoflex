package sonstiges;

import org.junit.Before;
import org.junit.Test;


// This Test was intended to test a theory for Thomas.
//     Has no real implication for the program itself.
public class bla {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		double cost = 0.0;
		double eingenommen = 0.0;
		
		//System.out.println((22.0+17.0+19.5+14.0+18.0+15.5+(53.0/3.0))/7);
		
		double factor = ((22.0 + 17.0 + 22.0 + 17.0 + 14.0 + 22.0 + 14.0 + 14.0 + 17.0 + 14.0 + 17.0 + 22.0) / 12);

		System.out.println(factor);
//		double factor = ((22.0 + 17.0 + 19.5 +14.0 + 18.0 + 15.5 + (53.0/3.0)) / 7);
		double diff = 0.0;
		
		
		for (int i = 0; i < 100000000; i++) {
			int fall =  ((int) (Math.random() * 9.0));
			switch (fall) {
			case 0:								
				cost = cost + 0.0 + 0.0 + 0.0;
				eingenommen = eingenommen + 0.0 + 0.0 + 0.0;
				break;
			case 1:
				cost = cost + 0.0 + 0.0 + 22.0;						// 22
				eingenommen = eingenommen + 0.0 + 0.0 + factor;
				break;
			case 2:
				cost = cost + 0.0 + 0.0 + 17.0;						// 17
				eingenommen = eingenommen + 0.0 + 0.0 + factor;
				break;
			case 3:
				cost = cost + 0.0 + 17.0 + 22.0;				// 19.5
				eingenommen = eingenommen +  factor +  factor;
				break;
			case 4:										// 14
				cost = cost + 0.0 + 0.0 + 14.0 ;
				eingenommen = eingenommen + 0.0 + 0.0 + factor;
				break;
			case 5:										// 18
				cost = cost + 0.0 + 14.0 + 22.0;
				eingenommen = eingenommen + 0.0 + factor +  factor;
				break;
			case 6:										// 15.5
				cost = cost + 0.0 + 14.0 + 17.0 ;
				eingenommen = eingenommen + 0.0 + factor +  factor ;
				break;
			case 7:										//	53.0/3.0
				cost = cost + 14.0 + 17.0 + 22.0;
				eingenommen = eingenommen + factor +  factor +  factor;
				break;

			default:
				break;
			}
			
			
		}
		diff = eingenommen - cost;
		System.out.println("Kosten:      " + cost);
		System.out.println("Eingenommen: " + eingenommen);
		System.out.println("Differenz:   " + diff);
	}

}
