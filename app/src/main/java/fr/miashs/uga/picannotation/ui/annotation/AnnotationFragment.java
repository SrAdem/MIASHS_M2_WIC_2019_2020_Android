package fr.miashs.uga.picannotation.ui.annotation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import fr.miashs.uga.picannotation.R;

import static android.app.Activity.RESULT_OK;

public class AnnotationFragment extends Fragment {

    private AnnotationViewModel annotationViewModel;

    private static final int PICK_IMG = 1;
    private static final int PICK_CONTACT = 2;

    private ImageView img;
    private Button addContactBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        annotationViewModel = ViewModelProviders.of(this).get(AnnotationViewModel.class);

        View view = inflater.inflate(R.layout.fragment_annotation, container, false);

        //Composants Graphique
        img = (ImageView)view.findViewById(R.id.annotImageView);
        addContactBtn = (Button)view.findViewById(R.id.buttonContact);

        int imageDefault = getResources().getIdentifier("@mipmap/ic_launcher", null, view.getContext().getPackageName());
        img.setImageResource(imageDefault);

        img.setOnClickListener(imgBtnAdd);
        addContactBtn.setOnClickListener(contactAddBtn);

        return view;
    }

    private View.OnClickListener imgBtnAdd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Log.i(" DEBUG ", "Click sur l'image");
            Intent pickImg = new Intent(Intent.ACTION_PICK);
            pickImg.setType("image/*");
            startActivityForResult(pickImg,PICK_IMG);
        }
    };

    private View.OnClickListener contactAddBtn = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.i("DEBUG", "Click sur Button Contact");
            Intent pickContact = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            //pickContact.setData(ContactsContract.Data.CONTENT_URI);
            startActivityForResult(pickContact, PICK_CONTACT);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMG && resultCode == RESULT_OK){
            //Log.i("DEBUG", "On a choisi notre image");
            img.setImageURI(data.getData());
        }
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK){
            Log.i("DEBUG", "On a choisi notre contact");
        }
    }
}