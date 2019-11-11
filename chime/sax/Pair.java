package chime.sax;

public class Pair {
	
	public Pair( int pos,int dim) {
		super();
		this.dim = dim;
		this.pos = pos;
	}
	
	public int dim=-1;
	public int pos=-1;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dim;
		result = prime * result + pos;
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
		Pair other = (Pair) obj;
		if (dim != other.dim)
			return false;
		if (pos != other.pos)
			return false;
		return true;
	}
}
