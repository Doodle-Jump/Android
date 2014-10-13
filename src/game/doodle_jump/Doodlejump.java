package game.doodle_jump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.*;
import android.util.*;
import android.view.MotionEvent;
import android.view.View;
import game.engine.*;

public class Doodlejump extends Engine {
	TextPrinter tp;
	Paint paint;
	Canvas canvas;
	Timer timer;
	int height,width;
	Monster monster;
	Player doodle;
	List<Board> boards;
	List<Bullet> bullets;
	public static final int g = 900,least=500;
	double total_up,score,bullet_dt;
	Texture left,right,photo,ball;
	String msg;
	double lorr;
	MediaPlayer mediaPlayer;
	SoundPool soundPool;
	HashMap<String, Integer> hashMap;
	MediaPlayer hatPlayer,rocketPlayer;
	AudioManager audioManager;
	int maxVolume;
	boolean gameover,dead,gamestart,statistics,settings,pause,music_on,dir_shoot;
	int highest_score,last_score,last_jump,max_jump;
	int last_time,max_time,total_time,total_play,total_score;
	int this_jump;
	double this_time;
	double average_score;
	float mmx,mmy;
	
	public Doodlejump() {
		Log.d("Game","Game constructor");
		paint=new Paint();
		canvas=null;
		tp=new TextPrinter();
		tp.setColor(Color.WHITE);
		tp.setTextSize(24);
		tp.setLineSpacing(28);
		timer=new Timer();
		setFrameRate(50);
		doodle=new Player(this);
		monster=null;
		//boards=null;bullets=null;
		boards=new ArrayList<Board>();
		bullets=new ArrayList<Bullet>();
		left=new Texture(this);right=new Texture(this);photo=new Texture(this);
		ball=new Texture(this);
		msg=new String("click");
		soundPool=new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		hashMap=new HashMap<String, Integer>();
	}
	
	public void init() {
		Log.d("Game","Game.init");
		super.setScreenOrientation(ScreenModes.PORTRAIT);
		dir_shoot=true;
		music_on=true;
		set_statistics(0);
		another_init();
	}
	
	public void another_init() {
		monster=null;
		boards=new ArrayList<Board>();
		boards.add(new Board(190, 600, this, doodle));
		another_board(0);
		another_board(0);
		another_board(0);
		another_board(0);
		doodle.init_player();
		bullet_dt=0;
		score=0;
		gameover=false;
		gamestart=false;
		dead=false;
		statistics=false;
		settings=false;
		pause=false;
		this_jump=0;
		this_time=0;
		setFrameRate(FRAME_NO_DELAY);
	}
	
	public void load() {
		Log.d("Game","Game.load");
		left.loadFromAsset("left.png");
		right.loadFromAsset("right.png");
		photo.loadFromAsset("photo.png");
		ball.loadFromAsset("ball.png");
		doodle.set_texture(left, right);
		hashMap.put("beat.ogg", soundPool.load(this, R.raw.beat, 0));
		hashMap.put("click.ogg", soundPool.load(this, R.raw.click, 0));
		hashMap.put("dead.ogg", soundPool.load(this, R.raw.dead, 0));
		hashMap.put("monster.ogg", soundPool.load(this, R.raw.monster, 0));
		hashMap.put("shoot.ogg", soundPool.load(this, R.raw.shoot, 0));
		hashMap.put("spring.ogg", soundPool.load(this, R.raw.spring, 0));
		mediaPlayer=MediaPlayer.create(this, R.raw.music);
		hatPlayer=MediaPlayer.create(this, R.raw.hat);
		rocketPlayer=MediaPlayer.create(this, R.raw.rocket);
		hatPlayer.setLooping(true);
		rocketPlayer.setLooping(true);
		mediaPlayer.setLooping(true);
		audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
		maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mediaPlayer.setVolume(maxVolume/10, maxVolume/10);
		if (music_on) mediaPlayer.start();
	}
	
