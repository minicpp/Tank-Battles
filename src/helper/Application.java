package helper;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class Application extends JPanel  implements Runnable, ActionListener  {

	public static final int WINDOW_WIDTH = 640;
	public static final int WINDOW_HEIGHT = 640;
	public static final int FPS = 30;
	public static final int DELAY = 1000/FPS;
	

	private Thread animator;
	public WindowFrame winFrame;
	private Graphics graphic;
	private boolean initEnd;
	private boolean beginDraw;
	private InputKeyAdapter keyAdapter;
	
	private Hashtable<Integer, Integer> keyTable = new Hashtable<Integer, Integer>();

	public Application() {
		winFrame = new WindowFrame(this);
		keyAdapter = new InputKeyAdapter();
		addKeyListener(keyAdapter);
        setFocusable(true);
        
		this.initEnd = false;
		beginDraw = false;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				winFrame.setVisible(true);
				winFrame.pack();
				//run after all construction
				Application.this.initEnd = true;
			}
		});
	}

	public void init() {

	}

	public void update() {

	}
	
	public void draw(){
		
	}
	
	public BufferedImage loadImage(String path){
		BufferedImage img = null;
		try {
			File f = new File(path);
			if(f.exists() && !f.isDirectory())
				img = ImageIO.read(f);
			else{
				//System.out.println("Try to load image from resource in jar");
				img = ImageIO.read(getClass().getResource("/"+path));
			}
		} catch (IOException e) {
			System.out.println("Cannot find image file from the path:"+path);
			System.out.println("IOException:"+e.getMessage());
		}
		return img;
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		if(!this.beginDraw)
			return;
		super.paintComponent(g);
		Application.this.draw(g);
		
		Toolkit.getDefaultToolkit().sync();
	}

	public void addNotify() {
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}

	public void run() {
		while(!this.initEnd){
			try{
				Thread.sleep(1);
			}catch(InterruptedException e){
				System.out.println("Interrupted:"+e.getMessage());
			}	
		}
		long beforeTime, timeDiff, sleep;
		Application.this.init();
		while (true) {
			beforeTime = System.currentTimeMillis();
			this.update();
			this.beginDraw = true;
			repaint();
			timeDiff = System.currentTimeMillis() - beforeTime;
			sleep = DELAY - timeDiff;
			if(sleep < 1)
				sleep = 1;
			try{
				Thread.sleep(sleep);
			}catch(InterruptedException e){
				System.out.println("Interrupted:"+e.getMessage());
			}				
		}
	}
	
	public void setWindowSize(int width, int height){
		this.setPreferredSize(new Dimension(width, height));
		winFrame.pack();
	}
	
	public void setTitle(String title){
		this.winFrame.setTitle(title);
	}
	
	public void setSize(int width, int height){
		super.setPreferredSize(new Dimension(width, height));
		this.winFrame.pack();
	}
	
	public void drawBoundary(int left, int top, int width, int height){
		Color old = graphic.getColor();
		graphic.setColor(Color.MAGENTA);
		graphic.drawRect (left, top, width, height);
		graphic.setColor(old);
	}
	
	public void drawRectangle(int left, int top, int width, int height, int color){
		Color old = graphic.getColor();
		color |= 0xFF000000;
		Color presentColor = new Color(color);
		graphic.setColor(presentColor);
		graphic.fillRect(left, top, width, height);
		graphic.setColor(old);
	}

	public void drawImage(Image image, int x, int y){
		//Graphics2D g2d = (Graphics2D)this.graphic;
		this.graphic.drawImage(image, x, y, this);
	}
	
	private void draw(Graphics g) {
		this.graphic = g;
		this.draw();
		this.graphic = null;
	}

	
	class WindowFrame extends JFrame {
		
		private Application app;
		public boolean initEnd;
		
		public WindowFrame(Application app){
			
			this.app = app;
			app.setBackground(Color.BLACK);
			app.setPreferredSize(new Dimension(Application.WINDOW_WIDTH, Application.WINDOW_HEIGHT));
			app.setDoubleBuffered(true);
			
			this.add(app, BorderLayout.CENTER);
			this.pack();
			this.setResizable(false);
			this.setTitle("Game Demo");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setLocationRelativeTo(null);
		}
	}
	
	public boolean keyPressed(int code){
		return this.keyAdapter.checkKey(code);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private class InputKeyAdapter extends KeyAdapter {

        @Override
        public synchronized  void keyReleased(KeyEvent e) {
        	int code = e.getKeyCode();
        	Application.this.keyTable.put(code, 0);
        }

        @Override
        public synchronized  void keyPressed(KeyEvent e) {
        	int code = e.getKeyCode();
        	Application.this.keyTable.put(code, 1);
        }
        
        public synchronized boolean checkKey(int code){
        	if(Application.this.keyTable.containsKey(code)){
        		if(Application.this.keyTable.get(code).intValue() == 1)
        			return true;
        	}
        	return false;
        	
        }
    }
}
