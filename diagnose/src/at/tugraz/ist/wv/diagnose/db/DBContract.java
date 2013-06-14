package at.tugraz.ist.wv.diagnose.db;

import android.provider.BaseColumns;
import at.tugraz.ist.wv.diagnose.abstraction.GameLevel;

public abstract class DBContract {

	//tables
	
	//table describing game and options
	public static abstract class Levels implements BaseColumns {
	    public static final String TABLE_NAME = "levels";
	    public static final String COL_LVL = "level";
	    public static final String COL_CON = "constraints";
	    public static final String COL_DIAG = "diagnoses";
	    public static final String COL_SCORE = "score";
	    public static final String COL_HIGHSCORE = "highscore";

	}
	
	public static abstract class TimeLevel implements BaseColumns {
	    public static final String TABLE_NAME = "time_level";
	    public static final String COL_DIFF = "difficulty";
	    public static final String COL_NUM_LEVELS = "num_levels";
	    public static final String COL_SECONDS = "seconds";
	}
	
	//default queries
	
	private static final String TYPE_TEXT = " TEXT";
	private static final String CONSTRAINT_UNIQUE = " UNIQUE";
	private static final String TYPE_INTEGER = " INTEGER";
	private static final String TYPE_BOOLEAN = " BOOLEAN";
	private static final String TYPE_DATETIME =	 " DATETIME DEFAULT CURRENT_TIMESTAMP";
	private static final String TYPE_PRIMARY_KEY = " INTEGER PRIMARY KEY";
	private static final String COMMA_SEP = ",";
	
	public static final String[] SQL_CREATE_ENTRIES = new String[]{
		
		"CREATE TABLE " + Levels.TABLE_NAME + "(" + 
				Levels._ID + 			TYPE_PRIMARY_KEY + 	COMMA_SEP + 
				Levels.COL_LVL + 		TYPE_INTEGER + 		COMMA_SEP + 
				Levels.COL_CON + 		TYPE_TEXT + 		COMMA_SEP + 
				Levels.COL_DIAG + 		TYPE_TEXT + 		COMMA_SEP +
				Levels.COL_SCORE + 		TYPE_INTEGER +	 " DEFAULT 0" + COMMA_SEP +
				Levels.COL_HIGHSCORE + 	TYPE_INTEGER +	 " DEFAULT 0" + 

				" )"
		,
		"CREATE TABLE " + TimeLevel.TABLE_NAME + "(" + 
				TimeLevel._ID + 			TYPE_PRIMARY_KEY + 	COMMA_SEP + 
				TimeLevel.COL_DIFF + 		TYPE_INTEGER + 		COMMA_SEP + 
				TimeLevel.COL_NUM_LEVELS + 		TYPE_INTEGER + 		COMMA_SEP + 
				TimeLevel.COL_SECONDS + 		TYPE_INTEGER + 		COMMA_SEP +

				" )"

	    };
	

	public static final String[] SQL_DELETE_ENTRIES = new String[] {
		//Table Game
	    "DROP TABLE IF EXISTS " + DBContract.Levels.TABLE_NAME,
	    "DROP TABLE IF EXISTS " + DBContract.TimeLevel.TABLE_NAME,

	};
	
	//private constructor - don't construct this!
	private DBContract() {}
}
