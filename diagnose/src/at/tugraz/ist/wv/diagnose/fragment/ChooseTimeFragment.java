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

public class ChooseTimeFragment extends Fragment implements OnClickListener{

	//information
	private Cursor highscoreCursor;
	
	//textviews
	TextView textviewHighscoreShort;
	TextView textviewHighscoreNormal;
	TextView textviewHighscoreLengthy;
	TextView textviewHighscoreLong;
	
	//callback for chosen time event
	OnTimeChosenListener onTimeChosenListener;
	
    /*
     * Construction interface
     */
    public static ChooseTimeFragment newInstance() {
    	ChooseTimeFragment f = new ChooseTimeFragment();
    	f.initializeFragment();
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

        //bind onClick listeners
        layout.findViewById(R.id.text_label_time_short).setOnClickListener(this);
        layout.findViewById(R.id.text_label_time_normal).setOnClickListener(this);
        layout.findViewById(R.id.text_label_time_lengthy).setOnClickListener(this);
        layout.findViewById(R.id.text_label_time_long).setOnClickListener(this);
        
        //set highscoreCursor to null
        highscoreCursor = null;
        
        //return layout
        return layout;
    }
    
	public interface OnTimeChosenListener {
		public void onTimeChosen(int time);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	onTimeChosenListener = (OnTimeChosenListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeChosenListener");
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
    }

    public void initializeFragment() {
    	//nothing to do here
    }
    
	@Override
	public void onClick(View view) {
		//clicked on a gametype -> start game
		TextView textView = (TextView) view;
		ChooseTimeGameActivity activity = (ChooseTimeGameActivity) getActivity();
		int difficulty = activity.getDifficulty();
		System.out.println("started game from view with text " + textView.getText() + " and difficulty " + difficulty);
	}
}