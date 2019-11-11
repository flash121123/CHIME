package seg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import chime.discretization.TimeSeriesTokenize;
import chime.sax.MatchedPair;
import interfaces.MSAXNode;
import utils.StatUtils;

/**
 * Copyright and terms of use:
 * 
 * The code is made freely available for non-commercial uses only, provided that
 * the copyright header in each file not be removed, and suitable citation(s) be
 * made for papers
 * 
 * We are not responsible for any errors that might occur in the code.
 * 
 * The copyright of the code is retained by the authors. By downloading/using
 * this code you agree to all the terms stated above.
 * 
 * 
 **/

public class SAXSymbolTable {

	private static ArrayList<HashMap<String, VariableLengthSAX>> tables = new ArrayList<HashMap<String, VariableLengthSAX>>();

	public static void init() {
		for (int i = 0; i < TimeSeriesTokenize.M; i++) {
			HashMap<String, VariableLengthSAX> table = new HashMap<String, VariableLengthSAX>();
			tables.add(table);
		}
	}

	public static int size() {
		return tables.size();
	}

	public static int cal = 0;

	public static int longest_length[][] = null;

	private static void updateLabel(MatchedPair vs, int k) {
		// TODO Auto-generated method stub
		int[] v = vs.observed.getDim();
		for (int i : v) {
			// MSAXNode tmp = HIMEFactory.pointer[i];
			if (i > k) {

				if (CHIMEFactory.jumpSax[i] != null && vs.observed.getLens() < CHIMEFactory.jumpSax[i].getLens())
					continue;
			
				CHIMEFactory.label[i] = 1;
			}
		}
	}

	public static boolean check(MSAXNode observed, int k, int[] dim) {

		if (observed == null)
			return false;

		if (observed.getLens() > CHIMEFactory.lmax)
			return false;


		observed = StatUtils.generateVLSAX(observed, CHIMEFactory.paa, k);

		if (observed == null)
			return false;

		String key = new String(observed.getSAXString());

		if (!tables.get(k).containsKey(key)) { // If word existed
			VariableLengthSAX vsax = new VariableLengthSAX(key);
			vsax.put(observed.getLens(), observed);
			tables.get(k).put(key, vsax);
			return false;
		} else {

			// if similar length record existed

			VariableLengthSAX tmp = tables.get(k).get(key);
			MSAXNode matched = tmp.get(observed.getLens());

			if (matched == null) { // Not existed similar length matching
				tmp.put(observed.getLens(), observed);
				return false;
			}
			if (StatUtils.isOverlapped(matched, observed)) { // Overlapped
				return false;
			} else {

				MatchedPair pair = StatUtils.expand(matched, observed, k); 
				
				if (pair == null)
					return false;

				MatchedPair subdim_pair = null;

		
				cal++;
				
				subdim_pair = StatUtils.matchDim(pair,dim,k);
				
				if (subdim_pair == null)
					return false;
				
				
				
				boolean noloop = MotifSet.put(subdim_pair);

				if (!noloop)
					return false;

				// If we found a subdimensional motif with size of dimension greater
				// than one
				
				pair.matched.setDim(subdim_pair.matched.getDim());
				pair.observed.setDim(subdim_pair.matched.getDim());

				if (subdim_pair.observed.getDim().length > 1)
					updateLabel(subdim_pair, k);

				boolean isenumerated = checklength(subdim_pair, k);

				longest_length[pair.observed.getLoc()][k] = pair.observed.getLens();
				check(pair.observed, k, CHIMEFactory.dim);
				
				if (isenumerated) {
					check(pair.matched, k,CHIMEFactory.dim);
				}

				return true;
			}
		}
	}

	public static void put(int k, String key, MSAXNode observed)
	{
		if(observed==null)
			return;
		if (!tables.get(k).containsKey(key)) { // If word existed
			VariableLengthSAX vsax = new VariableLengthSAX(key);
			vsax.put(observed.getLens(), observed);
			tables.get(k).put(key, vsax);
			return;
		} else {
			// if similar length record existed

			VariableLengthSAX tmp = tables.get(k).get(key);
			MSAXNode matched = tmp.get(observed.getLens());

			if (matched == null) { // Not existed similar length matching
				tmp.put(observed.getLens(), observed);
				return;
			}
	 }
	}
	public static int getTableSize() {
		int m = 0;
		for (int i = 0; i < TimeSeriesTokenize.M; i++)
			for (String s : SAXSymbolTable.tables.get(i).keySet()) {
				VariableLengthSAX a = SAXSymbolTable.tables.get(i).get(s);
				m = m + a.size();
			}
		return m;
	}

	private static boolean checklength(MatchedPair pair, int k) {
		// TODO Auto-generated method stub
		boolean isenumerated = false;

		if (pair.matched.getLens() > longest_length[pair.matched.getLoc()][k])
			isenumerated = true;

		// Update length

		int[] dim = pair.matched.getDim();

		for (int x : dim) {
			if (pair.matched.getLens() > longest_length[pair.matched.getLoc()][x]) {
				longest_length[pair.matched.getLoc()][x] = pair.matched.getLens();
				CHIMEFactory.label[x] = 2;
			}
			if (pair.observed.getLens() > longest_length[pair.observed.getLoc()][x]) {
				longest_length[pair.observed.getLoc()][x] = pair.observed.getLens();
				CHIMEFactory.label[x] = 2;
			}
		}

		return isenumerated;
	}

	
	public static int[] mixture(int[] dim, int[] dim2) {
		if (dim2 == null)
			return dim;
		// TODO Auto-generated method stub
		Set<Integer> s1 = new HashSet<Integer>();
		for (int x : dim)
			s1.add(x);
		for (int x : dim2)
			s1.add(x);
		int[] array = new int[s1.size()];
		int i = 0;
		for (int x : s1) {
			array[i] = x;
			i++;
		}
		return array;
	}

}
