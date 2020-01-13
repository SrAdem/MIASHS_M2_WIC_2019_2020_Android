package fr.miashs.uga.picannotation.ui.home;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.miashs.uga.picannotation.model.EventAnnotation;
import fr.miashs.uga.picannotation.ui.annotation.AnnotationRepository;

public class HomeViewModel extends AndroidViewModel {

    private HomeRepository myRepository;
    private LiveData<List<Uri>> myAllEventAnnotations;
    private List<Uri> mpicsUri;

    public HomeViewModel(Application app) {
        super(app);
        myRepository = new HomeRepository(app);
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

    //Récupère la liste de tous les EventAnnotation de la bdd
    public LiveData<List<Uri>> getAllEventAnnotations() {return myRepository.getAllEventAnnotations();}

}