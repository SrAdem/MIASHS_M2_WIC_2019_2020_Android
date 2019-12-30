package fr.miashs.uga.picannotation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class ChooseEvent extends AppCompatActivity implements CalendarView.OnDateChangeListener, OnItemActivatedListener<Long> {

    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR= 0;
    private EventListAdapter adapter;
    private boolean readCalendarAuthorize = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(this);

        RecyclerView recyclerview = findViewById(R.id.recyclerView);

        adapter = new EventListAdapter(this);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        SelectionTracker tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerview,
                new StableIdKeyProvider(recyclerview),
                new EventListAdapter.EventDetailsLookup(recyclerview),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.<Long>createSelectSingleAnything())
                .withOnItemActivatedListener(this)
                .build();
    }

    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull MotionEvent e){
        /* Toast.makeText(this, "event id : "+item.getSelectionKey(), Toast.LENGTH_LONG).show(); */
        Log.v("ITEM SELECTED", "event id : "+item.getSelectionKey());

        Uri selectedEvent = Uri.withAppendedPath(CalendarContract.Events.CONTENT_URI, item.getSelectionKey().toString());

        Intent res = new Intent();
        res.setData(selectedEvent);
        this.setResult(Activity.RESULT_OK, res);
        this.finish();

        // Ajouter les bons IntentFilter  de  type ActionPick sur le Calendar
        Toast.makeText(this, "event uri : "+selectedEvent, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
        Toast.makeText(this.getApplicationContext(),"Date : "+dayOfMonth+"/"+(month+1)+"/"+year, Toast.LENGTH_LONG).show();

        checkCalendarReadPermission();
        if(readCalendarAuthorize){
            adapter.setDate(year, month, dayOfMonth);
        }

    }

    public void checkCalendarReadPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            readCalendarAuthorize = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    readCalendarAuthorize = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    readCalendarAuthorize = false;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}
