package utils;

import chime.discretization.TimeSeriesTokenize;

import java.util.Arrays;
import chime.sax.Pair;
import chime.sax.MatchedPair;
import chime.sax.SAXGuard;
import chime.sax.index.BatchSAX;
import interfaces.MSAXNode;
import seg.AdaptiveSAX;
import seg.CHIMEFactory;
import seg.VarMotifEncoder;

/**
Copyright and terms of use:

  The code is made freely available for non-commercial uses only, provided that the copyright 
  header in each file not be removed, and suitable citation(s) be made for papers 

  We are not responsible for any errors that might occur in the code.
 
  The copyright of the code is retained by the authors.  By downloading/using this code you
  agree to all the terms stated above.
 
**/
/**
 * Functions for finding motifs
 * 
 * yfeng
 *
 */
public class StatUtils {
	public static int MAX_DIM=1000;
	public static double mean(double x, double N) {
		return x / N;
	}

	public static double std(double Ex, double Ex2, double N) {
		return Math.sqrt((Ex2 - Ex * Ex / N) / (N - 1));
	}

	public static double var(double Ex, double Ex2, double N) {
		return (Ex2 - Ex * Ex / N) / (N - 1);
	}

	public static double distance(double Ex, double Ey, double Ex2, double Ey2, double Exy, double N) {
		double sigX = std(Ex, Ex2, N);
		double sigY = std(Ey, Ey2, N);
		if (sigX == 0 || sigY == 0)
			return 100;

		double mX = mean(Ex, N);
		double mY = mean(Ey, N);

		double tmpX = 1 / (sigX * sigX);
		double tmpY = 1 / (sigY * sigY);
		double tmpXY = 2 / (sigX * sigY);

		double p1 = tmpX * (Ex2 - 2 * Ex * mX + N * mX * mX);
		double p2 = tmpY * (Ey2 - 2 * Ey * mY + N * mY * mY);
		double t1 = mY * Ex;
		double t2 = mX * Ey;

		double pp2 = -t1 - t2;
		double pp3 = N * mX * mY;
		double tmp = p1 + p2 - tmpXY * (pp2 + pp3 + Exy);
		if (tmp < 0) {
			if (Math.abs(tmp) < 0.0000001)
				tmp = Math.abs(tmp);
			// System.out.println("Error: Distance Belows 0 because of accuracy:
			// "+tmp);
		}
		if (Double.isNaN(tmp)) {
			return 100;
		}
		return Math.sqrt(p1 + p2 - tmpXY * (pp2 + pp3 + Exy));
	}

	public static double tightness = 0.5;

	public static int findResolution(String[] x1, String[] x2, double rdist) {
		int a = 10;

		double tmp = ComputeSAXMinDist(AdaptiveSAX.switchString(x1, a), AdaptiveSAX.switchString(x2, a), a, rdist);

		if (tmp > tightness) {
			a = BinarySearchA(x1, x2, rdist, 0, a);
		} else {
			a = BinarySearchA(x1, x2, rdist, a + 1, 20);
		}
		return a;
	}

	public static double distance(int start1, int start2, int L) {
		double dist = -1;
		double Exy = 0;
		double Ex = TimeSeriesTokenize.x[start1 + L - 1][0] - TimeSeriesTokenize.x[start1][0]
		    + TimeSeriesTokenize.timeseries[start1][0];
		double Ey2 = TimeSeriesTokenize.x2[start2 + L - 1][0] - TimeSeriesTokenize.x2[start2][0]
		    + TimeSeriesTokenize.timeseries[start2][0] * TimeSeriesTokenize.timeseries[start2][0];
		double Ey = TimeSeriesTokenize.x[start2 + L - 1][0] - TimeSeriesTokenize.x[start2][0]
		    + TimeSeriesTokenize.timeseries[start2][0];
		double Ex2 = TimeSeriesTokenize.x2[start1 + L - 1][0] - TimeSeriesTokenize.x2[start1][0]
		    + TimeSeriesTokenize.timeseries[start1][0] * TimeSeriesTokenize.timeseries[start1][0];

		// if(Ex2<7.2480e+09)
		// return 1000;
		for (int i = 0; i < L; i++) {
			Exy += TimeSeriesTokenize.timeseries[start1 + i][0] * TimeSeriesTokenize.timeseries[start2 + i][0];
		}
		dist = distance(Ex, Ey, Ex2, Ey2, Exy, L);
		return dist;
	}

