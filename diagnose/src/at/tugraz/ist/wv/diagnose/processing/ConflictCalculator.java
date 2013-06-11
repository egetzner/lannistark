package at.tugraz.ist.wv.diagnose.processing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.abstraction.Constraint;

public class ConflictCalculator {
	
	public ConflictCalculator() {
	
	}
	
	
	public static List<Set<Constraint>> calculateConflicts(int numColours, 
			int numConstraints, int minCardinality, int maxCardinality) {

		System.out.println(numColours + ", " + numConstraints + ", " + minCardinality + " - " + maxCardinality);
		
		List<Constraint> list = new LinkedList<Constraint>(Constraint.getAllConstraints());
		Collections.shuffle(list);
		List<Constraint> solutionColours = list.subList(0, numColours);

		//System.out.println("sol: " + solutionColours);
		
		//initialize empty diagnoses
		List<Set<Constraint>> conflicts = new LinkedList<Set<Constraint>>();

		for (int i = 0; i < numConstraints; i++)
		{
			Set<Constraint> constraint = new HashSet<Constraint>();
			constraint.add(solutionColours.get(i%solutionColours.size()));
			conflicts.add(constraint);
		}
				
		//System.out.println(conflicts);

		//now, there must be at least one that contains ONLY the colour of the solutions. 
		int colourIndex = 0;
		while (minCardinality > conflicts.get(0).size())
		{
			conflicts.get(0).add(solutionColours.get(colourIndex++%solutionColours.size()));
		}
		
		//System.out.println(conflicts+"\n--------------");

		List<Constraint> restColour = list.subList(numColours+1, list.size());
		
		//System.out.println("rest: " + restColour);
		
		Iterator<Constraint> it = restColour.iterator();
	
		List<Set<Constraint>> other = conflicts.subList(1, conflicts.size());
		
		if (!other.isEmpty())
		{
			//as long as we have colours to give out: fill out min cardinality constraints first
			for (int count = 0; count < other.size(); count++)
			{
				while (it.hasNext() && other.get(count).size() < minCardinality)
				{
					other.get(count).add(it.next());
				}
			}
			
			//System.out.println("other1: " + other);
			int count = 0;
			while(it.hasNext())
			{
				//now, use up the rest of the colours
				Set<Constraint> conf = other.get(count++%other.size());
				if (conf.size() < maxCardinality)
					conf.add(it.next());
				else
					break;
			}

			//System.out.println("other2: " + other);

		}
		
		System.out.println(conflicts+"\n############");

		Collections.shuffle(conflicts);
		return conflicts;
	}
	
}
