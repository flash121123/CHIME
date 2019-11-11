package seg;

import chime.discretization.TimeSeriesTokenize;
import chime.sax.SAXGuard;
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
 * Main Function for CHIME
 * 
 * 
 * @author yfeng
 * 
 */
public final class CHIMEFactory {

	public int countsum = 0, countsuccess = 0;
	public static int ww, CHUNK_SIZE = 100000000;
	public static double thres;

	/**
	 * Disabling the constructor.
	 */
	private CHIMEFactory() {
		assert true;
	}

	public static long lens = 0;
	public static int paa;
	public static int a;
	public static boolean adaptive = false;
	public static int[] label = null;
	public static SAXGuard[] jumpSax = null;
	public static SAXGuard[] jumpSax2 = null;
	public static MSAXNode[] pointer;

	public static int[] dim=null;
	public static int lmax=8000000;
	
	/**
	 * Digests a string of symbols separated by space.
	 * 
	 * @param inputString
	 *          The string to digest. Symbols expected to be separated by space.
	 * 
	 * @return The top rule handler.
	 * @throws Exception
	 */
	public static void runHIME(String datafile, int paa, int alp, int w) throws Exception {

		// clear global collections
		//
		ww = w;
		int count = 1;
		TimeSeriesTokenize tokens = new TimeSeriesTokenize(datafile, w, paa, alp, 0.05);
		tokens.setBuff(CHUNK_SIZE);

		MSAXNode[] heads;
		String last = "";

		heads = tokens.readTokens();
		System.gc();

		if (heads[0].next() == null)
			return;
		@SuppressWarnings("unused")
		int currentPosition = 0;
		boolean ischeck = true;
		long tok = TimeSeriesTokenize.lineN;

		pointer= new MSAXNode[TimeSeriesTokenize.M];

		dim=new int[TimeSeriesTokenize.M];
		for (int j = 0; j < heads.length; j++)
		{
			pointer[j] = heads[j];
			dim[j]=j;
		}

		SAXSymbolTable.init();
		boolean isend = false;
		int pos = 0, pos2 = 0;
		int ss=0;
		int count2=1;
		/*
		 * 
		 * Main Process
		 * 
		 */
		while (!isend && pointer[0].next() != null) {
			//System.out.println("position: "+pointer[0].getLoc());
			ss++;
			CHIMEFactory.label = new int[TimeSeriesTokenize.M];
			jumpSax=new SAXGuard[TimeSeriesTokenize.M];
			jumpSax2=new SAXGuard[TimeSeriesTokenize.M];
			// int pos=pointer[0].getLoc();
			pos = pos2;
			pos2 = Integer.MAX_VALUE;
			
			for (int k1 = 0; k1 < TimeSeriesTokenize.M; k1++) {
				
				if (pointer[k1] == null) {
					continue;
				}

				if (pointer[k1].getLoc() - pos > 0.2 * CHIMEFactory.ww) {
					if (pos2 > pointer[k1].getLoc())
						pos2 = pointer[k1].getLoc();
					continue;
				}

				//SAXGuard s=null;
				
				if(CHIMEFactory.label[k1] == 0) {
					if (pointer[k1] == null) {
						isend = true;
						break;
					}
					
					lens += 1;
					
					MSAXNode saxsymbol = pointer[k1];
					
					int[] tmp=new int[1];
					tmp[0]=k1;
					saxsymbol.setDim(tmp);
					// Forms Longer SAX word
					SAXSymbolTable.check(saxsymbol, k1, dim);
					 
				}
				
				pointer[k1] = pointer[k1].next();
				
				if (pointer[k1] == null) {
					continue;
				}
				if (pos2 > pointer[k1].getLoc())
					pos2 = pointer[k1].getLoc();

			}

			if (pointer[0].getLoc()  > count*10000) {
				count++;
				//MotifSet.clearTrivial();
				long elapsedTime = System.nanoTime() - TimeSeriesTokenize.startTime;
				
				System.out.println(elapsedTime / 1.0e9 + " seconds" + " at " + pointer[0].getLoc());
				
			}
		}

		long elapsedTime = System.nanoTime() - TimeSeriesTokenize.startTime;
		System.out.println(elapsedTime / 1.0e9 + " seconds" + " at " + TimeSeriesTokenize.lineC);

	}
}