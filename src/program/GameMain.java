package program;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.PlayerTank;
import game.Tank;
import game.BoundingBox;
import game.BulletSystem;
import game.EnemyTank;
import game.Map;
import helper.Application;

public class GameMain extends Application{

	Color color;
	
	PlayerTank playerTank;
	EnemyTank[] enemyArray;
	Tank[] allTanks;
	
	BufferedImage stone;
	Map map;
	
	BoundingBox screenBox;
	BulletSystem bulletSystem;
	
	public GameMain(){
	}
	
	public static void main(String [] args){
		GameMain gameMain = new GameMain();
		gameMain.setTitle("Game - Tank Battles");
	}
	
	public void init(){	//run only once at the beginning of the application
		color = new Color(0,0,0);
		map = new Map(this);
		map.createMapFromFile("img/map5.bmp");
		map.loadImage();
		
		Point playerBirthPosition = map.getPlayerBirthPlace();
		playerTank = new PlayerTank(this);
		playerTank.setBirthPlaceInMap(playerBirthPosition.x, playerBirthPosition.y);
		
		this.setTitle("Game - Tank Battles - Credits:"+playerTank.getCredits() + " - Score:0");
		
		ArrayList<Point> enemyBirthPlaces = map.getEnemyBirthPlaces();
		Point pos;
		enemyArray = new EnemyTank[enemyBirthPlaces.size()];
		for(int i=0; i<enemyBirthPlaces.size(); ++i){
			enemyArray[i] = new EnemyTank(this);
			pos = enemyBirthPlaces.get(i);
			enemyArray[i].setBirthPlaceInMap(pos.x, pos.y);
		}
		
		allTanks = new Tank[enemyArray.length+1];
		System.arraycopy(enemyArray, 0, allTanks, 0, enemyArray.length);
		allTanks[allTanks.length - 1] = playerTank;
		
		screenBox = new BoundingBox(0, 0, this.WINDOW_WIDTH, this.WINDOW_HEIGHT);
		bulletSystem = new BulletSystem(this);
		
	}
	
	public void update(){
		bulletSystem.update(screenBox, map, allTanks, this.playerTank);
		
		for(Tank tank: allTanks)
			tank.control(screenBox, map, allTanks, bulletSystem);
		
	}
	
	public void draw(){
		boolean showBoundary = false;
		map.draw(showBoundary);
		for(int i=0; i<allTanks.length; ++i){
			allTanks[i].draw(showBoundary);
		}
		bulletSystem.draw(showBoundary);
	}
}