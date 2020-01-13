package fr.miashs.uga.picannotation.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import fr.miashs.uga.picannotation.R;
import fr.miashs.uga.picannotation.ui.search.SearchResultFragment;

public class HomeFragment extends Fragment {

    static final int REQUEST_PERMISSION_KEY = 1;
    private boolean readGalleryAuthorized = false;

    private HomeViewModel homeViewModel;
    private GridView grid_home;
    private TextView textRes;
    List<Uri> finalResults = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Composants Graphiques
        grid_home = view.findViewById(R.id.grid_home);
        textRes = view.findViewById(R.id.home_counter_results);

        final MutableLiveData<List<Uri>> listAnnot = new MutableLiveData<>();

        checkReadGalleryPermission();

        final HomeAdapter myHomeAdapter = new HomeAdapter(getContext(), listAnnot.getValue());

        if(homeViewModel.getPicsUri().size() != 0){
            if(readGalleryAuthorized){
                grid_home.setAdapter(myHomeAdapter);
                myHomeAdapter.setData(homeViewModel.getPicsUri());
            }
        }else {
            //Récupère toutes les photos de la bdd
            homeViewModel.getAllEventAnnotations().observe(HomeFragment.this, new Observer<List<Uri>>() {
                @Override
                public void onChanged(List<Uri> uris) {
                    finalResults = uris;

                    textRes.setText("Resultats obtenus : "+finalResults.size());

                    //Vérification des droits avant d'afficher les images
                    if(readGalleryAuthorized){
                        grid_home.setAdapter(myHomeAdapter);
                        homeViewModel.setPicsUri(finalResults);
                        myHomeAdapter.setData(finalResults);
                    }
                }
            });
        }

        //Setter des composants
        grid_home.setOnItemClickListener(img_redirection);

        return view;
    }

    //Redirection vers Annotate pour l'image choisie
    private AdapterView.OnItemClickListener img_redirection = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Object myObj = grid_home.getItemAtPosition(position);
            Bundle bundle = new Bundle();
            bundle.putString("IMGURI",myObj.toString());
            Navigation.findNavController(view).navigate(R.id.navigation_annotation, bundle);
        }
    };

    final class HomeAdapter extends BaseAdapter {
        private List<Uri> myData;
        private LayoutInflater myInflater;
        private Context myContext;

        public HomeAdapter(Context context, List<Uri> listData){
            this.myContext = context;
            if(listData == null){
                this.myData = new ArrayList<>();
            }else {
                this.myData = listData;
            }
            myInflater = LayoutInflater.from(context);
        }

        void setData(List<Uri> data){
            if(this.myData.size() > 0){
                this.myData.clear();
            }
            this.myData.addAll(data);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(myData == null){
                return 0;
            }else {
                return myData.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return myData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            HomeFragment.ViewHolder holder;

            if(view == null){
                view = myInflater.inflate(R.layout.img,null);
                holder = new HomeFragment.ViewHolder();
                holder.myPicUri = view.findViewById(R.id.imageView);
                view.setTag(holder);
            }else {
                holder = (HomeFragment.ViewHolder)view.getTag();
            }

            Uri picUri = this.myData.get(position);

            try {
                holder.myPicUri.setImageURI(picUri);
            } catch(Exception e){
                Log.i("DEBUG","SET ImgView PB"+e);
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView myPicUri;
    }

    public void checkReadGalleryPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_KEY);
            }
        } else {
            // Permission has already been granted
            readGalleryAuthorized = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    readGalleryAuthorized = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}