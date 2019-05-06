package us.egeler.matt.bcl;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class menuAdapter extends  RecyclerView.Adapter<menuAdapter.MyViewHolder> {
    ArrayList<String[]> launchers = new ArrayList<String[]>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout layout;
        public MyViewHolder(ConstraintLayout l) {
            super(l);
            layout = l;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public menuAdapter(ArrayList<String[]> myDataset) {
        launchers = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public menuAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ConstraintLayout l = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        TextView v = l.findViewById(R.id.textView1);
        v.getPaint().setAntiAlias(false);

        TextView v2 = l.findViewById(R.id.textView2);
        v2.getPaint().setAntiAlias(false);

        MyViewHolder vh = new MyViewHolder(l);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ConstraintLayout l = holder.layout;

        ((TextView) l.getChildAt(0)).setText(launchers.get(position)[1]);
        ((TextView) l.getChildAt(1)).setText(launchers.get(position)[0]);

        if(launchers.get(position)[3].equals("1")) {
            l.setBackgroundColor(0xFF000000);
            ((TextView) l.getChildAt(0)).setTextColor(0xFFFFFFFF);
            ((TextView) l.getChildAt(1)).setTextColor(0xFFFFFFFF);
        } else {
            l.setBackgroundColor(0xFFFFFFFF);
            ((TextView) l.getChildAt(0)).setTextColor(0xFF000000);
            ((TextView) l.getChildAt(1)).setTextColor(0xFF000000);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return launchers.size();
    }
}