	public static double distance(int start1, int start2, int L, int k) {
		double dist = -1;
		double Exy = 0;
		double Ex = TimeSeriesTokenize.x[start1 + L - 1][k] - TimeSeriesTokenize.x[start1][k]
		    + TimeSeriesTokenize.timeseries[start1][k];
		double Ey2 = TimeSeriesTokenize.x2[start2 + L - 1][k] - TimeSeriesTokenize.x2[start2][k]
		    + TimeSeriesTokenize.timeseries[start2][k] * TimeSeriesTokenize.timeseries[start2][k];
		double Ey = TimeSeriesTokenize.x[start2 + L - 1][k] - TimeSeriesTokenize.x[start2][k]
		    + TimeSeriesTokenize.timeseries[start2][k];
		double Ex2 = TimeSeriesTokenize.x2[start1 + L - 1][k] - TimeSeriesTokenize.x2[start1][k]
		    + TimeSeriesTokenize.timeseries[start1][k] * TimeSeriesTokenize.timeseries[start1][k];

		// if(Ex2<7.2480e+09)
		// return 1000;
		for (int i = 0; i < L; i++) {
			Exy += TimeSeriesTokenize.timeseries[start1 + i][k] * TimeSeriesTokenize.timeseries[start2 + i][k];
		}
		dist = distance(Ex, Ey, Ex2, Ey2, Exy, L);
		return dist;
	}

