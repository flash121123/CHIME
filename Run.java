import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import chime.discretization.TimeSeriesTokenize;
import seg.CHIMEFactory;
import seg.MotifSet;
import seg.Segment;
import seg.VLWord;
import utils.StatUtils;

public class Run {

	/**
	 * Main Class for Hierarchical based Motif Enumeration
	 * 
	 * 
	 * @author yfeng
	 * 
	 */

	/*
	 * Default pattern is not input
	 */
	public static int paa = 5, a = 6, x = 300;
	public static String INPUT_FILE = "demo.csv";
	public static String dir = "";
	public static boolean ignore_low_dim=false;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CHIMEFactory.thres = 0.02;
		//TimeSeriesTokenize.M = 10;
		compute_pair=false;
		CHIMEFactory.adaptive = false;
		ignore_low_dim=false;
		
	  Parseinput(args);
		CHIMEFactory.paa = paa;
		CHIMEFactory.ww = x;
		CHIMEFactory.a = a;
    

		CHIMEFactory.runHIME(dir + INPUT_FILE, paa, a, x);

		writePairMotif();

	}


	/**
	 * Parsing input string
	 * 
	 * @param args
	 * 
	 */
	private static void Parseinput(String[] args) {
		// TODO Auto-generated method stub

		if (args.length == 1)
			INPUT_FILE = args[0];
		if (args.length == 2) {
			INPUT_FILE = args[0];
			x = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			INPUT_FILE = args[0];
			paa = Integer.parseInt(args[2]);
			x = Integer.parseInt(args[1]);
		}
		if (args.length == 4) {
			INPUT_FILE = args[0];
			paa = Integer.parseInt(args[2]);
			x = Integer.parseInt(args[1]);
			a = Integer.parseInt(args[3]);
			CHIMEFactory.adaptive = false;
		}
		if (args.length == 5) {
			INPUT_FILE = args[0];
			paa = Integer.parseInt(args[1]);
			x = Integer.parseInt(args[2]);
			a = Integer.parseInt(args[3]);
			CHIMEFactory.thres = Double.parseDouble(args[4]);
		}

	}

	
	
	public static double[][] pairmotif=null;
	public static boolean compute_pair=true;
	/*
	 * 
	 * Post Processing: Based on Meaningful Multivariate Time Series Motif Definition
	 * 
	 */
	
	
	private static void writePairMotif() {
		// TODO Auto-generated method stub
		Set<Integer> seed = MotifSet.keySet();
		
		int i = 1;
		int sizes=0;
		int count=0;
		//System.out.println("Total: "+seed.size());
		for(Integer sx : seed)
		{
			VLWord<Segment> tmp_sets = MotifSet.get(sx);
			count+=tmp_sets.getCount();
		}
		System.out.println("Total Stored Subsequences: "+String.valueOf(count));
		for (Integer s : seed) {
			sizes++;

			VLWord<Segment> tmp_sets = MotifSet.get(s);
			Set<Integer> keys = tmp_sets.keySet();
			int[] dim = tmp_sets.getDim();
			for(int key_id : keys)
			{
			
				ArrayList<Segment> ss2 = new ArrayList<>(tmp_sets.get(key_id));
			
			Collections.sort(ss2);
			
			ArrayList<Segment> sx = new ArrayList<Segment>();

			
			long tmp = -CHIMEFactory.ww - 1000;
			
			for (Segment r : ss2) {
				long tmp2 = r.getStart();
				if (tmp2 - tmp < r.getLength()) {
					continue;
				}
				sx.add(r);
				tmp = tmp2;
			}
			if(sx.isEmpty())
				continue;
			double dmin = 1000000;
			int p1 = 0;
			int p2 = 0;
			int[] v = dim;
			double[] dv = new double[dim.length];
			
			double[] dbest = new double[dim.length];

			for (int c = 0; c < sx.size(); c++) {
				for (int c2 = c + 1; c2 < sx.size(); c2++) {
					if(Math.abs(sx.get(c).getLength()-sx.get(c2).getLength())>sx.get(p1).getLength()/10)
						continue;
					double d = 0;
					for (int k = 0; k < v.length; k++) {
						dv[k]= StatUtils.distance((int) sx.get(c).getStart(), (int) sx.get(c2).getStart(),
						    Math.min(sx.get(c).getLength(), sx.get(c2).getLength()), v[k]);
								d=dv[k]+d;
								count++;
					}
					if (dmin > d) {
						dmin = d;
						for(int num=0;num<dv.length;num++)
							dbest[num]=dv[num];
						p1 = c;
						p2 = c2;
					}
				}
			}
			
			dmin = dmin / v.length;
			int l = Math.min(sx.get(p1).getLength(), sx.get(p2).getLength());
			if(Math.abs(sx.get(p1).getLength()-sx.get(p2).getLength())>sx.get(p1).getLength()/10)
				System.out.println("Error");
			if(compute_pair)
			{
			Arrays.sort(dbest);
			
			}
			if (dmin < l*CHIMEFactory.thres)
			{
				int[] ds2=dim;
				
				ArrayList<Integer> ns=new ArrayList<Integer>();
				for(int num=0;num<dbest.length;num++)
					if(dbest[num]<=l*CHIMEFactory.thres)
						ns.add(ds2[num]);
				String o1 = ns.toString();
				o1 = "dim{" + i + "}=" + o1 + ";";
				int ds=ns.size();
				System.out.println(o1);
				System.out.println("Motif: " + sx.get(p1).getStart() + " " + sx.get(p1).getEnd() + " " + sx.get(p2).getStart()
					   + " " + sx.get(p2).getEnd() + " " + l + " " + dmin+" "+ds);
				i++;
			}
			}
			
		}
		
		System.out.println("Total Counts: " + count);
		long elapsedTime = System.nanoTime() - TimeSeriesTokenize.startTime;
		System.out.println(elapsedTime / 1.0e9 + " seconds" + " at end");
	}
	
}
