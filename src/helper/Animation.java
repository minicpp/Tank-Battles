package helper;

import java.awt.image.BufferedImage;

public class Animation {
	
	Application app;
	private BufferedImage [] imageArray;
	private int playFrameIndex;
	private boolean stop;
	int x;
	int y;
	boolean loop;
	public int flag;
	public Animation(Application app){
		this.app = app;
		this.flag = 0;
	}
	
	public void loadAnimation(String path, int frameNumber, int width, int height){
		imageArray = new BufferedImage[frameNumber];
		BufferedImage image = app.loadImage(path);
		int count = 0;
		int maxY = image.getHeight() / height;
		int maxX = image.getWidth() / width;
		for(int y=0; y < maxY; ++y){
			for(int x=0; x < maxX; ++x){
				imageArray[count] = image.getSubimage(x*width, y*height, width, height);
				++count;
				if(count >= frameNumber)
					return;
			}
		}
	}
	
	public void play(int x, int y, boolean loop){
		this.x = x;
		this.y = y;
		this.loop = loop;
		if(stop){
			stop = false;
			playFrameIndex = -1;
		}
		
	}
	
	public void update(){
		if(!stop){
			++playFrameIndex;
			if(playFrameIndex >= imageArray.length ){
				if(!loop)
					stop = true;
				else
					playFrameIndex = 0;
			}
		}
	}
	
	public void draw(){
		if(!stop){
			app.drawImage(this.imageArray[playFrameIndex], x, y);
		}
	}
	
	public boolean isStop(){
		return stop;
	}
	
	public Animation cloneAnmiation(){
		Animation animation = new Animation(app);
		animation.imageArray = imageArray;
		animation.stop = true;
		animation.playFrameIndex = -1;
		return animation;
	}
}
