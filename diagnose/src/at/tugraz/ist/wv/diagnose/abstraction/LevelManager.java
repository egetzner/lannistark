package at.tugraz.ist.wv.diagnose.abstraction;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.db.DBProxy;
import at.tugraz.ist.wv.diagnose.processing.ConflictCalculator;

public class LevelManager {

	private enum Action {
		start(0), incCol(1), incCount(2), incCard(3);
		
		private int index;

		private Action(int index) {
			this.index = index;
		}
	};
	
	private static final int MAX_CONFLICTS = 8;
	
	public void resetLevels() {
		levelCounter = 1;
		numCorrectDiags = 0;
		numTries = 0;
		numPossibleDiags = 0;	
		
		numColoursInDiags = 1;
		numConstraints = 2;
		maxCardinality = 2;
		minCardinality = 1;	
		
		action = Action.start;
	}

	public LevelManager(int maxLevels, DBProxy proxy) {
		
		//creates new levels, check if we already have some!!
		resetLevels();
		createLevels(proxy);
	}
	
	//constructor for timed games -> we do not want to overwrite the already created levels
	public LevelManager() {
		//creates new levels, check if we already have some!!
		resetLevels();
	}
	
	private void createLevels(DBProxy proxy) {
		HashMap<Integer, GameLevel> levels = new HashMap<Integer, GameLevel>();
				
		int NUM_LEVELS = 20;
		
		int count = 0;

		while (count++ < NUM_LEVELS+5)
		{
			GameLevel lvl = this.getNewLevel();
			
			if (lvl == null)
				continue;
						
			int numDiags = lvl.getTargetDiagnoses().size();
			int complexity = lvl.getComplexity();

/*			System.err.println("num Diags: " + numDiags + ", " + complexity);
			System.err.println(lvl.getConflicts());
			System.err.println(lvl.getTargetDiagnoses());
*/
			int complex = (complexity)*(numDiags);
			
			//the higher the level, the more important it is to have a low number of diagnoses.
			GameLevel level = levels.get(complex);
			
			if (level == null)
				levels.put(complex, lvl);
			else
				count--;
			//else conflict!! create new level!!
			
			
		}

		LinkedList<Integer> complexities = new LinkedList<Integer>(levels.keySet());
		
		Collections.sort(complexities);
		
		levelCounter = 1;
		for (int comp : complexities)
		{
			GameLevel lvl = levels.get(comp);
			lvl.setLevelNum(levelCounter++);
			System.err.println(lvl.getLevelNum() + " " + comp + "(" + lvl.getComplexity() + " * " + lvl.getTargetDiagnoses().size()+") , value: " + lvl.getConflicts());
			proxy.addNewLevel(lvl);
		}

		
	}

	//counters for display
	private int levelCounter;
	private int numCorrectDiags;
	private int numTries;
	private int numPossibleDiags;
	
	//counters for conflict calculator
	private int numColoursInDiags;
	private int numConstraints;
	private int maxCardinality;
	private int minCardinality;
	
	private Action action;
	private int constraintCounter = 0;
	int MAX_CARD_MAX = 5;

	private Action increaseNumColours()
	{
		numColoursInDiags++;
		return Action.incCard;
	}
	
	
	private Action increaseCount()
	{
		System.out.println("INCREASE COUNT CALLED");
		constraintCounter = 0;
		numConstraints++;

		if (numConstraints > 3)
		{
			maxCardinality = 3;
			minCardinality = 2;
		}
		else
		{
			maxCardinality = 2;
			minCardinality = 1;
		}
		numColoursInDiags = numConstraints-2;
		
		return Action.incCard;
	}
	
	private Action increaseCardinality()
	{
		
		if (maxCardinality < MAX_CARD_MAX)
		{
			maxCardinality++;
		}
		else if (minCardinality < maxCardinality)
		{
			minCardinality++;
		}
		else
			return Action.incCount;
		
		if (constraintCounter > numConstraints)
		{
			return Action.incCount;
		}
		//if we simply increased the Cardinality, we should increase the colour count. 
		return increaseNumColours();
			
		}


	
	private Action performAction() {

		constraintCounter++;
		
		switch (action)
		{
		case start:
			return Action.incCol;
		case incCard:
			return increaseCardinality();
		case incCol:
			return increaseNumColours();
		case incCount:
			return increaseCount();
		}
		return Action.start;
	}
	
	
	public GameLevel getNewLevel()
	{
		action = performAction();
		List<Set<Constraint>> conflicts = ConflictCalculator.calculateConflictsNew(
				numColoursInDiags, numConstraints, minCardinality, maxCardinality);
		
		if (conflicts == null)
			return null;
		
		return new GameLevel(levelCounter++, conflicts);
	}
	


	public GameLevel getNewLevel(int i) {

		levelCounter = i;
		return getNewLevel();
	}

	public int getLevelCounter() {
		return levelCounter;
	}
	
	public int getNumCorrectDiags() {
		return numCorrectDiags;
	}

	public void addToNumCorrectDiags(int numCorrectDiags) {
		this.numCorrectDiags += numCorrectDiags;
	}

	public int getNumTries() {
		return numTries;
	}

	public void addToNumTries(int numTries) {
		this.numTries += numTries;
	}

	public int getNumPossibleDiags() {
		return numPossibleDiags;
	}

	public void addToNumPossibleDiags(int numPossibleDiags) {
		this.numPossibleDiags += numPossibleDiags;
	}

	public void setDifficulty(Difficulty difficulty) {
		switch(difficulty.getIndex()) {
		case 0: //easy
			numColoursInDiags = 1;
			numConstraints = 2;
			maxCardinality = 2;
			minCardinality = 1;	
			break;
		case 1: //medium
			numColoursInDiags = 2;
			numConstraints = 3;
			maxCardinality = 3;
			minCardinality = 2;	
			break;
		case 2: //hard
			numColoursInDiags = 3;
			numConstraints = 4;
			maxCardinality = 4;
			minCardinality = 3;	
			break;
		}
	}



}
