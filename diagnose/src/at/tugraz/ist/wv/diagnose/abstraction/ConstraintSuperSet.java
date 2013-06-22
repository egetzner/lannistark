package at.tugraz.ist.wv.diagnose.abstraction;

import java.util.HashSet;
import java.util.Set;

public class ConstraintSuperSet extends HashSet<ConstraintSet> {

	public ConstraintSuperSet(String stringSet) {
		
		super();

		if (stringSet.startsWith("[") && stringSet.endsWith("]"))
		{
			String cut = stringSet.substring(1, stringSet.length()-1);
			
			String[] split = cut.split("], ");
			
			for (String constraint : split)
			{
				
				String  conSet = constraint;
				if (!constraint.startsWith("["))
					conSet = "["+constraint;
				if (!constraint.endsWith("]"))
					conSet = constraint + "]";
				
				ConstraintSet set = new ConstraintSet(conSet);
				
				if (!set.isEmpty())
					this.add(set);
			}
			
		}		
	}	
	
	public ConstraintSuperSet() 
	{
		super();
	}

	public void add(Set<Constraint> diagnose) {

		this.add(new ConstraintSet(diagnose));
	}
}
