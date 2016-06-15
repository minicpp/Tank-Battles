package game;

import java.awt.image.BufferedImage;


import helper.Application;

public class Tank {
	protected Application app;
	
	//position
	protected int posX;
	protected int posY;
	protected int tankDirection; //0 up, 1 right, 2 down, 3 left.
	
	//speed
	protected int tankSpeed;
	protected int bulletSpeed;
	
	//pictures
	protected BufferedImage[] imageArray;
	protected BufferedImage presentImage;
	
	//weapon
	protected int coolDownRequiredCounts;
	protected int coolDown;
	
	//life
	protected boolean dead;
	
	//boundary
	protected BoundingBox box = new BoundingBox();
	
	
	public Tank(Application app){
		this.app = app;
		this.dead = false;
	}
	
	public int getPosX(){
		return posX;
	}
	
	public int getPosY(){
		return posY;
	}
	
	public void setBirthPlaceInMap(int x, int y){
		this.posX = x * 32;
		this.posY = y * 32;
	}
	
	public void setDead(boolean dead){
		this.dead = dead;
	}
	
	public boolean getDead(){
		return dead;
	}
	
	public BoundingBox getBoundingBox(){
		box.setBoundary(posX + 5, posY + 5, 32 - 10, 32 - 10);
		return box;
	}
	
	public boolean collideScreen(BoundingBox screen, BoundingBox expectedBox){
		return (expectedBox.isContained(screen) == false);
	}
	
	public boolean collideObstacleOnMap(Map map, BoundingBox expectedBox, int expectedX, int expectedY){
		return map.collideObstacle(expectedBox, expectedX, expectedY, null);
	}
	
	public boolean collideOtherTanks(Tank[] tankArray, BoundingBox expectedBox){
		BoundingBox box = null;
		for(Tank tank: tankArray){
			if(tank.dead || tank == this)
				continue;
			box = tank.getBoundingBox();
			if(expectedBox.isCollision(box)){
				return true;
			}
		}
		return false;
	}
	
	public void control(BoundingBox screen, Map map, Tank[] tankArray, BulletSystem bulletSystem){
		if(dead)
			return;
			
		this.move(screen, map, tankArray);
		this.shoot(bulletSystem);
	}
	
	public void move(BoundingBox screen, Map map, Tank[] tankArray){
	}
	
	public void shoot(BulletSystem bulletSystem){
	}
	
	public void draw(boolean boundary){
		if(dead)
			return;
		app.drawImage(presentImage, posX, posY);
		if(boundary){
			app.drawBoundary( posX + 5, posY + 5, 32-10, 32-10 );
		}
	}
}
