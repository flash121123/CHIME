package seg;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import interfaces.MutivariateDim;

public class VLWord<T> implements MutivariateDim {
	
	public VLWord(int[] dim, String sax) {
		super();
		this.dim = dim;
		this.sax = sax;
		this.len2record = new HashMap<Integer,Set<T>>();
	}
	private int count=0;
	private int biggest_motif_size=0;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	private int[] dim=null;
	//private Set<T> data=null;
	private String sax=null;
	
	public HashMap<Integer, Set<T>> len2record=null;
	
	
	@Override
	public int[] getDim() {
		// TODO Auto-generated method stub
		return dim;
	}

	@Override
	public void setMaxDim() {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public boolean setDim(int[] d) {
		// TODO Auto-generated method stub
		dim=d;
		return true;
	}
	
	/*
	public Set<T> get(Integer key) {
		int gap=key/10;
	  Set<T> s=null;
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
	*/
	
	public int get_id(Integer key) {
		int gap=key/10;
	  int l=50;
	  gap=Math.min(gap, l);
		boolean detected=false;

		for(int i=key;i<key+gap;i++)
		{
			if(len2record.containsKey(i))
			{
				return i;
			}
		}
		if(!detected)
		for(int i=key-1;i>key-gap;i--)
		{
			if(len2record.containsKey(i))
			{
				if(l>key-i)
				{
					return i;
				}
			}
		}
		
		return key;
	}
	

	public boolean containsKey(Object key) {
		return len2record.containsKey(key);
	}

	public void put(Integer key, T s) {
		int l=this.get_id(key);
		this.put2set(l, s);
		count++;
	}

	public void put_direct(Integer key, T s) {
		//int l=this.get_id(key);
		this.put2set(key, s);
		count++;
	}

	
	private void put2set(int l, T s) {
		// TODO Auto-generated method stub
		Set<T> tmp=null;
		if(!len2record.containsKey(l))
		{
			tmp=new HashSet<T>();
			tmp.add(s);
			len2record.put(l,tmp);
		}
		else
		{
			tmp=len2record.get(l);
			tmp.add(s);
		}
		
		if(tmp.size()>biggest_motif_size)
			biggest_motif_size=tmp.size();
		
	}

	public Set<Integer> keySet() {
		return len2record.keySet();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(dim);
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
		VLWord other = (VLWord) obj;
		if (!Arrays.equals(dim, other.dim))
			return false;
		if (sax == null) {
			if (other.sax != null)
				return false;
		} else if (!sax.equals(other.sax))
			return false;
		return true;
	}

	public int size() {
		return len2record.size();
	}


	@Override
	public String toString() {
		return "VLWord: " + sax + ", dim:"+Arrays.toString(dim)+", total motif: "+this.len2record.size()+" total instances: "+String.valueOf(count)+" biggest motif: "+String.valueOf(biggest_motif_size);
	}

	public boolean contains(int l,T value) {
		// TODO Auto-generated method stub
		int ll=get_id(l);
		Set<T> tmp=len2record.get(ll);
		//if(tmp==null)
		//	return false;
		return tmp.contains(value);
	}

	public Set<T> get(int key_id) {
		// TODO Auto-generated method stub
		return len2record.get(key_id);
	}

	public boolean existedKey(int length) {
		// TODO Auto-generated method stub
		int ll=get_id(length);
		Set<T> tmp=len2record.get(ll);
		if(tmp==null)
			return false;
		else
		return true;
	}

	public Collection<Set<T>> values() {
		return len2record.values();
	}

	

}
