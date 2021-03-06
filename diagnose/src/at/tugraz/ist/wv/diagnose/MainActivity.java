package at.tugraz.ist.wv.diagnose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import at.tugraz.ist.wv.diagnose.db.DBProxy;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

	}

	public void onSubmit(View v) {				
    	Intent intent = new Intent(this, ChooseTimeGameActivity.class);
    	startActivity(intent);
	}
	
	public void onBtnLevelList(View v) {
    	Intent intent = new Intent(this, LevelActivity.class);
    	startActivity(intent);
	}	
	
	public void onBtnClearDB(View v) {
		DBProxy proxy = new DBProxy(this);
		proxy.clearDB();
		proxy.dumpTables();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
				
		return true;
	}

}
