package chime.discretization;

import java.io.BufferedInputStream;


import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import chime.sax.MSAXRecord;
import chime.sax.Pair;
import chime.sax.index.BatchSAX;
import interfaces.MSAXNode;
import seg.AdaptiveSAX;
import seg.CHIMEFactory;
import seg.SAXSymbolTable;
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
 * TimeSeriesTokenize is used for streaming read time series and converts the
 * raw data to discrete tokens
 * 
 * @author yfeng
 */
public class TimeSeriesTokenize {
	double[] buffer;

	// SAX Parameters
	int saxWindowSize, saxPAASize, saxAlphabetSize, buff = 10000;

	double normalizationThreshold;

	// Streaming Read State Variable
	boolean isEnd = false, isStart = true;

	// Counting & Performance Measures
	public static long lineC = 0;

	public static long startTime = System.nanoTime();
	public static long lineN = 0;

	// File Reader
	CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	FileInputStream input;
	BufferedReader reader;
	String files;

	// Adaptive SAX Table & Time Series Data
	public static final BatchSAX normalA = new BatchSAX();
	public static double[][] timeseries;
	public static double[][] x;

	public static double[][] x2;

	public double[] xy;

	/**
	 * Conduct streaming discertizing process
	 * 
	 * @return the discretized token sequence
	 * @throws Exception
	 */
	public MSAXNode[] readTokens() throws Exception {
		if (isEnd)
			return null;
		try {
			timeseries = this.readTS(buff);
			if (timeseries.length == 0)
				return null;
			return this.discretize(saxWindowSize, saxPAASize, saxAlphabetSize, normalizationThreshold);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int getDimSize(String files) throws IOException {

		String text = null;
		BufferedReader brTest = new BufferedReader(new FileReader(files));
		text = brTest.readLine();
		//System.out.println(text);
		String[] t = text.split(",");
		brTest.close();
		//System.out.println("Dimension"+String.valueOf(t.length));
		return t.length;
	}

	public TimeSeriesTokenize(String files, int saxWindowSize, int saxPAASize, int saxAlphabetSize,
	    double normalizationThreshold) throws IOException {
		super();
		this.files = files;
		int l = countLines(files);
		N = l;
		M = getDimSize(files);
		timeseries = new double[l][0];
		SAXSymbolTable.longest_length = new int[TimeSeriesTokenize.N][TimeSeriesTokenize.M];
		x = new double[l][0];
		x2 = new double[l][0];

		this.saxWindowSize = saxWindowSize;
		this.saxPAASize = saxPAASize;
		this.saxAlphabetSize = saxAlphabetSize;
		this.normalizationThreshold = normalizationThreshold;

		Path path = Paths.get(files);
		if (!(Files.exists(path))) {
			try {
				throw new Exception("unable to load data - data source not found.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		buffer = new double[this.saxWindowSize];
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		input = new FileInputStream(files);
		InputStreamReader readers = new InputStreamReader(input, decoder);
		reader = new BufferedReader(readers);
	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {

				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	public static int N = -1;
	public static int M = -1;

	public double[][] readTS(int loadLimit) throws Exception {

		timeseries = new double[N][M];
		try {
			String line = null;
			long lineCounter = 0;
			lineN = lineC;
			while ((line = reader.readLine()) != null) {
				lineC++;
				double value = -1;
				try {
					String[] lineSplit = line.trim().split(",");
					if (lineSplit[0].equals("NaN")) {
						value = 0;
					} else {
						for (int i = 0; i < M; i++) {
							value = new BigDecimal(lineSplit[i]).doubleValue();
							timeseries[(int) lineCounter][i] = value;
						}
					}
					lineCounter++;
					/*
					 * if(lineCounter%10000==0) System.out.println(lineCounter);
					 */
					if ((loadLimit > 0) && (lineCounter >= loadLimit)) {
						break;
					}
				} catch (Exception e) {
					for (int i = 0; i < M; i++)
						timeseries[(int) lineCounter][i] = -1.0;
					lineCounter++;
					if ((loadLimit > 0) && (lineCounter >= loadLimit)) {
						break;
					}
					continue;
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			assert true;
		}
		if (!(timeseries.length == 0)) {

			return timeseries;
		}
		isEnd = true;
		return new double[0][0];
	}

	public static HashMap<Pair,MSAXNode> map=new HashMap<Pair,MSAXNode>();
	
	public MSAXNode[] discretize(int saxWindowSize, int saxPAASize, int saxAlphabetSize, double normalizationThreshold)
	    throws Exception {

		// scan across the time series extract sub sequences, and convert
		// them to strings

		// init params
		x = new double[timeseries.length][M];
		x2 = new double[timeseries.length][M];
		double sum = 0, sum2 = 0;
		int i = 0;
		int step = 0;
		int paat = Math.min(32,CHIMEFactory.ww);
		double[][] paa_prev = new double[M][paat];
		double[][] paaTest = new double[M][paat];
		double[] dist=new double[M];
		MSAXNode[] heads = new MSAXNode[M];
		MSAXNode[] prev = new MSAXNode[M];
		for (int k = 0; k < M; k++) {

			for (i = 0; i < timeseries.length; i = i + 1) {
				sum = sum + timeseries[i][k];
				sum2 = sum2 + timeseries[i][k] * timeseries[i][k];
				x[i][k] = sum;
				x2[i][k] = sum2;
			}
		}
		if (!isStart) {
			lineN = lineN - saxWindowSize;
		} else {
			isStart = false;
		}

		// Computing Streaming Linkage for each word
		// MSAXNode head=StreamingLink(k);
		// MSAXNode prev=null;
		for (i = 0; i < paat; i++) {
			for (int k = 0; k < M; k++)
				paa_prev[k][i] = 1000;
		}
		// MSAXNode pointer=head;
		// System.out.println("Alp: "+HIMEFactory.a);
		// System.out.println("Sliding Window: "+HIMEFactory.ww);

		for (i = 0; i < N - CHIMEFactory.ww; i++) {
			if (i % 10000 == 0)
				System.out.println(i);
			String[] sax = new String[M];
			MSAXRecord[] s = new MSAXRecord[M];
			for (int k = 0; k < M; k++) {

				paaTest[k] = StatUtils.ComputePAA(i, k, paat, saxWindowSize);
				double[] p = StatUtils.ComputePAA(i, k, saxPAASize, saxWindowSize);
				String[] currentString = normalA.get(p).clone();
				sax[k] = AdaptiveSAX.switchString(currentString, CHIMEFactory.a);
				// PAA distance based skip sequence
				s[k] = new MSAXRecord(sax[k], i);
			}

			if (i != 0) {

				// PAA distance based skip sequence

				double distPAA = 0;
				//int p=0;
				double factor = CHIMEFactory.ww / 32;
				for (int k = 0; k < M; k++) {
					double d_tmp = 0;
					for (int j = 0; j < paat; j++) {
						d_tmp += factor * (paaTest[k][j] - paa_prev[k][j]) * (paaTest[k][j] - paa_prev[k][j]);
					}
					
					dist[k]=d_tmp;
					if(distPAA< Math.sqrt(d_tmp))
						distPAA = Math.sqrt(d_tmp);
					
					
					//if(dist[k] < HIMEFactory.thres * HIMEFactory.ww)
					//	p++;
				}
				
				//distPAA = distPAA / Math.sqrt(M);
				// HIMEFactory.thres*HIMEFactory.ww

				if (distPAA< CHIMEFactory.thres * 2 * CHIMEFactory.ww && i - prev[0].getLoc() <= 0.1*(CHIMEFactory.ww - 1)) {
					continue;
				}

				for (int k = 0; k < M; k++) {
					// Update link list
					
					prev[k].setnext(s[k]);
					s[k].setprev(prev[k]);
					
					Pair tmp = new Pair(i,k);
					map.put(tmp, s[k]);
				}

			} else
			{
				heads = s;
				for (int k = 0; k < M; k++) {
				Pair tmp = new Pair(i,k);
				map.put(tmp, s[k]);
				}
			}

			for (int k = 0; k < M; k++) {
				// Update link list
				prev[k] = s[k];
				paa_prev[k] = paaTest[k].clone();
			}
		}

		return heads;
	}
	

	private void determineAlp() {
		// Choosing parameter a from tightness of lowerbound
		double est_a = 0;
		int c = 0;
		for (int i = 0; i < 1000; i++) {
			int start1 = randInt(0, timeseries.length - CHIMEFactory.ww);
			int start2 = randInt(0, timeseries.length - CHIMEFactory.ww);
			double rdist = StatUtils.distance(start1, start2, CHIMEFactory.ww);
			String[] str1 = normalA.get(ComputePAA(start1, CHIMEFactory.ww, CHIMEFactory.paa)).clone();
			String[] str2 = normalA.get(ComputePAA(start2, CHIMEFactory.ww, CHIMEFactory.paa)).clone();
			est_a += StatUtils.findResolution(str1, str2, rdist);
			c++;
		}

		CHIMEFactory.a = (int) est_a / c;
		System.out.println("Adaptive Choosing: " + CHIMEFactory.a);
	}

	private double[] ComputePAA(int i, int ww, int paa) {
		// TODO Auto-generated method stub
		double Ex2 = x2[i + saxWindowSize - 1][0] - x2[i][0] + timeseries[i][0] * timeseries[i][0];
		double Ex = x[i + saxWindowSize - 1][0] - x[i][0] + timeseries[i][0];
		double sig = Math.sqrt((Ex2 - Ex * Ex / saxWindowSize) / (saxWindowSize - 1));
		double means = Ex / saxWindowSize;
		int S = saxWindowSize / saxPAASize;
		double[] paax = new double[saxPAASize];
		// compute PAA for SAX word
		int step = 0;
		if (sig > 0 && !Double.isNaN(sig)) {
			for (int j = i; j <= i + saxWindowSize - saxPAASize; j = j + S) {
				int n = j + S;
				double ExN = x[n - 1][0] - x[j][0] + timeseries[j][0];
				paax[step] = ExN / (S * sig) - means / sig;
				step++;
			}
		}
		return paax;
	}

	public static int randInt(int min, int max) {

		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	ArrayList<MSAXNode> ss = new ArrayList<MSAXNode>();

	/**
	 * Generating The Bi-linkage data structure (For new algorithm)
	 * 
	 * @return
	 */
	private MSAXRecord StreamingLink(int k) {
		// TODO Auto-generated method stub
		int i = 0;
		double Ex2 = x2[i + saxWindowSize - 1][k] - x2[i][k] + timeseries[i][k] * timeseries[i][k];
		double Ex = x[i + saxWindowSize - 1][k] - x[i][k] + timeseries[i][k];
		double sig = Math.sqrt((Ex2 - Ex * Ex / saxWindowSize) / (saxWindowSize - 1));
		double means = Ex / saxWindowSize;
		int S = saxWindowSize / saxPAASize;
		double[] paa = new double[saxPAASize];
		// compute PAA for SAX word
		int step = 0;
		if (sig > 0 && !Double.isNaN(sig)) {
			for (int j = i; j <= i + saxWindowSize - saxPAASize; j = j + S) {
				int n = j + S;
				double ExN = x[n - 1][k] - x[j][k] + timeseries[j][k];
				paa[step] = ExN / (S * sig) - means / sig;
				step++;
			}
		}
		// compute PAA under w=32
		step = 0;
		// convert to Multi-resolution SAX word
		String[] currentString = normalA.get(paa).clone();
		// create a SAXRecrod item
		MSAXRecord h = new MSAXRecord(AdaptiveSAX.switchString(currentString, CHIMEFactory.a), i);
		// put to adaptive SAX Forest for determing symbol
		MSAXNode pointer = h;
		for (i = 1; i < timeseries.length - (saxWindowSize - 1); i = i + 1) {
			/*
			 * if(i%10000==0) System.out.println(i);
			 */
			// compute subsections and paa size
			Ex2 = x2[i + saxWindowSize - 1][k] - x2[i][k] + timeseries[i][k] * timeseries[i][k];
			Ex = x[i + saxWindowSize - 1][k] - x[i][k] + timeseries[i][k];
			sig = Math.sqrt((Ex2 - Ex * Ex / saxWindowSize) / (saxWindowSize - 1));
			means = Ex / saxWindowSize;
			S = saxWindowSize / saxPAASize;
			paa = new double[saxPAASize];

			// compute PAA for SAX word
			step = 0;
			if (sig > 0 && !Double.isNaN(sig)) {
				for (int j = i; j <= i + saxWindowSize - saxPAASize; j = j + S) {
					int n = j + S;
					double ExN = x[n - 1][k] - x[j][k] + timeseries[j][k];
					paa[step] = ExN / (S * sig) - means / sig;
					step++;
				}
			}
			// convert to Multi-resolution SAX word
			currentString = normalA.get(paa).clone();
			// create a SAXRecrod item
			MSAXRecord sax = new MSAXRecord(AdaptiveSAX.switchString(currentString, CHIMEFactory.a), i);
			// put to adaptive SAX Forest for determing symbol
			pointer.setneighbor(sax);
			ss.add(pointer);
			pointer = pointer.neighbor();
		}

		reverselink(h);
		return h;
	}

	private void reverselink(MSAXRecord h) {
		// TODO Auto-generated method stub
		MSAXNode p = null, n;
		p = h;
		while (p.next() != null) {
			n = p.next();
			MSAXNode ss = n.prev();
			ss = p;
			p = p.next();
		}
	}

	public void close() throws IOException {
		reader.close();
	}

	public int getBuff() {
		return buff;
	}

	public void setBuff(int buff) {
		this.buff = buff;
	}

}