	public void draw() {
		Log.d("Game","Game.draw");
		//paint.setColor(Color.WHITE);
		canvas=super.getCavas();		
		height=canvas.getHeight();
		width=canvas.getWidth();
		canvas.drawColor(Color.rgb(255, 182, 193));
		paint.setColor(Color.rgb(255, 105, 180));
		//paint.setColor(Color.BLUE);
		for (int i=0;i<=height;i+=20)
			canvas.drawLine(0, i, width, i, paint);
		for (int i=0;i<=width;i+=20)
			canvas.drawLine(i, 0, i, height, paint);
		/*
		tp.setCanvas(canvas);
		tp.draw("height: "+height, 100, 100);
		tp.draw("Width: "+width);
		*/
		doodle.draw(canvas, paint,photo);
		if (monster!=null) {
			monster.draw(canvas, paint, photo);
		}
		
		ListIterator<Board> it=boards.listIterator();
		while (it.hasNext()) {
			Board temp=it.next();
			switch (temp.get_id()) {
				case 0:
					canvas.drawBitmap(photo.getBitmap(), 
							new Rect(0, 0, 60, 16), new Rect((int)temp.get_x(), 
									(int)temp.get_y(), (int)temp.get_x()+Board.board_length,
										(int)temp.get_y()+28), paint);
					break;
				case 1:
					canvas.drawBitmap(photo.getBitmap(), 
							new Rect(0, 17, 60, 35), new Rect((int)temp.get_x(), 
									(int)temp.get_y(), (int)temp.get_x()+Board.board_length,
										(int)temp.get_y()+28), paint);
					break;
				case 2:
					canvas.drawBitmap(photo.getBitmap(), 
							new Rect(0, 35, 60, 53), new Rect((int)temp.get_x(), 
									(int)temp.get_y(), (int)temp.get_x()+Board.board_length,
										(int)temp.get_y()+28), paint);
					break;
				case 3:
					canvas.drawBitmap(photo.getBitmap(), 
							new Rect(0, 53, 60, 71), new Rect((int)temp.get_x(), 
									(int)temp.get_y(), (int)temp.get_x()+Board.board_length,
										(int)temp.get_y()+28), paint);
					break;
				default:
					canvas.drawBitmap(photo.getBitmap(), 
							new Rect(0, 182+18*(temp.get_id()-4), 60, 
									182+18*(temp.get_id()-3)),new Rect((int)temp.get_x(), 
									(int)temp.get_y(), (int)temp.get_x()+Board.board_length,
									(int)temp.get_y()+28), paint);
					break;
			}
			temp.draw_item(canvas, paint, photo);
		}
		for (int i=0;i<bullets.size();i++) {
			bullets.get(i).draw(canvas, paint);
		}
		
		tp.setCanvas(canvas);
		tp.setColor(Color.RED);
		tp.setTextSize(22);
		tp.setLineSpacing(25);
		tp.setAlign(TextPrinter.ALIGN_LEFT);
		tp.draw(""+(int)score, 15, 30);
		//tp.draw("("+mmx+","+mmy+")");
		
		if (!pause && gamestart) {
			tp.setColor(Color.BLACK);
			tp.setTextSize(52);
			tp.setColor(Color.BLACK);
			tp.setAlign(TextPrinter.ALIGN_RIGHT);
			tp.draw("Pause", width-30, 63);
		}
		
		if (settings) {
			tp.setColor(Color.BLACK);
			tp.setTextSize(48);
			tp.setLineSpacing(56);
			tp.setAlign(TextPrinter.ALIGN_MIDDLE);
			tp.draw("MUSIC:", width/2, 200);
			tp.draw("ON      OFF");
			tp.draw("");tp.draw("");
			tp.draw("AUTO SHOOTING");
			tp.draw("ON      OFF");
			tp.draw("");tp.draw("");tp.draw("");
			tp.draw("BACK");
			if (dir_shoot) tp.draw("*", width/2-110, 480);
			else tp.draw("*", width/2+10, 480);
			if (music_on) tp.draw("*",width/2-110,256);
			else tp.draw("*", width/2+10, 256);
			return;
		}
		
		if (pause) {
			tp.setColor(Color.BLACK);
			tp.setAlign(TextPrinter.ALIGN_MIDDLE);
			tp.setTextSize(56);
			tp.setLineSpacing(150);
			tp.draw("Pausing......", width/2, 200);
			tp.draw("SETTINGS");
			tp.draw("BACK");
		}
		
		if (!gamestart)
		{
			if (statistics) {
				tp.setColor(Color.BLACK);
				tp.setTextSize(36);
				tp.setLineSpacing(40);
				tp.setAlign(TextPrinter.ALIGN_LEFT);
				set_statistics(0);
				tp.draw("Highest score: "+highest_score,100,200);
				tp.draw("Average score: "+average_score);
				tp.draw("Last score: "+last_score);
				tp.draw("Last jump: "+last_jump);
				tp.draw("Max jump: "+max_jump);
				tp.draw("Last time: "+last_time+" s");
				tp.draw("Max time: "+max_time+" s");
				tp.draw("Total time: "+total_time+" s");
				tp.draw("Total play: "+total_play);
				tp.setTextSize(48);
				tp.setAlign(TextPrinter.ALIGN_MIDDLE);
				tp.draw("RESET        BACK",width/2, 600);
				return;
			}
			tp.setColor(Color.BLACK);
			tp.setTextSize(56);
			tp.setLineSpacing(150);
			tp.setAlign(TextPrinter.ALIGN_MIDDLE);
			tp.draw("START", width/2, 200);
			tp.draw("SETTINGS");
			tp.draw("STATISTICS");
			tp.draw("EXIT");
		}
		if (gameover)
		{
			tp.setColor(Color.BLACK);
			tp.setTextSize(52);
			tp.setLineSpacing(100);
			tp.setAlign(TextPrinter.ALIGN_MIDDLE);
			tp.draw("GAME OVER", width/2, 300);
			tp.draw(""+(int)score);
		}
	}
	
