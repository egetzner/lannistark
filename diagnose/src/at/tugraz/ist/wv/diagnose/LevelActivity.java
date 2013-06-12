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
import at.tugraz.ist.wv.diagnose.fragment.GameFragment;
import at.tugraz.ist.wv.diagnose.fragment.GameFragment.OnGameCompletedListener;

public class LevelActivity extends FragmentActivity implements OnGameCompletedListener {

	TextView level;
	TextView points;
	LevelManager manager;
	GameLevel gameLevel;
	GameFragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level);
		
		//prepare game fragment
		manager = new LevelManager();
		gameLevel = manager.getNewLevel();
		fragment = GameFragment.newInstance(gameLevel);
		
		//show game fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
		
		ImageView next = (ImageView) findViewById(R.id.icon_navigation_next_item);
		
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displaySolveDialog();
			}
		});
		
		level = (TextView) findViewById(R.id.text_level);
		level.setText(getResources().getString(R.string.text_level) + manager.getLevelCounter());
	
		points = (TextView) findViewById(R.id.text_points);
		points.setText("Points: " + manager.getNumCorrectDiags() + "/" + manager.getNumPossibleDiags());

	}
	
	private void goToNextLevel()
	{
		gameLevel = manager.getNewLevel();
		fragment = GameFragment.newInstance(gameLevel);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
		
		level.setText(getResources().getString(R.string.text_level) + manager.getLevelCounter());
		//TODO: reset star
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
					goToNextLevel();
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
			goToNextLevel();
			return;
		} 
		//otherwise show dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setPositiveButton("Solve!", (AlertDialog.OnClickListener) fragment);
		builder.setNegativeButton("Try Another Time", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		        	   goToNextLevel();
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
