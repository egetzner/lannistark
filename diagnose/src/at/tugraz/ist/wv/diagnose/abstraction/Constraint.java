package at.tugraz.ist.wv.diagnose.abstraction;

import java.util.HashSet;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.R;


public enum Constraint {
	RED(0), GREEN(1), BLUE(2), CYAN(3), MAGENTA(4), YELLOW(5), BLACK(6), WHITE(7); 
	
	private int colorIndex;
	private boolean active;
	
	private Constraint(int colorIndex) {
		this.colorIndex = colorIndex;
		this.active = true;
	}
	
	public void activate() {
		this.active = true;
	}
	
	public void deactivate() {
		this.active = false;
	}
	
	public static Set<Constraint> getAllConstraints()
	{
		HashSet<Constraint> set = new HashSet<Constraint>();
		set.add(RED);
		set.add(GREEN);
		set.add(BLUE);
		set.add(CYAN);
		set.add(MAGENTA);
		set.add(YELLOW);
		set.add(BLACK);
		set.add(WHITE);
		return set;
	}
	
	public int getDrawable() {
		if (active)
			return drawableIds[colorIndex];
		else
			return drawableInactive;
	}
	
	public int getActiveDrawable() {
		return drawableIds[colorIndex];
	}
	
	private static final int[] drawableIds = new int[] {
		R.drawable.ball_red,
		R.drawable.ball_green,
		R.drawable.ball_blue,
		R.drawable.ball_cyan,
		R.drawable.ball_magenta,
		R.drawable.ball_yellow,
		R.drawable.ball_black,
		R.drawable.ball_white
	};
	
	private static final int drawableInactive = R.drawable.ball_grey;
}
