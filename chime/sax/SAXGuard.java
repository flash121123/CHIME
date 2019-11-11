package chime.sax;

import interfaces.MSAXNode;

/**
Copyright and terms of use:

  The code is made freely available for non-commercial uses only, provided that the copyright 
  header in each file not be removed, and suitable citation(s) be made for papers 

  We are not responsible for any errors that might occur in the code.
 
  The copyright of the code is retained by the authors.  By downloading/using this code you
  agree to all the terms stated above.
 

**/

/**
 * Data Structure to Store Long SAX word node
 * Revised from Grammar Induction Paper's code wrote by psenin
 * 
 */

public class SAXGuard extends MSAXRecord implements MSAXNode{

	public SAXGuard(String switchString, int start) {
		// TODO Auto-generated constructor stub
		super(switchString,start);
	}

	public SAXGuard(String str, int idx,MSAXRecord s1, MSAXRecord s2) {
		super(str, idx);
		this.prev=s1;
		this.next=s2;
	}
	
	public SAXGuard(String str, int idx, int len,MSAXNode s1, MSAXNode s2) {
		super(str, idx);
		this.prev=s1;
		this.setLens(len);
		this.next=s2;
	}
	
	public SAXGuard(MSAXNode record, int idx, MSAXNode s1, MSAXNode s2) {
		// TODO Auto-generated constructor stub
		super(record.getSAXString(),idx);
		this.prev=s1;
		this.next=s2;
	}

	public SAXGuard(MSAXNode guard) {
		// TODO Auto-generated constructor stub
		super(guard.getSAXString(),guard.getLoc());
		this.setLens(guard.getLens());

		this.prev=guard.prev();
		this.next=guard.next();
		
	}


	@Override
	public boolean isGuard() {
		return true;
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public String toString() {
		return "SAX: "+super.toString() +" Length: "+this.getLens()+" ";
	}


}
