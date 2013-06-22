package at.tugraz.ist.wv.diagnose.fragment;

import android.app.Activity;
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
import at.tugraz.ist.wv.diagnose.fragment.ChooseTimeFragment.OnDifficultyTappedListener;
import at.tugraz.ist.wv.diagnose.fragment.ChooseTimeFragment.OnTimeChosenListener;

public class ChooseDifficultyFragment extends Fragment {

	private OnDifficultyChosenListener onDifficultyChosenListener;
	
	public interface OnDifficultyChosenListener {
		public void onDifficultyChosen(Difficulty difficulty);
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
        layout.findViewById(R.id.text_label_difficulty_easy).setOnClickListener(new OnItemClickListener(Difficulty.EASY));
        layout.findViewById(R.id.text_label_difficulty_medium).setOnClickListener(new OnItemClickListener(Difficulty.MEDIUM));
        layout.findViewById(R.id.text_label_difficulty_hard).setOnClickListener(new OnItemClickListener(Difficulty.HARD));
        
        //return layout
        return layout;
    }
 
    public void initializeFragment() {
    	//nothing to do here
    }
	
    @Override
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	onDifficultyChosenListener = (OnDifficultyChosenListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDifficultyChosenListener");
        }
    }
    
    public class OnItemClickListener implements OnClickListener {
    	private Difficulty difficulty;
    	public OnItemClickListener(Difficulty difficulty) {
    		this.difficulty = difficulty;
    	}
		@Override
		public void onClick(View v) {
			onDifficultyChosenListener.onDifficultyChosen(difficulty);
		}
    }
}