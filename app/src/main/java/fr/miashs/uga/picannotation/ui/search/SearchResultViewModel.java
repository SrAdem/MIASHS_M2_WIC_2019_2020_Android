package fr.miashs.uga.picannotation.ui.search;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultViewModel extends AndroidViewModel {

    private List<Uri> picsUri;

    public SearchResultViewModel(Application application){
        super(application);
        picsUri = new ArrayList<>();
    }

    public void setPicsUri(List<Uri> picsUri) {
        if(picsUri.size() != 0){
            picsUri.clear();
        }
        picsUri.addAll(picsUri);
    }

    public List<Uri> getPicsUri() {
        return picsUri;
    }
}
