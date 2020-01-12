package fr.miashs.uga.picannotation.ui.search;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.miashs.uga.picannotation.ChooseEvent;
import fr.miashs.uga.picannotation.R;
import fr.miashs.uga.picannotation.model.PicAnnotation;
import fr.miashs.uga.picannotation.ui.annotation.AnnotationFragment;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS= 1;
    private boolean readContactAuthorize = false;

    private static final int PICK_CONTACT = 2;
    private static final int PICK_EVENT = 3;

    private Button searchBtn;
    private Button contactBtn;
    private Button eventBtn;
    private TextView eventView;
    private ChipGroup myChipGroup;
    private Bundle bundle = new Bundle();

    private List<Uri> listResult = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Composants graphiques
        searchBtn = view.findViewById(R.id.btn_search);
        contactBtn = view.findViewById(R.id.contact_btn);
        eventBtn = view.findViewById(R.id.event_btn);
        eventView = view.findViewById(R.id.edit_event);
        myChipGroup = view.findViewById(R.id.chipGroup2);

        //Setter des Listeners
        searchBtn.setOnClickListener(search);
        contactBtn.setOnClickListener(insertContact);
        eventBtn.setOnClickListener(insertEvent);
        eventView.setOnClickListener(insertEvent);

        return view;
    }

    //Action lors du click du bouton Search
    private View.OnClickListener search = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.i("DEBUG","On lance la recherche");
            listResult.clear();
            searchViewModel.search().observe(SearchFragment.this, new Observer<List<PicAnnotation>>() {
                @Override
                public void onChanged(List<PicAnnotation> picAnnotations) {
                    //Recherche faite juste avec une liste de contact
                    if(searchViewModel.getContacts().size() > 0 && searchViewModel.getEventUri().getValue() == null){
                        Log.i("DEBUG","Resultat Recherche par contacts seulement");
                        Toast.makeText(getContext(),"Recherche par contacts seulement",Toast.LENGTH_SHORT).show();
                        if(searchViewModel.getContacts().size()== 1){
                            Log.i("DEBUG","Resultat Recherche pour 1 contact seulement");
                            for(PicAnnotation pic : picAnnotations){
                                listResult.add(pic.getPicUri());
                            }
                        }else {
                            Log.i("DEBUG","Resultat Recherche pour plusieurs contacts seulement");
                            for(PicAnnotation pic : picAnnotations){
                                if(pic.getContactsUris().size() == searchViewModel.getContacts().size()){
                                    //Log.i("DEBUG","Cette image : "+pic.getPicUri()+" contient nos "+searchViewModel.getContacts().size()+" contacts : "+pic.getContactsUris());
                                    listResult.add(pic.getPicUri());
                                }
                            }
                        }
                    }else if(searchViewModel.getContacts().size() == 0 && searchViewModel.getEventUri().getValue() != null){
                        Log.i("DEBUG","Resultat Recherche par event seulement");
                        Toast.makeText(getContext(),"Recherche par event seulement",Toast.LENGTH_SHORT).show();
                        for(PicAnnotation pic : picAnnotations){
                            listResult.add(pic.getPicUri());
                        }
                    } else {
                        Log.i("DEBUG","Resultat Recherche par event et contact");
                        Toast.makeText(getContext(),"Recherche par event et contact",Toast.LENGTH_SHORT).show();
                        for(PicAnnotation pic : picAnnotations){
                            if(pic.getContactsUris().size() == searchViewModel.getContacts().size()){
                                Log.i("DEBUG","RES : "+pic.toString());
                                listResult.add(pic.getPicUri());
                            }
                        }
                    }
                    Log.i("DEBUG","Resultat final de la recherche : "+listResult.size());
                    bundle.putString("Result",listResult.toString());
                    Navigation.findNavController(view).navigate(R.id.action_navigation_search_to_navigation_searchresult,bundle);
                }
            });
        }
    };

    //Action lors du click du bouton Add Contact
    private View.OnClickListener insertContact = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checkContactReadPermission();
            Intent pickContact = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContact, PICK_CONTACT);
        }
    };

    //Action lors du click du bouton Add Event ou click sur eventView
    private View.OnClickListener insertEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent pickEvent = new Intent(view.getContext(), ChooseEvent.class);
            startActivityForResult(pickEvent, PICK_EVENT);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //On a choisi notre contact et on va l'ajouter à notre vue
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK){
            Uri UriContact = data.getData();
            String contact = retrieveContactName(UriContact);
            addChipToGroup(UriContact, contact, myChipGroup);
        }
        //On a choisi notre event et on va l'ajouter à notre vue
        if(requestCode == PICK_EVENT && resultCode == RESULT_OK){
            Uri EventUri = data.getData();
            searchViewModel.setEventUri(EventUri);

            eventView.setText(getEventName(EventUri.getLastPathSegment()));
        }
    }

    //Ajout un Chip au Chip Group + interactions ViewModel
    private void addChipToGroup(Uri UriContact, String person, ChipGroup chipGroup){
        if(searchViewModel.getContacts().contains(UriContact)){
            Toast.makeText(getContext(),"Ce contact est déjà présent",Toast.LENGTH_SHORT).show();
        }else {
            searchViewModel.insertContact(UriContact);

            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip, chipGroup, false);

            chip.setText(person);

            //Suppression du Chip lors du click CloseIcon + animation
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation anim = new AlphaAnimation(1f,0f);
                    anim.setDuration(250);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            chipGroup.removeView(chip);
                            searchViewModel.removeContact(UriContact);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    chip.startAnimation(anim);
                }
            });
            chipGroup.addView(chip);
        }
    }

    //Récupère le nom du contact grâce à son Uri
    private String retrieveContactName(Uri contactUri){
        String contactName = null;

        //Query du contact stocké
        Cursor cursor = this.getContext().getContentResolver().query(contactUri, null, null, null, null);

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();

        return contactName;
    }

    //Récupère le nom de l'event en fonction de son id
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

    //Check les permissions d'accès aux contacts
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

    //Authorise accès si permissions ok
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
        }
    }

}