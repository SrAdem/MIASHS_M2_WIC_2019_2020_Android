package fr.miashs.uga.picannotation.ui.annotation;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import fr.miashs.uga.picannotation.model.ContactAnnotation;
import fr.miashs.uga.picannotation.model.EventAnnotation;
import fr.miashs.uga.picannotation.model.PicAnnotation;

public class AnnotationViewModel extends AndroidViewModel {

    private AnnotationRepository myRepository;
    private LiveData<List<PicAnnotation>> myAllAnnotations;
    private MutableLiveData<List<Uri>> contacts;

    public AnnotationViewModel(Application app) {
        super(app);
        myRepository = new AnnotationRepository(app);
        contacts = new MutableLiveData<>();
        contacts.setValue(new ArrayList<>());
        myAllAnnotations = myRepository.getAllAnnotations();
    }

    //public LiveData<List<PicAnnotation>> getAllAnnotations() { return myAllAnnotations;}

    public LiveData<List<Uri>> getAllContact() { return contacts;}

    public void insertPictureEvent(EventAnnotation eventAnnot){
        myRepository.insertPictureEvent(eventAnnot);
    }

    public void insertContact(Uri text){
        //Insert un contact test
        Log.i("DEBUG", "On ajoute l'Uri d'un contact à notre liste : "+text);
        if(!contacts.getValue().contains(text)){
            contacts.getValue().add(text);
            contacts.postValue(contacts.getValue());
        }
        //Log.i("DEBUG","Liste Uri contact length : "+contacts.getValue().size());
    }

    public void insertPictureContact(ContactAnnotation contactAnnot){
        myRepository.insertPictureContact(contactAnnot);
    }

}