package chime.sax;

import interfaces.MSAXNode;

public class MatchedPair {
	
public MatchedPair(MSAXNode observed, MSAXNode matched) {
		super();
		this.observed = observed;
		this.matched = matched;
	}

public MSAXNode[] vob=null;
public MSAXNode observed;
public MSAXNode matched;
@Override
public String toString() {
	return "MatchedPair [observed=" + observed + ", matched=" + matched + "]";
}
}
