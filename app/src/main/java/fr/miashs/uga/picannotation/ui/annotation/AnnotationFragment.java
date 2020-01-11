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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fr.miashs.uga.picannotation.R;
import fr.miashs.uga.picannotation.ChooseEvent;
import fr.miashs.uga.picannotation.model.PicAnnotation;
import fr.miashs.uga.picannotation.ui.home.HomeFragment;

import static android.app.Activity.RESULT_OK;

public class AnnotationFragment extends Fragment {

    public AnnotationViewModel annotationViewModel;

    private static final int PICK_IMG = 1;
    private static final int PICK_CONTACT = 2;
    private static final int PICK_EVENT = 3;

    private ImageView img;
    private Button addContactBtn;
    private Button addEventBtn;
    private Button btn_save;
    private Button btn_trash;
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

        //Composants graphiques
        img = view.findViewById(R.id.annotImageView);
        addContactBtn = view.findViewById(R.id.buttonContact);
        addEventBtn = view.findViewById(R.id.buttonEvent);
        btn_save = view.findViewById(R.id.button_save);
        btn_trash = view.findViewById(R.id.button_trash);
        eventView = view.findViewById(R.id.event_View);
        listContact = view.findViewById(R.id.listContacts);

        //Fixe une taille fixe à la liste de contacts
        listContact.setHasFixedSize(true);

        myLayoutManager = new LinearLayoutManager(view.getContext());
        listContact.setLayoutManager(myLayoutManager);

        myAdapter = new ContactAnnotAdapter(view.getContext());
        listContact.setAdapter(myAdapter);

        myAdapter.getMyContactsLive().observe(this, new Observer<List<Uri>>() {
            @Override
            public void onChanged(@Nullable final List<Uri> contacts) {
                // Update la copie des contacts dans l'adapter
               annotationViewModel.setContacts(contacts);
            }
        });

        //Suppression d'un ContactAnnotation à la volée
        //Observe le contact supprimé dans le RecyclerView et vérifie si un ContactAnnotation existe pour lui sur l'image en cours
        myAdapter.getMyContactDelete().observe(AnnotationFragment.this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri contactUri) {
                Uri imageUri = annotationViewModel.getPicUri();
                if(imageUri != null){
                    annotationViewModel.getCountContactAnnotExist(imageUri,contactUri).observe(AnnotationFragment.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            if(integer == 1){
                                Log.i("DEBUG","L'image : "+imageUri+" pour le contact : "+contactUri+" possède une ContactAnnotation dans notre bdd -> Supprimer");
                                annotationViewModel.deleteContactAnnotation(imageUri, contactUri);
                            }
                        }
                    });
                }
            }
        });

        //Divider entre les items de la liste
        listContact.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));

        //Défini l'image par défaut (ici un vector add_a_photo_black)
        int imageDefault = getResources().getIdentifier("@drawable/ic_add_a_photo_black_24dp", null, view.getContext().getPackageName());

        Glide.with(this)
                .load(imageDefault)
                .apply(new RequestOptions().override(500,500))
                /*.centerCrop()*/
                .into(img);

        //Setter des Listeners
        img.setOnClickListener(imgBtnAdd);
        addContactBtn.setOnClickListener(contactAddBtn);
        eventView.setOnClickListener(eventAddBtn);
        addEventBtn.setOnClickListener(eventAddBtn);
        btn_save.setOnClickListener(saveAll);
        btn_trash.setOnClickListener(deleteBtn);

        return view;
    }

    //Action lors du click du bouton add Image
    private View.OnClickListener imgBtnAdd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checkImageReadPermission();
            Intent pickImg = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickImg.setType("image/*");
            pickImg.addFlags((Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
            startActivityForResult(pickImg,PICK_IMG);
        }
    };

    //Action lors du click du bouton add Contact
    private View.OnClickListener contactAddBtn = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            checkContactReadPermission();
            Intent pickContact = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContact, PICK_CONTACT);
        }
    };

    //Action lors du click du bouton add Event
    private View.OnClickListener eventAddBtn = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent pickEvent = new Intent(view.getContext(), ChooseEvent.class);
            startActivityForResult(pickEvent, PICK_EVENT);
        }
    };

    //Action lors du click du bouton Save + redirection vers Home
    private View.OnClickListener saveAll = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Test les valeurs demandées présentes
            //SI présentes -> Faire les insert SINON -> Afficher "Données manquantes, Réessayez"
            if(annotationViewModel.getPicUri() != null && annotationViewModel.getEventUri().getValue() != null && myAdapter.getItemCount() > 0){
                annotationViewModel.save();
                Navigation.findNavController(view).navigate(R.id.action_navigation_annotation_to_navigation_home);
            }else {
                Toast.makeText(getContext(),"Données manquantes, Réessayez !",Toast.LENGTH_SHORT).show();
            }
        }
    };

    //Action lors du click bouton delete
    private View.OnClickListener deleteBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Suppression de l'Annotation de l'image
            //Test s'il existe une annotation pour cette image
            annotationViewModel.getCountEventAnnotExist(annotationViewModel.getPicUri()).observe(AnnotationFragment.this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(integer == 1){
                        annotationViewModel.deletePicEventAnnotation();
                        annotationViewModel.deletePicContactAnnotation();
                        Toast.makeText(getContext(),"Annotations de l'image supprimées",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(),"Pas d'annotations sauvegardées",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //On a choisi notre image et on va l'ajouter à notre vue
        if(requestCode == PICK_IMG && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // Check for the freshest data.
            this.getContext().getContentResolver().takePersistableUriPermission(imageUri,takeFlags);

            annotationViewModel.setPicUri(imageUri);

            //Vérifie s'il existe un PicAnnotation pour cette image
            annotationViewModel.getPicAnnotation(imageUri).observe(AnnotationFragment.this, new Observer<PicAnnotation>() {
                @Override
                public void onChanged(@Nullable PicAnnotation picAnnotation) {
                    //Si déjà annoté, remplir les champs avec les infos
                    if(picAnnotation != null){
                        if(picAnnotation.getEventUri() != null) {
                            eventView.setText(getEventName(picAnnotation.getEventUri().getLastPathSegment()));
                            annotationViewModel.setEventUri(picAnnotation.getEventUri());
                        }
                        if(picAnnotation.getContactsUris() != null){
                            for(Uri contact : picAnnotation.getContactsUris()){
                                annotationViewModel.addContact(contact);
                            }
                            myAdapter.setContacts(picAnnotation.contactsUris);
                        }
                    }else {
                        eventView.setText("Votre futur Event");
                        myAdapter.setContacts(new ArrayList<>());
                    }
                }
            });
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
        //On a choisi notre contact et on va l'ajouter à notre liste data et UI
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK){
            UriContact = data.getData();
            myAdapter.addContact(UriContact);
            annotationViewModel.addContact(UriContact);
        }
        //On a choisi notre event et on va l'ajouter à notre vue
        if(requestCode == PICK_EVENT && resultCode == RESULT_OK){
            Uri EventUri = data.getData();
            annotationViewModel.setEventUri(EventUri);

            eventView.setText(getEventName(EventUri.getLastPathSegment()));
        }
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

    //Check les permissions d'accès aux fichiers externes (images)
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