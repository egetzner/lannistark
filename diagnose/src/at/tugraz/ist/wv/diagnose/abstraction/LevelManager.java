package at.tugraz.ist.wv.diagnose.abstraction;

import java.util.List;
import java.util.Set;

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
	
	private static LevelManager instance;

	public static LevelManager getInstance() {
		if (instance == null)
			instance = new LevelManager();
		
		return instance;
	}
	
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

	
	private LevelManager() {
		resetLevels();
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
	
	private Action increaseCols()
	{
		System.out.println("incCol");

		numColoursInDiags++;
		numConstraints = (numColoursInDiags<3)?3:numColoursInDiags;
		maxCardinality = 2;
		minCardinality = 1;
		return Action.incCard;
	}
	
	private Action increaseCount()
	{
		System.out.println("incCount");

		float max_num = (numColoursInDiags*2)-(numColoursInDiags/2);
		//increases the number of conflicts, if we aren't already over the limit.
		
		if (numColoursInDiags == 2) max_num += 1;
		
		if (numConstraints < max_num)
		{
			numConstraints++;
			return Action.incCard;
		}
		else
		{
			return increaseCardinality();
		}
	}
	
	private Action increaseCardinality()
	{
		System.out.println("incCard");
		float max_num = (numColoursInDiags*2)-(numColoursInDiags/2);

		if (maxCardinality < max_num)
		{
			maxCardinality++;
		}
		else if (minCardinality < maxCardinality && minCardinality < numColoursInDiags)
			minCardinality++;
		else
			return Action.incCol;
			
		return Action.incCount;
	}
	
	private Action getNext() {
		switch (action)
		{
		case start:
			return Action.incCol;
		case incCol:
			return increaseCols();
		case incCount:
			return increaseCount();
		case incCard:
			return increaseCardinality();
			
		}
		return Action.start;
	}	
	
	public GameLevel getNewLevel()
	{
		Action old = action;
		
		action = getNext();
		
		System.out.println(old +" --> " + action);
		
		List<Set<Constraint>> conflicts = ConflictCalculator.calculateConflicts(
					numColoursInDiags, numConstraints, minCardinality, maxCardinality);
		
		return new GameLevel(levelCounter++, conflicts);
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

}
