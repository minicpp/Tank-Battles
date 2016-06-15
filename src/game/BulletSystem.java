package game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import helper.Animation;
import helper.Application;

public class BulletSystem {
	
	public static final int ACTOR_BULLET = 0;
	public static final int ENEMY_BULLET = 1;
	
	LinkedList<Bullet> bulletList = new LinkedList<Bullet>();
	LinkedList<Animation> animationList = new LinkedList<Animation>();
	Animation explode;
	Animation smoke;
	BufferedImage bulletImageActor;
	BufferedImage bulletImageEnemy;
	Application app;
	Point collisionPoint = new Point();
	
	int score = 0;
	
	public BulletSystem(Application app){
		this.app = app;
		explode = new Animation(app);
		bulletImageActor = app.loadImage("img/bullet.png");
		bulletImageEnemy = app.loadImage("img/bullet_red.png");
		explode.loadAnimation("img/explode.png", 64, 32, 32);
		smoke = new Animation(app);
		smoke.loadAnimation("img/smoke.png", 16, 32, 32);
	}
	
	
	public void shot(int centerPosX, int centerPosY, int speedX, int speedY, int bulletType){
		bulletList.add(new Bullet(centerPosX-8, centerPosY - 8, speedX, speedY, bulletType));
	}
	
	public void update(BoundingBox screen, Map map, Actor actor, Enemy[] enemyArr){
		for(Iterator<Bullet> iterator = bulletList.iterator(); iterator.hasNext();){
			Bullet bullet = iterator.next();
			bullet.posX += bullet.speedX;
			bullet.posY += bullet.speedY;
			if(outScope(bullet, screen)){
				iterator.remove();
				System.out.println("Remove bullet out of scope");
			}
			else{
				//check collision with blocks on map
				Animation ani = null;
				if(map.collideObstacle(bullet.getBoundingBox(), bullet.posX, bullet.posY, collisionPoint))
				{
					iterator.remove();
					
					int[][] mapArray = map.getMapArray();
					int color = mapArray[collisionPoint.y][collisionPoint.x];
					if(color == Map.BLUE){
						ani = smoke.cloneAnmiation();	//add smoke
						ani.play(collisionPoint.x*32, collisionPoint.y*32, true);
						animationList.add(ani);
						mapArray[collisionPoint.y][collisionPoint.x] = Map.WHITE;
					}
					
					ani = explode.cloneAnmiation();
					ani.play(collisionPoint.x*32, collisionPoint.y*32, false);
					animationList.add(ani);
					
					if(color == Map.YELLOW){
						if(bullet.bulletType == BulletSystem.ACTOR_BULLET){
							ani.flag = 1; //firendly fire
							mapArray[collisionPoint.y][collisionPoint.x] = Map.WHITE;
						}
						else{
							ani.flag = 2; //enemy fire
							mapArray[collisionPoint.y][collisionPoint.x] = Map.WHITE;
						}
					}
					
					
				}
				else{
					if(bullet.bulletType == BulletSystem.ACTOR_BULLET){
						//check enemy
						for(Enemy enemy:enemyArr){
							BoundingBox box = enemy.getBoundingBox();
							if(enemy.getDead() == false && box.isCollision(bullet.getBoundingBox())){
								enemy.setDead(true);
								iterator.remove();
								ani = explode.cloneAnmiation();
								ani.play(enemy.pos.x, enemy.pos.y, false);
								animationList.add(ani);
								++score;
								app.setTitle("Demo - Tank Battles - Killed:"+score);
							}
						}
					}
					else{
						//check actor
						BoundingBox box = actor.getBoundingBox();
						if(actor.getDead() == false && box.isCollision(bullet.getBoundingBox())){
							actor.setDead(true);
							iterator.remove();
							ani = explode.cloneAnmiation();
							ani.play(actor.pos.x, actor.pos.y, false);
							animationList.add(ani);
							ani.flag = 3;
						}
					}
				}
			}
		}
		for(Iterator<Animation> iterator = animationList.iterator(); iterator.hasNext();){
			Animation ani = iterator.next();
			ani.update();
			if(ani.isStop()){
				if(ani.flag == 1){
					JOptionPane.showMessageDialog(app.winFrame, "Friendly Fire! Mission Failed! The headquarter is destroyed.");
					System.exit(0);
				}
				else if(ani.flag == 2){
					JOptionPane.showMessageDialog(app.winFrame, "Mission Failed! The headquarter is destroyed.");
					System.exit(0);
				}
				else if(ani.flag == 3){
					JOptionPane.showMessageDialog(app.winFrame, "Mission Failed! Your tank is destroyed.");
					System.exit(0);
				}
				iterator.remove();
			}
		}
	}
	
	public void draw(boolean boundary){
		for(Bullet bullet:bulletList){
			if(bullet.bulletType == BulletSystem.ACTOR_BULLET)
				app.drawImage(bulletImageActor, bullet.posX, bullet.posY);
			else
				app.drawImage(bulletImageEnemy, bullet.posX, bullet.posY);
			if(boundary){
				app.drawBoundary(bullet.posX + 4, bullet.posY + 4 , 16 - 8, 16 - 8);
			}
		}
		for(Animation ani:animationList){
			ani.draw();
		}
	}
	
	boolean outScope(Bullet bullet, BoundingBox screen){
		BoundingBox box = bullet.getBoundingBox();
		if(box.isContained(screen))
			return false;
		return true;
	}
	
}

class Bullet{
	public int posX;
	public int posY;
	public int speedX;
	public int speedY;
	public int bulletType;
	
	public BoundingBox box = new BoundingBox();
	
	public Bullet(int posX, int posY, int speedX, int speedY, int bulletType){
		this.posX = posX;
		this.posY = posY;
		this.speedX = speedX;
		this.speedY = speedY;
		this.bulletType = bulletType;

	}
	
	public BoundingBox getBoundingBox(){
		box.left = posX + 4;
		box.top = posY + 4;
		box.right = posX + 8;
		box.bottom = posY + 8;
		return box;
	}
}