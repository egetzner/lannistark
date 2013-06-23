package at.tugraz.ist.wv.diagnose.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import at.tugraz.ist.wv.diagnose.abstraction.GameLevel;


public class DBProxy {

	/*
	 * CONSTANTS
	 */
	public static final String[] PRESETS = {
		"PRESET_NO_GAMES",
		"PRESET_SELECT_BLACK",
		"PRESET_SELECT_WHITE",
	};
	
	/*
	 * UTILITY
	 */
	
	private DBOpenHelper dbHelper;
	private Context context;
	private SQLiteDatabase readableDatabase;
	private SQLiteDatabase writableDatabase;
	
	
	public DBProxy(Context context) {
		this.context = context;
		this.dbHelper = new DBOpenHelper(context);
		this.readableDatabase = null;
		this.writableDatabase = null;
		
	}
	
	protected SQLiteDatabase getReadableDatabase() {
	
		if (readableDatabase == null)
			readableDatabase = dbHelper.getReadableDatabase();
		return readableDatabase;
	}
	
	protected SQLiteDatabase getWritableDatabase() {
		if (writableDatabase == null)
			writableDatabase = dbHelper.getWritableDatabase();
		return writableDatabase;
	}
	
	public void closeReadableDatabase() {
	
		if (readableDatabase != null)
			readableDatabase.close();
		this.readableDatabase = null;
	}
	
	public void closeWritableDatabase() {
		if (writableDatabase != null)
			writableDatabase.close();
		this.writableDatabase = null;
	}
	
	public void onStop() {
		closeReadableDatabase();
		closeWritableDatabase();
	}
		
    // Adding new contact
	public void addNewLevel(GameLevel level) {
				
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(DBContract.Levels.COL_LVL, level.getLevelNum());
	    values.put(DBContract.Levels.COL_CON, level.getConflicts().toString()); 
	    values.put(DBContract.Levels.COL_DIAG, level.getCurrentDiagnoses().toString());
		 
	    // Inserting Row
		long id = db.insert(DBContract.Levels.TABLE_NAME, null, values);
		
		System.err.println(id);

		closeWritableDatabase();
	}

	public void dumpTables() {
		SQLiteDatabase db = this.getReadableDatabase();    

		Cursor cursor = db.query(
				DBContract.Levels.TABLE_NAME, 
				new String[] {  },
				null,
				new String[] {},
				null, null, null, null);
		
		System.out.println(DatabaseUtils.dumpCursorToString(cursor));
		cursor.close();
		
		cursor = db.query(
				DBContract.TimeLevel.TABLE_NAME, 
				new String[] {  },
				null,
				new String[] {},
				null, null, null, null);
		
		System.out.println(DatabaseUtils.dumpCursorToString(cursor));
		cursor.close();
		
		closeReadableDatabase();
		//dbHelper.close();
	}

	public GameLevel getLevel(int num) {
		
		if (num == 0)
			return null;
		
		SQLiteDatabase db = this.getReadableDatabase();    
	    
		Cursor cursor = db.query(
				DBContract.Levels.TABLE_NAME, 
				new String[] { 
						DBContract.Levels.COL_CON, 
						DBContract.Levels.COL_DIAG,
						DBContract.Levels.COL_HIGHSCORE,
						DBContract.Levels.COL_SCORE},
				DBContract.Levels.COL_LVL+"=?",
				new String[] { String.valueOf(num) },
				null, null, null, null);
		
		GameLevel lvl = null;
		
		if (cursor != null)
		{
			System.out.println(cursor.getColumnCount() + " " + cursor.getCount());

			//move to the last possible entry
			if (cursor.moveToLast())
			{
				lvl = new GameLevel(num, 
						cursor.getString(0), 
						cursor.getString(1),
						cursor.getInt(2),
						cursor.getInt(3));
			}
			
			cursor.close();
		}
		else
			System.out.println("cursor is null?!");

		//dbHelper.close();

		closeReadableDatabase();
	    return lvl;
		
	}
	
