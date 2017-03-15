package demand;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

//27.02.2017  	The Test are not running correctly due to all the changes done by Thomas Kuenzel
//				TODO Change and write Tests again	

public class ResidualLoadTest {

	ResidualLoad resLo;
	
	float r0 = 8437.25f;
	float r1 = 8476.25f;
	float r2 = 8463f;
	float r3 = 8480f;
	
	@Before
	public void setUp() throws Exception {
		//resLo = new ResidualLoad("run-config/s1/", null);
	}

	@Test
	public void test() {
		assertTrue("ResLoad was: " + resLo.resLoad(0), resLo.resLoad(0) < r0+1.0f && resLo.resLoad(0) > r0-1.0f);
		assertTrue("ResLoad was: " + resLo.resLoad(1), resLo.resLoad(1) < r1+1.0f && resLo.resLoad(1) > r1-1.0f);
		assertTrue("ResLoad was: " + resLo.resLoad(2), resLo.resLoad(2) < r2+1.0f && resLo.resLoad(2) > r2-1.0f);
		assertTrue("ResLoad was: " + resLo.resLoad(3), resLo.resLoad(3) < r3+1.0f && resLo.resLoad(3) > r3-1.0f);
	}

}






/**		csv Inhalte:


tick	| GridLoad for the tests	| Renewables for tests  | Residual Load			|
--------|---------------------------|-----------------------|-----------------------|
		|	demand[MWh]				| supply[MWh]			| [Mwh]					|
--------|---------------------------|-----------------------|-----------------------|
0		|	10590					| 2152,75				| 8437.25				|
1		|	10590					| 2113,75				| 8476.25				|
2		|	10590					| 2127					| 8463					|
3		|	10590					| 2110					| 8480					|
4		|	...						| ...					| ...					|


*/


