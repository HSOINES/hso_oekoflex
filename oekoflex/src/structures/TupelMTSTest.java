package structures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TupelMTSTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void Sort_DSC_ASC_test() {
		List<TupelMTS> low = new ArrayList<TupelMTS>();

		TupelMTS lowPos2 = new TupelMTS(3, 25);
		TupelMTS lowPos3 = new TupelMTS(4, 25);
		TupelMTS lowPos1 = new TupelMTS(14, 30);
		TupelMTS lowPos4 = new TupelMTS(15, 25);

		low.add(lowPos2);
		low.add(lowPos3);
		low.add(lowPos1);
		low.add(lowPos4);

		low.sort(new TupelMTS.descPriceAscTickComparator());

		assertEquals(low.get(0), lowPos1);
		assertEquals(low.get(1), lowPos2);
		assertEquals(low.get(2), lowPos3);
		assertEquals(low.get(3), lowPos4);
	}

	@Test
	public void Sort_ASC_ASC_test() {
		List<TupelMTS> high = new ArrayList<TupelMTS>();

		TupelMTS highPos09  = new TupelMTS(0, 50);
		TupelMTS highPos07  = new TupelMTS(1, 45);
		TupelMTS highPos03  = new TupelMTS(2, 35);
		TupelMTS highPos01  = new TupelMTS(3, 30);
		TupelMTS highPos02  = new TupelMTS(6, 30);
		TupelMTS highPos04  = new TupelMTS(7, 35);
		TupelMTS highPos05  = new TupelMTS(8, 35);
		TupelMTS highPos06  = new TupelMTS(9, 40);
		TupelMTS highPos10  = new TupelMTS(10, 50);
		TupelMTS highPos12  = new TupelMTS(11, 55);
		TupelMTS highPos11  = new TupelMTS(12, 50);
		TupelMTS highPos08  = new TupelMTS(13, 45);

		high.add(highPos09);
		high.add(highPos07);
		high.add(highPos03);
		high.add(highPos01);
		high.add(highPos02);
		high.add(highPos04);
		high.add(highPos05);
		high.add(highPos06);
		high.add(highPos10);
		high.add(highPos12);
		high.add(highPos11);
		high.add(highPos08);
		high.sort(new TupelMTS.ascPriceAscTickComparator());

		assertEquals(high.get(0), highPos01);
		assertEquals(high.get(1), highPos02);
		assertEquals(high.get(2), highPos03);
		assertEquals(high.get(3), highPos04);
		assertEquals(high.get(4), highPos05);
		assertEquals(high.get(5), highPos06);
		assertEquals(high.get(6), highPos07);
		assertEquals(high.get(7), highPos08);
		assertEquals(high.get(8), highPos09);
		assertEquals(high.get(9), highPos10);
		assertEquals(high.get(10), highPos11);
		assertEquals(high.get(11), highPos12);

	}
}
