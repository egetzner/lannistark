package at.tugraz.ist.wv.diagnose.abstraction;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.fragment.GameFragment;
import at.tugraz.ist.wv.diagnose.processing.DiagnoseCalculator;

public class GameLevel {

	//static data
	private int levelNum;
	private int numTriesBest;
	private List<Set<Constraint>> conflicts;
	private Set<Set<Constraint>> targetDiagnoses;
	private Set<Constraint> availableConstraints;
	
	//gamestate
	private int numTries;
	private ConstraintSuperSet currentDiagnoses;
	
	public GameLevel(int num, List<Set<Constraint>> conflicts) {
		//acquire input data
		this.levelNum = num;
		this.conflicts = conflicts;
		
		//calculate target
		DiagnoseCalculator diagnosisCalculator = new DiagnoseCalculator(new HashSet<Set<Constraint>>(conflicts));
		targetDiagnoses = diagnosisCalculator.getDiagnoses();

		//calculate constraints
		availableConstraints = new HashSet<Constraint>();
		for(Set<Constraint> temp : conflicts)
			availableConstraints.addAll(temp);
		
		//initialize remaining static data
		numTriesBest = 0;
		
		//initialize gamestate
		numTries = 0;
		currentDiagnoses = new ConstraintSuperSet();
	}

	public GameLevel(int num, String constraints, String diagnoses, int highscore, int tries) {

		this.levelNum = num;
		this.conflicts = new LinkedList<Set<Constraint>>(
						new ConstraintSuperSet(constraints));
		numTriesBest = highscore;
		
		//calculate target
		DiagnoseCalculator diagnosisCalculator = new DiagnoseCalculator(new HashSet<Set<Constraint>>(conflicts));
		targetDiagnoses = diagnosisCalculator.getDiagnoses();

		//calculate constraints
		availableConstraints = new HashSet<Constraint>();
		for(Set<Constraint> temp : conflicts)
			availableConstraints.addAll(temp);

		//initialize gamestate
		numTries = 0;
		currentDiagnoses = new ConstraintSuperSet(diagnoses);

	}

	/*
	 * GETTERS FOR STATIC DATA
	 */
	public int getLevelNum() {
		return levelNum;
	}
	
	public int getNumTriesBest() {
		return numTriesBest;
	}
	
	public Set<Set<Constraint>> getConflicts() {
		return new HashSet<Set<Constraint>>(conflicts);
	}
	
	public Set<Set<Constraint>> getTargetDiagnoses() {
		return targetDiagnoses;
	}
	
	public Set<Constraint> getAvailableConstraints() {
		return availableConstraints;
	}
	
	/*
	 * GETTERS FOR GAMESTATE
	 */
	public Set<Set<Constraint>> getCurrentDiagnoses() {
		return new HashSet<Set<Constraint>>(currentDiagnoses);
	}
	
	public int getNumTries() {
		return numTries;
	}

	/*
	 * GAME INTERFACE
	 */
	public int addDiagnose(Set<Constraint> diagnose) {
		numTries++;
		
		//handle errors
		if (currentDiagnoses.contains(diagnose))
			return GameFragment.ERROR_DUPLICATE_DIAGNOSE;
		if (!targetDiagnoses.contains(diagnose))
			return GameFragment.ERROR_INVALID_DIAGNOSE;
		
		//no error
		currentDiagnoses.add(diagnose);
		return 0;
	}

	public boolean isComplete() {
		return (currentDiagnoses.size() == targetDiagnoses.size());
	}

	public void reset() {
		
		for (Constraint con : availableConstraints)
		{
			con.activate();
		}
		
		//initialize gamestate
		numTries = 0;
		currentDiagnoses = new ConstraintSuperSet();		
	}
}
