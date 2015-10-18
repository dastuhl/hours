package de.dastuhl.hours;

import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerViewAdapter;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.common.collect.Lists;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dastuhl.hours.data.model.SessionsSummary;

/**
 * Created by Martin on 24.09.2015.
 */
public class SessionsViewAdapter extends FirebaseRecyclerViewAdapter<SessionsSummary, SessionsViewAdapter.SessionListViewHolder> {


    public SessionsViewAdapter(Class<SessionsSummary> modelClass, int modelLayout, Class<SessionListViewHolder> viewHolderClass, Firebase ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    public void populateViewHolder(SessionListViewHolder pSessionListViewHolder, SessionsSummary pSessionsSummary) {

        pSessionListViewHolder.date.setText(pSessionsSummary.createTimerangeString());

        List<BarDataSet> dataSets = Lists.newArrayList();
        BarEntry entrySwimming = new BarEntry(pSessionsSummary.getSwimDuration().floatValue(), 0);
        BarEntry entryCycling = new BarEntry(pSessionsSummary.getCycleDuration().floatValue(), 1);
        BarEntry entryRunning = new BarEntry(pSessionsSummary.getRunDuration().floatValue(), 2);
        BarEntry entryAthletic = new BarEntry(pSessionsSummary.getAthleticDuration().floatValue(), 3);
        BarDataSet set = new BarDataSet(Lists.newArrayList(entrySwimming, entryCycling, entryRunning, entryAthletic), "gggg");
        dataSets.add(set);

        List<String> xVals = Lists.newArrayList("S", "C", "R", "A");

        HorizontalBarChart chart = pSessionListViewHolder.chart;
        chart.getXAxis().setEnabled(false);

        YAxis yTop = pSessionListViewHolder.chart.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yBottom = pSessionListViewHolder.chart.getAxis(YAxis.AxisDependency.RIGHT);
        yBottom.setEnabled(false);
        yTop.setEnabled(false);
        yBottom.setAxisMaxValue(480f);
        yTop.setAxisMaxValue(480f);
        chart.getLegend().setEnabled(false);
        chart.setContentDescription("");
        chart.setDescription("");

        BarData data = new BarData(xVals, dataSets);
        data.setDrawValues(false);

        chart.setData(data);
        chart.invalidate();
    }

    static class SessionListViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.list_item_date_textview)
        TextView date;
        @Bind(R.id.list_item_chart_view)
        HorizontalBarChart chart;

        public SessionListViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            final int adapterPosition = getAdapterPosition();
            final long itemId = getItemId();

        }
    }
}