	private static double ComputeSAXMinDist(String x, String x2, int a, double rdist) {
		// TODO Auto-generated method stub
		try {
			double d = BatchSAX.SAXMinDist(x, x2, a);
			return d / rdist;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	private static int BinarySearchA(String[] x1, String[] x2, double rdist, int start2, int end) {
		// TODO Auto-generated method stub
		int start = (end + start2) / 2;
		// System.out.println(value);
		// System.out.println(index[start][0]+" "+index[start][1]);
		double value = -1;
		if (start == start2 || start == end) {
			return start;
		} else {
			value = ComputeSAXMinDist(AdaptiveSAX.switchString(x1, start), AdaptiveSAX.switchString(x2, start), start, rdist);
			if (value <= tightness) {
				return BinarySearchA(x1, x2, rdist, start + 1, end);
			} else {
				return BinarySearchA(x1, x2, rdist, start2, start - 1);
			}
		}
	}

	public static SAXGuard generateVLSAX(MSAXNode saxsymbol, int saxPAASize, int k) {
		// TODO Auto-generated method stub
		AdaptiveSAX.count++;
		if (saxsymbol == null)
			return null;

		if (saxsymbol.next() == null && saxsymbol.guard() == null)
			return null;

		MSAXNode ns = null;
		if (saxsymbol.guard() == null || saxsymbol.isGuard())
			ns = saxsymbol.next();
		else
			ns = saxsymbol.guard();

		if (ns == null)
			return null;

		int end = (int) ns.getLoc() + ns.getLens();
		if (end >= TimeSeriesTokenize.x.length)
			return null;

		int start = (int) saxsymbol.getLoc();
		int saxWindowSize = end - start + 1;

		double[] paa = ComputePAA(start, k, saxPAASize, saxWindowSize);

		String[] currentString = TimeSeriesTokenize.normalA.get(paa).clone();

		SAXGuard h = new SAXGuard(AdaptiveSAX.switchString(currentString, CHIMEFactory.a), start);

		// Update Link
		h.prev = saxsymbol.prev();

		h.next = saxsymbol.next().next();
		h.setLens(saxWindowSize);
		h.setDim(saxsymbol.getDim());
		return h;
	}

	public static double[] ComputePAA(int i, int k, int saxPAASize, int saxWindowSize) {
		// TODO Auto-generated method stub

		double Ex2 = TimeSeriesTokenize.x2[i + saxWindowSize - 1][k] - TimeSeriesTokenize.x2[i][k];
		double Ex = TimeSeriesTokenize.x[i + saxWindowSize - 1][k] - TimeSeriesTokenize.x[i][k];
		double sig = Math.sqrt((Ex2 - Ex * Ex / saxWindowSize) / (saxWindowSize - 1));
		double means = Ex / saxWindowSize;
		int S = saxWindowSize / saxPAASize;
		double[] paax = new double[saxPAASize];
		// compute PAA for SAX word
		int step = 0;

		if (sig > 0 && !Double.isNaN(sig)) {
			int j = i;
			for (step = 0; step < saxPAASize; step++) {
				// System.out.println(j);
				int n = j + S;
				double ExN = TimeSeriesTokenize.x[n - 1][k] - TimeSeriesTokenize.x[j][k] + TimeSeriesTokenize.timeseries[j][k];
				paax[step] = ExN / (S * sig) - means / sig;
				j = j + S;
			}

		}
		// AdaptiveSAX.count++;
		return paax;
	}

	public static final BatchSAX normalA = new BatchSAX();
	/*
	 * public static MatchedPair matchDim(MatchedPair pair, int[] dim) { // Get
	 * SAX word for all dimension. //int pos1 = pair.observed.getLoc(); //int pos2
	 * = pair.matched.getLoc(); //int len = pair.observed.getLens(); //int len2 =
	 * pair.matched.getLens(); //int paa=HIMEFactory.paa;
	 * 
	 * 
	 * if(ks.length<TimeSeriesTokenize.M) System.out.println("S");
	 * 
	 * 
	 * if(Math.max(pos1,pos2)+Math.max(len, len2)>=TimeSeriesTokenize.N) return
	 * null;
	 * 
	 * int maxdim = TimeSeriesTokenize.M; String[] w1 = new String[maxdim];
	 * double[][] paaSet1 = new double[maxdim][paa];
	 * 
	 * for (int i = 0; i < maxdim; i++) { paaSet1[i] = ComputePAA(pos1, i, paa,
	 * len); String[] currentString = normalA.get(paaSet1[i]).clone(); String sax
	 * = AdaptiveSAX.switchString(currentString, HIMEFactory.a); w1[i] = sax; }
	 * 
	 * String[] w2 = new String[maxdim]; double[][] paaSet2 = new
	 * double[maxdim][paa];
	 * 
	 * for (int i = 0; i < maxdim; i++) { paaSet2[i] = ComputePAA(pos2, i, paa,
	 * len2); String[] currentString = normalA.get(paaSet2[i]).clone(); String sax
	 * = AdaptiveSAX.switchString(currentString, HIMEFactory.a); w2[i] = sax; }
	 * 
	 * // Get matching dimension
	 * 
	 * int[] tmp = new int[maxdim]; //int[] tmp2= new int[maxdim];
	 * 
	 * int plen = 0; //int index2=0; for (int i = 0; i < maxdim; i++) { if
	 * (w1[i].equals(w2[i])) { tmp[plen] = i; plen=plen+1; } //
	 * if(checkpaa(paaSet1[i],paaSet2[i],len)){ // tmp2[index2] = i; // index2++;
	 * // } } int[] d = new int[Math.max(plen, 0)];
	 * 
	 * for (int i = 0; i < plen; i++) { d[i] = tmp[i]; }
	 * 
	 * // int[] dsearch = new int[Math.max(index2, 0)]; // for (int i = 0; i <
	 * index2; i++) { // dsearch[i] = tmp2[i]; // }
	 * 
	 * if (d.length == 0) return null; // Get summary SAX words for s
	 * 
	 * double[] psum = new double[paa];
	 * 
	 * for (int i = 0; i < d.length; i++) { for (int j = 0; j < paa; j++) {
	 * psum[j] += paaSet1[d[i]][j]; } }
	 * 
	 * 
	 * for (int j = 0; j < paa; j++) { psum[j] = psum[j] / d.length; }
	 * 
	 * // Get summary SAX words for s
	 * 
	 * double[] psum2 = new double[paa];
	 * 
	 * for (int i = 0; i < d.length; i++) { for (int j = 0; j < paa; j++) {
	 * psum2[j] += paaSet2[d[i]][j]; } }
	 * 
	 * 
	 * for (int j = 0; j < paa; j++) { psum2[j] = psum2[j] / d.length; }
	 * 
	 * 
	 * String[] currentString = normalA.get(psum).clone(); String sax =
	 * AdaptiveSAX.switchString(currentString, HIMEFactory.a); VarMotifEncoder vs
	 * = new VarMotifEncoder(d, sax, pos1);
	 * 
	 * vs.prev = pair.observed.prev(); vs.next = pair.observed.next();
	 * vs.setLens(pair.observed.getLens());
	 * 
	 * //String[] currentString2 = normalA.get(psum).clone(); //String sax2 =
	 * AdaptiveSAX.switchString(currentString2, HIMEFactory.a); VarMotifEncoder
	 * vs2 = new VarMotifEncoder(d, sax, pos2); vs2.prev = pair.matched.prev();
	 * vs2.next = pair.matched.next(); vs2.setLens(pair.matched.getLens());
	 * matchDim(pair.matched, pair.observed, dim); MatchedPair res = new
	 * MatchedPair(vs,vs2);
	 * 
	 * return res; }
	 */

	public static boolean checkpaa(double[] ds, double[] ds2, int len) {
		// TODO Auto-generated method stub
		double tmp = len / CHIMEFactory.paa;
		double distPAA = 0;

		for (int j = 0; j < ds.length; j++) {
			distPAA += tmp * (ds[j] - ds2[j]) * (ds[j] - ds2[j]);
		}
		distPAA = Math.sqrt(distPAA);
		if (distPAA < CHIMEFactory.thres * len)
			return true;
		else
			return false;
	}

	public static SAXGuard generateVLSAX(VarMotifEncoder saxsymbol, int paa) {
		// TODO Auto-generated method stub
		if (saxsymbol == null)
			return null;

		if (saxsymbol.next() == null && saxsymbol.guard() == null)
			return null;

		MSAXNode ns = null;
		if (saxsymbol.guard() == null || saxsymbol.isGuard())
			ns = saxsymbol.next();
		else
			ns = saxsymbol.guard();

		if (ns == null)
			return null;

		int end = (int) ns.getLoc() + ns.getLens();
		if (end >= TimeSeriesTokenize.x.length)
			return null;
		int start = (int) saxsymbol.getLoc();
		int saxWindowSize = end - start + 1;

		int[] d = saxsymbol.getDim();
		double[][] paa_seg = new double[d.length][paa];
		for (int i = 0; i < d.length; i++)
			paa_seg[i] = ComputePAA(saxsymbol.getLoc(), d[i], paa, saxWindowSize);

		double[] psum = new double[CHIMEFactory.paa];

		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < CHIMEFactory.paa; j++) {
				psum[j] += paa_seg[i][j];
			}
		}

		for (int j = 0; j < CHIMEFactory.paa; j++) {
			psum[j] = psum[j] / d.length;
		}

		String[] currentString = TimeSeriesTokenize.normalA.get(psum).clone();

		SAXGuard h = new SAXGuard(AdaptiveSAX.switchString(currentString, CHIMEFactory.a), start);

		// Update Link
		h.prev = saxsymbol.prev();

		h.next = saxsymbol.next().next();
		h.setLens(saxWindowSize);
		h.setDim(d);

		return h;
	}

