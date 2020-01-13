package fr.miashs.uga.picannotation.ui.home;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.miashs.uga.picannotation.database.AnnotationDatabase;
import fr.miashs.uga.picannotation.database.PicAnnotationsDao;

public class HomeRepository {

    private PicAnnotationsDao myPicAnnotationsDao;

    public HomeRepository(Application application){
        AnnotationDatabase db = AnnotationDatabase.getDatabase(application);
        myPicAnnotationsDao = db.getPicAnnotationDao();
    }

    public LiveData<List<Uri>> getAllEventAnnotations() {return myPicAnnotationsDao.loadAllAnnotations();}
}
