package seg;

import java.util.Collection;


import java.util.HashMap;
import java.util.Set;

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
 * 
 * SAX data structure for storing variable length SAX
 * 
 * @author ygao12
 *
 */
public class VariableLengthSAX {

	public String sax;
	public HashMap<Integer,MSAXNode> len2record=new HashMap<Integer,MSAXNode>();
	
	public VariableLengthSAX(String sax) {
		super();
		this.sax = sax;
	}

	public String getSax() {
		return sax;
	}

	public void setSax(String sax) {
		this.sax = sax;
	}

	public int size() {
		return len2record.size();
	}

	public boolean isEmpty() {
		return len2record.isEmpty();
	}

	public MSAXNode get(Integer key) {
		int gap=key/10;
	  MSAXNode s=null;
	  int l=50;
	  gap=Math.min(gap, l);
		

		for(int i=key;i<key+gap;i++)
		{
			if(len2record.containsKey(i))
			{
				l=i-key;
				s=len2record.get(i);
				break;
			}
		}
		if(l!=0)
		for(int i=key-1;i>key-gap;i--)
		{
			if(len2record.containsKey(i))
			{
				if(l>key-i)
				{
					s=len2record.get(i);
					l=key-i;
				}
			}
		}
		
		return s;
	}

	public boolean containsKey(Object key) {
		return len2record.containsKey(key);
	}

	public MSAXNode put(Integer key, MSAXNode s) {
		return len2record.put(key, s);
	}

	public Set<Integer> keySet() {
		return len2record.keySet();
	}

	public Collection<MSAXNode> values() {
		return len2record.values();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sax == null) ? 0 : sax.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableLengthSAX other = (VariableLengthSAX) obj;
		if (sax == null) {
			if (other.sax != null)
				return false;
		} else if (!sax.equals(other.sax))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "("+len2record.size()+")"+"["+len2record.keySet().toString()+"]";
	}
}
