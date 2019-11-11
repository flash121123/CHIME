package seg;


/**
Copyright and terms of use:

  The code is made freely available for non-commercial uses only, provided that the copyright 
  header in each file not be removed, and suitable citation(s) be made for papers 

  We are not responsible for any errors that might occur in the code.
 
  The copyright of the code is retained by the authors.  By downloading/using this code you
  agree to all the terms stated above.
 
 
**/
public class AdaptiveSAX {

	public static int count=0;

	/**
	 *  A class with key - SAX word and value - level of alphabeta size
	 *  @author yfeng
	 */

	public static String switchString(String[] key, int level) {
		// TODO Auto-generated method stub
		char[] tmp=new char[key.length];
		level=level-1;
		for(int i=0;i<key.length;i++)
		{
			tmp[i]=key[i].charAt(level);
		}
		
		return String.valueOf(tmp);
	}

	
}