	public void update() {
		double dt=(double)timer.getTimeDelta()/1000;
		Log.d("Game", "Game.update");
		if (!gamestart) return;
		if (gameover) return;
		if (pause) return;
		bullet_dt+=dt;
		this_time+=dt;
		if (height!=0) {
			while (boards.get(0).get_y()>height) {
				boards.remove(0);			
				another_board(-1);
			}
		}
		
		if (width!=0 && monster==null) {
			if (new Random().nextInt(1000)==1) {
				monster=new Monster(this,doodle);
			}
		}
		
		if (monster!=null)
			monster.go(dt);
		
		for (int i=0;i<bullets.size();i++) {
			if (monster!=null && bullets.get(i).shoot()) {
				monster=null;
				bullets.remove(i);
				i--;
				play("beat.ogg");
			}
		}
		
		for (int i=0;i<bullets.size();i++) {
			bullets.get(i).go(dt);
			if (bullets.get(i).destroy()) {
				bullets.remove(i);
				i--;
			}
		}
		
		int i=0;
		while (i<boards.size()) {
			Board temp=boards.get(i);
			temp.go_ahead(dt);
			if (temp.disappear(dt)) {
				boards.remove(temp);
				i--;
				another_board(-1);
			}
			i++;
		}
		/*
		if (lorr<0) doodle.go_vx(false, -dt*lorr);
		else if (lorr>0) doodle.go_vx(true, dt*lorr);
		lorr=0;
		*/
		if (width!=0)
			doodle.go_x(dt,width);
		if (msg.equals("click")) {
			doodle.reset();
			msg=new String("up");
		}
		else if (msg.equals("up")) {
			if (doodle.move) {
				if (doodle.y>=2*height/5) {
					doodle.go_y(dt, false);
				}
				else {
					i=0;
					while (i<boards.size()) {
						Board temp=boards.get(i);
						temp.move_y(dt);
						temp.down();
						i++;
					}
					for (int j=0;j<bullets.size();j++) {
						bullets.get(j).down(dt);
					}
					total_up-=dt*doodle.y;
					
					if (monster!=null) {
						monster.down(dt);
						if (monster.destroy()) monster=null;
					}
				}
				score+=(height-doodle.y)*dt/10;
				if (!doodle.is_flying() && total_up<0) {
					doodle.set_move(false);
					total_up=0;
				}
			}
			else
				doodle.go_y(dt, false);
			doodle.go_vy(dt, false);
			if (!doodle.is_flying() && monster!=null && monster.dead()) {
				doodle.monster();
				dead=true;
			}
			if (doodle.vy<=0) {
				doodle.set_state(0);
				total_up=0;
				msg=new String("down");
				if (hatPlayer.isPlaying()) hatPlayer.pause();
				if (rocketPlayer.isPlaying()) rocketPlayer.pause();
			}
		}
		else if (msg.equals("down")) {
			doodle.go_y(dt, true);
			doodle.go_vy(dt, true);
			if (!dead && monster!=null && monster.dead()) {
				doodle.click();
				msg=new String("click");
				play("monster.ogg");
				monster=null;
				this_jump++;
			}
			for (i=0;i<boards.size();i++) {
				Board temp=boards.get(i);
				if (!dead && temp.is_click()) {
					doodle.click();
					this_jump++;
					if (i==boards.size()-1) {
						another_board(-1);
					}
					//play("click.ogg");
					doodle.set_state(temp.get_state());
					switch (doodle.state) {
						case 0: play("click.ogg");break;
						case 1: case 4: play("spring.ogg");break;
						case 2: hatPlayer.start();
							hatPlayer.setVolume(maxVolume/15, maxVolume/15);break;
						case 3: rocketPlayer.start();
							rocketPlayer.setVolume(maxVolume/15, maxVolume/15);break;
					}
					msg=new String("click");
					if (temp.destroy()) {
						boards.remove(temp);
						i--;
						another_board(-1);
					}
				}
			}
			if (height!=0 && doodle.y>height+97)
			{
				play("dead.ogg");
				gameover=true;
				int tmp=(int)score;
				total_score+=tmp;
				total_play++;
				total_time+=this_time;
				last_score=tmp;
				last_time=(int)this_time;
				last_jump=this_jump;
				if (tmp>highest_score) highest_score=tmp;
				if (this_time>max_time) max_time=(int)this_time;
				if (this_jump>max_jump) max_jump=this_jump;
				this_jump=0;
				this_time=0;
				//score=0;
				set_statistics(1);
			}
		}
	}
	
