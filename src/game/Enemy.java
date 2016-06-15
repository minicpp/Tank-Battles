package game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import helper.Application;

public class Enemy {
	Point pos;
	int speed;
	int bulletSpeed;
	BufferedImage[] imageArray;
	BufferedImage presentImage;
	Application app;
	BoundingBox box = new BoundingBox();
	boolean dead;
	final int coolDownMax = 20;
	int coolDown = 0;
	int tankDirection = 0;// 0 up, 1 right, 2 down, 3 left.
	Random r = new Random();
	int revivalCounter = 0;

	public Enemy(Application app) {
		this.app = app;
		imageArray = new BufferedImage[4];
		for (int i = 0; i < 4; ++i) {
			imageArray[i] = app.loadImage("img/enemy" + i + ".png");
		}

		tankDirection = r.nextInt(4);
		speed = 2 + r.nextInt(3);
		bulletSpeed = 8 + r.nextInt(6);
		presentImage = imageArray[tankDirection];
		pos = new Point(0, 0);
		this.dead = false;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public boolean getDead() {
		return dead;
	}

	public BoundingBox getBoundingBox() {
		box.setBoundary(pos.x + 5, pos.y + 5, 32 - 10, 32 - 10);
		return box;
	}
	
	private void revival(Map map, Actor actor, Enemy[] enemyArray){
		
		BoundingBox box;
		BoundingBox tileBox = new BoundingBox();
		ArrayList<Point> pList = map.getEnemyBirthPlaces();
		ArrayList<Point> availableList = new ArrayList<Point>();
		for(Point point:pList){
			boolean available = true;
			tileBox.setBoundary(point.x*32, point.y*32, 32, 32);
			for(Enemy enemy: enemyArray){
				box = enemy.getBoundingBox();
				if(box.isCollision(tileBox)){
					available = false;
					break;
				}
			}
			if(available){
				box = actor.getBoundingBox();
				if(box.isCollision(tileBox))
					available = false;
			}
			if(available){
				availableList.add(point);
			}
		}
		
		if(availableList.isEmpty())
		{
			revivalCounter = 1;
		}
		else{
			int index = r.nextInt(availableList.size());
			Point p = availableList.get(index);
			tankDirection = r.nextInt(4);
			speed = 2 + r.nextInt(3);
			bulletSpeed = 8 + r.nextInt(6);
			presentImage = imageArray[tankDirection];
			this.pos.x = p.x * 32;
			this.pos.y = p.y * 32;
			coolDown = 0;
			dead = false;
		}
	}

	public void artifcialIntelligenceControl(BoundingBox screen, Map map, Actor actor, Enemy[] enemyArray,
			BulletSystem bulletSystem) {
		if (dead && revivalCounter == 0){
			this.revivalCounter = 30 + r.nextInt(100);
			return;
		}
		else if(dead && revivalCounter > 0){
			if(--revivalCounter == 0){
				revival(map, actor, enemyArray);
			}
			else
				return;
		}
		// movement
		int speedX = 0, speedY = 0;
		double changeDirection = r.nextDouble();
		if (changeDirection <= 0.03) {
			tankDirection = r.nextInt(4);
		}
		switch (tankDirection) {
		case 0:
			speedY = -speed;
			break;
		case 1:
			speedX = speed;
			break;
		case 2:
			speedY = speed;
			break;
		case 3:
			speedX = -speed;
			break;
		}
		int expectedX = pos.x + speedX;
		int expectedY = pos.y + speedY;

		BoundingBox box = new BoundingBox(expectedX + 5, expectedY + 5, 32 - 10, 32 - 10);
		BoundingBox enemyBox;
		boolean touchedOtherTank = false;
		if (box.isContained(screen) && !map.collideObstacle(box, expectedX, expectedY, null)) {
			for (Enemy enemy : enemyArray) {
				if (enemy.dead || enemy == this)
					continue;
				enemyBox = enemy.getBoundingBox();
				if (enemyBox.isCollision(box)) {
					touchedOtherTank = true;
					break;
				}
			}
			if (actor.getBoundingBox().isCollision(box)) {
				touchedOtherTank = true;
			}
			if (!touchedOtherTank) {
				this.pos.setLocation(expectedX, expectedY);
			} else {
				tankDirection = r.nextInt(4); // if there is collision with
												// other tanks
			}
		} else {
			tankDirection = r.nextInt(4); // if there is collision with block
											// tiles
		}

		presentImage = this.imageArray[tankDirection];

		++coolDown;
		if (coolDown > coolDownMax)
			coolDown = coolDownMax;
		if (coolDown == coolDownMax) {

			if (r.nextInt(2) == 1) {
				switch (tankDirection) {
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

				bulletSystem.shot(this.pos.x + 16, this.pos.y + 16, speedX, speedY, BulletSystem.ENEMY_BULLET);
			}
			coolDown = 0;
		}

	}

	public void setBirthPlaceInMap(Point birthPos) {
		pos.x = birthPos.x * 32;
		pos.y = birthPos.y * 32;
	}

	public void draw(boolean boundary) {
		if (dead)
			return;
		app.drawImage(presentImage, pos.x, pos.y);
		if (boundary) {
			app.drawBoundary(pos.x + 5, pos.y + 5, 32 - 10, 32 - 10);
		}
	}

}
