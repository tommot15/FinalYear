//HighlightLine class that stores the start and end offset of a token
//finished
public class HighlightLine {
	private int start, end, line;
	private String highlightOn;
	
	public HighlightLine(int s, int e, String ho, int l){
		start = s;
		end = e;
		highlightOn = ho;
		line = l;
	}
	
	public int getStart(){
		return start;
	}
	
	public int getEnd(){
		return end;
	}
	
	public String getWhere(){
		return highlightOn;
	}
	
	public int getLine(){
		return line;
	}
}
