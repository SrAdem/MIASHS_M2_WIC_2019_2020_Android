package fr.miashs.uga.picannotation.ui.annotation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import fr.miashs.uga.picannotation.R;
import fr.miashs.uga.picannotation.ChooseEvent;

import static android.app.Activity.RESULT_OK;

public class AnnotationFragment extends Fragment {

    public AnnotationViewModel annotationViewModel;

    private static final int PICK_IMG = 1;
    private static final int PICK_CONTACT = 2;
    private static final int PICK_EVENT = 3;

    private ImageView img;
    private Button addContactBtn;
    private Button addEventBtn;
    private TextView eventView;
    private Uri UriContact;

    private RecyclerView listContact;
    private ContactAnnotAdapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS= 4;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 5;
    private boolean readContactAuthorize = false;
    private boolean readImageAuthorize = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        annotationViewModel = ViewModelProviders.of(this).get(AnnotationViewModel.class);

        View view = inflater.inflate(R.layout.fragment_annotation, container, false);

        //Composants Graphique
        img = (ImageView)view.findViewById(R.id.annotImageView);
        addContactBtn = (Button)view.findViewById(R.id.buttonContact);
        addEventBtn = (Button)view.findViewById(R.id.buttonEvent);
        eventView = (TextView)view.findViewById(R.id.event_View);
        listContact = (RecyclerView)view.findViewById(R.id.listContacts);

        listContact.setHasFixedSize(true);

        myLayoutManager = new LinearLayoutManager(view.getContext());
        listContact.setLayoutManager(myLayoutManager);

        myAdapter = new ContactAnnotAdapter(view.getContext(),new ArrayList<>(0));
        listContact.setAdapter(myAdapter);

        annotationViewModel.getAllContact().observe(this, new Observer<List<Uri>>() {
            @Override
            public void onChanged(@Nullable final List<Uri> contacts) {
                // Update the cached copy of the words in the adapter.
                myAdapter.setContact(contacts);
            }
        });

        //Divider entre les items de la liste
        listContact.addItemDecoration(
                new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));

        int imageDefault = getResources().getIdentifier("@mipmap/ic_launcher", null, view.getContext().getPackageName());
        //img.setImageResource(imageDefault);
        Glide.with(this)
                .load(imageDefault)
                .apply(new RequestOptions().override(500,500))
                /*.centerCrop()*/
                .into(img);

        img.setOnClickListener(imgBtnAdd);
        addContactBtn.setOnClickListener(contactAddBtn);
        addEventBtn.setOnClickListener(eventAddBtn);

        return view;
    }

    private View.OnClickListener imgBtnAdd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Log.i(" DEBUG ", "Click sur l'image");
            checkImageReadPermission();
            Intent pickImg = new Intent(Intent.ACTION_PICK);
            pickImg.setType("image/*");
            startActivityForResult(pickImg,PICK_IMG);
        }
    };

    private View.OnClickListener contactAddBtn = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //Log.i("DEBUG", "Click sur Button Contact");
            checkContactReadPermission();
            Intent pickContact = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            //pickContact.setData(ContactsContract.Data.CONTENT_URI);
            startActivityForResult(pickContact, PICK_CONTACT);
        }
    };

    private View.OnClickListener eventAddBtn = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //Log.i("DEBUG", "Click sur Button Event");

            Intent pickEvent = new Intent(view.getContext(), ChooseEvent.class);

            startActivityForResult(pickEvent, PICK_EVENT);

        }
    };

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMG && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), imageUri);

                Display display = this.getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int sw,sh;

                if(bitmap.getHeight() > bitmap.getWidth()) {
                    sh = size.y / 2;
                    sw = bitmap.getWidth()*sh/bitmap.getHeight();
                }
                else {
                    sw = size.x;
                    sh = bitmap.getHeight()*sw/bitmap.getWidth();
                }
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, sw, sh, true);
                Glide.with(this)
                        .load(resized)
                        .apply(new RequestOptions().override(sw,sh))
                        .centerCrop()
                        .into(img);
            }
            catch(Exception e) {
            }
        }
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK){
            UriContact = data.getData();
            //Log.i("DEBUG", "On a choisi notre contact : "+ UriContact);
            annotationViewModel.insertContact(UriContact);
        }
        if(requestCode == PICK_EVENT && resultCode == RESULT_OK){
            Uri EventUri = data.getData();
            //Log.i("DEBUG", "On a choisi notre Event : "+EventUri);

            eventView.setText(getEventName(EventUri.getLastPathSegment()));
        }
    }

    public String getEventName(String id) {
        Cursor cursor = null;
        String result = "";
        try {
            if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
                cursor = this.getContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI,null,CalendarContract.Events._ID + "=?",new String[]{id},null);
                if(cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                    cursor.close();
                }
            }
        }catch(Exception e){
            Log.i("DEBUG"," erreur "+e);
        }
        return result;
    }

    public void checkContactReadPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            // Permission has already been granted
            readContactAuthorize = true;
        }
    }

    public void checkImageReadPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            // Permission has already been granted
            readImageAuthorize = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    readContactAuthorize = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    readContactAuthorize = false;
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    readImageAuthorize = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    readImageAuthorize = false;
                }
                return;
            }
        }
    }

}