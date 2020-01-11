package fr.miashs.uga.picannotation.ui.search;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import fr.miashs.uga.picannotation.R;

public class SearchResultFragment extends Fragment {

    private TextView counterResult;
    private ArrayList<Uri> resultsParse;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resultsearch, container, false);

        //Futur composants graphiques
        counterResult = view.findViewById(R.id.counter_results);

        //Futur Setter
        counterResult.setOnClickListener(redirection);

        resultsParse = getArguments().getParcelableArrayList("Result");

        for(Uri uri : resultsParse){
            Log.i("DEBUG","Uri Image Resultats : "+uri);
        }
        counterResult.setText("Resultats obtenus : "+resultsParse.size());
        return view;
    }

    private View.OnClickListener redirection = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigation.findNavController(view).navigate(R.id.action_navigation_searchresult_to_navigation_annotation);
        }
    };
}
