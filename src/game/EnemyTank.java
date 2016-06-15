package game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import helper.Application;

public class EnemyTank extends Tank {

	Random r = new Random();
	int revivalCounter = 0;

	public EnemyTank(Application app) {
		super(app);
		imageArray = new BufferedImage[4];
		for (int i = 0; i < 4; ++i) {
			imageArray[i] = app.loadImage("img/enemy" + i + ".png");
		}

		init(0, 0);
	}

	public void init(int posX, int posY) {
		tankDirection = r.nextInt(4);
		presentImage = imageArray[tankDirection];

		this.tankSpeed = 2 + r.nextInt(3); // 2 to 4
		bulletSpeed = 8 + r.nextInt(6); // 8 to 13
		this.dead = false;
		coolDownRequiredCounts = 20; // bullet waiting for particular frames
		coolDown = 0;
	}

	private void revival(Map map, Tank[] tankArray) {

		BoundingBox box;
		BoundingBox tileBox = new BoundingBox();
		ArrayList<Point> pList = map.getEnemyBirthPlaces();
		ArrayList<Point> availableList = new ArrayList<Point>();

		// find all available birth place
		for (Point point : pList) {
			boolean available = true;
			tileBox.setBoundary(point.x * 32, point.y * 32, 32, 32);
			for (Tank tank : tankArray) {
				if (tank.dead)
					continue;
				box = tank.getBoundingBox();
				if (box.isCollision(tileBox)) {
					available = false;
					break;
				}
			}
			if (available) {
				availableList.add(point);
			}
		}

		if (availableList.isEmpty()) {
			// no avaiable birth place
			revivalCounter = 1;
		} else {
			// randomly pick one
			int index = r.nextInt(availableList.size());
			Point p = availableList.get(index);
			this.posX = p.x * 32;
			this.posY = p.y * 32;
			this.init(posX, posY);
		}
	}

	public void control(BoundingBox screen, Map map, Tank[] tankArray, BulletSystem bulletSystem) {

		// AI strategy
		if (dead && revivalCounter == 0) {
			this.revivalCounter = 30 + r.nextInt(100);
			return;
		} else if (dead && revivalCounter > 0) {
			if (--revivalCounter == 0) {
				revival(map, tankArray);
			} else
				return;
		}

		// movement
		this.move(screen, map, tankArray);
		this.shoot(bulletSystem);
	}

	public void move(BoundingBox screen, Map map, Tank[] tankArray) {

		

		// AI in changing direction
		// Very high probability to keep original direction
		double changeDirection = r.nextDouble();
		if (changeDirection <= 0.03) {
			tankDirection = r.nextInt(4);
		}

		presentImage = imageArray[tankDirection];

		int speedX = 0, speedY = 0;
		switch (tankDirection) {
		case 0:
			speedY = -tankSpeed;
			break;
		case 1:
			speedX = tankSpeed;
			break;
		case 2:
			speedY = tankSpeed;
			break;
		case 3:
			speedX = -tankSpeed;
			break;
		}
		int expectedX = posX + speedX;
		int expectedY = posY + speedY;

		BoundingBox expectedBox = new BoundingBox(expectedX + 5, expectedY + 5, 32 - 10, 32 - 10);

		if (!this.collideScreen(screen, expectedBox)
				&& !this.collideObstacleOnMap(map, expectedBox, expectedX, expectedY)
				&& !this.collideOtherTanks(tankArray, expectedBox)) {

			this.posX = expectedX;
			this.posY = expectedY;

		}

	}

	public void shoot(BulletSystem bulletSystem) {

		++coolDown;
		if (coolDown > coolDownRequiredCounts)
			coolDown = coolDownRequiredCounts;
		
		//AI shoot strategy
		if (coolDown == coolDownRequiredCounts) {

			int speedX = 0, speedY = 0;
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

				bulletSystem.shoot(posX + 16, posY + 16, speedX, speedY, BulletSystem.ENEMY_BULLET);
			}
			coolDown = 0;
		}

	}

}
