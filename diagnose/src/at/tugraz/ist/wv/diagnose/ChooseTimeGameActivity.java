package at.tugraz.ist.wv.diagnose;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import at.tugraz.ist.wv.diagnose.fragment.ChooseTimeFragment;
import at.tugraz.ist.wv.diagnose.fragment.ChooseTimeFragment.OnTimeChosenListener;

public class ChooseTimeGameActivity extends FragmentActivity implements OnTimeChosenListener {

	public static final String CHOOSETIMEGAMEACTIVITY_KEY_DIFFICULTY = "DIFFICULTY";
	
	private int difficulty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_time_game);
		setupActionBar();
		
		//set initial difficulty
		if (savedInstanceState != null && savedInstanceState.containsKey(CHOOSETIMEGAMEACTIVITY_KEY_DIFFICULTY))
			difficulty = savedInstanceState.getInt(CHOOSETIMEGAMEACTIVITY_KEY_DIFFICULTY);
		else
			difficulty = 0; //TODO: create difficulty constants in level or levelManager and use those
		
		//create initial fragment
		Fragment fragment = ChooseTimeFragment.newInstance();
		
		//show game fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
	}
	
	public int getDifficulty() {
		return difficulty;
	}

	
	
	/*
	 * INITIAL LIFECYCLE SUPPORT METHODS
	 */
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_time_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTimeChosen(int time) {
		// TODO Auto-generated method stub
		
	}
}
