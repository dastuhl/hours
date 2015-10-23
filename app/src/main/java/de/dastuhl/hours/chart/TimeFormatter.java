package de.dastuhl.hours.chart;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import de.dastuhl.hours.Utility;

/**
 * Created by Martin on 22.10.2015.
 */
public class TimeFormatter implements ValueFormatter, YAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return Utility.getPeriodString((int) value);
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return Utility.getPeriodString((int) value);
    }
}
