package game.doodle_jump;

import game.engine.Texture;

import java.util.*;
import android.graphics.*;

public class Board {
	protected double x,y;
	Player doodle;
	Doodlejump doodlejump;
	public static final int board_length=120;
	public Board(double _x,double _y,Doodlejump doodlejump,Player player)
	{
		this.doodlejump=doodlejump;
		x=_x;y=_y;
		doodle=player;
	}
	void move_y(double dt){y+=dt*doodle.vy;}
	double get_x() {return x;}
	double get_y() {return y;}
	boolean is_click() { // Judge if player clicks the board
		return (doodle.y-get_y()>=0 && doodle.y-get_y()<=25 && 
				doodle.x>=x-Player.length+10 && doodle.x<=x+board_length-10);
	}
	int get_state() {return 0;} // Judge if player gets the item
	void go_ahead(double t) {} // Blue board and brown board: move
	boolean destroy() {return false;} // White board: destroy
	boolean disappear(double dt) {return false;} // Yellow board: disappear
	int get_id() {return 0;} // Return board id
	boolean has_item() {return false;}
	void down() {} // If there is item on the board: item down
	void destroy_item(int id) {}
	double get_left() {return -1;} // Return the left distance of item on board
	Item get_item() {return null;}
	void draw_item(Canvas canvas,Paint paint,Texture texture) {}
}

class Greenboard extends Board {
	private double left;
	Item item;
	public Greenboard(double _x,double _y,int _id,double __x,
			Doodlejump doodlejump,Player player) {
		super(_x, _y,doodlejump,player);
		if (_id!=0)
		{
			item=new Item(_id);
			left=__x;
			item.set_rect((int)x, (int)y, (int)left);
		}
		else
		{
			item=null;
			left=-1;
		}
	}
	boolean has_item() {return item!=null;}
	Item get_item() {return item;}
	void destroy_item(int id) {if (id!=1) item=null;}
	int get_state() {
		if (item==null) return 0;
		switch (item.id) {
			case spring:
				if (doodle.x>=x+get_left()-Player.length && 
					doodle.x<=x+get_left()+Item.spring_length)
						return 1;
			break;
			case hat:
				if (doodle.x>=x+get_left()-Player.length &&
					doodle.x<=x+get_left()+Item.hat_length)
				{
					destroy_item(2);
					return 2;
				}
			break;
			case rocket:
				if (doodle.x>=x+get_left()-Player.length &&
					doodle.x<=x+get_left()+Item.rocket_length)
				{
					destroy_item(3);
					return 3;
				}
			break;
			case shoes:
				if (doodle.x>=x+get_left()-Player.length &&
					doodle.x<=x+get_left()+Item.shoes_length)
				{
					destroy_item(4);
					return 4;
				}
			break;
		}
		return 0;
	}
	void down() {
		if (item!=null)
			item.set_rect((int)x, (int)y, (int)left);
	}
	double get_left() {return left;}
	int get_id() {return 0;}
	void draw_item(Canvas canvas,Paint paint,Texture texture) {
		if (item!=null)
			item.draw(canvas, paint, texture);
	}
}

class Blueboard extends Board {
	private double speed;
	public Blueboard(double _x,double _y,Doodlejump doodlejump,Player player) {
		super(_x, _y,doodlejump,player);
		speed=0;
		while (speed<50)
			speed=(double)new Random().nextInt(150);
	}
	void go_ahead(double dt) {
		if (x>0 && x<doodlejump.width-board_length)
		{
			x+=speed*dt;
			return;
		}
		speed=-speed;
		while (x>doodlejump.width-board_length || x<0)
			x+=speed*dt;
	}
	int get_id() {return 1;}
}

class Brownboard extends Board {
	private double speed,up,has_up;
	public Brownboard(double _x,double _y,Doodlejump doodlejump,Player player) {
		super(_x,_y,doodlejump,player);
		speed=up=has_up=0;
		while (up<50)
			up=(double)new Random().nextInt(200);
		while (speed<50)
			speed=(double)new Random().nextInt(150);
	}
	int get_id() {return 2;}
	double get_y() {return y+has_up;}
	void go_ahead(double dt) {
		if (has_up<=up && has_up>=-up)
		{
			has_up+=speed*dt;
			return;
		}
		speed=-speed;
		while (has_up>up || has_up<=-up)
			has_up+=speed*dt;
	}
}

class Whiteboard extends Board {
	public Whiteboard(double _x,double _y,Doodlejump doodlejump,Player player) 
	{super(_x, _y,doodlejump,player);}
	boolean destroy() {return true;}
	int get_id() {return 3;}
}

class Yellowboard extends Board {
	private double decrease;
	public Yellowboard(double _x,double _y,Doodlejump doodlejump,Player player) {
		super(_x, _y,doodlejump,player);
		decrease=6;
	}
	boolean disappear(double dt) {
		if (get_y()<0)
			return false;
		decrease-=dt;
		if (decrease>=0)
			return false;
		return true;
	}
	int get_id() {
		if (decrease>=3)
			return 4;
		if (decrease>=1.5)
			return 5;
		if (decrease>=0.7)
			return 6;
		if (decrease>=0.35)
			return 7;
		if (decrease>=0.175)
			return 8;
		return 9;
	}
}