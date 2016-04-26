import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PrintObjects extends JPanel{
	
	List<Object> objects = new ArrayList<Object>();
	List<Rectangle2D> rects = new ArrayList<Rectangle2D>();
	List<Point> pointList = new ArrayList<Point>();
	int closestDistance;
	List<Point> linePoints = new ArrayList<Point>();
	Graphics2D g2d;
	Rectangle2D lineOne, lineTwo;
	
	public PrintObjects(){

	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//sets up the variables for printing to the screen
		g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D rect, objRect;
        Rectangle2D arrayRect;
        int x, y, h, w, textX, textY, tmpW; 
        int tmpWidth;
        String varString;
        String val;
        
        x = 10;
		y = 10;
		tmpW = 0;
		
		//Enables antialiasing to make sure the rectangles aren't jagged
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(qualityHints);
		
        //starts printing a rectangle for each object
		for(Object r : objects){
			if(r instanceof Parser){
				Parser p = (Parser)r;
				
				//gets the height and width of the objects name to be used for the dimensions of the rectangle
				rect = fm.getStringBounds(p.getName(), g2d);
				objRect = rect;
				h = (int)rect.getHeight()+20;
				w = (int)rect.getWidth()+20;
				
				//sets the x and y to be shown in a grid with two columns
				if(objects.indexOf(p) == 0 || objects.indexOf(p)%2 == 0){
					x = 50;
					y = y + h + 50;
				}
				else{
					x = this.getWidth() - tmpW - 100;
				}
				
				tmpWidth = w;
				
				//gets the width of every variable in the object
				//to be used for the printing of the rectangle
				
				for(Object o : p.getVars()){
					
					if(o instanceof Variable){
						Variable var = (Variable) o;
						
						//Keeps track of the total height of all variables
						h = h + (int)rect.getHeight();
						
						//Formats the output of the variables to get the width the rectangle needs to be
						if(var.getValue().equals("")){
							val = "\"\"";
						}
						else{
							val = var.getValue();
						}
						varString = var.getDataType() + " " + var.getName() + ": \"" + var.getValue() + "\"";
						rect = fm.getStringBounds(varString, g2d);
						
						//Keeps track of the widest variable
						if(tmpWidth < (int)rect.getWidth()){
							tmpWidth = (int)rect.getWidth();
						}
					}
					
					if(o instanceof Parser){
						Parser var = (Parser) o;
						
						//Keeps track of the total height of all the variables
						h = h + (int)rect.getHeight();
						varString = var.cName + " " + var.getName();
						rect = fm.getStringBounds(varString, g2d);
						
						//Keeps track of the widest variable
						if(tmpWidth < (int)rect.getWidth()){
							tmpWidth = (int)rect.getWidth();
						}
					}
				}
				
				//Sets the width of the current rectangle to the widest variable
				w = tmpWidth + 10;
				
				//adds a new rectangle to the arrayRect list
				arrayRect = new Rectangle(x, y, w, h);
				rects.add(arrayRect);
				
				//Gets the x and y for the object name
				textX = x + (w - (int)objRect.getWidth())/2;
				textY = y + (int)objRect.getHeight();
				
				//Prints the object name and the rectangle
				g2d.setColor(Color.BLACK);
				g2d.drawRoundRect(x, y, w, h, 10, 10);
				g2d.setColor(Color.decode("#cfe2f3"));
				g2d.fillRoundRect(x,  y, w, h, 10, 10);
				g2d.setColor(Color.BLACK);
				g2d.drawString(p.getName(), textX, textY);
				
				//Keeps track of the height the rectangle needs to be
				int tmpY = textY + (int)objRect.getHeight();
				
				//gets the x and y for each variable and prints the variable
				for(Object o : p.getVars()){
					
					if(o instanceof Variable){
						Variable var = (Variable) o;
						
						//Keeps track of the height the rectangle needs to be
						//for each variable that the object has
						h = h + (int)rect.getHeight() + 2;
						
						//Formats the variable for printing
						varString = var.getDataType() + " " + var.getName() + ": \"" + var.getValue() + "\"";
						rect = fm.getStringBounds(varString, g2d);
						
						//Sets the text X coordinate to align within the rectangle
						textX = x + (w - (int)rect.getWidth())/2;
						
						//Draws the string into the rectangle
						g2d.drawString(varString,  textX, tmpY);
						
						//Sets the next variables Y coordinate to just below the current variable in the rectangle
						tmpY = tmpY + (int)objRect.getHeight();
					}
					else if(o instanceof Parser) {
						
						Parser var = (Parser) o;
						
						//Keeps track of the height the rectangle needs to be 
						//for each variable that the object has
						h = h + (int)rect.getHeight() + 2;
						
						//Formats the object variable for output
						varString = var.cName + " " + var.getName();
						rect = fm.getStringBounds(varString, g2d);
						
						//Sets the text X coordinate to align within the rectangle
						textX = x + (w - (int)rect.getWidth())/2;
						
						//Draws the string into the rectangle
						g2d.drawString(varString,  textX, tmpY);
						tmpY = tmpY + (int)objRect.getHeight();
					}
				}
				//Keeps track of the current width
				tmpW = w;
			}
		}
	
		//Checking if an object is another objects variable
		for(Object o : objects){
			Parser one = (Parser) o;
			
			for(Object p : objects){
				Parser two = (Parser)p;
				
				//Gets the second for loops variable list 
				for(Object d : one.getVars()){
					if(d instanceof Parser){
						Parser var = (Parser)d;
						if(two.getLineNo() == var.getLineNo()){
							linePoints = getClosest(rects.get(objects.indexOf(one)), rects.get(objects.indexOf(two)));
							lineOne = rects.get(objects.indexOf(one));
							lineTwo = rects.get(objects.indexOf(two));
							drawLine(lineOne, lineTwo);
						}
					}
				}
				
			}
		}
    }  
	
	public void drawLine(Rectangle2D one, Rectangle2D two){
		
		//Gets the closest two points of the rectangles sides and adds them to the list
		//linePoints
		linePoints = getClosest(one, two);
		
		//Gets the X and Y of the first two linePoints
		int x1 = (int)linePoints.get(0).getX();
		int y1 = (int)linePoints.get(0).getY();
		int x2 = (int)linePoints.get(1).getX();
	    int y2 = (int)linePoints.get(1).getY();
	    
	    //Draws the line using them points
	    g2d.drawLine(x1, y1, x2, y2);
		
	}
	
	public List<Point> getClosest(Rectangle2D one, Rectangle2D two)
		{
		List<Point> onePoints = new ArrayList<Point>();
		List<Point> twoPoints = new ArrayList<Point>();
		
		//Clears the points for the next two rectangles
		pointList.clear();
		int thisDist = 0;
		int i, j;
		i = 0;
		j = 0;
		
		//Gets the X and Y of the middle of each side of the rectangles;
		onePoints = getPoints(one);
		twoPoints = getPoints(two);
		
		//Checks which two points in onePoints and twoPoints is the closest
		//and returns them both as a list
		for(Point p : onePoints){
			
			for(Point q : twoPoints){
				thisDist = getDistance(p, q);
				if(i == 0 && j == 0){
					closestDistance = thisDist;
					pointList.add(p);
					pointList.add(q);
				}
				else if(closestDistance > thisDist){
					closestDistance = thisDist;
					pointList.clear();
					pointList.add(p);
					pointList.add(q);
				}
				
				j++;
			}
			i++;
		}
		return pointList;
	}
	
	public List<Point> getPoints(Rectangle2D o){
		
		List<Point> points = new ArrayList<Point>();
		//Gets the middle point of each side of a rectangle and returns it as a list
		for(int i = 0; i < 4; i++){
			switch(i){
			case 0:
				points.add(new Point((int)o.getX(), (int)(o.getY()+(o.getHeight()/2))));
				break;
			case 1:
				points.add(new Point((int)(o.getX() + o.getWidth()/2), (int)o.getY()));
				break;
			case 2:
				points.add(new Point((int)(o.getX() + o.getWidth()), (int)(o.getY() + (o.getHeight()/2))));
				break;
			case 3:
				points.add(new Point((int)(o.getX() + (o.getWidth()/2)), (int) (o.getY() + o.getHeight())));
				break;
			}
		}
		return points;
	}
	
	public int getDistance(Point p, Point q){
		int dist = 0;
		//Returns the distance between two sides of a rectangle
		//using pythagoras' theorem
		int dx = (int)Math.pow(p.getX() - q.getX(), 2);
		int dy = (int)Math.pow(p.getY() - q.getY(), 2);
		
		dist = (int) Math.sqrt(dx + dy);
		
		return dist;
	}
}
