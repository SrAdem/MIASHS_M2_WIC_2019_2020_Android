package fr.miashs.uga.picannotation.database;

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

@Dao
public interface PicAnnotationsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPictureEvent(EventAnnotation a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPictureContact(ContactAnnotation a);

    @Query("DELETE FROM event_annotation")
    void deleteAll();

    @Transaction
    @Query("SELECT * FROM event_annotation")
    LiveData<List<PicAnnotation>> loadAnnotations();
}
