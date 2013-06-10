package at.tugraz.ist.wv.diagnose.processing;

import java.util.HashSet;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.abstraction.Constraint;

public class DiagnoseCalculator {

	private Set<Set<Constraint>> conflicts;
	private Set<Set<Constraint>> diagnoses;
	
	public DiagnoseCalculator(Set<Set<Constraint>>conflicts) {
		this.conflicts = conflicts;
		calculateDiagnoses();
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
							// add new constraint to currentDiagnose
							Set<Constraint> diagnose = new HashSet<Constraint>(currentDiagnose);
							diagnose.add(constraint);
							newDiagnoses.add(diagnose);
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
}
