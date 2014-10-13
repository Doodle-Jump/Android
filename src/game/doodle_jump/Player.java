package game.doodle_jump;

import game.engine.*;
import android.graphics.*;

public class Player {
	double x,y,vx,vy,jump_y;
	public static final int length=96;
	boolean dir;
	int state; // Item state
	boolean move;
	int shoes_time;
	Doodlejump doodlejump;
	int speed;
	Item item;
	Texture left,right;
	
	public Player(Doodlejump doodle) {
		doodlejump=doodle;
		init_player();
		left=right=null;
	}
	void set_texture(Texture _left,Texture _right) {
		left=new Texture(_left);
		right=new Texture(_right);
	}
	void init_player() {
		x=210;y=600;vx=0;vy=0.7;dir=true;
		state=0;move=false;speed=600;
		item=null;
	}
	boolean is_flying() {return state!=0;}
	void set_move(boolean _move) {move=_move;}
	void monster() {vy=0;} // hit monster: die
	void go_x(double dt,int width) {
		x+=vx*dt;
		if (x>width-length/2)
			x=-length/2;
		if (x<-length/2)
			x=width-length/2;
	}
	void go_y(double dt,boolean c) {
		if (c) y+=vy*dt;
		else y-=vy*dt;
	}
	void go_vx(boolean _dir,double dt) {
		dir=_dir;
		if (dir)
		{
			if (vx<=0) vx+=5*speed*dt;
			vx+=speed*dt;
		}
		else
		{
			if (vx>=0) vx-=5*speed*dt;
			vx-=speed*dt;
		}
	}
	void go_vy(double dt,boolean c) {
		if (c) vy+=Doodlejump.g*dt;
		else vy-=Doodlejump.g*dt;
	}
	void reset() {
		vx=0;
		switch (state) {
			case 1:case 4: vy=1400;break;
			case 2:vy=6000;break;
			case 3:vy=13000;break;
			default:vy=600;break;
		}
		jump_y=Doodlejump.least;
	}
	void click() {
		jump_y=y;
		if (jump_y<Doodlejump.least)
		{
			doodlejump.total_up=Doodlejump.least-jump_y;
			move=true;
		}
	}
	void set_state(int _id) {
		if (_id==0) {
			if (state==4) {
				shoes_time--;
				if (shoes_time==0) {
					state=0;
				}
			}
			else state=0;
			vy=0; return;
		}
		if (_id==1 && state==4) {
			shoes_time--;
			if (shoes_time==0) {
				state=1;
			}
			return;
		}
		move=true;
		state=_id;
		shoes_time=(state==4)?9:0;
	}
	int get_state() {return state;}
	int print_item() {
		if (state==0 || state==1) {item=null;return 0;}
		item=new Item(state);
		if (state==2) {
			if (dir) item.set_rect(x+20,y-length,x+75,y-length+35);
			else item.set_rect(x+20,y-length,x+75,y-length+35);
		}
		if (state==3) {
			if (dir) item.set_rect(x+5,y-length+25,x+55,y);
			else item.set_rect(x+38,y-length+25,x+88,y);
		}
		if (state==4) {
			if (dir) item.set_rect(x+20,y-20,x+75,y+19);
			else item.set_rect(x+18,y-20,x+73,y+19);
		}
		return state;
	}
	void draw(Canvas canvas,Paint paint,Texture texture) {
		print_item();
		if (state==3) item.draw(canvas, paint, texture);
		if (dir) canvas.drawBitmap(right.getBitmap(), new Rect(0, 0, 124, 120), new Rect((int)x, (int)y-96, (int)x+96, (int)y), paint);
		else canvas.drawBitmap(left.getBitmap(), new Rect(0, 0, 124, 120), new Rect((int)x, (int)y-96, (int)x+96, (int)y), paint);
		if (state==2 || state==4) item.draw(canvas, paint, texture);
	}
}