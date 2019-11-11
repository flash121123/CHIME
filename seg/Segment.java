package seg;


import interfaces.Interval;
import interfaces.MutivariateDim;

	public class Segment implements Comparable<Segment>, Cloneable, Interval, MutivariateDim {

		public long startPos; // interval start
	  public long endPos; // interval stop

	  public Segment() {
	    super();
	    this.startPos = -1;
	    this.endPos = -1;
	  }

	  public Segment(long startPos, long endPos) {
	    super();
	    this.startPos = startPos;
	    this.endPos = endPos;
	  }

	  /**
	   * @param startPos starting position within the original time series
	   */
	  public void setStart(long startPos) {
	    this.startPos = startPos;
	  }

	  /**
	   * @return starting position within the original time series
	   */
	  public long getStart() {
	    return startPos;
	  }

	  /**
	   * @param endPos ending position within the original time series
	   */
	  public void setEnd(long endPos) {
	    this.endPos = endPos;
	  }

	  /**
	   * @return ending position within the original time series
	   */
	  public long getEnd() {
	    return endPos;
	  }


	  public int getLength() {
	    return (int)(this.endPos - this.startPos);
	  }

		@Override
		public int compareTo(Segment o) {
			// TODO Auto-generated method stub
			return Long.compare(this.startPos, o.getStart());
		}

		@Override
		public int[] getDim() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setMaxDim() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean setDim(int[] d) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (endPos ^ (endPos >>> 32));
			result = prime * result + (int) (startPos ^ (startPos >>> 32));
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
			Segment other = (Segment) obj;
			if (endPos != other.endPos)
				return false;
			if (startPos != other.startPos)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "("+String.valueOf(this.getStart())+ ","+String.valueOf(this.getLength())+")" ;
		}
}
