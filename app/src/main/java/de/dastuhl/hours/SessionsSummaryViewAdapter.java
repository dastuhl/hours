package de.dastuhl.hours;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
import de.dastuhl.hours.chart.TimeFormatter;
import de.dastuhl.hours.data.model.MonthlySessionsSummary;
import de.dastuhl.hours.data.model.SessionsSummary;
import de.dastuhl.hours.data.model.WeeklySessionsSummary;
import de.dastuhl.hours.data.model.YearlySessionsSummary;

/**
 * Created by Martin on 24.09.2015.
 */
public class SessionsSummaryViewAdapter extends FirebaseRecyclerViewAdapter<SessionsSummary, SessionsSummaryViewAdapter.SessionListViewHolder> {

    Context context;
    SummarySelectionListener listener;

    public SessionsSummaryViewAdapter(Class<SessionsSummary> modelClass, int modelLayout,
                                      Class<SessionListViewHolder> viewHolderClass,
                                      Firebase ref, Context context, SummarySelectionListener listener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public SessionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SessionListViewHolder vh = super.onCreateViewHolder(parent, viewType);
        vh.setListener(new SessionListViewHolder.SessionSummaryClick() {
            @Override
            public void sessionSummaryClicked(View caller, int adapterPosition) {
                Firebase ref = getRef(adapterPosition);
                listener.summarySelected(ref.toString());
            }
        });
        return vh;
    }

    @Override
    public void populateViewHolder(SessionListViewHolder pSessionListViewHolder, SessionsSummary pSessionsSummary) {

        pSessionListViewHolder.date.setText(pSessionsSummary.createPeriodString());
        pSessionListViewHolder.total.setText(Utility.getPeriodString(pSessionsSummary.computeTotals()));

        if (Utility.getPreferredBarChartStyle(context)) {
            createStackedChart(pSessionListViewHolder, pSessionsSummary);
        } else {
            createTotalChart(pSessionListViewHolder, pSessionsSummary);
        }
    }

    private void createTotalChart(SessionListViewHolder pSessionListViewHolder, SessionsSummary pSessionsSummary) {
        float maxValueYAxis = getMaxValueYAxis(pSessionsSummary);
        Integer total = pSessionsSummary.computeTotals();

        List<BarDataSet> dataSets = Lists.newArrayList();
        BarEntry totalEntry = new BarEntry(total, 0);
        BarDataSet set = new BarDataSet(Lists.newArrayList(totalEntry), "");
        set.setColors(Lists.newArrayList(context.getResources().getColor(R.color.hours_light_grey)));
        dataSets.add(set);

        List<String> xVals = Lists.newArrayList("T");

        configureChart(pSessionListViewHolder, maxValueYAxis, dataSets, xVals);
    }

    private float getMaxValueYAxis(SessionsSummary pSessionsSummary) {
        float maxValueYAxis = 480; // 8h
        if (pSessionsSummary instanceof WeeklySessionsSummary) {
            maxValueYAxis = 1800; // 30h
        } else if (pSessionsSummary instanceof MonthlySessionsSummary) {
            maxValueYAxis = 4800; // 80h
        } else if (pSessionsSummary instanceof YearlySessionsSummary) {
            maxValueYAxis = 60000; // 1000
        }
        return maxValueYAxis;
    }


    private void createStackedChart(SessionListViewHolder pSessionListViewHolder, SessionsSummary pSessionsSummary) {
        float maxValueYAxis = getMaxValueYAxis(pSessionsSummary);

        List<BarDataSet> dataSets = Lists.newArrayList();

        float[] durations = new float[] {
                pSessionsSummary.getSwimDuration().floatValue(),
                pSessionsSummary.getCycleDuration().floatValue(),
                pSessionsSummary.getRunDuration().floatValue(),
                pSessionsSummary.getAthleticDuration().floatValue()};

        BarEntry entryStackedTotal = new BarEntry(durations, 0);
        BarDataSet set = new BarDataSet(Lists.newArrayList(entryStackedTotal), "");
        set.setColors(Utility.getSportsColors(context));
        dataSets.add(set);

        List<String> xVals = Lists.newArrayList("");

        configureChart(pSessionListViewHolder, maxValueYAxis, dataSets, xVals);
    }

    private void configureChart(SessionListViewHolder pSessionListViewHolder, float maxValueYAxis, List<BarDataSet> dataSets, List<String> xVals) {
        HorizontalBarChart chart = pSessionListViewHolder.chart;
        chart.getXAxis().setEnabled(false);

        YAxis yTop = pSessionListViewHolder.chart.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yBottom = pSessionListViewHolder.chart.getAxis(YAxis.AxisDependency.RIGHT);
        yBottom.setEnabled(false);
        yTop.setEnabled(false);
        yBottom.setAxisMaxValue(maxValueYAxis);
        yTop.setAxisMaxValue(maxValueYAxis);
        chart.getLegend().setEnabled(false);
        chart.setContentDescription("");
        chart.setDescription("");
        chart.setDrawValueAboveBar(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setClickable(false);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(12f);
        data.setDrawValues(false);
        data.setValueFormatter(new TimeFormatter());

        chart.setData(data);
        chart.invalidate();
    }

    static class SessionListViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements View.OnClickListener{

        SessionSummaryClick listener;

        @Bind(R.id.list_item_date_textview)
        TextView date;
        @Bind(R.id.list_item_total_textview)
        TextView total;
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
            listener.sessionSummaryClicked(v, adapterPosition);
        }

        public void setListener(SessionSummaryClick listener) {
            this.listener = listener;
        }

        public interface SessionSummaryClick {
            void sessionSummaryClicked(View caller, int adapterPosition);
        }
    }

    public interface SummarySelectionListener {
        void summarySelected(String ref);
    }
}
