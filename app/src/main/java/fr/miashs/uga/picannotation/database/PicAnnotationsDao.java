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

    //<-------- GET ALL -------->
    @Transaction
    @Query("SELECT picUri FROM event_annotation UNION SELECT picUri FROM contact_annotation")
    LiveData<List<Uri>> loadAllAnnotations();

    //<-------- SEARCH -------->

    @Transaction
    @Query("SELECT * FROM event_annotation WHERE picUri=:picUri")
    LiveData<PicAnnotation> getPicAnnotation(Uri picUri);

    @Transaction
    @Query("SELECT picUri FROM event_annotation WHERE eventUri=:eventUri")
    LiveData<List<Uri>> searchByGivenEvent(Uri eventUri);

    @Transaction
    @Query("SELECT DISTINCT picUri FROM contact_annotation WHERE contactUri IN(:contacts) GROUP By picUri HAVING COUNT(picUri) == :sizeList")
    LiveData<List<Uri>> searchByListContact(List<Uri> contacts, Integer sizeList);

    @Transaction
    @Query("SELECT picUri FROM event_annotation WHERE eventUri=:eventUri " +
            "INTERSECT " +
            "SELECT DISTINCT picUri FROM contact_annotation WHERE contactUri IN(:contacts) GROUP By picUri HAVING COUNT(picUri) == :sizeList")
    LiveData<List<Uri>> searchByEventAndContacts(Uri eventUri, List<Uri> contacts, Integer sizeList);

    //<-------- COUNTER -------->

    @Transaction
    @Query("SELECT COUNT(*) FROM contact_annotation WHERE picUri=:picUri AND contactUri=:contactUri")
    LiveData<Integer> countContactAnnotationExist(Uri picUri, Uri contactUri);

    @Transaction
    @Query("SELECT COUNT(*) FROM event_annotation WHERE picUri=:picUri")
    LiveData<Integer> countEventAnnotationExist(Uri picUri);
}
