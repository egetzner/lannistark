package at.tugraz.ist.wv.diagnose.abstraction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.tugraz.ist.wv.diagnose.processing.DiagnoseCalculator;

public class GameLevel {

	private List<Set<Constraint>> conflicts;
	private DiagnoseCalculator diagnosisCalculator;
	private int levelNum;
	
	public GameLevel(int num, List<Set<Constraint>> conflicts) {
		this.levelNum = num;
		this.conflicts = conflicts;
		
		HashSet<Set<Constraint>> conflictSet = new HashSet<Set<Constraint>>(conflicts);
		
		this.diagnosisCalculator = new DiagnoseCalculator(conflictSet);
	
		}

	public List<Set<Constraint>> getConflicts() {
		return conflicts;
	}

	public void setConflicts(List<Set<Constraint>> conflicts) {
		this.conflicts = conflicts;
	}

	public DiagnoseCalculator getDiagnosisCalculator() {
		return diagnosisCalculator;
	}

	public void setDiagnosisCalculator(DiagnoseCalculator diagnosisCalculator) {
		this.diagnosisCalculator = diagnosisCalculator;
	}

	public int getLevelNum() {
		return levelNum;
	}
	
	
}
