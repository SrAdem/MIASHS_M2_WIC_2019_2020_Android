package fr.miashs.uga.picannotation;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.miashs.uga.picannotation.database.*;
import fr.miashs.uga.picannotation.model.*;

public class AnnotationRepository {

    private PicAnnotationsDao myPicAnnotationsDao;
    private LiveData<List<PicAnnotation>> myAllPicAnnotations;

    public AnnotationRepository(Application application){
        AnnotationDatabase db = AnnotationDatabase.getDatabase(application);
        myPicAnnotationsDao = db.getPicAnnotationDao();
        myAllPicAnnotations = myPicAnnotationsDao.loadAnnotations();
    }

    public LiveData<List<PicAnnotation>> getAllAnnotations() {
        return myAllPicAnnotations;
    }

    public void insertPictureEvent(EventAnnotation eventAnnot){
        AnnotationDatabase.databaseWriteExecutor.execute(() -> {
            myPicAnnotationsDao.insertPictureEvent(eventAnnot);
        });
    }

    public void insertPictureContact(ContactAnnotation contactAnnot){
        AnnotationDatabase.databaseWriteExecutor.execute(() -> {
            myPicAnnotationsDao.insertPictureContact(contactAnnot);
        });
    }
}