	public void updateLevel(GameLevel gameLevel) {
	    SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(DBContract.Levels.COL_CON, gameLevel.getConflicts().toString()); 
	    values.put(DBContract.Levels.COL_DIAG, gameLevel.getCurrentDiagnoses().toString());
		values.put(DBContract.Levels.COL_SCORE, gameLevel.getNumTries());
	    
		if (gameLevel.isComplete() && gameLevel.getNumTries() > gameLevel.getNumTriesBest())
			values.put(DBContract.Levels.COL_HIGHSCORE, gameLevel.getNumTries());
		// Updating Row
		long id = db.update(DBContract.Levels.TABLE_NAME, values, DBContract.Levels.COL_LVL + "=?",
				new String[] {String.valueOf(gameLevel.getLevelNum())});
		
		System.err.println(id);

		//dbHelper.close();
		closeWritableDatabase();	
	}
	
	public void updateScoreForTiming(int difficulty_level, int numLevels, int numSeconds)
	{
		SQLiteDatabase db = this.getWritableDatabase();    
	    
		Cursor cursor = db.query(
				DBContract.TimeLevel.TABLE_NAME, 
				new String[] { 
						DBContract.TimeLevel._ID, 
						},
				DBContract.TimeLevel.COL_DIFF+"=?",
				new String[] { String.valueOf(difficulty_level) },
				null, null, null, null);

		int id = -1;
		if (cursor != null)
		{
			//move to the last possible entry
			if (cursor.moveToLast())
			{
				id = cursor.getInt(0);
			}
			cursor.close();
		}

		
	    ContentValues values = new ContentValues();
	    values.put(DBContract.TimeLevel.COL_DIFF, difficulty_level); 
	    values.put(DBContract.TimeLevel.COL_NUM_LEVELS, numLevels);
		values.put(DBContract.TimeLevel.COL_SECONDS, numSeconds);

		
		if (id > 0)
		{
			// Updating Row
			db.update(DBContract.TimeLevel.TABLE_NAME, values, DBContract.TimeLevel._ID + "=?",
					new String[] {String.valueOf(id)});
		}
		else
			db.insert(DBContract.TimeLevel.TABLE_NAME, null, values);
		
		closeWritableDatabase();
	}
	
	public int[] getScoreForTiming(int difficulty_level)
	{
		SQLiteDatabase db = this.getReadableDatabase();    
	    
		Cursor cursor = db.query(
				DBContract.TimeLevel.TABLE_NAME, 
				new String[] { 
						DBContract.TimeLevel.COL_SECONDS,
						DBContract.TimeLevel.COL_NUM_LEVELS, 
						},
				DBContract.TimeLevel.COL_DIFF+"=?",
				new String[] { String.valueOf(difficulty_level) },
				null, null, String.valueOf(DBContract.TimeLevel.COL_SECONDS), null);
				
		//initialize score array
		int[] score = new int[4];
		for(int i = 0; i < score.length; i++)
			score[i] = 0;
		
		if (cursor != null)
		{
			if (cursor.moveToFirst())
				do {
					score[cursor.getInt(0)] = cursor.getInt(1);
				} while(cursor.moveToNext());
			cursor.close();
		}
		else
			System.out.println("cursor is null or empty!");

		closeReadableDatabase();
	    return score;
	}
	
	public int getRecordForTiming(int difficulty_level, int time_id)
	{
		SQLiteDatabase db = this.getReadableDatabase();    
	    
		Cursor cursor = db.query(
				DBContract.TimeLevel.TABLE_NAME, 
				new String[] { 
						DBContract.TimeLevel.COL_NUM_LEVELS, 
						},
				DBContract.TimeLevel.COL_DIFF + " = ? AND " + DBContract.TimeLevel.COL_SECONDS + " = ? " ,
				new String[] { String.valueOf(difficulty_level), String.valueOf(time_id) },
				null, null, null, null);
				
		int retval = 0;
		if (cursor != null) {
			if (cursor.moveToFirst())
				retval = cursor.getInt(0);
			cursor.close();
		}

		closeReadableDatabase();
	    return retval;
	}

	public void clearDB() {
	    SQLiteDatabase db = this.getWritableDatabase();
		 
	    dbHelper.reinitialize(db);
	    
		closeWritableDatabase();
	}
	
}
