package fr.miashs.uga.picannotation.ui.search;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.miashs.uga.picannotation.database.AnnotationDatabase;
import fr.miashs.uga.picannotation.database.PicAnnotationsDao;
import fr.miashs.uga.picannotation.model.PicAnnotation;

public class SearchRepository {

    private PicAnnotationsDao myPicAnnotationsDao;
    private LiveData<List<PicAnnotation>> resultSearchByGivenEvent;
    private LiveData<List<PicAnnotation>> resultSearchByGivenContacts;

    private LiveData<Integer> countResultSearchGivenEvent;
    private LiveData<Integer> countResultSearchGivenContacts;

    public SearchRepository(Application application){
        AnnotationDatabase db = AnnotationDatabase.getDatabase(application);
        myPicAnnotationsDao = db.getPicAnnotationDao();

    }

    public LiveData<List<PicAnnotation>> getResultSearchByGivenEvent(Uri eventUri){
        return myPicAnnotationsDao.searchByGivenEvent(eventUri);
    }

    public LiveData<List<PicAnnotation>> getResultSearchByGivenOneContact(Uri contactUri){
        return myPicAnnotationsDao.searchByOneContact(contactUri);
    }

    public LiveData<List<PicAnnotation>> getResultSearchByGivenContacts(List<Uri> contacts){
        return myPicAnnotationsDao.searchByListContact(contacts);
    }

    public LiveData<List<PicAnnotation>> getResultSearchByEventAndContacts(Uri eventUri, List<Uri> contacts){
        return myPicAnnotationsDao.searchByEventAndContacts(eventUri, contacts);
    }

    public LiveData<Integer> getCountResultSearchGivenEvent(Uri eventUri){
        return myPicAnnotationsDao.countResultSearchByGivenEvent(eventUri);
    }

    public LiveData<Integer> getCountResultSearchGivenContacts(List<Uri> contacts){
        return myPicAnnotationsDao.countResultSearchByListContact(contacts);
    }

    public LiveData<Integer> getCountResultSearchEventAndContacts(Uri eventUri, List<Uri> contacts){
        return myPicAnnotationsDao.countResultSearchByEventAndContacts(eventUri, contacts);
    }
}
