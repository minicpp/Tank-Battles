package program;

import java.awt.Image;

import javax.swing.ImageIcon;

import helper.Application;

public class GameMain extends Application{

	private Image star;
	int x,y;
	
	public static void main(String [] args){
		GameMain gameMain = new GameMain();
		gameMain.setTitle("Tank Battles");
	}
	
	public void init(){
		
		//load resources
		ImageIcon ii = new ImageIcon("img/Golden.png");
        star = ii.getImage();
	}
	
	public void update(){
		++x;
		++y;
		if(x > this.getWidth()){
			x = 0;
		}
		if(y > this.getHeight()){
			y = 0;
			setSize(400,500);
		}
	}
	
	public void draw(){
		this.drawImage(star, x, y);
	}

}