	public static double[][] paaSet_observed = new double[MAX_DIM][CHIMEFactory.paa];
	public static double[][] paaSet_matched = new double[MAX_DIM][CHIMEFactory.paa];

	public static MatchedPair matchDim(MatchedPair pair, int[] dim, int k) {
		// TODO Auto-generated method stub
		// Get SAX word for all dimension.
		//System.out.println(dim.length);
		
		int pos_observed = pair.observed.getLoc();
		int pos_matched = pair.matched.getLoc();
		int len_observed = pair.observed.getLens();
		int len_matched = pair.matched.getLens();

		if (isOutofBound(pos_observed, pos_matched, Math.max(len_observed, len_matched)))
			return null;
		
		int maxdim = dim.length;
		int[] dim_updated = null;
		String saxSum = pair.matched.getSAXString();
		MSAXNode[] vob=null;
		
		if (maxdim != 1) {

			String[] w1 = new String[maxdim];
			String[] w2 = new String[maxdim];
	
			for (int i = 0; i < dim.length; i++) {

				paaSet_observed[dim[i]] = ComputePAA(pos_observed, dim[i], CHIMEFactory.paa, len_observed);
				String[] currentString = normalA.get(paaSet_observed[dim[i]]).clone();
				String sax = AdaptiveSAX.switchString(currentString, CHIMEFactory.a);
				w1[i] = sax;

				paaSet_matched[dim[i]] = ComputePAA(pos_matched, dim[i], CHIMEFactory.paa, len_matched);
				currentString = normalA.get(paaSet_matched[dim[i]]).clone();
				sax = AdaptiveSAX.switchString(currentString, CHIMEFactory.a);
				w2[i] = sax;
			}

			// Get matching dimension

			// int[] d=findmatch(w1,w2,dim);

			// int maxdim = dim.length;

			int plen = 0;

			for (int i = 0; i < maxdim; i++) {
				if (w1[i].equals(w2[i])) {
					reuse_array[plen] = dim[i];
					saxSum=saxSum+w1[i];
					plen++;
				}
			}


			// if(plen==0)
			// return null;

			dim_updated = Arrays.copyOf(reuse_array, plen);
			
			if (dim_updated.length == 0)
				return null;
			
			MSAXNode end_matched_node = pair.matched.next();
			MSAXNode end_observed_node = pair.observed.next();
			
			if(dim_updated.length>1)
			{
			vob=new MSAXNode[dim_updated.length];
			
			for(int i=0;i<dim_updated.length;i++)
			{
				String sax_tmp=w1[dim_updated[i]];
				
				if(i==k)
					continue;
				
				if(pair.matched.prev() == null)
					continue;
				if(pair.matched.next() == null)
					continue;
				if(pair.observed.prev() == null)
					continue;
				if(pair.observed.next() == null)
					continue;
				
				Pair end_p = new Pair(end_matched_node.getLoc(),dim_updated[i]);
				Pair start_p = new Pair(pair.matched.prev().getLoc(),dim_updated[i]);
				
				MSAXNode end_dim_match_node=TimeSeriesTokenize.map.get(end_p);
				MSAXNode start_dim_match_node=TimeSeriesTokenize.map.get(start_p);
				
				SAXGuard new_node=new SAXGuard(sax_tmp,pair.matched.getLoc(),pair.matched.getLens(),start_dim_match_node,end_dim_match_node);
				start_dim_match_node.setnext(new_node);
				
				end_p = new Pair(end_observed_node.getLoc(),dim_updated[i]);
				start_p = new Pair(pair.observed.prev().getLoc(),dim_updated[i]);
				
				MSAXNode end_dim_observed_node=TimeSeriesTokenize.map.get(end_p);
				MSAXNode start_dim_observed_node=TimeSeriesTokenize.map.get(start_p);
				
				SAXGuard new_node2=new SAXGuard(sax_tmp,pair.observed.getLoc(), pair.observed.getLens(),start_dim_observed_node,end_dim_observed_node);
				start_dim_observed_node.setnext(new_node2);
				
				vob[i]=new_node;
				
			}
			}
		}
		else
		{
			dim_updated=dim;
		}
		
		/*
		 * int[] dimnew=new int[d.length];
		 * 
		 * for(int i=0;i<d.length;i++) dimnew[i]=dim[d[i]];
		 */
		/*
		 * for(int i=0;i<d.length;i++) { map.put(dimnew[i], w1[d[i]]); }
		 */
		// Get summary SAX words for observed

		// String sax=SummarySAX(paaSet_matched,d);
    
		//System.out.println(dim_updated);
		
		VarMotifEncoder vs = new VarMotifEncoder(dim_updated, saxSum, pos_observed);
		vs.prev = pair.observed.prev();
		vs.next = pair.observed.next();
		vs.setLens(pair.observed.getLens());

		// vs.setSaxMap(map);
		// vs.setDim(d);
		VarMotifEncoder vs2 = new VarMotifEncoder(dim_updated, saxSum, pos_matched);
		vs2.setLens(pair.matched.getLens());
	
		vs2.prev = pair.matched.prev();
		vs2.next = pair.matched.next();
		// vs2.setSaxMap(map);
		// vs2.setDim(d);

		
		
		MatchedPair res = new MatchedPair(vs, vs2);
		res.vob=vob;
		return res;
	}

