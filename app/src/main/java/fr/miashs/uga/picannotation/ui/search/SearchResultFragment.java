package fr.miashs.uga.picannotation.ui.search;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.miashs.uga.picannotation.R;

public class SearchResultFragment extends Fragment {

    static final int REQUEST_PERMISSION_KEY = 1;
    private boolean readGalleryAuthorized = false;

    private SearchResultViewModel searchResultViewModel;
    private TextView counterResult;
    private GridView gridResult;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        searchResultViewModel = ViewModelProviders.of(this).get(SearchResultViewModel.class);
        View view = inflater.inflate(R.layout.fragment_resultsearch, container, false);

        //Futur composants graphiques
        counterResult = view.findViewById(R.id.counter_results);
        gridResult = view.findViewById(R.id.grid_result);
        final MutableLiveData<List<Uri>> listAnnot = new MutableLiveData<>();
        checkReadGalleryPermission();

        final SearchResultAdapter mySearchResultAdapter = new SearchResultAdapter(getContext(), listAnnot.getValue());

        if(searchResultViewModel.getPicsUri().size() != 0){
            if(readGalleryAuthorized){
                gridResult.setAdapter(mySearchResultAdapter);
                mySearchResultAdapter.setData(searchResultViewModel.getPicsUri());
            }
        }else {
            if(getArguments().getString("Result") != null){

                String resultsParse = getArguments().getString("Result");
                List<String> listStringResult = Arrays.asList(resultsParse.substring(1,resultsParse.length()-1).split("\\s*(,\\s*)+"));
                Log.i("DEBUG","listStringResult : "+listStringResult);
                List<Uri> finalResults = new ArrayList<>();

                if(listStringResult.size() != 0 && !listStringResult.toString().equals("[]")){
                    for(String uri : listStringResult){
                        finalResults.add(Uri.parse(uri));
                    }
                }

                Log.i("DEBUG","listUriFinal : "+finalResults);
                //Affiche le nombre de  résultats
                counterResult.setText("Resultats obtenus : "+finalResults.size());

                if(readGalleryAuthorized){
                    gridResult.setAdapter(mySearchResultAdapter);
                    searchResultViewModel.setPicsUri(finalResults);
                    mySearchResultAdapter.setData(finalResults);
                }
            }
        }

        //Futur Setter
        counterResult.setOnClickListener(redirection);

        return view;
    }

    //TODO : Retravailler la redirection vers annotate + tard
    private View.OnClickListener redirection = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigation.findNavController(view).navigate(R.id.action_navigation_searchresult_to_navigation_annotation);
        }
    };

    final class SearchResultAdapter extends BaseAdapter {
        private List<Uri> myData;
        private LayoutInflater myInflater;
        private Context myContext;

        public SearchResultAdapter(Context context, List<Uri> listData){
            this.myContext = context;
            if(listData == null){
                this.myData = new ArrayList<>();
            }else {
                this.myData = listData;
            }
            Log.i("DEBUG","My data constructor : "+myData);
            myInflater = LayoutInflater.from(context);
        }

        void setData(List<Uri> data){
            Log.i("DEBUG","data : "+data);
            if(this.myData.size() > 0){
                this.myData.clear();
            }
            this.myData.addAll(data);
            Log.i("DEBUG","My data : "+myData);
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
            ViewHolder holder;

            if(view == null){
                view = myInflater.inflate(R.layout.img,null);
                holder = new ViewHolder();
                holder.myPicUri = view.findViewById(R.id.imageView);
                view.setTag(holder);
            }else {
                holder = (ViewHolder)view.getTag();
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

