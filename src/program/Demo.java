package program;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.sun.glass.events.KeyEvent;

import game.MyTank;
import game.BoundingBox;
import game.BulletSystem;
import game.EnemyTank;
import game.Map;
import helper.Application;

//main application loop
//title
//color
//background color
//change color using counter and state
public class Demo extends Application{

	Color color;
	
	MyTank myTank;
	EnemyTank[] enemyArray;
	
	BufferedImage stone;
	Map map;
	
	BoundingBox screenBox;
	BulletSystem bulletSystem;
	
	public Demo(){
	}
	
	public static void main(String [] args){
		Demo gameMain = new Demo();
		gameMain.setTitle("Demo - Tank Battles");
		//BufferedImage image = gameMain.loadImage("img/abc.png");
	}
	
	public void init(){	//run only once at the beginning of the application
		
		
		
		color = new Color(0,0,0);
		
		
		map = new Map(this);
		map.createMapFromFile("img/map1.bmp");
		map.loadImage();
		Point actorBirthPosition = map.getActorBirthPlace();
		
		myTank = new MyTank(this);
		myTank.setBirthPlaceInMap(actorBirthPosition);
		
		ArrayList<Point> enemyBirthPlaces = map.getEnemyBirthPlaces();
		enemyArray = new EnemyTank[enemyBirthPlaces.size()];
		for(int i=0; i<enemyBirthPlaces.size(); ++i){
			enemyArray[i] = new EnemyTank(this);
			enemyArray[i].setBirthPlaceInMap(enemyBirthPlaces.get(i));
		}
		
		screenBox = new BoundingBox(0, 0, this.WINDOW_WIDTH, this.WINDOW_HEIGHT);
		
		bulletSystem = new BulletSystem(this);
		
	}
	
	public void update(){
		bulletSystem.update(screenBox, map, myTank, enemyArray);
		myTank.control(this.screenBox, map, this.enemyArray, bulletSystem);
		
		for(int i=0; i<enemyArray.length; ++i)
			enemyArray[i].artifcialIntelligenceControl(screenBox, map, myTank, enemyArray, bulletSystem);
	}
	
	public void draw(){
		boolean showBoundary = false;
		map.draw(showBoundary);
		myTank.draw(showBoundary);
		for(int i=0; i<enemyArray.length; ++i){
			enemyArray[i].draw(showBoundary);
		}
		bulletSystem.draw(showBoundary);
	}

}