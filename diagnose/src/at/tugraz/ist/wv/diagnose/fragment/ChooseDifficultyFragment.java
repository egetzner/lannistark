package at.tugraz.ist.wv.diagnose.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tugraz.ist.wv.diagnose.ChooseTimeGameActivity;
import at.tugraz.ist.wv.diagnose.R;

public class ChooseDifficultyFragment extends Fragment implements OnClickListener{

	public interface OnDifficultyChosenListener {
		public void onDifficultyChosen(int difficulty);
	}
	
    /*
     * Construction interface
     */
    public static ChooseDifficultyFragment newInstance() {
    	ChooseDifficultyFragment f = new ChooseDifficultyFragment();
    	f.initializeFragment();
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_choose_difficulty, container, false);

        //bind onClick listeners
        layout.findViewById(R.id.text_label_difficulty_easy).setOnClickListener(this);
        layout.findViewById(R.id.text_label_difficulty_medium).setOnClickListener(this);
        layout.findViewById(R.id.text_label_difficulty_hard).setOnClickListener(this);
        
        //return layout
        return layout;
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
		System.out.println("chosen difficulty from view with text " + textView.getText() + " and difficulty " + difficulty);
		
	}
}