	void another_board(int l) {
		Random random=new Random();
		if (l==-1) l=random.nextInt(5);
		double t=0,p=0;
		int id=0;
		int xx=0;
		while (xx<130) xx=random.nextInt(210);
		switch (l) {
			case 0:
				t=random.nextDouble();
				if (t<=0.2) {
					p=random.nextDouble()*(Board.board_length-Item.spring_length);
					id=1;
				}
				else if (t<=0.25) {
					p=random.nextDouble()*(Board.board_length-Item.hat_length);
					id=2;
				}
				else if (t<=0.28) {
					p=random.nextDouble()*(Board.board_length-Item.rocket_length);
					id=3;
				}
				else if (t<=0.33) {
					p=random.nextDouble()*(Board.board_length-Item.shoes_length);
					id=4;
				}
				boards.add(new Greenboard(random.nextDouble()*(400-Board.board_length), 
						boards.get(boards.size()-1).get_y()-xx, id, p, this,doodle));
				break;
			case 1:
				boards.add(new Blueboard(random.nextDouble()*(400-Board.board_length), 
						boards.get(boards.size()-1).get_y()-xx, this,doodle));
				break;
			case 2:
				boards.add(new Brownboard(random.nextDouble()*(400-Board.board_length), 
						boards.get(boards.size()-1).get_y()-xx, this,doodle));
				break;
			case 3:
				boards.add(new Whiteboard(random.nextDouble()*(400-Board.board_length), 
						boards.get(boards.size()-1).get_y()-xx, this,doodle));
				break;
			case 4:
				boards.add(new Yellowboard(random.nextDouble()*(400-Board.board_length), 
						boards.get(boards.size()-1).get_y()-xx, this,doodle));
				break;
		}
	}
	
