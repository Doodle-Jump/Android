package game.doodle_jump;

import android.graphics.*;

public class Bullet {
	Doodlejump doodlejump;
	Player doodle;
	Bitmap bitmap;
	Rect rect,tRect;
	
	private double x,y,vx,vy;
	public Bullet(double _x,double _y,Player player,Doodlejump _doodle) {
		//bullet=new Texture(_doodle);
		x=_x;y=_y;
		doodle=player;
		doodlejump=_doodle;
		bitmap=doodlejump.ball.getBitmap(96, 64, 32, 32);
		if (!doodlejump.dir_shoot || doodlejump.monster==null) {vx=0;vy=700;}
		else if (doodlejump.monster.y+Monster.monster_height/2>=y)
		{
			vx=0;vy=700;
		}
		else
		{
			double delta_x=doodlejump.monster.x+Monster.monster_length/2-x,
					delta_y=y-doodlejump.monster.y+Monster.monster_height/2;
			double delta=Math.sqrt(delta_x*delta_x+delta_y*delta_y);
			vx=700*delta_x/delta;
			vy=700*delta_y/delta;
		}
	}
	void go(double dt) { x+=vx*dt;vy-=Doodlejump.g*dt;y-=vy*dt;}
	void down(double dt) {y+=dt*doodle.vy;}
	boolean shoot() {
		return (x>doodlejump.monster.x && x<doodlejump.monster.x+Monster.monster_length 
				&& y>doodlejump.monster.y-Monster.monster_height && y<doodlejump.monster.y);
	}
	boolean destroy() {return (x<0 || x>doodlejump.width || y>doodlejump.height);}
	void draw(Canvas canvas,Paint paint) {
		canvas.drawBitmap(bitmap, (float)x, (float)y, paint);
	}
}