package de.dastuhl.hours.navigation;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.dastuhl.hours.R;

/**
 * Created by Martin on 22.10.2015.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<String> {

    private int selectedItem;

    public NavigationDrawerAdapter(Context context, int resource, int textViewResourceId,
                                   String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(getItem(position));

        if (position == selectedItem) {
            view.setTextColor(getContext().getResources().getColor(R.color.hours_grey));
            view.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            view.setTextColor(getContext().getResources().getColor(android.R.color.white));
            view.setTypeface(Typeface.DEFAULT);
        }

        return view;
    }



    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}
