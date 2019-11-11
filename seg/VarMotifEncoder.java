package seg;

import java.util.Arrays;

import java.util.HashMap;

import chime.sax.SAXGuard;
import interfaces.MSAXNode;
import interfaces.MutivariateDim;
import interfaces.SAXWord;

public class VarMotifEncoder implements MutivariateDim, SAXWord, MSAXNode {

	private int[] dim=null;
	private int[] dsearch=null;
	
	private String sax=null;
	private HashMap<Integer,String> saxmap=null;
	
	private int len=0;
	public HashMap<Integer, String> getSaxMap() {
		return saxmap;
	}

	public void setSaxMap(HashMap<Integer, String> m) {
		 saxmap=m;
	}
	
	public SAXGuard createGuard(int k) {
		if(!saxmap.containsKey(k))
			return null;
		SAXGuard res=new SAXGuard(this);
		res.setDim(dim);
		String str=saxmap.get(k);
		res.setSaxString(str);

		res.setLens(this.getLens());
		
		return res;
	}
	
	public void setSaxString(String saxString) {
		this.sax = saxString;
	}

	private int hashvalue_d=-1;
	private int hashvalue_sax=-1;
	
	public VarMotifEncoder(int[] d, String sax2, int occur) {
		this.setDim(d);
		this.setSAX(sax2);
		this.occurences=occur;
	}

	@Override
	public int[] getDim() {
		return dim;
	}

	@Override
	public void setMaxDim() {
		return;
	}

	@Override
	public boolean setDim(int[] d) {
		dim=d;
		hashvalue_d=Arrays.hashCode(dim);
		return true;
	}

	@Override
	public String getSAX() {
		return sax;
	}

	@Override
	public boolean setSAX(String s) {

		sax=s;
		hashvalue_sax=sax.hashCode();
		return true;
	}

	public MSAXNode neighbor=null,next=null,prev=null,guard=null;
	private int occurences;

	public int getLoc() {
		return occurences;
	}

	@Override
	public MSAXNode next() {
		return this.next;
	}


	@Override
	public MSAXNode neighbor() {
		return this.neighbor;
	}


	@Override
	public MSAXNode prev() {
		return this.prev;
	}


	@Override
	public boolean check() {
		return true;
	}



	@Override
	public MSAXNode guard() {
		return this.guard;
	}


	@Override
	public void setGuard(MSAXNode s) {
		this.guard=s;
	}


	@Override
	public void setnext(MSAXNode s) {
		this.next=s;
	}


	@Override
	public void setneighbor(MSAXNode s) {
		this.neighbor=s;
	}


	@Override
	public void setprev(MSAXNode s) {
		this.prev=s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.hashvalue_d;
		result = prime * result + ((sax == null) ? 0 : hashvalue_sax);
		return result;
	}

	@Override
	public String toString() {
		return "SubMoitf dim=" + Arrays.toString(dim) + ", (" + sax + ","+ this.occurences+","+this.getLens()+ ")";
	}

	@Override
	public int getIndexes() {
		return 0;
	}

	@Override
	public boolean isGuard() {
		return false;
	}

	@Override
	public Integer[] match(MSAXNode s) {
		return null;
	}

	@Override
	public int getLens() {
		return len;
	}

	
	public void setLens(int l) {
		len=l;
	}
	
	@Override
	public String getSAXString() {
		return sax;
	}


	
	public int[] getDsearch() {
		return dsearch;
	}

	public void setDsearch(int[] dsearch) {
		this.dsearch = dsearch;
	}
	
}
