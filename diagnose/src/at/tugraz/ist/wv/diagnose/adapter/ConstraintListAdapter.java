package at.tugraz.ist.wv.diagnose.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import at.tugraz.ist.wv.diagnose.R;
import at.tugraz.ist.wv.diagnose.abstraction.Constraint;

public class ConstraintListAdapter extends BaseAdapter{

	private Context context;
	private List<List<Constraint>> constraints;
	private boolean deactivatableConstraints;
	
	public ConstraintListAdapter(Context context, Collection<Set<Constraint>> constraints, boolean deactivatableConstraints) {
		this.context = context;
		this.constraints = new ArrayList<List<Constraint>>();
		for (Set<Constraint> s : constraints) {
			ArrayList<Constraint> list = new ArrayList<Constraint>(s);
			this.constraints.add(list);
		}
		this.deactivatableConstraints = deactivatableConstraints;
	}
	
	@Override
	public int getCount() {
		return constraints.size();
	}

	@Override
	public Object getItem(int position) {
		return constraints.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.rowlayout_constraintlist, parent, false);
		}
		LinearLayout row = (LinearLayout) rowView.findViewById(R.id.linearlayout_constraints);
		row.removeAllViews();
		for (Constraint c : constraints.get(position)) {
			ImageView imageView = new ImageView(context);
			if (constraints.get(position).size() <= 4) {
				//utilize space for bigger representation
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(32, 32);
				lp.setMargins(3, 5, 3, 5);
				imageView.setLayoutParams(lp);
			} else if (constraints.get(position).size() <= 6) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(24, 24);
				imageView.setLayoutParams(lp);				
			} else {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(18, 18);
				imageView.setLayoutParams(lp);	
			}
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			if (deactivatableConstraints)
				imageView.setImageResource(c.getDrawable());
			else
				imageView.setImageResource(c.getActiveDrawable());
			row.addView(imageView);
		}
		return rowView;
	}	
	
	public void addDiagnose(Collection<Constraint> diagnose) {
		List<Constraint> diag = new ArrayList<Constraint>(diagnose);
		this.constraints.add(diag);
	}
}
