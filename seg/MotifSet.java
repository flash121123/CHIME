package seg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import java.util.Set;


import chime.sax.MatchedPair;
import interfaces.MSAXNode;
import utils.StatUtils;

/**
Copyright and terms of use:

  The code is made freely available for non-commercial uses only, provided that the copyright 
  header in each file not be removed, and suitable citation(s) be made for papers 

  We are not responsible for any errors that might occur in the code.
 
  The copyright of the code is retained by the authors.  By downloading/using this code you
  agree to all the terms stated above.
 

**/

/**
 * Motif Set: Storing basic function for describing set of motif
 * 
 * @author yfeng
 *
 */

public class MotifSet {

	public static HashMap<Integer, VLWord<Segment>> table = new HashMap<Integer, VLWord<Segment>>();
	//private static HashMap<Integer, MSAXNode> tableVR = new HashMap<Integer, MSAXNode>();
	private static HashMap<Integer, MSAXNode> current_pointer = new HashMap<Integer, MSAXNode>();

	public static int getTableSize() {
		int m = 0;

		for (int s : table.keySet()) {
			VLWord<Segment> a = table.get(s);
			m = m + a.getCount();
		}
		return m;
	}

	public static int size() {
		return table.size();
	}

	public static VLWord<Segment> get(Integer key) {
		return table.get(key);
	}

	public static boolean containsKey(Object key) {
		return table.containsKey(key);
	}

	public static boolean put(MatchedPair pair) {

		int hash_key=pair.matched.hashCode();		
		Segment value = new Segment(pair.observed.getLoc() + 1, pair.observed.getLoc() + pair.observed.getLens() + 1);
		
		VLWord<Segment> s = null;
		
		if (table.containsKey(hash_key)) {

			s = table.get(pair.matched.hashCode());
			int key = pair.matched.getLens();
			if (s.existedKey(key)) {
				if (s.contains(key, value))
					return false;
				

				MSAXNode refer = current_pointer.get(hash_key);
				
				if(StatUtils.isOverlapped(refer, pair.observed))
				{
					return true;
				}
				
				s.put(key, value);
				
				current_pointer.put(key, pair.observed);

				return true;
			}
		} else
			s = new VLWord<Segment>(pair.matched.getDim(), pair.matched.getSAXString());

		Segment value_match = new Segment(pair.matched.getLoc() + 1, pair.matched.getLoc() + pair.matched.getLens() + 1);

		s.put(value_match.getLength(), value_match);


		s.put(value_match.getLength(), value);

		table.put(pair.matched.hashCode(), s);
		current_pointer.put(pair.matched.hashCode(), pair.observed);
		return true;

	}

	static Segment tmp = null;

	public static void clear() {
		table.clear();
	}

	public static boolean containsValue(Object value) {
		return table.containsValue(value);
	}

	public static Set<Integer> keySet() {
		return table.keySet();
	}

	public static boolean put(VarMotifEncoder vs, Segment value) {
		// TODO Auto-generated method stub
		return false;
	}

	public static int c1=0;
	public static void clearTrivial() {
		// TODO Auto-generated method stub
		Set<Integer> seed = keySet();

		int c2=0;
		for(Integer sx : seed)
		{
			VLWord<Segment> tmp_sets = MotifSet.get(sx);
			Collection<Set<Segment>> tmpx = tmp_sets.values();
			for(Set<Segment> tmpy : tmpx)
			{
				ArrayList<Segment> ss2=new ArrayList<Segment>(tmpy);
				Collections.sort(ss2);
				long tmp=-10000;
				for(Segment r : ss2)
				{
					long tmp2=r.getStart();
					if(tmp2-tmp<r.getLength())
					{
						tmpy.remove(r);
						c1++;
						continue;
					}
					tmp=tmp2;
				}
			}
		}
		
	}

}
