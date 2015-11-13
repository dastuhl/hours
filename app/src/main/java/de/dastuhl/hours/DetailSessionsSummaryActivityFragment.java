package de.dastuhl.hours;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dastuhl.hours.chart.TimeFormatter;
import de.dastuhl.hours.data.HoursFirebaseConnector;
import de.dastuhl.hours.data.model.DailySessionsSummary;
import de.dastuhl.hours.data.model.SessionsSummary;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailSessionsSummaryActivityFragment extends Fragment
        implements HoursFirebaseConnector.SessionsSummaryLoader {

    private static final String TAG_TIME_PICKER_DIALOG = "time_picker";
    private static final String TAG_DATE_PICKER_DIALOG = "date_picker";

    private static final int DATE_DIALOG = 1;
    private static final int TIME_DIALOG_SWIM = 2;
    private static final int TIME_DIALOG_CYCLE = 3;
    private static final int TIME_DIALOG_RUN = 4;
    private static final int TIME_DIALOG_ATHLETIC = 5;

    private enum EditMode {
        SHOW(false),
        UPDATE(true),
        NEW(true);

        private boolean editable;

        EditMode(boolean editable) {
            this.editable = editable;
        }

        boolean isEditable() {
            return editable;
        }

        ;
    }

    static final String ARG_SUMMARY_URL = "argSummary";
    static final String ARG_USER_ID = "argUserId";

    @Bind(R.id.session_date)
    EditText dailySummaryDateText;

    @Bind(R.id.session_duration_swim)
    TextView durationSwimmingText;

    @Bind(R.id.session_duration_cycle)
    TextView durationCyclingText;

    @Bind(R.id.session_duration_run)
    TextView durationRunningText;

    @Bind(R.id.session_duration_athletic)
    TextView durationAthleticText;

    @Bind(R.id.details_pie_chart_view)
    PieChart pieChart;

    @Bind(R.id.details_bar_chart_view)
    BarChart barChart;

    @Bind(R.id.swim_button)
    ImageButton swimButton;
    @Bind(R.id.cycle_button)
    ImageButton cycleButton;
    @Bind(R.id.run_button)
    ImageButton runButton;
    @Bind(R.id.athletic_button)
    ImageButton athleticButton;

    private SessionsSummary summary;
    private EditMode editMode;

    private HoursFirebaseConnector firebaseConnector;

    MenuItem doneButton;

    public DetailSessionsSummaryActivityFragment() {
    }

    public static DetailSessionsSummaryActivityFragment newInstance(String userId, String url) {
        DetailSessionsSummaryActivityFragment fragment = new DetailSessionsSummaryActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUMMARY_URL, url);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_sessions_summary, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);

        editMode = EditMode.SHOW;

        definePieChart();
        defineBarChart();
        // default invisible
        barChart.setVisibility(View.GONE);

        // TODO args set?
        Bundle args = getArguments();
        String url = args.getString(ARG_SUMMARY_URL);
        String userId = args.getString(ARG_USER_ID);
        if (userId != null) {
            firebaseConnector = new HoursFirebaseConnector(userId, getActivity());
            if (url == null || url.isEmpty()) {
                summary = DailySessionsSummary.newInstanceForToday();
                editMode = EditMode.NEW;
                updateUI();
            } else {
                // see <code>onDataLoaded</code>
                firebaseConnector.loadSessionsSummary(url, this);
            }
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_sessions_summary_fragment, menu);

        doneButton = menu.findItem(R.id.menu_action_done);
        configureDoneButton();
    }

    private void configureDoneButton() {
        if (doneButton != null) {
            doneButton.setEnabled(editMode.isEditable());
            doneButton.setVisible(editMode.isEditable());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_action_done:
                firebaseConnector.saveDailySummary((DailySessionsSummary) summary);
                getActivity().finish();
                return true;
            case R.id.menu_chart_type:
                if (pieChart.getVisibility() == View.VISIBLE) {
                    item.setIcon(R.drawable.ic_data_usage_white_48dp);
                    pieChart.setVisibility(View.GONE);
                    barChart.setVisibility(View.VISIBLE);
                } else {
                    item.setIcon(R.drawable.ic_equalizer_white_48dp);
                    pieChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.swim_button)
    void onDurationSwimmingClicked() {
        if (editMode.isEditable()) {
            openDialog(TIME_DIALOG_SWIM);
        }
    }

    @OnClick(R.id.cycle_button)
    void onDurationCyclingClicked() {
        if (editMode.isEditable()) {
            openDialog(TIME_DIALOG_CYCLE);
        }
    }

    @OnClick(R.id.run_button)
    void onDurationRunningClicked() {
        if (editMode.isEditable()) {
            openDialog(TIME_DIALOG_RUN);
        }
    }

    @OnClick(R.id.athletic_button)
    void onDurationAthleticClicked() {
        if (editMode.isEditable()) {
            openDialog(TIME_DIALOG_ATHLETIC);
        }
    }

    @OnClick(R.id.session_date)
    void onSessionDateClicked() {
        if (editMode == EditMode.NEW) {
            openDialog(DATE_DIALOG);
        }
    }

    private void openDialog(int dialogId) {
        int minutes = 0;
        boolean timePicker = false;
        switch (dialogId) {
            case DATE_DIALOG:
                DialogFragment dateFragment = DatePickerFragment.newInstance(
                        ((DailySessionsSummary) summary).buildCalendar());
                dateFragment.setTargetFragment(this, dialogId);
                dateFragment.show(getActivity().getSupportFragmentManager(), TAG_DATE_PICKER_DIALOG);
                break;
            case TIME_DIALOG_ATHLETIC:
                minutes = summary.getAthleticDuration();
                timePicker = true;
                break;
            case TIME_DIALOG_CYCLE:
                minutes = summary.getCycleDuration();
                timePicker = true;
                break;
            case TIME_DIALOG_RUN:
                minutes = summary.getRunDuration();
                timePicker = true;
                break;
            case TIME_DIALOG_SWIM:
                minutes = summary.getSwimDuration();
                timePicker = true;
                break;
        }
        if (timePicker) {
            DialogFragment timeFragment = TimePickerFragment.newInstance(minutes);
            timeFragment.setTargetFragment(this, dialogId);
            timeFragment.show(getActivity().getSupportFragmentManager(), TAG_TIME_PICKER_DIALOG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATE_DIALOG:
                int year = data.getIntExtra("year", -1);
                int month = data.getIntExtra("month", -1);
                int day = data.getIntExtra("day", -1);
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                summary.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
                summary.setWeekOfYear(cal.get(Calendar.WEEK_OF_YEAR));
                summary.setMonth(cal.get(Calendar.MONTH) + 1);
                summary.setYear(cal.get(Calendar.YEAR));
                break;
            case TIME_DIALOG_ATHLETIC:
                summary.setAthleticDuration(data.getIntExtra("minutes", 0));
                break;
            case TIME_DIALOG_CYCLE:
                summary.setCycleDuration(data.getIntExtra("minutes", 0));
                break;
            case TIME_DIALOG_RUN:
                summary.setRunDuration(data.getIntExtra("minutes", 0));
                break;
            case TIME_DIALOG_SWIM:
                summary.setSwimDuration(data.getIntExtra("minutes", 0));
                break;
        }
        updateUI();
    }

    @Override
    public void onDataLoaded(Collection<SessionsSummary> sessionsSummaries) {
        summary = sessionsSummaries.iterator().next();
        if (summary instanceof DailySessionsSummary) {
            editMode = EditMode.UPDATE;
            configureDoneButton();
        }
        updateUI();
    }

    @Override
    public void onLoadingFailed() {
        getActivity().finish();
    }

    private void updateUI() {
        changeTitle();
        String periodString = Utility.getDayName(getActivity(), summary);
        dailySummaryDateText.setText(periodString + " "
                + Utility.createFriendlyPeriodString(getActivity(), summary));
        durationAthleticText.setText(Utility.getDurationString(summary.getAthleticDuration()));
        durationSwimmingText.setText(Utility.getDurationString(summary.getSwimDuration()));
        durationCyclingText.setText(Utility.getDurationString(summary.getCycleDuration()));
        durationRunningText.setText(Utility.getDurationString(summary.getRunDuration()));

        dailySummaryDateText.setClickable(editMode.isEditable());
        swimButton.setEnabled(editMode.isEditable());
        cycleButton.setEnabled(editMode.isEditable());
        runButton.setEnabled(editMode.isEditable());
        athleticButton.setEnabled(editMode.isEditable());

        addPieChartData();
        addBarChartData();
    }

    private void changeTitle() {
        if (summary != null && getActivity() != null) {
            getActivity().setTitle(Utility.getTitleFromSummary(getActivity(), summary));
        }
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private void definePieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.setClickable(false);
    }

    private void defineBarChart() {
        barChart.setDescription("");
        barChart.getLegend().setEnabled(false);
        barChart.setClickable(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void addPieChartData() {
        if (!isAdded()) {
            return;
        }
        List<Integer> colors = Utility.getSportsColors(getActivity());
        List<Integer> chartColors = Lists.newArrayList();
        ArrayList<Entry> yVals1 = Lists.newArrayList();
        ArrayList<String> xVals = Lists.newArrayList();
        if (summary.getSwimDuration() != null && summary.getSwimDuration() != 0) {
            yVals1.add(new Entry(summary.getSwimDuration(), 0));
            xVals.add(getString(R.string.swimming));
            chartColors.add(colors.get(0));
        }
        if (summary.getCycleDuration() != null && summary.getCycleDuration() != 0) {
            yVals1.add(new Entry(summary.getCycleDuration(), 1));
            xVals.add(getString(R.string.cycling));
            chartColors.add(colors.get(1));
        }
        if (summary.getRunDuration() != null && summary.getRunDuration() != 0) {
            yVals1.add(new Entry(summary.getRunDuration(), 2));
            xVals.add(getString(R.string.running));
            chartColors.add(colors.get(2));
        }
        if (summary.getAthleticDuration() != null && summary.getAthleticDuration() != 0) {
            yVals1.add(new Entry(summary.getAthleticDuration(), 3));
            xVals.add(getString(R.string.athletics));
            chartColors.add(colors.get(3));

        }

        int total = summary.computeTotal();
        String totalString = "";
        if (total > 0) {
            totalString = Utility.getDurationString(total);
        }

        PieDataSet dataset = new PieDataSet(yVals1, getString(R.string.timeShare));
        dataset.setColors(chartColors);
        dataset.setHighlightEnabled(false);

        PieData data = new PieData(xVals, dataset);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setHighlightEnabled(false);

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterText(totalString);

        pieChart.invalidate();
    }

    private void addBarChartData() {
        if (!isAdded()) {
            return;
        }
        List<Integer> colors = Utility.getSportsColors(getActivity());

        BarEntry swimEntry = new BarEntry(summary.getSwimDuration(), 0);
        BarEntry rideEntry = new BarEntry(summary.getCycleDuration(), 1);
        BarEntry runEntry = new BarEntry(summary.getRunDuration(), 2);
        BarEntry athleticEntry = new BarEntry(summary.getAthleticDuration(), 3);

        BarDataSet dataSet = new BarDataSet(
                Lists.newArrayList(swimEntry, rideEntry, runEntry, athleticEntry), "");
        dataSet.setColors(colors);
        dataSet.setValueFormatter(new TimeFormatter());

        List<BarDataSet> dataSets = Lists.newArrayList(dataSet);
        dataSet.setHighlightEnabled(false);

        BarData data = new BarData(Lists.newArrayList(
                getString(R.string.swimming), getString(R.string.cycling),
                getString(R.string.running), getString(R.string.athletics)), dataSets);
        barChart.setData(data);
        barChart.invalidate();
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public static TimePickerFragment newInstance(int minutes) {
            TimePickerFragment fragment = new TimePickerFragment();
            Bundle args = new Bundle();
            int hours = minutes / 60;
            int mins = minutes % 60;
            args.putInt("hours", hours);
            args.putInt("minutes", mins);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            int hours = 0;
            int minutes = 0;
            Bundle args = getArguments();
            if (args != null && args.containsKey("hours") && args.containsKey("minutes")) {
                hours = args.getInt("hours");
                minutes = args.getInt("minutes");
            }
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hours, minutes,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hours, int minutes) {
            final Fragment targetFragment = getTargetFragment();
            Intent data = new Intent();
            Integer result = hours * 60 + minutes;
            data.putExtra("minutes", result);
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public static DatePickerFragment newInstance(Calendar cal) {
            DatePickerFragment fragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putInt("year", cal.get(Calendar.YEAR));
            args.putInt("month", cal.get(Calendar.MONTH));
            args.putInt("day", cal.get(Calendar.DAY_OF_MONTH));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            Calendar initialDate = Calendar.getInstance();
            Bundle args = getArguments();
            if (args != null && args.containsKey("year")
                    && args.containsKey("month") && args.containsKey("day")) {
                initialDate.set(Calendar.YEAR, args.getInt("year"));
                initialDate.set(Calendar.MONTH, args.getInt("month"));
                initialDate.set(Calendar.DAY_OF_MONTH, args.getInt("day"));
            }
            // Use the current date as the default date in the picker
            int year = initialDate.get(Calendar.YEAR);
            int month = initialDate.get(Calendar.MONTH);
            int day = initialDate.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            final Fragment targetFragment = getTargetFragment();
            Intent data = new Intent();
            data.putExtra("year", year);
            data.putExtra("month", monthOfYear);
            data.putExtra("day", dayOfMonth);
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }
}
