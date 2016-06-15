package game;

public class BoundingBox {
	int left;
	int top;
	int right;
	int bottom;
	public BoundingBox(int x, int y, int width, int height){
		setBoundary(x,y,width,height);
	}
	
	public BoundingBox(){
		left=top=right=bottom = 0;
	}
	

	
	public void setBoundary(int x, int y, int width, int height){
		this.left = x;
		this.top = y;
		this.right = x + width;
		this.bottom = y+ height;
	}
	
	public boolean isCollision(BoundingBox box){
		BoundingBox box1 = this;
		BoundingBox box2 = box;
	
		if(box1.bottom < box2.top) return false;
		if(box1.top > box2.bottom) return false;
		if(box1.left > box2.right) return false;
		if(box1.right < box2.left) return false;
		
		return true;
	}
	public boolean isContained(BoundingBox outerBox){
		
		if(outerBox.left <= this.left &&
				outerBox.right >= this.right &&
				outerBox.top <= this.top &&
				outerBox.bottom >= this.bottom)
			return true;
		
		return false;
	}
}
