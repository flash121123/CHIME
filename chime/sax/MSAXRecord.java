package chime.sax;

import java.util.ArrayList;
import java.util.Arrays;

import interfaces.MSAXNode;
import interfaces.MutivariateDim;
import seg.CHIMEFactory;

/**
Copyright and terms of use:

  The code is made freely available for non-commercial uses only, provided that the copyright 
  header in each file not be removed, and suitable citation(s) be made for papers 

  We are not responsible for any errors that might occur in the code.
 
  The copyright of the code is retained by the authors.  By downloading/using this code you
  agree to all the terms stated above.
 
**/

/**
 * Data Structure to Store SAX records
 * Revised from Grammar Induction Paper's code wrote by psenin
 * 
 */
public class MSAXRecord implements Comparable<MSAXRecord>,MSAXNode,MutivariateDim {


	private int hashvalue_d=-1;
	private int hashvalue_sax=-1;

  /** The index of occurrences in the raw sequence. */
  private int occurrences=-1;

  /** Disable the constructor. */
	public MSAXRecord() {
    super();
  }
	
	protected String strs=null;
	int[] dim=null;

  public MSAXRecord(String str, int idx) {
		// TODO Auto-generated constructor stub
  	hashvalue_sax=str.hashCode();
  	 strs = str;
  	 this.occurrences = idx;
  	 this.lens=CHIMEFactory.ww;
	}  


  public int lens=-1;

	public int getLens() {
		return lens;
	}

	public void setLens(int lens) {
		this.lens = lens;
	}
	
	protected int base=2*CHIMEFactory.ww;

	
	

  /**
   * Get all indexes.
   * 
   * @return all indexes.
   */
  public int getIndexes() {
    return this.occurrences;
  }

  /**
   * This comparator compares entries by the length of the entries array - i.e. by the total
   * frequency of entry occurrence.
   * 
   * @param o an entry to compare with.
   * @return results of comparison.
   */
  @Override
  public int compareTo(MSAXRecord o) {
    int a = this.occurrences;
    int b = o.getLoc();
    if (a == b) {
      return 0;
    }
    else if (a > b) {
      return 1;
    }
    return -1;
  }


	@Override
	public String toString() {
		int tmp=this.occurrences+this.lens-1;
		return "("+ strs +","+this.occurrences+"-"+String.valueOf(tmp)+")";
	}


	public String getSaxString() {
		return strs;
	}

	public void setSaxString(String saxString) {
		this.strs = saxString;
	}
  
	public boolean isGuard() {
		return false;
	}
  
	
	public MSAXNode neighbor=null,next=null,prev=null,guard=null;


	public int getLoc() {
		// TODO Auto-generated method stub
		return occurrences;
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
		// TODO Auto-generated method stub
		return true;
	}



	@Override
	public MSAXNode guard() {
		// TODO Auto-generated method stub
		
		return this.guard;
	}


	@Override
	public void setGuard(MSAXNode s) {
		// TODO Auto-generated method stub
		this.guard=s;
	}


	@Override
	public void setnext(MSAXNode s) {
		// TODO Auto-generated method stub
		this.next=s;
	}


	@Override
	public void setneighbor(MSAXNode s) {
		// TODO Auto-generated method stub
		this.neighbor=s;
	}


	@Override
	public void setprev(MSAXNode s) {
		// TODO Auto-generated method stub
		this.prev=s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.hashvalue_d;
		result = prime * result + ((strs == null) ? 0 : hashvalue_sax);
		return result;
	}
	


	@Override
	public int[] getDim() {
		// TODO Auto-generated method stub
		return dim;
	}


	@Override
	public void setMaxDim() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean setDim(int[] d) {
		// TODO Auto-generated method stub
		this.dim=d;
		hashvalue_d=Arrays.hashCode(d);
		return true;
	}


	@Override
	public String getSAXString() {
		// TODO Auto-generated method stub
		return strs;
	}

	@Override
	public Integer[] match(MSAXNode s) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}