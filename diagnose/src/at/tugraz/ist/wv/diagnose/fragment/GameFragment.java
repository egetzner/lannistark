package at.tugraz.ist.wv.diagnose.fragment;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.wv.diagnose.R;
import at.tugraz.ist.wv.diagnose.abstraction.Constraint;
import at.tugraz.ist.wv.diagnose.abstraction.GameLevel;
import at.tugraz.ist.wv.diagnose.adapter.ConstraintAdapter;
import at.tugraz.ist.wv.diagnose.adapter.ConstraintListAdapter;

public class GameFragment extends Fragment {

	//error constants
	public static final int ERROR_DUPLICATE_DIAGNOSE = -1;
	public static final int ERROR_INVALID_DIAGNOSE = -2;
	
	//gametype constants
	public static final int GAMETYPE_LEVEL_COMPLETION = 1;
	public static final int GAMETYPE_TIME_BASIC = 2;
	
	//adapters
	private ConstraintListAdapter conflictListAdapter;
	private ConstraintListAdapter diagnoseListAdapter;
	private ConstraintAdapter diagnoseAdapter;
	private ConstraintAdapter constraintAdapter;
	
	//game information
	GameLevel level;
	int gametype;
	
	//textviews
	TextView textviewDiagnoses;
	TextView textviewTries;
	TextView textviewBest;
	
	//activity interfaces
    OnGameCompletedListener onGameCompletedListener;
    GametypeTimingBasicInformationSupplier gametypeTimingBasicInformationSupplier; 
    
	//callback listener for activities
    public interface OnGameCompletedListener {
		void onGameCompleted(boolean solutionRequested);
    }
    
    //callback for information getters in gametype: GAMETYPE_TIMING_BASIC
    public interface GametypeTimingBasicInformationSupplier {
    	int getNumCompletedLevels();
    	int getNumCompletedLevelsBest();
    }
    
