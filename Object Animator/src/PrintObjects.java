import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.math.*;

import javax.swing.JPanel;

public class PrintObjects extends JPanel{
	
	List<Object> objects = new ArrayList<Object>();
	List<Rectangle2D> rects = new ArrayList<Rectangle2D>();
	List<Point> pointList = new ArrayList<Point>();
	int closestDistance;
	
	public PrintObjects(){

	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D rect, objRect;
        Rectangle2D arrayRect;
        int x, y, h, w, textX, textY, tmpW; 
        int tmpWidth;
        String varString;
        String val;
        List<Point> linePoints = new ArrayList<Point>();
        x = 10;
		y = 10;
		tmpW = 0;
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(qualityHints);
		
		for(Object r : objects){
			if(r instanceof Parser){
				Parser p = (Parser)r;
				
				rect = fm.getStringBounds(p.getName(), g2d);
				objRect = rect;
				h = (int)rect.getHeight()+20;
				w = (int)rect.getWidth()+20;
				
				if(objects.indexOf(p) == 0 || objects.indexOf(p)%2 == 0){
					x = 50;
					y = y + h + 50;
				}
				else{
					x = this.getWidth() - tmpW - 100;
				}
				
				tmpWidth = w;
				
				for(Object o : p.getVars()){
					if(o instanceof Variable){
						Variable var = (Variable) o;
						h = h + (int)rect.getHeight();
						if(var.getValue().equals("")){
							val = "\"\"";
						}
						else{
							val = var.getValue();
						}
						varString = var.getDataType() + " " + var.getName() + ": \"" + var.getValue() + "\"";
						rect = fm.getStringBounds(varString, g2d);
						if(tmpWidth < (int)rect.getWidth()){
							tmpWidth = (int)rect.getWidth();
						}
					}
					if(o instanceof Parser){
						Parser var = (Parser) o;
						h = h + (int)rect.getHeight();
						varString = var.cName + " " + var.getName();
						rect = fm.getStringBounds(varString, g2d);
						if(tmpWidth < (int)rect.getWidth()){
							tmpWidth = (int)rect.getWidth();
						}
					}
				}
				w = tmpWidth + 10;
				
				arrayRect = new Rectangle(x, y, w, h);
				rects.add(arrayRect);
				textX = x + (w - (int)objRect.getWidth())/2;
				textY = y + (int)objRect.getHeight();
				g2d.setColor(Color.BLACK);
				g2d.drawRoundRect(x, y, w, h, 10, 10);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(x,  y, w, h, 10, 10);
				g2d.setColor(Color.BLACK);
				g2d.drawString(p.getName(), textX, textY);
				int tmpY = textY + (int)objRect.getHeight();
				
				for(Object o : p.getVars()){
					
					if(o instanceof Variable){
						Variable var = (Variable) o;
						h = h + (int)rect.getHeight() + 2;
						
						varString = var.getDataType() + " " + var.getName() + ": \"" + var.getValue() + "\"";
						rect = fm.getStringBounds(varString, g2d);
						textX = x + (w - (int)rect.getWidth())/2;
						g2d.drawString(varString,  textX, tmpY);
						tmpY = tmpY + (int)objRect.getHeight();
					}
					else if(o instanceof Parser) {
						
						Parser var = (Parser) o;
						h = h + (int)rect.getHeight() + 2;
						varString = var.cName + " " + var.getName();
						rect = fm.getStringBounds(varString, g2d);
						textX = x + (w - (int)rect.getWidth())/2;
						g2d.drawString(varString,  textX, tmpY);
						tmpY = tmpY + (int)objRect.getHeight();
						System.out.println("HELLO I AM HERE");
					}
				}
				tmpW = w;
			}
		}
		
		for(Rectangle2D one : rects){
			for(Rectangle2D two : rects){
				linePoints = getClosest(one, two);
				
				if(rects.indexOf(two) == rects.indexOf(one) + 1){
					int x1 = (int)linePoints.get(0).getX();
					int y1 = (int)linePoints.get(0).getY();
					int x2 = (int)linePoints.get(1).getX();
			        int y2 = (int)linePoints.get(1).getY();
			        g2d.drawLine(x1, y1, x2, y2);
				}
			}
		}
		
		addMouseListener(new MouseListener() {
	        public void mouseClicked(MouseEvent me) {
//	            if(me.getX() >= (x-r) && me.getX() < (x+r) && me.getY() >= (y-r) && me.getY() < (y+r)) {
//	                Color.YELLOW;
//	                repaint();
//	            }
	        }

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
	    });
    }  
	
	public List<Point> getClosest(Rectangle2D one, Rectangle2D two){
		List<Point> onePoints = new ArrayList<Point>();
		List<Point> twoPoints = new ArrayList<Point>();
		pointList.clear();
		int thisDist = 0;
		int i, j;
		i = 0;
		j = 0;
		onePoints = getPoints(one);
		twoPoints = getPoints(two);
		
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
		int dx = (int)Math.pow(p.getX() - q.getX(), 2);
		int dy = (int)Math.pow(p.getY() - q.getY(), 2);
		
		dist = (int) Math.sqrt(dx + dy);
		
		return dist;
	}
}
