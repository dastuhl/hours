package de.dastuhl.hours;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Calendar;

import de.dastuhl.hours.data.model.DailySessionsSummary;
import de.dastuhl.hours.data.model.MonthlySessionsSummary;
import de.dastuhl.hours.data.model.WeeklySessionsSummary;

/**
 * Created by Martin on 18.11.2015.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Drawable mDivider;

    public DividerItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        final SessionsSummaryViewAdapter adapter = (SessionsSummaryViewAdapter) parent.getAdapter();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            boolean draw = false;

            int adapterPos = parent.getChildAdapterPosition(child);
            Object summary = adapter.getItem(adapterPos);
            if (summary instanceof DailySessionsSummary) {
                draw = ((DailySessionsSummary) summary).buildCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
            } else if (summary instanceof WeeklySessionsSummary) {
                draw = ((WeeklySessionsSummary) summary).getWeekOfYear() == 1;
            } else if (summary instanceof MonthlySessionsSummary) {
                draw = ((MonthlySessionsSummary) summary).getMonth() - 1 == Calendar.JANUARY;
            }

            if (draw) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
}
