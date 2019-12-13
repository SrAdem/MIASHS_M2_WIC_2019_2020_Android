package fr.miashs.uga.picannotation.ui.annotation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
        //img.setImageResource(imageDefault);
        Glide.with(this)
                .load(imageDefault)
                .apply(new RequestOptions().override(500,500))
                /*.centerCrop()*/
                .into(img);

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
            Log.i("DEBUG", "On a choisi notre contact");
        }
    }
}