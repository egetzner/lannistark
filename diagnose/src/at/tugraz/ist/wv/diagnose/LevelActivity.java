package at.tugraz.ist.wv.diagnose;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.wv.diagnose.abstraction.GameLevel;
import at.tugraz.ist.wv.diagnose.abstraction.LevelManager;
import at.tugraz.ist.wv.diagnose.db.DBProxy;
import at.tugraz.ist.wv.diagnose.fragment.GameFragment;
import at.tugraz.ist.wv.diagnose.fragment.GameFragment.OnGameCompletedListener;

public class LevelActivity extends FragmentActivity implements OnGameCompletedListener {

	public static final int MAX_LEVELS = 20;
	
	TextView level;
	TextView points;
	View solve;
	LevelManager manager;
	GameLevel gameLevel;
	GameFragment fragment;
	DBProxy proxy;
	
	ImageView prev;
	ImageView next;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level);
		
		proxy = new DBProxy(this);
		
		//prepare game fragment

		//TODO: temporary solution
		proxy.clearDB();
		manager = new LevelManager(MAX_LEVELS,proxy);
				
		level = (TextView) findViewById(R.id.text_level);
	
		points = (TextView) findViewById(R.id.text_points);
		points.setText("Points: " + manager.getNumCorrectDiags() + "/" + manager.getNumPossibleDiags());

		
		next = (ImageView) findViewById(R.id.icon_navigation_next_item);
				
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToLevel(gameLevel.getLevelNum()+1);
			}
		});
		
		prev = (ImageView) findViewById(R.id.icon_navigation_previous_item);
		
		prev.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToLevel(gameLevel.getLevelNum()-1);
			}
		});
		
		ImageView refresh = (ImageView) findViewById(R.id.icon_navigation_refresh);
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gameLevel.reset();
				changeLevel(gameLevel);
			}
		});		
		
		
		solve = findViewById(R.id.text_solve2);
		solve.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//show dialog
				displaySolveDialog();
			}
		});
		
		//proxy.addNewLevel(gameLevel);
		proxy.dumpTables();

		gameLevel = null;
		goToLevel(1);

	}
		
	private void goToLevel(int i) {
		if (gameLevel != null)
			proxy.updateLevel(gameLevel);
		
		GameLevel level = proxy.getLevel(i);
		
		if (level == null)
		{
			level = manager.getNewLevel(i);
			if (level == null)
				return;
			proxy.addNewLevel(level);
		}
		
		changeLevel(level);
	}

	private void changeLevel(GameLevel lvl)
	{		
		gameLevel = lvl;

		if (gameLevel.getLevelNum() <= 1)
			prev.setClickable(false);
		else
			prev.setClickable(true);
		
		if (gameLevel.getLevelNum() >= LevelActivity.MAX_LEVELS)
			next.setClickable(false);
		else
			next.setClickable(true);
		
		solve.setEnabled(!gameLevel.isComplete());
		
		fragment = GameFragment.newInstance(gameLevel, GameFragment.GAMETYPE_LEVEL_COMPLETION);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
					
		level.setText(getResources().getString(R.string.text_level) + gameLevel.getLevelNum());

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.level, menu);
		return true;
	}

	@Override
	public void onGameCompleted(boolean solvePressed) {
		
		manager.addToNumTries(gameLevel.getNumTries());
		manager.addToNumCorrectDiags(gameLevel.getCurrentDiagnoses().size());
		manager.addToNumPossibleDiags(gameLevel.getTargetDiagnoses().size());
		points.setText("Points: " + manager.getNumCorrectDiags() + "/" + manager.getNumPossibleDiags());	
		
		//TODO: what if clicked on solve?
		if (!solvePressed && gameLevel.getLevelNum() < MAX_LEVELS)
			showLevelEndDialog();
		
		if (gameLevel.getLevelNum() >= MAX_LEVELS)
			showEndOfGameDialog();
		
		solve.setEnabled(false);
		
	

	}


	public void finishActivity(View view) {
		finish();
	}
	
	
	private void showEndOfGameDialog() {
		// TODO Auto-generated method stub
		String header = "Game Over!";
		String message = "You guessed " + manager.getNumCorrectDiags() +" of "+ manager.getNumPossibleDiags()+" possible Diagnoses.";
		AlertDialog dialog = AlertDialogManager.showAlertDialog(
				this, 
				header,
				message
				);
		

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, 
				"Go To Menu", 
				new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
			        	   onBackPressed();
					}
				});
		
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, 
				"Back To Game",
				new AlertDialog.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("backToGame");
					}
				});	

		dialog.show();

	}
	
	private void showLevelEndDialog()
	{
		
		String header = "Congratulations!";
		String message = "You completed the level in " + gameLevel.getNumTries() + ((gameLevel.getNumTries()>1)?" tries!":" try!");
		
		if (gameLevel.getNumTriesBest() > 0 && gameLevel.getNumTries() < gameLevel.getNumTriesBest())
		{
			header = "New High Score!";
			message = "Congratulations! You completed the level in " + gameLevel.getNumTries() + 
					" tries, the highscore was " + gameLevel.getNumTriesBest() +"!";
		}
				
		AlertDialog dialog = AlertDialogManager.showAlertDialog(
				this, 
				header,
				message
				);
		

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, 
				"Next Level", 
				new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
			        	   goToLevel(gameLevel.getLevelNum()+1);					
					}
				});
		
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, 
				"Back To Game",
				new AlertDialog.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("backToGame");
					}
				});	

		dialog.show();
	}
	
	
	public void displaySolveDialog() {
		
		System.out.println("Solve btn called");
		
		AlertDialog dialog = AlertDialogManager.showAlertDialog(
				this, 
				"Calculate Solution",
				"If you request the solution for this level, " +
				"you will lose the points for the unguessed diagnoses."
				);

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, 
				"Show Solution", 
				new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
			        	   fragment.showSolution();					
					}
				});
		
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, 
				"Back To Game", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
	 
		dialog.show();
	}




}
