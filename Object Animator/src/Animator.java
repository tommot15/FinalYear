import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

public class Animator {
	JTextArea main, other;
	List<HighlightLine> highlighters = new ArrayList<HighlightLine>();
	List<Object> vars = new ArrayList<Object>();
	int count, thisLine;
	DefaultHighlighter.DefaultHighlightPainter hilite;
	PrintObjects po;
	
	public Animator(JTextArea m, JTextArea o, List<HighlightLine> hl, PrintObjects p){
		count = 0;
		main = m;
		other = o;
		highlighters = hl;
		po = p;
		hilite = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	}
	
	public void highlight(){
		
		int delay = 100;
		
	    ActionListener tp = new ActionListener(){

	        public void actionPerformed(ActionEvent ae){
	        	try{
	        		if(highlighters!=null && !highlighters.isEmpty()){
	        			//Removes the previous highlighter
	        			removeHighlight(); 
	        			
	        			//For use when sending an object through
	        			thisLine = highlighters.get(count).getLine(); 
	        			
	        			//Checks which text area the object has come from
	        			if(highlighters.get(count).getWhere().equals("Main")){	
							main.getHighlighter().addHighlight(highlighters.get(count).getStart(), highlighters.get(count).getEnd(), hilite);
						}
						else{
							other.getHighlighter().addHighlight(highlighters.get(count).getStart(), highlighters.get(count).getEnd(), hilite);
						}
					
	        			//Checks whether an object has been created on the previous line, or has been printed before and sends it to PrintObjects
						for(Object o : vars){
							Parser p = (Parser) o;
							try{
								if(thisLine >= p.getLineNo() + 1 && p.isPainted() == false && highlighters.get(count+1).getLine() != thisLine && highlighters.get(count).getWhere().equals("Main")){
									p.painted = true;
									
									//Clears the rects list array for use in the PrintObjects
									po.rects.clear(); 
									po.objects.add(p);
				
									//Repaints the animation panel
									po.repaint();
								}
							}
							catch(IndexOutOfBoundsException ie){
								
							}
						}
						//Checks whether all of the lines have been highlighted and stops the timer
						if(count == highlighters.size() - 1){ 
							((Timer)ae.getSource()).stop();
							main.getHighlighter().removeAllHighlights();
							other.getHighlighter().removeAllHighlights();
						}
						count++;
	        		}
			    } catch (BadLocationException ex) {
			        ex.printStackTrace();
			    }
	        }
	    };
	    //Sets a timer to go off after the delay has elapsed
	    Timer timer = new Timer(delay, tp);
	    timer.setRepeats(true);
	    timer.start();      
	}
	
	public void removeHighlight(){
		try{
			//Removes all the highlights of the current point in the highlighters list
			if(highlighters.get(count).getWhere().equals("Main")){
				main.getHighlighter().removeAllHighlights();
			}
			else{
				other.getHighlighter().removeAllHighlights();
			}
		}
		catch(IndexOutOfBoundsException ie){
			System.out.println(ie);
		}
	}
}