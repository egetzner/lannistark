package at.tugraz.ist.wv.diagnose.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 7;

	// Database Name
	private static final String DATABASE_NAME = "mhs";

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		System.out.println("onCreate called, ");
		
    	try{
        	for (String query : DBContract.SQL_CREATE_ENTRIES) 
        		db.execSQL(query);
    	} catch (Exception e)
    	{
    		//do nothing, robolectric calls this once too many.
    		//reinitialize(db);
    	}		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		reinitialize(db);
	}

	public DBOpenHelper(Context context) {
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	public void reinitialize(SQLiteDatabase db)
	{
		
		System.out.println("reinit");
		
    	for (String query : DBContract.SQL_DELETE_ENTRIES) 
    		db.execSQL(query);

		// Create tables again
		onCreate(db);		

	}
	
}
