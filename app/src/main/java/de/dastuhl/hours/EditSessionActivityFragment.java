package de.dastuhl.hours;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dastuhl.hours.data.model.DailySessionsSummary;
import de.dastuhl.hours.data.model.SessionsSummary;
import de.dastuhl.hours.data.model.SessionsSummaryFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class EditSessionActivityFragment extends Fragment {

    private static final String TAG_TIME_PICKER_DIALOG = "time_picker";
    private static final String TAG_DATE_PICKER_DIALOG = "date_picker";

    private static final int DATE_DIALOG = 1;
    private static final int TIME_DIALOG_SWIM = 2;
    private static final int TIME_DIALOG_CYCLE = 3;
    private static final int TIME_DIALOG_RUN = 4;
    private static final int TIME_DIALOG_ATHLETIC = 5;

    public static final String RESULT_SESSION = "resultSession";

    @Bind(R.id.session_date)
    EditText sessionDateText;

    @Bind(R.id.session_duration_swim)
    EditText durationSwimmingText;

    @Bind(R.id.session_duration_cycle)
    EditText durationCyclingText;

    @Bind(R.id.session_duration_run)
    EditText durationRunningText;

    @Bind(R.id.session_duration_athletic)
    EditText durationAthleticText;

    private Calendar sessionDate;
    private Integer durationSwim;
    private Integer durationCycle;
    private Integer durationRun;
    private Integer durationAthletic;

    public EditSessionActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_session, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_session_fragment, menu);

        MenuItem item = menu.findItem(R.id.menu_action_done);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_action_done:
                Intent data = new Intent();
                SessionsSummary dailySummary = SessionsSummaryFactory.INSTANCE.createDailySessionsSummary(sessionDate,
                        durationSwim == null ? 0 : durationSwim,
                        durationCycle == null ? 0 : durationCycle,
                        durationRun == null ? 0 : durationRun,
                        durationAthletic == null ? 0 : durationAthletic);
                data.putExtra(RESULT_SESSION, dailySummary);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                return true;
            case R.id.action_cancel:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.session_duration_swim)
    void onDurationSwimmingClicked() {
        openDialog(TIME_DIALOG_SWIM);
    }
    @OnClick(R.id.session_duration_cycle)
    void onDurationCyclingClicked() {
        openDialog(TIME_DIALOG_CYCLE);
    }
    @OnClick(R.id.session_duration_run)
    void onDurationRunningClicked() {
        openDialog(TIME_DIALOG_RUN);
    }
    @OnClick(R.id.session_duration_athletic)
    void onDurationAthleticClicked() {
        openDialog(TIME_DIALOG_ATHLETIC);
    }

    @OnClick(R.id.session_date)
    void onSessionDateClicked() {
        openDialog(DATE_DIALOG);
    }

    private  void openDialog(int dialogId) {
        switch (dialogId) {
            case DATE_DIALOG:
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.setTargetFragment(this, dialogId);
                dateFragment.show(getActivity().getSupportFragmentManager(), TAG_DATE_PICKER_DIALOG);
                break;
            case TIME_DIALOG_ATHLETIC:
            case TIME_DIALOG_CYCLE:
            case TIME_DIALOG_RUN:
            case TIME_DIALOG_SWIM:
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.setTargetFragment(this, dialogId);
                timeFragment.show(getActivity().getSupportFragmentManager(), TAG_TIME_PICKER_DIALOG);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATE_DIALOG:
                int year = data.getIntExtra("year", -1);
                int month = data.getIntExtra("month", -1);
                int day = data.getIntExtra("day", -1);
                sessionDate = Calendar.getInstance();
                sessionDate.set(year, month, day);
                break;
            case TIME_DIALOG_ATHLETIC:
                durationAthletic = data.getIntExtra("minutes", 0);
                break;
            case TIME_DIALOG_CYCLE:
                durationCycle = data.getIntExtra("minutes", 0);
                break;
            case TIME_DIALOG_RUN:
                durationRun = data.getIntExtra("minutes", 0);
                break;
            case TIME_DIALOG_SWIM:
                durationSwim = data.getIntExtra("minutes", 0);
                break;
        }
        updateLabel(requestCode);
    }

    private void updateLabel(int requestCode) {
        switch (requestCode) {
            case DATE_DIALOG:
                String format = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sessionDateText.setText(sdf.format(sessionDate.getTime()));
                break;
            case TIME_DIALOG_ATHLETIC:
                durationAthleticText.setText(durationAthletic + " " + getString(R.string.Minutes));
                break;
            case TIME_DIALOG_SWIM:
                durationSwimmingText.setText(durationSwim + " " + getString(R.string.Minutes));
                break;
            case TIME_DIALOG_CYCLE:
                durationCyclingText.setText(durationCycle + " " + getString(R.string.Minutes));
                break;
            case TIME_DIALOG_RUN:
                durationRunningText.setText(durationRun + " " + getString(R.string.Minutes));
                break;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

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
