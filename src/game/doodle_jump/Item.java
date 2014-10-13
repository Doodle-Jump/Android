package game.doodle_jump;

import android.graphics.*;
import game.engine.*;

public class Item{
	int h,l;
	enum type {
		spring,hat,rocket,shoes;
	};
	type id;
	Rect trect,rect;
	public static final int spring_length=20,shoes_length=30,
			hat_length=26,rocket_length=25;
	
	public Item(int _id) {
		if (_id==0) return;
		trect=new Rect();
		rect=new Rect();
		switch (_id) {
			case 1: 
				id=type.spring;
				h=14;l=spring_length;
				trect.set(402,96,422,110);
				break;
			case 2: 
				id=type.hat;
				h=21;l=hat_length;
				trect.set(223,278,249,299);
				break;
			case 3: 
				id=type.rocket;
				h=37;l=rocket_length;
				trect.set(197,264,222,301);
				break;
			case 4: 
				id=type.shoes;
				h=24;l=shoes_length;
				trect.set(299,202,329,226);
				break;
		}
	}
	public void set_rect(int x,int y,int left) {
		rect.set(x+left, y+2-h, x+left+l, y+2);
	}
	public void set_rect(int left,int top,int right,int bottom) {
		rect.set(left, top, right, bottom);
	}
	public void set_rect(double left,double top,double right,double bottom) {
		rect.set((int)left, (int)top, (int)right, (int)bottom);
	}
	public void set_trect(int left,int top,int right,int bottom) {
		trect.set(left, top, right, bottom);
	}
	
	void draw(Canvas canvas,Paint paint,Texture texture) {
		canvas.drawBitmap(texture.getBitmap(), trect, rect, paint);
	}
}