	private static String SummarySAX(double[][] paaSet1, int[] d) {
		// TODO Auto-generated method stub
		double[] psum = new double[CHIMEFactory.paa];
		System.out.println(d[0]);
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < CHIMEFactory.paa; j++) {
				psum[j] += paaSet1[d[i]][j];
			}
		}

		for (int j = 0; j < CHIMEFactory.paa; j++) {
			psum[j] = psum[j] / d.length;
		}

		// Get summary SAX words for matched

		String[] currentString = normalA.get(psum).clone();
		String sax = AdaptiveSAX.switchString(currentString, CHIMEFactory.a);
		return sax;
	}

	private static int[] reuse_array = new int[1000];

	private static int[] findmatch(String[] w1, String[] w2, int[] dim) {
		// TODO Auto-generated method stub

		int maxdim = dim.length;
		int plen = 0;
		for (int i = 0; i < maxdim; i++) {
			if (w1[i].equals(w2[i])) {
				reuse_array[plen] = dim[i];
				plen++;
			}
		}

		if (plen == 0)
			return null;

		int[] d = Arrays.copyOf(reuse_array, plen);

		/*
		 * for (int i = 0; i < plen; i++) { d[i] = tmp[i]; }
		 */
		return d;
	}

	public static void expand(VarMotifEncoder[] vs) {
		// TODO Auto-generated method stub
		int pos1 = vs[0].getLoc();
		int pos2 = vs[1].getLoc();
		MSAXNode m = vs[0].next();
		String sax = "a";
		String sax2 = "a";

		while (sax.equals(sax2)) {
			int len = m.getLoc() - vs[0].getLoc() + CHIMEFactory.ww;
			double[][] paa = new double[vs[0].getDim().length][CHIMEFactory.paa];
			double[][] paa2 = new double[vs[0].getDim().length][CHIMEFactory.paa];
			int d = vs[0].getDim().length;
			for (int i = 0; i < d; i++) {
				paa[i] = ComputePAA(pos1, vs[0].getDim()[i], CHIMEFactory.paa, len);
			}

			for (int i = 0; i < d; i++) {
				paa2[i] = ComputePAA(pos2, vs[0].getDim()[i], CHIMEFactory.paa, len);
			}

			double[] psum = new double[CHIMEFactory.paa];
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < CHIMEFactory.paa; j++) {
					psum[j] += paa[i][j];
				}
			}
			for (int j = 0; j < psum.length; j++) {
				psum[j] = psum[j] / d;
			}

			double[] psum2 = new double[CHIMEFactory.paa];
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < CHIMEFactory.paa; j++) {
					psum2[j] += paa2[i][j];
				}
			}
			for (int j = 0; j < psum.length; j++) {
				psum2[j] = psum2[j] / d;
			}

			String[] currentString = normalA.get(psum).clone();
			sax = AdaptiveSAX.switchString(currentString, CHIMEFactory.a);

			String[] currentString2 = normalA.get(psum2).clone();
			sax2 = AdaptiveSAX.switchString(currentString2, CHIMEFactory.a);
			m = m.next();
		}

	}

	public static boolean isoverlap = false;

	/**
	 * @param matched:
	 *          Matched SAX word
	 * @param observed:
	 *          Observed SAX word
	 * @param k:
	 *          Dimension
	 * @return
	 */
	public static MatchedPair expand(MSAXNode matched, MSAXNode observed, int k) {
		// Function return 3 non-teriminals. First 2 are the enumerated long
		// subsequences
		// last one is the key SAX word (maxmize same SAX word)

		// SAXGuard[] h=new SAXGuard[2];

		int match_pos = matched.getLoc();
		int observed_pos = observed.getLoc();

		SAXGuard expand_match = new SAXGuard(matched);
		SAXGuard expand_observed = new SAXGuard(observed);

		int len = Math.max(matched.getLens(), observed.getLens());
		// String saxmatch = "", saxobserved = "";
		SAXGuard tmp_observed = new SAXGuard(observed);
		SAXGuard tmp_matched = new SAXGuard(matched);

		while (tmp_matched != null & tmp_observed != null & !isOutofBound(match_pos, observed_pos, len)
		    & !isOverlapped(tmp_matched, tmp_observed)) {

			tmp_observed= enumeration(tmp_matched, tmp_observed, k, true);
			//matched = generateVLSAX(matched, CHIMEFactory.paa, k);
			tmp_matched = enumeration(tmp_observed, tmp_matched, k, true);

			expand_match= tmp_matched;
			expand_observed = tmp_observed;
			
			tmp_observed = generateVLSAX(tmp_observed, CHIMEFactory.paa, k);
			tmp_matched = generateVLSAX(tmp_matched, CHIMEFactory.paa, k);
			//if (matched == null || observed == null)
			//	break;
			if (!check_match(tmp_matched, tmp_observed)) 
				 break;
			len = Math.max(tmp_matched.getLens(), tmp_observed.getLens());			
		}
		
		if (matched == null || observed == null || isOverlapped(matched, observed))
			return null;
		
		MatchedPair p = new MatchedPair(expand_observed, expand_match);

		return p;
	}

	private static SAXGuard enumeration(SAXGuard matched, SAXGuard tmp, int k, boolean is_prev) {
		// TODO Auto-generated method stub
		SAXGuard m = new SAXGuard(tmp);
		while (true) {
		
			tmp = generateVLSAX(tmp, CHIMEFactory.paa, k);
			// matched = generateVLSAX(matched, CHIMEFactory.paa, k);
			if (!check_match(matched, tmp)) {
				if (is_prev)
					return m;
				else
					return tmp;
			}

			if (is_prev) {
				m = new SAXGuard(tmp);
			}
		
		}
	}

	public static boolean check_match(MSAXNode matched, MSAXNode observed) {
		if(matched == null || observed ==null)
			return false;
		// TODO Auto-generated method stub
		int gap = matched.getLens() / 10;
		int l = 50;
		gap = Math.min(gap, l);
		String saxmatch = matched.getSAXString();
		String saxobserved = observed.getSAXString();
		if (saxmatch.equals(saxobserved) & !isOverlapped(matched, observed)
		    & Math.abs(matched.getLens() - observed.getLens()) < gap)
			return true;
		else
			return false;
	}

	private static boolean isOutofBound(int match_pos, int observed_pos, int len) {
		// TODO Auto-generated method stub
		if (Math.max(match_pos, observed_pos) + len + 1 < TimeSeriesTokenize.N)
			return false;
		else
			return true;
	}

	public static boolean isOverlapped(MSAXNode matched, MSAXNode observed) {
		// TODO Auto-generated method stub
		int match_pos = matched.getLoc();
		int observed_pos = observed.getLoc();

		if (Math.min(match_pos + matched.getLens(), observed_pos + observed.getLens()) - 1 >= Math.max(match_pos,
		    observed_pos))
			return true;
		else
			return false;
	}

	public static SAXGuard expand2(MSAXNode record, MSAXNode s, int k) {
		// TODO Auto-generated method stub
		SAXGuard h = null;
		int pos1 = record.getLoc();
		int pos2 = s.getLoc();
		int len = Math.max(record.getLens(), s.getLens());
		MSAXNode m = CHIMEFactory.pointer[k];
		while (m != null && pos1 + len - CHIMEFactory.ww + 1 > m.getLoc())
			m = m.next();
		if (m == null)
			return null;

		// if(record.next().getLens()>300)
		// System.out.println(record.next());
		String sax = record.getSAXString();
		String sax2 = s.getSAXString();
		while (m != null & sax.equals(sax2) & Math.min(pos1, pos2) + len - 1 < Math.max(pos1, pos2)
		    & Math.max(pos1, pos2) + len + 1 < TimeSeriesTokenize.N) {

			len = m.getLoc() - record.getLoc() + m.getLens() - 1;

			// System.out.println("Out of Bound: "+Math.max(pos1, pos2)+" Length:
			// "+len+" Total: "+ TimeSeriesTokenize.N);
			if (Math.max(pos1, pos2) + len + 1 > TimeSeriesTokenize.N)
				break;
			double[] paa = ComputePAA(pos1, k, CHIMEFactory.paa, len);
			double[] paa2 = ComputePAA(pos2, k, CHIMEFactory.paa, len);

			String[] currentString = normalA.get(paa).clone();
			sax = AdaptiveSAX.switchString(currentString, CHIMEFactory.a);

			String[] currentString2 = normalA.get(paa2).clone();
			sax2 = AdaptiveSAX.switchString(currentString2, CHIMEFactory.a);
			m = m.next();
		}

		h = new SAXGuard(sax, record.getLoc());

		h.next = m;
		// h[2].next=m.prev();
		h.prev = record.prev();
		MSAXNode tmp = record.prev();
		if (tmp != null)
			tmp.setnext(h);
		if (pos1 + len + 1 > TimeSeriesTokenize.N)
			h.setLens(TimeSeriesTokenize.N - pos1 + 1);
		else
			h.setLens(len);


		return h;
	}

	public static VarMotifEncoder[] matchDim(MSAXNode s1, MSAXNode s, int k) {
		// TODO Auto-generated method stub
		int pos1 = s1.getLoc();
		int pos2 = s.getLoc();
		int len = s1.getLens();
		int len2 = s.getLens();
		if (Math.max(pos1, pos2) + Math.max(len, len2) > TimeSeriesTokenize.N)
			return null;
		int[] dim = new int[1];
		dim[0] = k;

		VarMotifEncoder vs = new VarMotifEncoder(dim, s1.getSAXString(), pos1);

		vs.prev = s1.prev();
		vs.next = s1.next();
		vs.setLens(s1.getLens());


		// String[] currentString2 = normalA.get(psum).clone();
		// String sax2 = AdaptiveSAX.switchString(currentString2, HIMEFactory.a);
		VarMotifEncoder vs2 = new VarMotifEncoder(dim, s.getSAXString(), pos2);
		vs2.prev = s.prev();
		vs2.next = s.next();
		vs2.setLens(s.getLens());

		VarMotifEncoder[] res = new VarMotifEncoder[2];
		res[1] = vs;
		res[0] = vs2;
		return res;
	}

}