    /*
     * Construction interface
     */
    public static GameFragment newInstance(GameLevel gameLevel, int gametype) {
    	GameFragment f = new GameFragment();
    	f.initializeFragment(gameLevel, gametype);
        return f;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	onGameCompletedListener = (OnGameCompletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
        if (gametype == GAMETYPE_TIME_BASIC) {
	        try {
	        	gametypeTimingBasicInformationSupplier = (GametypeTimingBasicInformationSupplier) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement GametypeTimingBasicInformationSupplier");
	        }
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_game, container, false);

        //bind text views
        textviewDiagnoses = (TextView) layout.findViewById(R.id.text_diagnoses);
        textviewTries = (TextView) layout.findViewById(R.id.text_tries);
        textviewBest = (TextView) layout.findViewById(R.id.text_best);
        //if game is not persistent (-> no levelActivity) change label of numtries to numsolved, as we are counting how many levels we have completed in a given time
        if (gametype == GAMETYPE_TIME_BASIC) {
        	TextView text = (TextView) layout.findViewById(R.id.text_label_tries);
        	text.setText(R.string.fragment_game_text_num_completed);
        }
        
        //show text information
        updateTextInformation();
        
        //create conflictList
        ListView conflictList = (ListView) layout.findViewById(R.id.listview_minimalconflicts);
        conflictListAdapter = new ConstraintListAdapter(getActivity(), level.getConflicts(), true);
        conflictList.setAdapter(conflictListAdapter);
        
        //create diagnoseList
        ListView diagnoseList = (ListView) layout.findViewById(R.id.listview_diagnoses);
        diagnoseListAdapter = new ConstraintListAdapter(getActivity(), level.getCurrentDiagnoses(), false);
        diagnoseList.setAdapter(diagnoseListAdapter);
        
        
        //create diagnose grid
        GridView diagnoseGrid = (GridView) layout.findViewById(R.id.gridview_diagnose);
        diagnoseAdapter = new ConstraintAdapter(getActivity(), new HashSet<Constraint>(), false);
        diagnoseGrid.setAdapter(diagnoseAdapter);
        diagnoseGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Constraint constraint = (Constraint) parent.getItemAtPosition(position);
            	diagnoseAdapter.toggleConstraint(constraint);
            	notifyAdapters();
            }
        });
        
        //create constraints grid
        GridView constraintsGrid = (GridView) layout.findViewById(R.id.gridview_constraints);
        
        if (level.isComplete())
        {
        	constraintAdapter = new ConstraintAdapter(getActivity(),new HashSet<Constraint>(), true);
        	
        	//constraintsGrid.setVisibility(View.INVISIBLE);
            View diag = (View) layout.findViewById(R.id.diagnose_control);
            diag.setVisibility(View.GONE);
        }
        else
        	constraintAdapter = new ConstraintAdapter(getActivity(), level.getAvailableConstraints(), true);
        constraintsGrid.setAdapter(constraintAdapter);
        constraintsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Constraint constraint = (Constraint) parent.getItemAtPosition(position);
            	diagnoseAdapter.toggleConstraint(constraint);
            	notifyAdapters();
            }
        });
        
        


        //setup onClick controls
        //clear diagnose
        layout.findViewById(R.id.icon_navigation_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				clearDiagnose();
		}});
        //accept diagnose
        layout.findViewById(R.id.icon_navigation_accept).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addDiagnose();
		}});
                
        //return layout
        return layout;
    }

    public void initializeFragment(GameLevel gameLevel, int gametype) {
    	this.level = gameLevel;
    	this.gametype = gametype;
    }
    
    public void clearDiagnose() {
    	diagnoseAdapter.clear();
    	notifyAdapters();
    }
    
    public void addDiagnose() {
    	//get diagnose from adapter
    	Set<Constraint> diagnose = new HashSet<Constraint>(diagnoseAdapter.getConstraints());
    	
    	//attempt to add diagnose
    	int error = level.addDiagnose(diagnose);
 
    	//handle errors
    	if (error == ERROR_INVALID_DIAGNOSE) {
    		Toast.makeText(getActivity(), getActivity().getString(R.string.fragment_game_toast_no_diagnose), Toast.LENGTH_SHORT).show();
    	} else if (error == ERROR_DUPLICATE_DIAGNOSE) {
    		Toast.makeText(getActivity(), getActivity().getString(R.string.fragment_game_toast_duplicate_diagnose), Toast.LENGTH_SHORT).show();
    	} else {
    		//no error
    		diagnoseListAdapter.addDiagnose(diagnose);
    		checkGameCompletion();
    	}
    	
    	//update ui
    	diagnoseAdapter.clear();
    	notifyAdapters();
    	updateTextInformation();
    }
    
    private void notifyAdapters() {
    	conflictListAdapter.notifyDataSetChanged();
    	diagnoseListAdapter.notifyDataSetChanged();
    	diagnoseAdapter.notifyDataSetChanged();
    	constraintAdapter.notifyDataSetChanged();
    }
    
    private void updateTextInformation() {
    	textviewDiagnoses.setText(level.getCurrentDiagnoses(	).size() + "/" + level.getTargetDiagnoses().size());
    	if (gametype == GAMETYPE_LEVEL_COMPLETION) {
	    	textviewTries.setText(String.valueOf(level.getNumTries()));
	    	if (level.getNumTriesBest() == 0)
	    		textviewBest.setText("-");
	    	else
	    		textviewBest.setText(String.valueOf(level.getNumTriesBest()));
    	} 
    	if (gametype == GAMETYPE_TIME_BASIC) {
	    	textviewTries.setText(String.valueOf(gametypeTimingBasicInformationSupplier.getNumCompletedLevels()));
	    	textviewBest.setText(String.valueOf(gametypeTimingBasicInformationSupplier.getNumCompletedLevelsBest()));
    	}
    }
    
    private void setGameCompletion(boolean solve)
    {
    	diagnoseAdapter.clear();
    	constraintAdapter.clear();
		onGameCompletedListener.onGameCompleted(solve);
    }
    
    private void checkGameCompletion() {
    	if (level.isComplete())
    		setGameCompletion(false);
    }
    
    
	public void showSolution() {
		//TODO: add some sort of penalty, maybe depending on gametype

    	for (Set<Constraint> diagnose : level.getTargetDiagnoses())
		{
        	if (!level.getCurrentDiagnoses().contains(diagnose)) 
    		{
        		//do not add the diagnose to the level, as the user didn't guess it himself. 
        		
	    		//level.addDiagnose(diagnose);
	    		diagnoseListAdapter.addDiagnose(diagnose);
    		} 
        	else {
        		//no need to do anything, we already have the diagnosis
        	}
		}

    	setGameCompletion(true);
    	notifyAdapters();
    	updateTextInformation();
	}

}
