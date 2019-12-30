package fr.miashs.uga.picannotation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    private final LayoutInflater mInflater;

    EventListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context); }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView EventTitleItemView;
        int position;
        Long eventId;
        private EventViewHolder(View itemView) {
            super(itemView);
            EventTitleItemView = itemView.findViewById(R.id.textViewEvent);
        }

        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new EventDetails(this);
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new EventViewHolder(itemView);
    }

    static final class EventDetailsLookup extends ItemDetailsLookup {

        private final RecyclerView mRecyclerView;

        EventDetailsLookup(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }

        public ItemDetails getItemDetails(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                if (holder instanceof EventViewHolder) {
                    return ((EventViewHolder) holder).getItemDetails();
                }
            }
            return null;
        }
    }

    static final class EventDetails extends ItemDetailsLookup.ItemDetails {
        EventViewHolder selected;

        EventDetails(EventViewHolder holder){
            selected = holder;
        }

        @Override
        public int getPosition() {
            return selected.position;
        }

        @Nullable
        @Override
        public Object getSelectionKey() {
            return selected.eventId;
        }
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            String eventTitle = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.TITLE));
            holder.EventTitleItemView.setText(eventTitle);
            holder.position = position;
            holder.eventId = mCursor.getLong(mCursor.getColumnIndex(CalendarContract.Events._ID));
        }
    }

    private final static String[] PROJECTION = new String[] {CalendarContract.Events._ID, CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};

    public void setDate(int year, int month, int dayOfMonth){

        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth, 0, 0);
        long jd = c.getTimeInMillis();

        c.set(year, month, dayOfMonth+ 1, 0, 0);
        long jf = c.getTimeInMillis();

        Log.v("RES CALENDAR", "date debut = "+jd);
        Log.v("RES CALENDAR", "date fin = "+jf);
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            mCursor = mContext.getContentResolver().query(
                    CalendarContract.Events.CONTENT_URI,
                    PROJECTION,
                    CalendarContract.Events.DTSTART+"<"+jf+" AND "+jd+"<"+CalendarContract.Events.DTEND,
                    null,
                    CalendarContract.Events.DTSTART+" ASC "
            );
            this.notifyDataSetChanged();
        }

    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mCursor != null)
            return mCursor.getCount();
        else return 0;
    }
}
