package at.tugraz.ist.wv.diagnose.abstraction;

import java.util.HashSet;
import java.util.Set;

public class ConstraintSet extends HashSet<Constraint> {

	public ConstraintSet(String stringSet) {

		super();
		if (stringSet.startsWith("[") && stringSet.endsWith("]"))
		{
			String cut = stringSet.substring(1, stringSet.length()-1);
			String[] split = cut.split(",");
			
			for (String constraint : split)
			{				
				Constraint obj = Constraint.createConstraint(constraint.trim());
				if (obj != null)
					this.add(obj);
			}
			
		}	
	}	
	
	public ConstraintSet() 
	{
		super();
	}

	public ConstraintSet(Set<Constraint> diagnose) {
		super(diagnose);
	}
}
