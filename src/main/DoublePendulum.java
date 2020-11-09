package main;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DoublePendulum extends JFrame {

	private static final long serialVersionUID = 7428287078549579328L;

	//Run this code when the window is created
	public DoublePendulum() {
		initUI();
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e){
			    int key = e.getKeyCode();
			    if (key == KeyEvent.VK_ESCAPE) {
			        setVisible(false);
			        dispose();
			    }
			}
        });
	}
	
	private void initUI() {
		//Add the drawing space to the window
		Canvas c = new Canvas();
		add(c);
		
		JPanel sliderBox = new JPanel();
		
		JSlider r1Slider = new JSlider();
		r1Slider.setMaximum(1000);
		r1Slider.setMinimum(50);
		
		JLabel r1Label = new JLabel("Radius 1: ", JLabel.CENTER);
		
		r1Slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                c.r1 = (int)((JSlider) e.getSource()).getValue();
            }
        });
		
		r1Slider.setValue((int) c.r1);
		
		sliderBox.add(r1Label);
		sliderBox.add(r1Slider);
		
		JSlider r2Slider = new JSlider();
		r2Slider.setMaximum(1000);
		r2Slider.setMinimum(50);
		
		JLabel r2Label = new JLabel("Radius 2: ", JLabel.CENTER);
		
		r2Slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                c.r2 = (int)((JSlider) e.getSource()).getValue();
            }
        });
		
		r2Slider.setValue((int) c.r2);
		
		sliderBox.add(r2Label);
		sliderBox.add(r2Slider);
		
		JSlider m1Slider = new JSlider();
		m1Slider.setMaximum(1000);
		m1Slider.setMinimum(1);
		
		JLabel m1Label = new JLabel("Mass 1: ", JLabel.CENTER);
		
		m1Slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                c.m1 = (int)((JSlider) e.getSource()).getValue();
            }
        });
		
		sliderBox.add(m1Label);
		sliderBox.add(m1Slider);
		
		m1Slider.setValue((int) c.m1);
		
		JSlider m2Slider = new JSlider();
		m2Slider.setMaximum(1000);
		m2Slider.setMinimum(1);
		
		JLabel m2Label = new JLabel("Mass 2: ", JLabel.CENTER);
		
		m2Slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                c.m2 = (int)((JSlider) e.getSource()).getValue();
            }
        });
		
		m2Slider.setValue((int) c.m2);
		
		sliderBox.add(m2Label);
		sliderBox.add(m2Slider);
		
		JSlider gSlider = new JSlider();
		gSlider.setMaximum(1000);
		gSlider.setMinimum(0);
		
		JLabel gLabel = new JLabel("Gravity Constant: ", JLabel.CENTER);
		
		gSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                c.g = (((JSlider) e.getSource()).getValue())/10.0;
            }
        });
		
		gSlider.setValue((int) c.g*10);
		
		sliderBox.add(gLabel);
		sliderBox.add(gSlider);
		
		JSlider f1Slider = new JSlider();
		f1Slider.setMaximum(20);
		f1Slider.setMinimum(0);
		
		JSlider f2Slider = new JSlider();
		f2Slider.setMaximum(20);
		f2Slider.setMinimum(0);
		
		JLabel f1Label = new JLabel("Friction (Bearing): ", JLabel.CENTER);
		
		f1Slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                c.k1 = (((JSlider) e.getSource()).getValue())/100.0;
                if (c.lockFriction == 1) {
                	c.k2 = c.k1;
                    f2Slider.setValue((int) (c.k1*100));
                }
            }
        });

		f1Slider.setValue((int) (c.k1*100));
		
		sliderBox.add(f1Label);
		sliderBox.add(f1Slider);
		
		JLabel f2Label = new JLabel("Friction (Joint): ", JLabel.CENTER);
		
		f2Slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                c.k2 = (((JSlider) e.getSource()).getValue())/100.0;
                if (c.lockFriction == 1) {
                	c.k1 = c.k2;
                	f1Slider.setValue((int) (c.k1*100));
                }
            }
        });

		f2Slider.setValue((int) (c.k2*100));
		
		sliderBox.add(f2Label);
		sliderBox.add(f2Slider);
		
		JButton reset=new JButton("Reset");  
		reset.addActionListener(new ActionListener(){  
		    public void actionPerformed(ActionEvent e){  
		    	c.a1 = 0;
		    	c.a2 = 0;
		    	c.theta1 = Math.PI/2;
		    	c.theta2 = Math.PI/2;
		    	c.v1 = 0;
		    	c.v2 = 0;
		    }  
	    });
		
		JCheckBox fCheckbox = new JCheckBox("Same Friction?");  
		
		fCheckbox.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent e) {                 
               c.lockFriction = e.getStateChange()==1?1:0;
               c.k2 = c.k1;
               f1Slider.setValue((int) (c.k1*100));
               f2Slider.setValue((int) (c.k1*100));
            }
         });  
		
		JPanel resetPanel = new JPanel();
		
		resetPanel.add(reset);
		
		sliderBox.add(fCheckbox);
		
		sliderBox.add(resetPanel);
		
		c.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		sliderBox.setMaximumSize(sliderBox.getPreferredSize());
		sliderBox.setMinimumSize(sliderBox.getPreferredSize());
		
		sliderBox.setLayout(new BoxLayout(sliderBox, BoxLayout.Y_AXIS));
		
		c.add(sliderBox);
		
		setResizable(false);
		pack();
		
		setTitle("Double Pendulum Simulation");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new DoublePendulum();
			frame.setVisible(true);
			try {
				frame.setIconImage(ImageIO.read(new File(".\\res\\gameIcon16.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
}
