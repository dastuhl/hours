package de.dastuhl.hours;

import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerViewAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dastuhl.hours.data.Session;

/**
 * Created by Martin on 24.09.2015.
 */
public class SessionsViewAdapter extends FirebaseRecyclerViewAdapter<Session, SessionsViewAdapter.SessionListViewHolder> {


    public SessionsViewAdapter(Class<Session> modelClass, int modelLayout, Class<SessionListViewHolder> viewHolderClass, Firebase ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    public void populateViewHolder(SessionListViewHolder pSessionListViewHolder, Session pSession) {

        pSessionListViewHolder.date.setText(pSession.getDate());
        pSessionListViewHolder.athletic.setText(String.valueOf(pSession.getAthleticDuration()));
        pSessionListViewHolder.swimming.setText(String.valueOf(pSession.getSwimDuration()));
        pSessionListViewHolder.cycling.setText(String.valueOf(pSession.getCycleDuration()));
        pSessionListViewHolder.running.setText(String.valueOf(pSession.getRunDuration()));
    }

    static class SessionListViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.list_item_date_textview)
        TextView date;
        @Bind(R.id.list_item_swimming_textview)
        TextView swimming;
        @Bind(R.id.list_item_cycling_textview)
        TextView cycling;
        @Bind(R.id.list_item_running_textview)
        TextView running;
        @Bind(R.id.list_item_athletic_textview)
        TextView athletic;

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
