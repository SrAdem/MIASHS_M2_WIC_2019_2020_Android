package fr.miashs.uga.picannotation.database;

import android.net.Uri;

import fr.miashs.uga.picannotation.model.*;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import fr.miashs.uga.picannotation.model.ContactAnnotation;
import fr.miashs.uga.picannotation.model.EventAnnotation;
import fr.miashs.uga.picannotation.model.PicAnnotation;

@Dao
public interface PicAnnotationsDao {

    //<------------ INSERT/UPDATE ------------>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPictureEvent(EventAnnotation a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPictureContact(ContactAnnotation a);

    //<------------ DELETE ------------>

    @Query("DELETE FROM event_annotation")
    void deleteAll();

    @Query("DELETE FROM contact_annotation")
    void deleteAllContactAnnot();

    @Query("DELETE FROM event_annotation WHERE picUri=:picUri")
    void deletePicEventAnnotation(Uri picUri);

    @Query("DELETE FROM contact_annotation WHERE picUri=:picUri")
    void deletePicContactAnnot(Uri picUri);

    @Query("DELETE FROM contact_annotation WHERE picUri=:picUri AND contactUri=:contactUri")
    void deleteContactAnnotation(Uri picUri, Uri contactUri);

    //<------------ READ ------------>

    @Transaction
    @Query("SELECT * FROM event_annotation")
    LiveData<List<EventAnnotation>> loadEventAnnotations();

    @Transaction
    @Query("SELECT * FROM contact_annotation")
    LiveData<List<ContactAnnotation>> loadContactAnnotations();

    @Transaction
    @Query("SELECT * FROM event_annotation WHERE picUri=:picUri")
    LiveData<PicAnnotation> getPicAnnotation(Uri picUri);

    @Transaction
    @Query("SELECT COUNT(*) FROM contact_annotation WHERE picUri=:picUri AND contactUri=:contactUri")
    LiveData<Integer> countContactAnnotationExist(Uri picUri, Uri contactUri);

    @Transaction
    @Query("SELECT COUNT(*) FROM event_annotation WHERE picUri=:picUri")
    LiveData<Integer> countEventAnnotationExist(Uri picUri);

    @Transaction
    @Query("SELECT COUNT(*) FROM event_annotation")
    LiveData<Integer> countEventAnnotation();
}
