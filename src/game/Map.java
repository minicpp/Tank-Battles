package game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import helper.Application;

public class Map {

	private int[][] gameMap;
	private Application app;

	private BufferedImage stone;
	public final static int BLACK = 0x00000000; // 0xAARRGGBB
	private BufferedImage wood;
	public final static int BLUE = 0x000000FF; // 0xAARRGGBB
	private BufferedImage grass;
	public final static int WHITE = 0x00FFFFFF;
	private BufferedImage marble;
	public final static int GREEN = 0x0000FF00;
	private BufferedImage headquarters;
	public final static int YELLOW = 0x00FFFF00;
	private BufferedImage sand;
	final private int RED = 0x00FF0000;

	private int width;
	private int height;

	private Point playerBirthPlace;
	private ArrayList<Point> enemyBirthPlace = new ArrayList<Point>();
	
	private BoundingBox tileBox = new BoundingBox();

	public Map(Application app) {
		this.app = app;
	}

	public void loadImage() {
		stone = app.loadImage("img/stone.png");
		wood = app.loadImage("img/wood.png");
		grass = app.loadImage("img/grass.png");
		marble = app.loadImage("img/marble.png");
		headquarters = app.loadImage("img/headquarters.png");
		sand = app.loadImage("img/sand.png");
	}

	public void createMapFromFile(String path) {
		BufferedImage img = app.loadImage(path);

		width = img.getWidth();
		height = img.getHeight();
		gameMap = new int[img.getHeight()][img.getWidth()];
		int color;

		for (int y = 0; y < img.getHeight(); ++y) {
			for (int x = 0; x < img.getWidth(); ++x) {
				color = img.getRGB(x, y);
				color = color & 0x00FFFFFF;
				gameMap[y][x] = color;
				if (color == GREEN) {
					this.playerBirthPlace = new Point(x, y);
				}
				if (color == RED) {
					this.enemyBirthPlace.add(new Point(x, y));
				}
			}
		}

	}
	
	public int[][] getMapArray(){
		return this.gameMap;
	}

	public Point getPlayerBirthPlace() {
		return this.playerBirthPlace;
	}
	
	public ArrayList<Point> getEnemyBirthPlaces(){
		return this.enemyBirthPlace;
	}
	
	public boolean collideObstacle(BoundingBox targetBox, int left, int top, Point collisionPointOnMap){
		int x = left/32;
		int y = top/32;
		
		int minX, maxX, minY, maxY;
		minX = x - 1;
		minY = y - 1;
		maxX = x+1;
		maxY = y+1;
		
		minX = minX < 0?0:minX;
		minY = minY < 0?0:minY;
		maxX = maxX >= this.width ? this.width - 1:maxX;
		maxY = maxY >= this.height ? this.height - 1:maxY;
		
		
		int color;
		for(int ty = minY; ty<=maxY; ++ty){
			for(int tx=minX; tx<=maxX; ++tx){
				this.tileBox.setBoundary(tx*32, ty*32, 32, 32);
				if(this.tileBox.isCollision(targetBox))
				{
					color = gameMap[ty][tx];
					if(color != WHITE && color != RED && color != GREEN){
						if(collisionPointOnMap != null){
							collisionPointOnMap.x = tx;
							collisionPointOnMap.y = ty;
						}
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public void draw(boolean boundary) {
		int color;
		int posX;
		int posY;
		for (int y = 0; y < height; ++y) {
			posY = y * 32;
			for (int x = 0; x < width; ++x) {
				color = gameMap[y][x];
				posX = x * 32;
				switch (color) {
				case WHITE:
					this.app.drawImage(grass, posX, posY);
					break;
				case BLACK:
					this.app.drawImage(stone, posX, posY);
					break;
				case BLUE:
					this.app.drawImage(wood, posX, posY);
					break;
				case RED:
					this.app.drawImage(sand, posX, posY);
					break;
				case GREEN:
					this.app.drawImage(marble, posX, posY);
					break;
				case YELLOW:
					this.app.drawImage(headquarters, posX, posY);
				}
				if(boundary){

					this.app.drawBoundary(posX, posY, 32, 32);
				}
			}
		}
	}
}
