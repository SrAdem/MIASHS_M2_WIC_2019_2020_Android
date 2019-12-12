package fr.miashs.uga.picannotation.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.miashs.uga.picannotation.model.*;

@Database(entities={EventAnnotation.class, ContactAnnotation.class}, version=1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AnnotationDatabase extends RoomDatabase {

    public abstract PicAnnotationsDao getPicAnnotationDao();

    private static volatile AnnotationDatabase INSTANCE;
    private static final int NUMBER_OF_TRHEADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_TRHEADS);

    public static AnnotationDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (AnnotationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AnnotationDatabase.class, "annotation_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