	void set_statistics(int mode) {
		//if (mode<=2) return;
		if (mode==0) {
			highest_score=getData("highest_score", 0);
			total_play=getData("total_play", 0);
			total_score=getData("total_score", 0);
			total_time=getData("total_time", 0);
			last_score=getData("last_score", 0);
			last_jump=getData("last_jump", 0);
			last_time=getData("last_time", 0);
			max_jump=getData("max_jump", 0);
			max_time=getData("max_time", 0);
			if (total_play==0) average_score=0;
			else average_score=(double)total_score/(double)total_play;
			average_score=round(average_score, 3);
		}
		else if (mode==1) { // set Input
			saveData("highest_score", highest_score);
			saveData("total_play", total_play);
			saveData("total_score", total_score);
			saveData("total_time", total_time);
			saveData("last_score", last_score);
			saveData("last_jump", last_jump);
			saveData("last_time", last_time);
			saveData("max_jump", max_jump);
			saveData("max_time", max_time);
			saveCommitted();
		}
		else if (mode==2) {
			highest_score=0;total_play=0;total_score=0;total_time=0;
			last_score=0;last_jump=0;last_time=0;max_jump=0;max_time=0;
			set_statistics(1);
		}
	}
	
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (!gamestart || gameover) return;
		switch (sensorEvent.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER: 
				double x0=(double) sensorEvent.values[0];
				doodle.vx=-x0*80;
				doodle.dir=x0>0?false:true;
		}
	}
	
	public void play(String string) {
		try {
			if (music_on)
				soundPool.play(hashMap.get(string), 1f, 1f, 1, 0, 1f);
		} catch (Exception e) {}
	}
	
	public void onPause() {
		super.onPause();
		if (!gameover && gamestart) pause=true;
		if (mediaPlayer.isPlaying()) mediaPlayer.pause();
		if (hatPlayer.isPlaying()) hatPlayer.pause();
		if (rocketPlayer.isPlaying()) rocketPlayer.pause();
	}
	
	public void onResume() {
		super.onResume();
		if (music_on) mediaPlayer.start();
	}
	
	public boolean onTouch(View view,MotionEvent motionEvent) {
		float x=motionEvent.getX(),y=motionEvent.getY();
		mmx=x;mmy=y;
		if (settings) {
			if (mmx>width/2-50 && mmx<width/2+60 && mmy>650 && mmy<716) settings=false;
			if (mmx>width/2+40 && mmx<width/2+100 && mmy>450 && mmy<500) dir_shoot=false;
			if (mmx>width/2-100 && mmx<width/2-45 && mmy>450 && mmy<500) dir_shoot=true;
			if (mmx>width/2-100 && mmx<width/2-45 && mmy>200 && mmy<265)
			{
				mediaPlayer.start();
				music_on=true;
			}
			if (mmx>width/2+40 && mmx<width/2+100 && mmy>200 && mmy<265)
			{
				mediaPlayer.pause();
				music_on=false;
			}
		}
		else if (statistics) {
			if (mmx>width/2-140 && mmx<width/2-45 && mmy>550 && mmy<605) set_statistics(2);
			if (mmx>width/2+75 && mmx<width/2+140 && mmy>550 && mmy<605) statistics=false;
		}
		else if (!gamestart) {
			if (x>width/2-90 && x<width/2+90 && y>140 && y<210) gamestart=true;
			if (x>width/2-120 && x<width/2+120 && y>290 && y<360) settings=true;
			if (x>width/2-150 && x<width/2+150 && y>450 && y<510) statistics=true;
			if (x>width/2-80 && x<width/2+70 && y>590 && y<648) System.exit(0);
		}
		else if (gameover) {
			another_init();
		}
		else if (pause) {
			if (mmx>width/2-100 && mmx<width/2+140 && mmy>280 && mmy<350)
				settings=true;
			else if (mmx>width/2-70 && mmx<width/2+70 && mmy<510 && mmy>440)
				pause=false;
			/*if (mmy>100)
			{
				pause=false;
				if (doodle.state==2 && music_on) hatPlayer.start();
				if (doodle.state==3 && music_on) rocketPlayer.start();;
			}*/
		}
		else if (mmx>width-140 && mmx<width && mmy>13 && mmy<70) {
			pause=true;
			if (hatPlayer.isPlaying()) hatPlayer.pause();
			if (rocketPlayer.isPlaying()) rocketPlayer.pause();
		}
		else if (bullet_dt>0.2) {
			bullets.add(new Bullet(doodle.x+48, doodle.y-48, doodle, this));
			bullet_dt=0;
			play("shoot.ogg");
		}
		return true;
	}

}
