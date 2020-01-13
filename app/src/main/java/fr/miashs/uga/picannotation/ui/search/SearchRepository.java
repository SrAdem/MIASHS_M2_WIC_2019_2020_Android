package fr.miashs.uga.picannotation.ui.search;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.miashs.uga.picannotation.database.AnnotationDatabase;
import fr.miashs.uga.picannotation.database.PicAnnotationsDao;

public class SearchRepository {

    private PicAnnotationsDao myPicAnnotationsDao;

    public SearchRepository(Application application){
        AnnotationDatabase db = AnnotationDatabase.getDatabase(application);
        myPicAnnotationsDao = db.getPicAnnotationDao();

    }

    public LiveData<List<Uri>> getResultSearchByGivenEvent(Uri eventUri){
        return myPicAnnotationsDao.searchByGivenEvent(eventUri);
    }

    public LiveData<List<Uri>> getResultSearchByGivenContacts(List<Uri> contacts){
        return myPicAnnotationsDao.searchByListContact(contacts, contacts.size());
    }

    public LiveData<List<Uri>> getResultSearchByEventAndContacts(Uri eventUri, List<Uri> contacts){
        return myPicAnnotationsDao.searchByEventAndContacts(eventUri, contacts, contacts.size());
    }
}
