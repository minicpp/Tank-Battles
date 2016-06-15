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

import game.Actor;
import game.BoundingBox;
import game.BulletSystem;
import game.Enemy;
import game.Map;
import helper.Application;

//main application loop
//title
//color
//background color
//change color using counter and state
public class Demo1 extends Application{

	Color color;
	
	Actor actor;
	Enemy[] enemyArray;
	
	BufferedImage stone;
	Map map;
	
	BoundingBox screenBox;
	BulletSystem bulletSystem;
	
	public Demo1(){
	}
	
	public static void main(String [] args){
		Demo1 gameMain = new Demo1();
		gameMain.setTitle("Demo - Tank Battles");
	}
	
	public void init(){	//run only once at the beginning of the application
		
		
		
		color = new Color(0,0,0);
		
		
		map = new Map(this);
		map.createMapFromFile("img/map0.bmp");
		map.loadImage();
		Point actorBirthPosition = map.getActorBirthPlace();
		
		actor = new Actor(this);
		actor.setBirthPlaceInMap(actorBirthPosition);
		
		ArrayList<Point> enemyBirthPlaces = map.getEnemyBirthPlaces();
		enemyArray = new Enemy[enemyBirthPlaces.size()];
		for(int i=0; i<enemyBirthPlaces.size(); ++i){
			enemyArray[i] = new Enemy(this);
			enemyArray[i].setBirthPlaceInMap(enemyBirthPlaces.get(i));
		}
		
		screenBox = new BoundingBox(0, 0, this.WINDOW_WIDTH, this.WINDOW_HEIGHT);
		
		bulletSystem = new BulletSystem(this);
		
	}
	
	public void update(){
		bulletSystem.update(screenBox, map, actor, enemyArray);
		actor.control(this.screenBox, map, this.enemyArray, bulletSystem);
		
		for(int i=0; i<enemyArray.length; ++i)
			enemyArray[i].artifcialIntelligenceControl(screenBox, map, actor, enemyArray, bulletSystem);
	}
	
	public void draw(){
		boolean showBoundary = false;
		map.draw(showBoundary);
		actor.draw(showBoundary);
		for(int i=0; i<enemyArray.length; ++i){
			enemyArray[i].draw(showBoundary);
		}
		bulletSystem.draw(showBoundary);
	}

}