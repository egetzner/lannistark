package at.tugraz.ist.wv.diagnose.fragment;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tugraz.ist.wv.diagnose.ChooseTimeGameActivity;
import at.tugraz.ist.wv.diagnose.R;
import at.tugraz.ist.wv.diagnose.abstraction.Difficulty;

public class ChooseTimeFragment extends Fragment implements OnClickListener{

	//information
	private Cursor highscoreCursor;
	private Difficulty difficulty;
	
	//textviews
	TextView textviewHighscoreShort;
	TextView textviewHighscoreNormal;
	TextView textviewHighscoreLengthy;
	TextView textviewHighscoreLong;
	TextView textviewDifficulty;
	
	//activity callbacks for events 
	OnTimeChosenListener onTimeChosenListener;
	OnDifficultyTappedListener onDifficultyTappedListener;
	
    /*
     * Construction interface
     */
    public static ChooseTimeFragment newInstance(Difficulty difficulty) {
    	ChooseTimeFragment f = new ChooseTimeFragment();
    	f.initializeFragment(difficulty);
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_choose_time, container, false);

        //bind text views
        textviewHighscoreShort   = (TextView) layout.findViewById(R.id.text_highscore_short);
        textviewHighscoreNormal  = (TextView) layout.findViewById(R.id.text_highscore_normal);
        textviewHighscoreLengthy = (TextView) layout.findViewById(R.id.text_highscore_lengthy);
        textviewHighscoreLong    = (TextView) layout.findViewById(R.id.text_highscore_long);

        //bind onClick listeners for time items
        layout.findViewById(R.id.layout_time_short).setOnClickListener(new OnItemClickedListener(0));
        layout.findViewById(R.id.layout_time_normal).setOnClickListener(new OnItemClickedListener(1));
        layout.findViewById(R.id.layout_time_lengthy).setOnClickListener(new OnItemClickedListener(2));
        layout.findViewById(R.id.layout_time_long).setOnClickListener(new OnItemClickedListener(3));
        
        //bind onClick listener for difficulty labels
        layout.findViewById(R.id.layout_difficulty).setOnClickListener(this);
        
        //show difficulty
        textviewDifficulty = (TextView) layout.findViewById(R.id.text_difficulty);
        textviewDifficulty.setText(difficulty.getText());
        textviewDifficulty.setTextColor(getResources().getColor(difficulty.getColor()));
        
        //set highscoreCursor to null
        highscoreCursor = null;
        
        //return layout
        return layout;
    }
    
	public interface OnTimeChosenListener {
		public void onTimeChosen(int time);
	}
	
	public interface OnDifficultyTappedListener {
		public void onDifficultyTapped();
	}
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	onTimeChosenListener = (OnTimeChosenListener) activity;
        	onDifficultyTappedListener = (OnDifficultyTappedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeChosenListener and OnDifficultyTappedListener");
        }
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	//TODO: get cursor and update highscore information
    }
    
    @Override
    public void onStop() {
    	if (highscoreCursor != null) {
    		highscoreCursor.close();
    		highscoreCursor = null;
    	}
    	super.onStop();
    }

    public void initializeFragment(Difficulty difficulty) {
    	this.difficulty = difficulty;
    }
    
    public class OnItemClickedListener implements OnClickListener {
    	
    	public int id;
    	
    	public OnItemClickedListener(int id) {
    		this.id = id;
    	}
		
    	@Override
		public void onClick(View view) {
			onTimeChosenListener.onTimeChosen(id);
		}
    }
    
	@Override
	public void onClick(View view) {
		onDifficultyTappedListener.onDifficultyTapped();
	}
}