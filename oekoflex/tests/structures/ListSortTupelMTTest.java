package structures;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


//27.02.2017 	Test works fine
//				TODO instead of just looking at the Lists and see if these are sorted accordingly , could add assertEquals() Methods
public class ListSortTupelMTTest {

	List<TupelMT> set;
	
	@Before
	public void setUp() throws Exception {
		set = new ArrayList<TupelMT>();
		

	}

	@Test
	public void test() {
		
		for(int i = 0; i < 5; i++ ){
			for(int j = 0; j < 5; j++ ){
				TupelMT tupel = new TupelMT(i,j);
				set.add(tupel);
			}
		}
		
		System.out.println("Liste vor dem Sortieren: ");
		print(set);
		System.out.println("\n\n");
		
		set.sort(new TupelMT.ascPriceDescTickComparator());
		
		System.out.println("Liste nach dem Sortieren: ");
		print(set);
		System.out.println("\n\n");
		
		
	}

	private void print(List<TupelMT> set){
		for(TupelMT tupel : set){
			System.out.println(tupel);
		}
	}
	
}
