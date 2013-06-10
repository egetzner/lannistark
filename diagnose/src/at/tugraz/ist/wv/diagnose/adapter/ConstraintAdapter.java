package at.tugraz.ist.wv.diagnose.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import at.tugraz.ist.wv.diagnose.abstraction.Constraint;

public class ConstraintAdapter extends BaseAdapter{

	private Context context;
	private List<Constraint> constraints;
	private boolean deactivatableConstraints;
	
	public ConstraintAdapter(Context context, Set<Constraint> constraints, boolean deactivatableConstraints) {
		this.context = context;
		this.constraints = new ArrayList<Constraint>(constraints);
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
		ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            //imageView.setLayoutParams(new GridView.LayoutParams(64, 64));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if (deactivatableConstraints)
        	imageView.setImageResource(constraints.get(position).getDrawable());
        else
        	imageView.setImageResource(constraints.get(position).getActiveDrawable());
        return imageView;
	}

	public void toggleConstraint(Constraint c) {
		if (constraints.contains(c)) {
			constraints.remove(c);
			c.activate();
		} else {
			constraints.add(c);
			c.deactivate();
		}
	}
	
	public void clear() {
		for (Constraint c : constraints) {
			c.activate();
		}
		constraints.clear();
	}
	
	public Collection<Constraint> getConstraints() {
		return this.constraints;
	}
}
