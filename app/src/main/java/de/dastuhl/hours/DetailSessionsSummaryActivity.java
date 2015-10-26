package de.dastuhl.hours;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class DetailSessionsSummaryActivity extends AppCompatActivity {

    public static void start(Context context, String userId, String url) {
        Intent detailSummaryIntent = new Intent(context, DetailSessionsSummaryActivity.class);
        detailSummaryIntent.putExtra(DetailSessionsSummaryActivityFragment.ARG_USER_ID, userId);
        detailSummaryIntent.putExtra(DetailSessionsSummaryActivityFragment.ARG_SUMMARY_URL, url);
        context.startActivity(detailSummaryIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sessions_summary);

        if (savedInstanceState == null) {
            String userId = getIntent().getStringExtra(DetailSessionsSummaryActivityFragment.ARG_USER_ID);
            String url = getIntent().getStringExtra(DetailSessionsSummaryActivityFragment.ARG_SUMMARY_URL);

            DetailSessionsSummaryActivityFragment fragment = DetailSessionsSummaryActivityFragment
                    .newInstance(userId, url);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sessions_summary_detail_container, fragment)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_sessions_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
