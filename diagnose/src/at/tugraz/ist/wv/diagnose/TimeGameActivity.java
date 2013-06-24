package at.tugraz.ist.wv.diagnose;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import at.tugraz.ist.wv.diagnose.abstraction.Difficulty;
import at.tugraz.ist.wv.diagnose.abstraction.GameLevel;
import at.tugraz.ist.wv.diagnose.abstraction.LevelManager;
import at.tugraz.ist.wv.diagnose.db.DBProxy;
import at.tugraz.ist.wv.diagnose.fragment.GameFragment;
import at.tugraz.ist.wv.diagnose.fragment.GameFragment.GametypeTimingBasicInformationSupplier;
import at.tugraz.ist.wv.diagnose.fragment.GameFragment.OnGameCompletedListener;

public class TimeGameActivity extends FragmentActivity implements OnGameCompletedListener, GametypeTimingBasicInformationSupplier {

	//utility classes
	LevelManager manager;
	DBProxy dbProxy;
	Timer timer;
	
	//views
	TextView level;
	TextView time;
	ImageView prev;
	
	//information
	Difficulty difficulty;
	int timeId;
	
	//game progress
	int completed;
	int record;
	int seconds;
		
	//game handling
	GameLevel gameLevel;
	GameFragment fragment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_game);
		
		//get information from bundle
		difficulty = Difficulty.getDifficulty(getIntent().getExtras().getInt(ChooseTimeGameActivity.EXTRA_DIFFICULTY_INDEX));
		timeId = getIntent().getExtras().getInt(ChooseTimeGameActivity.EXTRA_TIME_INDEX);
				
		//set record and current completed information for this game mode
		dbProxy = new DBProxy(this);
		record = dbProxy.getRecordForTiming(difficulty.getIndex(), timeId);
		completed = 0;
		
		//initialize level Manager
		manager = new LevelManager();
		manager.setDifficulty(difficulty);
		
		//bind views
		level = (TextView) findViewById(R.id.text_level);
		time = (TextView) findViewById(R.id.text_time);
		
		//button NEXT: setOnClickListener
		ImageView next = (ImageView) findViewById(R.id.icon_navigation_next_item);
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeLevel(manager.getNewLevelOrNull());
			}
		});
		
		//button PREV: disable
		prev = (ImageView) findViewById(R.id.icon_navigation_previous_item);
		prev.setEnabled(false);
		prev.setImageDrawable(getResources().getDrawable(R.drawable.navigation_previous_item_disabled));
		
		//button REFRESH: disable
		ImageView refresh = (ImageView) findViewById(R.id.icon_navigation_refresh);
		refresh.setEnabled(false);
		refresh.setImageDrawable(getResources().getDrawable(R.drawable.navigation_refresh_disabled));
		
		//prepare fragment
		gameLevel = manager.getNewLevelOrNull();
		changeLevel(gameLevel);
		
		//start timer
		switch(timeId) {
		case 0:
			seconds = 60;
			break;
		case 1:
			seconds = 120;
			break;
		case 2:
			seconds = 180;
			break;
		case 3:
			seconds = 300;
			break;
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tick();
					}
				});
			}
		}, 
		0, 1000);
		
	}
		
	private void changeLevel(GameLevel lvl)
	{		
		gameLevel = lvl;

		fragment = GameFragment.newInstance(gameLevel, GameFragment.GAMETYPE_TIME_BASIC);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
					
		level.setText(getResources().getString(R.string.text_level) + gameLevel.getLevelNum());

	}
	
	public void onGameCompleted(boolean solvePressed) {
		completed++;
		gameLevel = manager.getNewLevelOrNull();
		changeLevel(gameLevel);
	}
	
	@Override
	public int getNumCompletedLevels() {
		return completed;
	}

	@Override
	public int getNumCompletedLevelsBest() {
		return record;
	}
	
	public void finishActivity(View view) {
		timer.cancel();
		finish();
	}
	
	public void tick() {
		seconds--;
        int lnMin  = (seconds / 60);
        int lnSec  = (seconds % 60);            
        String lcStr =    ( lnMin < 10 ? "0": "") + String.valueOf(lnMin) +":"+
                          ( lnSec < 10 ? "0": "") + String.valueOf(lnSec) ;
        time.setText(lcStr);
        
        if (seconds == 30)
        	time.setTextColor(getResources().getColor(R.color.yellow));
        
        if (seconds == 10)
        	time.setTextColor(getResources().getColor(R.color.orange));
        
        if (seconds == 0) {
        	if (completed > record) {
        		//insert new record in db
        		dbProxy.updateScoreForTiming(difficulty.getIndex(), completed, timeId);
        	}
        	
        	
        	time.setTextColor(getResources().getColor(R.color.red));
        	timer.cancel();
        	showEndGameDialog();
        }
	}
	
	private void showEndGameDialog() {
		String header = null;
		String message = null;
		if (completed > record) {
			header = "Congratulations!";
			message = "You completed " + completed + " levels.\nThis is a new record!";
		} else {
			header = "Game Over!";
			message = "You completed " + completed + " levels.";			
		}
		
		AlertDialog dialog = AlertDialogManager.showAlertDialog(
				this, 
				header,
				message
				);
	
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, 
				"End Game",
				new AlertDialog.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});	

		dialog.show();
	}
}
