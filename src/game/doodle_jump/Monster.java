package game.doodle_jump;

import game.engine.*;
import java.util.*;
import android.graphics.*;

public class Monster {
	Texture texture;
	double x,y,speed;
	private boolean dir;
	Player doodle;
	static final int monster_length=80,monster_height=90;
	Rect rect;
	Rect trect;
	Doodlejump doodlejump;
	Bitmap bitmap;
	public Monster(Doodlejump doodlejump,Player player) {
		rect=new Rect();
		trect=new Rect();
		doodle=player;
		this.doodlejump=doodlejump;
		texture=new Texture(doodlejump);
		x=(double)new Random().nextDouble()*(doodlejump.width-monster_length);
		y=-10-monster_height;
		speed=0;
		while (speed<70)
			speed=(double)new Random().nextInt(130);
		dir=true;
		trect.set(65,188,103,237);
	}
	boolean get_dir() {
		rect.set((int)x,(int)(y-49),(int)(x+monster_length),(int)y);
		return dir;
	}
	void go(double dt) {
		if (x>0 && x<doodlejump.width-monster_length)
		{
			x+=speed*dt;
			return;
		}
		speed=-speed;
		dir=(speed>0)?true:false;
		while (x>doodlejump.width-monster_length || x<0)
			x+=speed*dt;
		if (!dir) trect.set(106,188,144,237);
		else trect.set(65,188,103,237);
	}
	void down(double dt) {
		y+=dt*doodle.vy;
	}
	boolean dead() {
		if (doodle.x+Player.length>=x && doodle.x+Player.length<=x+monster_length)
		{
			if (doodle.y-96>=y-monster_height && doodle.y-96<=y)
				return true;
			if (doodle.y>=y-monster_height && doodle.y<=y)
				return true;
		}
		if (doodle.x>=x && doodle.x<=x+monster_length)
		{
			if (doodle.y-96>=y-monster_height && doodle.y-96<=y)
				return true;
			if (doodle.y>=y-monster_height && doodle.y<=y)
				return true;
		}
		return false;
	}
	boolean destroy() {
		return y-monster_height-10>doodlejump.height;
	}
	void draw(Canvas canvas,Paint paint,Texture texture) {
		rect.set((int)x,(int)y-monster_height,(int)(x+monster_length),(int)y);
		canvas.drawBitmap(texture.getBitmap(), trect, rect, paint);
	}
}