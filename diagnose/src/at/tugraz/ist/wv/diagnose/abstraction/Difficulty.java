package at.tugraz.ist.wv.diagnose.abstraction;

import at.tugraz.ist.wv.diagnose.R;

public enum Difficulty {
	EASY(0, "easy", R.color.green), MEDIUM(1, "medium", R.color.yellow), HARD(2, "hard", R.color.red);
	
	private int index;
	private String text;
	private int color;
	
	Difficulty(int index, String text, int color) {
		this.index = index;
		this.text = text;
		this.color = color;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getText() {
		return text;
	}

	public int getColor() {
		return color;
	}
	
	public static Difficulty getDifficulty(int index) {
		for (Difficulty difficulty : Difficulty.values()) {
			if (difficulty.getIndex() == index)
				return difficulty;
		}
		return null;
	}
}
