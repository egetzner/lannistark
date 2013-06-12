package at.tugraz.ist.wv.diagnose.processing;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.abstraction.Constraint;

public class DiagnoseCalculator {

	private Set<Set<Constraint>> conflicts;
	private Set<Set<Constraint>> diagnoses;
	
	public DiagnoseCalculator(Set<Set<Constraint>>conflicts) {
		this.conflicts = conflicts;
		calculateDiagnosesSorted();
	}
	
	private void calculateDiagnosesSorted()
	{
		diagnoses = new HashSet<Set<Constraint>>();
		
		HashMap<Integer,Set<Set<Constraint>>> map = new HashMap<Integer,Set<Set<Constraint>>>();
		
		for (Set<Constraint> confl : conflicts)
		{
			Set<Set<Constraint>> conflictsWithThatSize = map.get(confl.size());
			if (conflictsWithThatSize == null)
				conflictsWithThatSize = new HashSet<Set<Constraint>>();
			
			conflictsWithThatSize.add(confl);
			map.put(confl.size(), conflictsWithThatSize);
		}
		
		List<Integer> cardinality = new LinkedList<Integer>(map.keySet());
		Collections.sort(cardinality);
	
		
		List<Set<Constraint>> sortedList = new LinkedList<Set<Constraint>>();
		
		for (int card : cardinality)
		{
			sortedList.addAll(map.get(card));
		}
	

		for (Set<Constraint> conflictSet : sortedList) {

			if(diagnoses.isEmpty())
			{
				//enter the first conflict set (must be smallest!!) as a diagnosis each
				for (Constraint con : conflictSet)
				{
					HashSet<Constraint> conflicts = new HashSet<Constraint>();
					conflicts.add(con);
					diagnoses.add(conflicts);
				}
			}
			else
			{
				//HashSet<Set<Constraint>> diagnosesCopy = new HashSet<Set<Constraint>>(diagnoses);
				
				HashSet<Set<Constraint>> newDiags = new HashSet<Set<Constraint>>();

				//see if already contained in current diagnosis
				for (Set<Constraint> diag : diagnoses)
				{
					Set<Constraint> copy = new HashSet<Constraint>(conflictSet);
					
					//keeps all elements also occuring in diagnosis
					copy.retainAll(diag);
					
					if (copy.isEmpty())
					{

						//contains no elements already occuring in the diagnosis 
						// therefore, we add each element of the conflict it to the diagnosis
						for (Constraint con : conflictSet)
						{
							Set<Constraint> originalDiagnosis = new HashSet<Constraint>(diag);
							originalDiagnosis.add(con);
							
							boolean contained = false;
							//check if new diagnosis isn't contained by any other diags
							for (Set<Constraint> otherDiag : diagnoses)
							{
								if(!otherDiag.equals(diag))
								{
									Set<Constraint> otherDiagCopy = new HashSet<Constraint>(otherDiag);

									otherDiagCopy.removeAll(originalDiagnosis);
									
									if (otherDiagCopy.isEmpty())
									{
										//do not add it to the diagnoses!
										contained = true;
										break;
									}
								}
							}
							
							if (!contained)
								newDiags.add(originalDiagnosis);
						}
					}
					else
					{
						newDiags.add(diag);
					}
				}
				
				diagnoses = newDiags;
				
			}
		}
	}
	
	private void calculateDiagnoses() {
		
		//initialize empty diagnoses
		diagnoses = new HashSet<Set<Constraint>>();
		

		for (Set<Constraint> conflictSet : conflicts) {
						
			if (diagnoses.isEmpty()) {
				//first conflictSet
				//->add each constraint as diagnose
				for(Constraint constraint : conflictSet) {
					Set<Constraint> diagnose = new HashSet<Constraint>();
					diagnose.add(constraint);
					diagnoses.add(diagnose);
				}
				
			} else {
				//each further conflictSet
				//build up new diagnose based on current diagnoses
				Set<Set<Constraint>> newDiagnoses = new HashSet<Set<Constraint>>();
				for(Set<Constraint> currentDiagnose : diagnoses) {
					//for each current diagnose
					Set<Constraint> tempSet = new HashSet<Constraint>(currentDiagnose);
					tempSet.retainAll(conflictSet);
					if(! tempSet.isEmpty()) {
						//if the current diagnose already contains one of the constraints of this set
						//then add currentDiagnose to new diagnoses
						newDiagnoses.add(new HashSet<Constraint>(currentDiagnose));
					} else {
						//for each constraint in the current conflictSet
						for(Constraint constraint : conflictSet) {
							boolean exists = false;
							for (Set<Constraint> nestedDiagnose : diagnoses) {
								exists = exists || nestedDiagnose.contains(constraint);
							}
							//if current constraint does not yet exist in an existing diagnose
							if (!exists) {
								// add new constraint to currentDiagnose
								Set<Constraint> diagnose = new HashSet<Constraint>(currentDiagnose);
								diagnose.add(constraint);
								newDiagnoses.add(diagnose);
							} 
						}
					}
				}
				//set diagnose to newDiagnoses
				diagnoses = newDiagnoses;

			}
		}
	}
	
	public Set<Set<Constraint>> getDiagnoses() {
		return diagnoses;
	}

	public boolean isDiagnose(Set<Constraint> diagnose) {
				
		return diagnoses.contains(diagnose);
	}

	public int getNumberOfDiagnoses() {
		
		return diagnoses.size();
	}
}
