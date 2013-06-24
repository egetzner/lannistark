package at.tugraz.ist.wv.diagnose.processing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.abstraction.Constraint;

public class ConflictCalculator {
	
	public ConflictCalculator() {
	
	}
	
	public static List<Set<Constraint>> calculateConflictsNew(int numColours, 
			int numConstraints, int minCardinality, int maxCardinality) {
		if (numColours > Constraint.getAllConstraints().size())
			return null;
		
		System.out.println(numColours + ", " + numConstraints + ", " + minCardinality + " - " + maxCardinality);
		
		List<Constraint> list = new LinkedList<Constraint>(Constraint.getAllConstraints());
		Collections.shuffle(list);
		List<Constraint> solutionColours = list.subList(0, numColours);

		Set<Set<Constraint>> conflicts = new HashSet<Set<Constraint>>();

		for (int i = 0; i < numConstraints; i++)
		{
			Set<Constraint> constraint = new HashSet<Constraint>();
			constraint.add(solutionColours.get(i%solutionColours.size()));
			conflicts.add(constraint);
		}
		
		List<Constraint> solCols = new LinkedList<Constraint>(solutionColours);

		int maxAdditionalColors = maxCardinality - minCardinality;
		
		Random r = new Random();

		Set<Constraint> singles = new HashSet<Constraint>();
		
		for (Set<Constraint> conflSet : conflicts)
		{
			Collections.shuffle(solCols);

			int number = minCardinality + r.nextInt(maxAdditionalColors+1);				
			
			int count = 0;
			while (count < solCols.size() && conflSet.size() < number)
			{
				Constraint con = solutionColours.get(count++);
				conflSet.add(con);
				
				if (number == 1)
					singles.add(con);
			}
		}
		

		//the colors that don't appear in the diagnoses
		List<Constraint> restColour = list.subList(numColours, list.size());	
		Iterator<Constraint> it = restColour.iterator();
		
		Set<Set<Constraint>> setOfConflicts = new HashSet<Set<Constraint>>();

		
		
		for (Set<Constraint> conflSet : conflicts)
		{
			System.err.println("conflict: " + conflSet);
			if (conflSet.size() > 1)
			{
				//we retain all elements that occur as singles
				Set<Constraint> copy = new HashSet<Constraint>(conflSet);
				copy.retainAll(singles);
								
				//we contain elements that also occur as singles
				if (!copy.isEmpty() && it.hasNext() && conflSet.size() < maxCardinality)
				{
					conflSet.add(it.next());
				}
			}
			List<Constraint> shuffelable = new LinkedList<Constraint>(conflSet);
			Collections.shuffle(shuffelable);

			if (conflSet.size() < minCardinality && it.hasNext())
				conflSet.add(it.next());

			setOfConflicts.add(new HashSet<Constraint>(shuffelable));
		}
				
		
		List<Set<Constraint>> confl = new LinkedList<Set<Constraint>>(setOfConflicts);

		int count = 0;
		while (confl.size() < numConstraints && it.hasNext())
		{
			List<Constraint> newset = new LinkedList<Constraint>(confl.get(count++%confl.size()));
			
			Collections.shuffle(newset);
			newset.remove(0);
			newset.add(it.next());
			confl.add(new HashSet<Constraint>(newset));
		}
				
		Collections.shuffle(confl);
		
		System.out.println(conflicts+"\n############");

		return confl;
	}
	
	
	public static List<Set<Constraint>> calculateConflicts(int numColours, 
			int numConstraints, int minCardinality, int maxCardinality) {

		
		if (numColours > Constraint.getAllConstraints().size())
			return null;
		
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

		//now, there must be at least one that contains ONLY the colors of the solutions. 
		int colourIndex = 0;
		while (minCardinality > conflicts.get(0).size())
		{
			conflicts.get(0).add(solutionColours.get(colourIndex++%solutionColours.size()));
		}
		
		
		//from here on out, *ALL* colors can be distributed between the constraints.
		//so that the minimal is met and for some, the maximum is met
		
		
		
		
		//System.out.println(conflicts+"\n--------------");

		List<Constraint> restColour = list.subList(numColours, list.size());
		
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
