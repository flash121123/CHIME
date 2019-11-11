package interfaces;

/**
Copyright and terms of use:

  The code is made freely available for non-commercial uses only, provided that the copyright 
  header in each file not be removed, and suitable citation(s) be made for papers 

  We are not responsible for any errors that might occur in the code.
 
  The copyright of the code is retained by the authors.  By downloading/using this code you
  agree to all the terms stated above.
 
**/

public interface MSAXNode {
	
	/**
	 * General Function for SAXRecord and SAXGuard
	 */

  public int getIndexes();
  public boolean isGuard();
  //public boolean isVR();
  

  /**
   * Link-List Data Structure for SAXGuard and SAXRecord
   */
  public MSAXNode next();
  public MSAXNode neighbor();
  public MSAXNode prev();
  public void setnext(MSAXNode s);
  public void setneighbor(MSAXNode s);
  public void setprev(MSAXNode s);
  public Integer[] match(MSAXNode s);  
  
  /**
   * Function for Grammar Induction
   */
  public boolean check();
	public int getLoc();
	public int getLens();
	public MSAXNode guard();
	public void setGuard(MSAXNode s);
	public int hashCode();
	public String getSAXString();
	
  /**
   * Function for Multi-variable
   */
	public int[] getDim();
	public boolean setDim(int[] d);
}
