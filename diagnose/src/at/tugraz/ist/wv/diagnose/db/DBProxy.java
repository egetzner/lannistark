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
	
	
	public String getConstraintSet(int id, String col_name)
	{
		SQLiteDatabase db = this.getReadableDatabase();    
	    
		Cursor cursor = db.query(
				DBContract.Levels.TABLE_NAME, 
				new String[] { col_name },
				//BContract.Levels.COL_LVL+"=?",
				null,
				new String[] { /*String.valueOf(id)*/ },
				null, null, null, null);
		
		String ret = "nothin'";
		
		if (cursor != null)
		{
			System.out.println(cursor.getColumnCount() + " " + cursor.getCount());

			if (cursor.moveToFirst())
				ret = cursor.getString(0);
			
			cursor.close();
		}
		else
			System.out.println("cursor is null?!");

		//dbHelper.close();

		closeReadableDatabase();
	    return ret;
	}
	
    // Adding new contact
	public void addNewLevel(GameLevel level) {
		
		//TODO: see if level exists already!!
		
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(DBContract.Levels.COL_LVL, level.getLevelNum());
	    values.put(DBContract.Levels.COL_CON, level.getConflicts().toString()); 
	    values.put(DBContract.Levels.COL_DIAG, level.getCurrentDiagnoses().toString());
		 
	    // Inserting Row
		long id = db.insert(DBContract.Levels.TABLE_NAME, null, values);
		
		System.err.println(id);

		//dbHelper.close();
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
	    
		if (gameLevel.getNumTries() > gameLevel.getNumTriesBest())
			values.put(DBContract.Levels.COL_HIGHSCORE, gameLevel.getNumTries());
		// Updating Row
		long id = db.update(DBContract.Levels.TABLE_NAME, values, DBContract.Levels.COL_LVL + "=?",
				new String[] {String.valueOf(gameLevel.getLevelNum())});
		
		System.err.println(id);

		//dbHelper.close();
		closeWritableDatabase();
		
	}

	public void clearDB() {
	    SQLiteDatabase db = this.getWritableDatabase();
		 
	    dbHelper.reinitialize(db);
	    
		closeWritableDatabase();
	}
	
}
