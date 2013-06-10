package at.tugraz.ist.wv.diagnose.fragment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import at.tugraz.ist.wv.diagnose.R;
import at.tugraz.ist.wv.diagnose.abstraction.Constraint;
import at.tugraz.ist.wv.diagnose.adapter.ConstraintAdapter;
import at.tugraz.ist.wv.diagnose.adapter.ConstraintListAdapter;
import at.tugraz.ist.wv.diagnose.processing.DiagnoseCalculator;

public class GameFragment extends Fragment {

	//adapters
	private ConstraintListAdapter conflictListAdapter;
	private ConstraintListAdapter diagnoseListAdapter;
	private ConstraintAdapter diagnoseAdapter;
	private ConstraintAdapter constraintAdapter;
	
	//constraint sets
	Set<Set<Constraint>> conflictSet;
	Set<Set<Constraint>> diagnoseSet;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_game, container, false);

        
        //handle input
        conflictSet = new HashSet<Set<Constraint>>();
        diagnoseSet = new HashSet<Set<Constraint>>();
        	//add dummy input
        Set<Constraint> constraints = new HashSet<Constraint>();
        constraints.add(Constraint.GREEN);
        constraints.add(Constraint.BLACK);
        constraints.add(Constraint.RED);
        conflictSet.add(constraints);
        constraints = new HashSet<Constraint>();
        constraints.add(Constraint.BLUE);
        constraints.add(Constraint.CYAN);
        constraints.add(Constraint.MAGENTA);
        constraints.add(Constraint.WHITE);
        conflictSet.add(constraints);
        constraints = new HashSet<Constraint>();
        constraints.add(Constraint.YELLOW);
        constraints.add(Constraint.GREEN);
        constraints.add(Constraint.WHITE);
        conflictSet.add(constraints);
        
        //test
        DiagnoseCalculator diagnoseCalculator = new DiagnoseCalculator(conflictSet);
        //diagnoseSet = diagnoseCalculator.getDiagnoses();
        
        //prepare set for conflict pool
        Set<Constraint> constraintPool = new HashSet<Constraint>();
        for (Set<Constraint> input : conflictSet)
        	constraintPool.addAll(input);
        
        //create conflictList
        ListView conflictList = (ListView) layout.findViewById(R.id.listview_minimalconflicts);
        conflictListAdapter = new ConstraintListAdapter(getActivity(), conflictSet, true);
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
    	Set<Constraint> diagnose = new HashSet<Constraint>(diagnoseAdapter.getConstraints());
    	if (!diagnoseSet.contains(diagnose)) {
    		diagnoseSet.add(diagnose);
    		diagnoseListAdapter.addDiagnose(diagnoseAdapter.getConstraints());
    	}
    	diagnoseAdapter.clear();
    	notifyAdapters();
    }
    
    private void notifyAdapters() {
    	conflictListAdapter.notifyDataSetChanged();
    	diagnoseListAdapter.notifyDataSetChanged();
    	diagnoseAdapter.notifyDataSetChanged();
    	constraintAdapter.notifyDataSetChanged();
    }

}
