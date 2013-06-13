package at.tugraz.ist.wv.diagnose;

import java.util.logging.Level;

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

	TextView level;
	TextView points;
	LevelManager manager;
	GameLevel gameLevel;
	GameFragment fragment;
	DBProxy proxy;
	
	ImageView prev;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level);
		
		proxy = new DBProxy(this);
		
		//prepare game fragment
		manager = new LevelManager();
		gameLevel = manager.getNewLevel();
		
		proxy.addNewLevel(gameLevel);
		proxy.dumpTables();

		level = (TextView) findViewById(R.id.text_level);
	
		points = (TextView) findViewById(R.id.text_points);
		points.setText("Points: " + manager.getNumCorrectDiags() + "/" + manager.getNumPossibleDiags());

		
		ImageView next = (ImageView) findViewById(R.id.icon_navigation_next_item);
				
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displaySolveDialog();
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
		
		changeLevel(gameLevel);

	}
		
	private void goToLevel(int i) {
		proxy.updateLevel(gameLevel);
		
		System.out.println("num: " + gameLevel.getLevelNum());
		GameLevel level = proxy.getLevel(i);
		
		if (level == null)
		{
			level = manager.getNewLevel(i);
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
	public void onGameCompleted() {
		
		manager.addToNumTries(gameLevel.getNumTries());
		manager.addToNumCorrectDiags(gameLevel.getCurrentDiagnoses().size());
		manager.addToNumPossibleDiags(gameLevel.getTargetDiagnoses().size());
		points.setText("Points: " + manager.getNumCorrectDiags() + "/" + manager.getNumPossibleDiags());	
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage("You completed the level in " + gameLevel.getNumTries() + " tries!")
		       .setTitle("Congratulations!")
		       .setNegativeButton("backToGame", new AlertDialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.out.println("backToGame");
				}
			})
		       .setPositiveButton("nextLevel", new AlertDialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
		        	   goToLevel(gameLevel.getLevelNum()+1);
				}
			});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void displaySolveDialog() {
		//next was clicked in the level activity, show dialog
		//if the current level is completed, skip to the next one
		if (gameLevel.isComplete()) {
			goToLevel(gameLevel.getLevelNum()+1);
			return;
		} 
		//otherwise show dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setPositiveButton("Solve!", (AlertDialog.OnClickListener) fragment);
		builder.setNegativeButton("Try Another Time", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		        	   goToLevel(gameLevel.getLevelNum()+1);
		           }
		       });
		// Set other dialog properties
		builder.setTitle("Solve or Skip");
		builder.setMessage("If you click \'Try another Time\', all your diagnoses will be lost. " +
				"If you click \'Solve!\', the diagnoses will be calculated for you.");

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}


}
