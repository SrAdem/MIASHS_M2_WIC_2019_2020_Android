package fr.miashs.uga.picannotation.ui.annotation;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.miashs.uga.picannotation.database.*;
import fr.miashs.uga.picannotation.model.*;

public class AnnotationRepository {

    private PicAnnotationsDao myPicAnnotationsDao;

    public AnnotationRepository(Application application){
        AnnotationDatabase db = AnnotationDatabase.getDatabase(application);
        myPicAnnotationsDao = db.getPicAnnotationDao();
    }
    //<------------ READ ------------>

    public LiveData<Integer> getCountContactAnnotExist(ContactAnnotation contactAnnot){
        return myPicAnnotationsDao.countContactAnnotationExist(contactAnnot.getPicUri(),contactAnnot.getContactUri());
    }

    public LiveData<Integer> getCountEventAnnotExist(Uri picUri){
        return myPicAnnotationsDao.countEventAnnotationExist(picUri);
    }


    public LiveData<PicAnnotation> getPicAnnotation(Uri picUri){return myPicAnnotationsDao.getPicAnnotation(picUri);}

    //<------------ INSERT/UPDATE ------------>

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

    //<------------ DELETE ------------>

    public void deleteAllAnnotations() {
        AnnotationDatabase.databaseWriteExecutor.execute(() -> {
            myPicAnnotationsDao.deleteAll();
        });
    }

    public void deleteAllContactAnnot(){
        AnnotationDatabase.databaseWriteExecutor.execute(() -> {
            myPicAnnotationsDao.deleteAllContactAnnot();
        });
    }

    public void deletePictureEventAnnot(Uri picUri){
        AnnotationDatabase.databaseWriteExecutor.execute(() -> {
            myPicAnnotationsDao.deletePicEventAnnotation(picUri);
        });
    }

    public void deletePictureContactAnnot(Uri picUri){
        AnnotationDatabase.databaseWriteExecutor.execute(() -> {
            myPicAnnotationsDao.deletePicContactAnnot(picUri);
        });
    }

    public void deleteContactAnnotation(Uri picUri, Uri contactUri){
        AnnotationDatabase.databaseWriteExecutor.execute(() -> {
            myPicAnnotationsDao.deleteContactAnnotation(picUri,contactUri);
        });
    }

}
