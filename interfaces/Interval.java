package interfaces;

/**
Copyright and terms of use:

  The code is made freely available for non-commercial uses only, provided that the copyright 
  header in each file not be removed, and suitable citation(s) be made for papers 

  We are not responsible for any errors that might occur in the code.
 
  The copyright of the code is retained by the authors.  By downloading/using this code you
  agree to all the terms stated above.
 
**/

public interface Interval {
	
	  public void setStart(long startPos);
	  /**
	   * @return starting position within the original time series
	   */
	  public long getStart();

	  /**
	   * @param endPos ending position within the original time series
	   */
	  public void setEnd(long endPos);

	  /**
	   * @return ending position within the original time series
	   */
	  public long getEnd();
	  
	  
	  
}
