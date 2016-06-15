package game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import com.sun.glass.events.KeyEvent;

import helper.Application;

public class Actor {
	Point pos;
	int speed = 3;
	int bulletSpeed = 10;
	BufferedImage[] imageArray;
	BufferedImage presentImage;
	Application app;
	BoundingBox box = new BoundingBox();
	final int coolDownMax = 30;
	int coolDown = coolDownMax;
	int tankDirection = 0;//0 up, 1 right, 2 down, 3 left.
	boolean dead;
	
	public Actor(Application app){
		this.app = app;
		imageArray = new BufferedImage[4];
		for(int i=0; i<4; ++i){
			imageArray[i] = app.loadImage("img/tank"+i+".png");
		}
		presentImage = imageArray[0];
		pos = new Point(0, 0);
	}
	
	public void setDead(boolean dead){
		this.dead = dead;
	}
	
	public boolean getDead(){
		return dead;
	}
	
	public void setBirthPlaceInMap(Point birthPos){
		pos.x = birthPos.x * 32;
		pos.y = birthPos.y * 32;
	}
	
	public BoundingBox getBoundingBox(){
		box.setBoundary(pos.x + 5, pos.y + 5, 32 - 10, 32 - 10);
		return box;
	}
	
	public void control(BoundingBox screen, Map map, Enemy[] enemyArray, BulletSystem bulletSystem){
		if(dead)
			return;
		int expectedX = pos.x;
		int expectedY = pos.y;
		if(app.keyPressed(KeyEvent.VK_UP)){
			expectedY -= speed;
			tankDirection = 0;
		}
		else if(app.keyPressed(KeyEvent.VK_DOWN)){
			expectedY += speed;
			tankDirection = 2;
		}
		else if(app.keyPressed(KeyEvent.VK_LEFT)){
			expectedX -= speed;
			tankDirection = 3;
		}
		else if(app.keyPressed(KeyEvent.VK_RIGHT)){
			expectedX += speed;
			tankDirection = 1;
		}
		
		presentImage = imageArray[tankDirection];
		
		
		
		BoundingBox box = new BoundingBox(expectedX+5, expectedY+5, 32-10, 32-10);
		BoundingBox enemyBox;
		boolean touchedEnemy = false;
		if(box.isContained(screen) && !map.collideObstacle(box, expectedX, expectedY, null)){
			for(Enemy enemy: enemyArray){
				if(enemy.dead)
					continue;
				enemyBox = enemy.getBoundingBox();
				if(enemyBox.isCollision(box)){
					touchedEnemy = true;
					break;
				}
			}
			if(!touchedEnemy){
				this.pos.setLocation(expectedX, expectedY);
			}
		}
		
		++coolDown;
		if(coolDown > coolDownMax) coolDown = coolDownMax;
		
		if(app.keyPressed(KeyEvent.VK_SPACE) && coolDown == coolDownMax){
			System.out.println("shoot");
			int speedX = 0, speedY = 0;
			switch(tankDirection){
			case 0:
				speedY = -bulletSpeed;
				break;
			case 1:
				speedX = bulletSpeed;
				break;
			case 2:
				speedY = bulletSpeed;
				break;
			case 3:
				speedX = -bulletSpeed;
				break;
			}
			
			bulletSystem.shot(this.pos.x + 16, this.pos.y + 16, speedX, speedY, BulletSystem.ACTOR_BULLET);
			coolDown = 0;
		}
	}
	
	
	public void draw(boolean boundary){
		if(dead)
			return;
		app.drawImage(presentImage, pos.x, pos.y);
		if(boundary){
			app.drawBoundary( pos.x + 5, pos.y + 5, 32-10, 32-10 );
		}
	}
}
