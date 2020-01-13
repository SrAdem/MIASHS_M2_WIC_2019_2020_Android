package fr.miashs.uga.picannotation.ui.search;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultViewModel extends AndroidViewModel {

    private List<Uri> mpicsUri;

    public SearchResultViewModel(Application application){
        super(application);
        mpicsUri = new ArrayList<>();
    }

    public void setPicsUri(List<Uri> picsUri) {
        if(mpicsUri.size() != 0){
            mpicsUri.clear();
        }
        mpicsUri.addAll(picsUri);
    }

    public List<Uri> getPicsUri() {
        return mpicsUri;
    }
}
