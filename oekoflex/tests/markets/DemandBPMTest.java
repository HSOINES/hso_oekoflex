package markets;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;


// 27.02.2017 Test works fine
public class DemandBPMTest {

	
    File forwardFile = new File("run-config/s1/", "BalancingVolume.cfg.csv");
    DemandBPM bCap = new DemandBPM(forwardFile);
    final float epsilon = 0.000001f;
    
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		//  tick	positiveVolume [MW]	 negativeVolume [MW]
		//	     0  	  4600				  4853
		assertTrue(Math.abs(bCap.getPositiveQuantity(0)-4600 )<= epsilon);
		assertTrue(Math.abs(bCap.getNegativeQuantity(0)-4853 )<= epsilon);
		
		//  tick	positiveVolume [MW]	 negativeVolume [MW]
		//  9999  	  4752				  3910
		assertTrue(Math.abs(bCap.getPositiveQuantity(9999)-4752 )<= epsilon);
		assertTrue(Math.abs(bCap.getNegativeQuantity(9999)-3910 )<= epsilon);
		
		//  tick	positiveVolume [MW]	 negativeVolume [MW]
		//  6208  	  4155				  4332
		assertTrue(Math.abs(bCap.getPositiveQuantity(6208)-4155 )<= epsilon);
		assertTrue(Math.abs(bCap.getNegativeQuantity(6208)-4332 )<= epsilon);
		
		//  tick	positiveVolume [MW]	 negativeVolume [MW]
		//  5794  	  4155				  4332
		assertTrue(Math.abs(bCap.getPositiveQuantity(5794)-4155 )<= epsilon);
		assertTrue(Math.abs(bCap.getNegativeQuantity(5794)-4332 )<= epsilon);
		
		//  tick	positiveVolume [MW]	 negativeVolume [MW]
		//  1606  	  4155				  4332
		assertTrue(Math.abs(bCap.getPositiveQuantity(1606)-4155 )<= epsilon);
		assertTrue(Math.abs(bCap.getNegativeQuantity(1606)-4332 )<= epsilon);
		
		//  tick	positiveVolume [MW]	 negativeVolume [MW]
		// 24543  	  3535				  3677
		assertTrue(Math.abs(bCap.getPositiveQuantity(24543)-3535 )<= epsilon);
		assertTrue(Math.abs(bCap.getNegativeQuantity(24543)-3677 )<= epsilon);
		
		//  tick	positiveVolume [MW]	 negativeVolume [MW]
		// 35039  	  3826				  3561
		assertTrue(Math.abs(bCap.getPositiveQuantity(35039)-3826 )<= epsilon);
		assertTrue(Math.abs(bCap.getNegativeQuantity(35039)-3561 )<= epsilon);
		
	}

}
