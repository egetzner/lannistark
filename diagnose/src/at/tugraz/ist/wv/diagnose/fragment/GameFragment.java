package at.tugraz.ist.wv.diagnose.fragment;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import at.tugraz.ist.wv.diagnose.abstraction.LevelManager;
import at.tugraz.ist.wv.diagnose.adapter.ConstraintAdapter;
import at.tugraz.ist.wv.diagnose.adapter.ConstraintListAdapter;
import at.tugraz.ist.wv.diagnose.processing.DiagnoseCalculator;

public class GameFragment extends Fragment implements AlertDialog.OnClickListener {

	//adapters
	private ConstraintListAdapter conflictListAdapter;
	private ConstraintListAdapter diagnoseListAdapter;
	private ConstraintAdapter diagnoseAdapter;
	private ConstraintAdapter constraintAdapter;
	
	//constraint sets
	//private Set<Set<Constraint>> conflictSet;
	private Set<Set<Constraint>> diagnoseSet;
	
	//gameplay
	private DiagnoseCalculator diagnoseCalculator;
	private int numTries;
	private int numGuessed;
	private int numBest;
	
	//textviews
	TextView textviewDiagnoses;
	TextView textviewTries;
	TextView textviewBest;
	
    OnGameCompletedListener onGameCompletedListener;

    GameLevel level;
    
	//callback listener for activities
    public interface OnGameCompletedListener {
		void onGameCompleted(int numTries, int numDiags, int numDiagsTotal);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	onGameCompletedListener = (OnGameCompletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
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
        
        
        level = LevelManager.getInstance().getNewLevel();
                
        //handle input
        diagnoseSet = new HashSet<Set<Constraint>>();
        
	    	//handle other input data
	    numTries = 0;
	    numGuessed = 0;
	    numBest = 0;
        
        //process diagnoses
        diagnoseCalculator = level.getDiagnosisCalculator(); 
        
        //show text information
        updateTextInformation();
        
        //prepare set for conflict pool
        Set<Constraint> constraintPool = new HashSet<Constraint>();
        for (Set<Constraint> input : level.getConflicts())
        	constraintPool.addAll(input);
        
        //create conflictList
        ListView conflictList = (ListView) layout.findViewById(R.id.listview_minimalconflicts);
        conflictListAdapter = new ConstraintListAdapter(getActivity(), level.getConflicts(), true);
        conflictList.setAdapter(conflictListAdapter);
        
        //create diagnoseList
        ListView diagnoseList = (ListView) layout.findViewById(R.id.listview_diagnoses);
        diagnoseListAdapter = new ConstraintListAdapter(getActivity(), diagnoseSet, false);
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
        constraintAdapter = new ConstraintAdapter(getActivity(), constraintPool, true);
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
    
    public void clearDiagnose() {
    	diagnoseAdapter.clear();
    	notifyAdapters();
    }
    
    public void addDiagnose() {
    	numTries++;
    	Set<Constraint> diagnose = new HashSet<Constraint>(diagnoseAdapter.getConstraints());
    	if (!diagnoseSet.contains(diagnose)) {
    		if (diagnoseCalculator.isDiagnose(diagnose)) {
	    		diagnoseSet.add(diagnose);
	    		numGuessed++;
	    		diagnoseListAdapter.addDiagnose(diagnoseAdapter.getConstraints());
	    		checkGameCompletion();
    		} else {
    			Toast.makeText(getActivity(), getActivity().getString(R.string.fragment_game_toast_no_diagnose), Toast.LENGTH_SHORT).show();
    		}
    	} else {
    		Toast.makeText(getActivity(), getActivity().getString(R.string.fragment_game_toast_duplicate_diagnose), Toast.LENGTH_SHORT).show();
    	}
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
    	textviewDiagnoses.setText(getActivity().getString(R.string.fragment_game_text_num_diagnoses) + diagnoseSet.size() + "/" + diagnoseCalculator.getNumberOfDiagnoses());
    	textviewTries.setText(getActivity().getString(R.string.fragment_game_text_num_tries) + numTries);
    	textviewBest.setText(getActivity().getString(R.string.fragment_game_text_num_best) + numBest);
    }
    
    private void checkGameCompletion() {
    	System.out.println(diagnoseSet.size() + " / " + diagnoseCalculator.getNumberOfDiagnoses());
    	if (diagnoseSet.size() == diagnoseCalculator.getNumberOfDiagnoses())
    		//game complete
    		onGameCompletedListener.onGameCompleted(numTries,numGuessed,diagnoseSet.size());
    }
    
	void onSolve(View v)
	{
		System.out.println("on Solve called!");
		onSolve();
		    	
	}
	void onSolve()
	{
    	for (Set<Constraint> diagnose: diagnoseCalculator.getDiagnoses())
		{
        	if (!diagnoseSet.contains(diagnose)) {
        		{
    	    		diagnoseSet.add(diagnose);
    	    		diagnoseListAdapter.addDiagnose(diagnose);
    	    		checkGameCompletion();
        		} 
        	} else {
        		//no need to do anything, we already have the diagnosis
        	}
		}

    	diagnoseAdapter.clear();
    	notifyAdapters();
    	updateTextInformation();
		onGameCompletedListener.onGameCompleted(numTries,numGuessed,diagnoseSet.size());


	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		onSolve();
		
	}



}
