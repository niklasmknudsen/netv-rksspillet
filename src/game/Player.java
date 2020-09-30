package game;

public class Player {
	public String name;
	public int xpos;
	public int ypos;
	public int point;
	public String direction;

	public Player(String name, int xpos, int ypos, String direction) {
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.point = 0;
	}
	
	public Player(String name) {
		this.name = name;
		this.xpos = 0;
		this.ypos = 0;
		this.direction = "";
	}

	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void addPoints(int p) {
		point+=p;
	}
	public String toString() {
		return name+ "," + this.xpos + "," + this.ypos + "," + this.direction;
	}
}
