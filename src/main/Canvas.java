package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Canvas extends JPanel implements Runnable {

	private static final long serialVersionUID = 3364515185033982053L;
	
	private final int WIDTH = 1280;
	private final int HEIGHT = 720;
	
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();

    DisplayMode dm = gs[0].getDisplayMode();

    int refreshRate = dm.getRefreshRate();
    
    //private final int FPS_CAP = refreshRate != DisplayMode.REFRESH_RATE_UNKNOWN ? refreshRate : 60;
    
    private final int FPS_CAP = 144;
	
	private final int DELAY = (int) Math.ceil((1.0/FPS_CAP)*1000.0);
	
	public double g = 9.80665;
	
	private int isClicked = 0;
	
	double zoomFactor = 1;
	
	private int mouseXOffset;
	private int mouseYOffset;
	
	public double theta1 = Math.PI/2;
	public double theta2 = Math.PI/2;
	
	double r1 = WIDTH/4;
	double r2 = WIDTH/4;
	
	double m1 = 100.0;
	double m2 = 100.0;
	
	double v1 = 0;
	double v2 = 0;
	
	double a1 = 0;
	double a2 = 0;
	
	double dt = 0.001;
	
	int calcsPerFrame = (int) Math.floor((DELAY/1000.0)/dt);
	
	double k1 = 0.0;
	double k2 = 0.0;
	
	int lockFriction;
	
	int fixed_x = (int) (zoomFactor * WIDTH/2);
	int fixed_y = (int) (zoomFactor * 50);
	
	int x1 = (int) (fixed_x + zoomFactor * (int)Math.floor(r1*Math.sin(theta1)));
	int y1 = (int) (fixed_y +zoomFactor * (int)Math.floor(r1*Math.cos(theta1)));
	
	int x2 = (int) (x1 + zoomFactor * (int)Math.floor(r2*Math.sin(theta2)));
	int y2 = (int) (y1 +zoomFactor * (int)Math.floor(r2*Math.cos(theta2)));
	
	private Thread animator;
	
	public Canvas() {
		initCanvas();
		addMouseListener(new MouseAdapter() {
			
            @Override
            public void mousePressed(MouseEvent e) {
                if (Math.sqrt(Math.pow(e.getX()-x1, 2)+(Math.pow(e.getY()-y1, 2))) < 50) {
                	isClicked = 1;
                } else if (Math.sqrt(Math.pow(e.getX()-x2, 2)+(Math.pow(e.getY()-y2, 2))) < 50) {
                	isClicked = 2;
                }
                mouseXOffset = e.getX()-e.getXOnScreen();
                mouseYOffset = e.getY()-e.getYOnScreen();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	isClicked = 0;
            }      
        });
		
		addMouseWheelListener(new MouseAdapter() {
            
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getPreciseWheelRotation() < 0) {
					if (zoomFactor + zoomFactor*0.1 > 1 && zoomFactor + zoomFactor*0.1 < 1.1 && zoomFactor != 1) {
						zoomFactor = 1;
					} else {
						zoomFactor += zoomFactor*0.1;
					}
				} else {
					if (zoomFactor - zoomFactor*0.1 < 1 && zoomFactor - zoomFactor*0.1 > 0.9 && zoomFactor != 1) {
						zoomFactor = 1;
					} else {
						zoomFactor -= zoomFactor*0.1;
					}
				}
			}
			
        });
		
	}
	
	private void initCanvas() {
		
		setBackground(new Color(102, 204, 255));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		
		animator = new Thread(this);
		animator.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (zoomFactor != 1) {
			double scale = Math.pow(10, 2);
		    double zoomFactorRounded = Math.round(zoomFactor * scale) / scale;
			g.drawString("Zoom: " + zoomFactorRounded+"x", 25, 25);
		}
		
		drawPendulumLines(g);
		drawBearing(g);
		drawPendulumMasses(g);
	}

	private void drawPendulumLines(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke((float) (5*zoomFactor)));
		
		x1 = (int) (fixed_x + zoomFactor*r1*Math.sin(theta1));
		y1 = (int) (fixed_y + zoomFactor*r1*Math.cos(theta1));
		x2 = (int) (x1 + zoomFactor*r2*Math.sin(theta2));
		y2 = (int) (y1 + zoomFactor*r2*Math.cos(theta2));
		
		g2.drawLine(fixed_x, fixed_y, x1, y1);
		
		g2.drawLine(x1, y1, x2, y2);
		
		g2.setStroke(new BasicStroke(1));
		
		Toolkit.getDefaultToolkit().sync();
	}
	
	private void drawPendulumMasses(Graphics g) {
		int massSize = (int) (50*zoomFactor);
		g.fillOval(x1-(massSize/2), y1-(massSize/2), massSize,massSize);
		g.fillOval(x2-(massSize/2), y2-(massSize/2), massSize,massSize);
		
		Toolkit.getDefaultToolkit().sync();
	}
	
	private void drawBearing(Graphics g) {
		g.setColor(Color.GRAY);
		int ovalWidth = (int) (50*zoomFactor);
		int ovalHeight = (int) (115*zoomFactor);
		g.fillOval(fixed_x-(ovalWidth/2),(int) (fixed_y-(0.85*ovalHeight)), ovalWidth,ovalHeight);
		
		g.setColor(Color.DARK_GRAY);
		int rectWidth = (int) (50*zoomFactor);
		int rectHeight = (int) (130*zoomFactor);
		g.fillRect(fixed_x-(rectWidth/2), (int) (fixed_y-(1.15*rectHeight)), rectWidth, rectHeight);
		
		
		g.setColor(Color.BLACK);
		int circleSize = (int) (10*zoomFactor);
		g.fillOval(fixed_x-(circleSize/2), fixed_y-(circleSize/2), circleSize, circleSize);

		
		Toolkit.getDefaultToolkit().sync();
	}
	
	private void cycle() {
		switch (isClicked) {
		case 0:
			for (int i = 0; i < calcsPerFrame*15; i++) {
				double a1num = -g*(2.0*m1+m2)*Math.sin(theta1)-m2*g*Math.sin(theta1-2.0*theta2)-2.0*Math.sin(theta1-theta2)*m2*((v2*v2)*r2+(v1*v1)*r1*Math.cos(theta1-theta2));
				double a1den = r1*(2.0*m1+m2-m2*Math.cos(2.0*theta1-2.0*theta2));
				a1 = a1num/a1den - k1 * v1;
				
				double a2num = 2.0*Math.sin(theta1-theta2)*((v1*v1)*r1*(m1+m2)+g*(m1+m2)*Math.cos(theta1)+(v2*v2)*r2*m2*Math.cos(theta1-theta2));
				double a2den = r2*(2*m1+m2-m2*Math.cos(2.0*theta1-2*theta2));
				a2 = a2num/a2den - k2 * v2;
				
				v1 += a1*dt;
				theta1 += v1*dt;
				x1 = (int) (fixed_x + r1*Math.sin(theta1));
				y1 = (int) (fixed_y + r1*Math.cos(theta1));
		
				v2 += a2*dt;
				theta2 += v2*dt;
				x2 = (int) (x1 + r2*Math.sin(theta2));
				y2 = (int) (y1 + r2*Math.cos(theta2));
			}
			
			break;
		case 1:
			double mouseX = MouseInfo.getPointerInfo().getLocation().x+mouseXOffset;
        	double mouseY = MouseInfo.getPointerInfo().getLocation().y+mouseYOffset;
        
        	theta1 = Math.atan2(mouseX-fixed_x, mouseY-fixed_y);
        	
        	x1 = (int) (fixed_x + r1*Math.sin(theta1));
			y1 = (int) (fixed_y + r1*Math.cos(theta1));
			
			x2 = (int) (x1 + r2*Math.sin(theta2));
			y2 = (int) (y1 + r2*Math.cos(theta2));
			
        	v1 = 0;
        	v2 = 0;
        	break;
		case 2:
			mouseX = MouseInfo.getPointerInfo().getLocation().x+mouseXOffset;
        	mouseY = MouseInfo.getPointerInfo().getLocation().y+mouseYOffset;
        	theta2 = Math.atan2(mouseX-x1, mouseY-y1);
			
			x2 = (int) (x1 + r2*Math.sin(theta2));
			y2 = (int) (y1 + r2*Math.cos(theta2));
			
        	v1 = 0;
        	v2 = 0;
        	break;
		}

	}

	@Override
	public void run() {
		
		long beforeTime, timeDiff, sleep;
		
		beforeTime = System.currentTimeMillis();
		
		while (true) {
			
			cycle();
			repaint();
			
			timeDiff = System.currentTimeMillis() - beforeTime;
			sleep = DELAY - timeDiff;
			
			if (sleep < 0) {
				sleep = 2;
			}
			
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				String msg = String.format("Thread interrupted: %s", e.getMessage());
				
				JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			beforeTime = System.currentTimeMillis();
			
		}
		
	}
	
}
