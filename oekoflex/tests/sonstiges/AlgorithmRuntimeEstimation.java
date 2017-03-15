package sonstiges;

import org.junit.Before;
import org.junit.Test;


// This Test is just a runtime evaluation for Thomas
public class AlgorithmRuntimeEstimation {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		final long timeStart = System.nanoTime(); 
		long sum = 0;
		boolean b = true;
		for (long h= 0; h < 10; h++) {
			for (long k = 0; k < 1000; k++) {
				for (long j = 0; j < 1000; j++) {
					for (long i = 0; i < 1000; i++) {
						if (b) {
							sum += 5;
							b = !b;
							sum = (long) ((sum * sum/2 - Math.abs(sum))*Math.acos((double)sum));
						} else {
							sum -= sum / 5 + 8;
							b = !b;
							sum = (long) ((sum * sum/2 - Math.abs(sum))*Math.acos((double)sum));
						}
						
						sum = (long) ((sum * sum/2 - Math.abs(sum))*Math.acos((double)sum));
					}
				}
			}
		}
		
       
        final long timeEnd = System.nanoTime(); 
        System.out.println("Verlaufszeit der Schleife: " + (timeEnd - timeStart) + " Nanosek."); 
	}
	public class TempoClass2 { 

	    
	} 
}
