package game;

import java.awt.image.BufferedImage;
import com.sun.glass.events.KeyEvent;

import helper.Application;

public class PlayerTank extends Tank{

	
	public PlayerTank(Application app){
		super(app);
		imageArray = new BufferedImage[4];
		for(int i=0; i<4; ++i){
			imageArray[i] = app.loadImage("img/tank"+i+".png");
		}
		//properties of the tank
		tankDirection = 0;
		presentImage = imageArray[tankDirection];
		coolDownRequiredCounts = 15;
		coolDown = coolDownRequiredCounts;
		tankSpeed = 3;
		bulletSpeed = 10;
	}
	
	public void move(BoundingBox screen, Map map, Tank[] tankArray){
		int expectedX = posX;
		int expectedY = posY;
		if(app.keyPressed(KeyEvent.VK_UP)){
			expectedY -= tankSpeed;
			tankDirection = 0;
		}
		else if(app.keyPressed(KeyEvent.VK_DOWN)){
			expectedY += tankSpeed;
			tankDirection = 2;
		}
		else if(app.keyPressed(KeyEvent.VK_LEFT)){
			expectedX -= tankSpeed;
			tankDirection = 3;
		}
		else if(app.keyPressed(KeyEvent.VK_RIGHT)){
			expectedX += tankSpeed;
			tankDirection = 1;
		}
		
		presentImage = imageArray[tankDirection];
		
		BoundingBox expectedBox = new BoundingBox(expectedX+5, expectedY+5, 32-10, 32-10);

		if(!this.collideScreen(screen, expectedBox) &&
				!this.collideObstacleOnMap(map, expectedBox, expectedX, expectedY) &&
				!this.collideOtherTanks(tankArray, expectedBox)){
			
				this.posX = expectedX;
				this.posY = expectedY;
				
		}
	}
	
	public void shoot(BulletSystem bulletSystem){
		++coolDown;
		if(coolDown > coolDownRequiredCounts) coolDown = coolDownRequiredCounts;
		
		if(app.keyPressed(KeyEvent.VK_SPACE) && coolDown == coolDownRequiredCounts){
			coolDown = 0;
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
			bulletSystem.shoot(posX + 16, posY + 16, speedX, speedY, BulletSystem.PLAYER_BULLET);
		}
	}
	
	
}
