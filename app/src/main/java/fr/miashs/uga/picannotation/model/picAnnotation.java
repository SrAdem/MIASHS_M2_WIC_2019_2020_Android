package fr.miashs.uga.picannotation.model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class picAnnotation {

    @Embedded
    public EventAnnotation eventAnnot;
    @Relation(
            parentColumn="picUri",
            entityColumn = "picUri",
            entity = ContactAnnotation.class,
            projection = {"contactUri"}
    )
    public List<Uri> contactsUris;

    public Uri getPicUri(){return eventAnnot.getPicUri();}
    public Uri getEventUri(){return eventAnnot.getEventUri();}
    public List<Uri> getContactsUris(){return contactsUris;}

    @NonNull
    @Override
    public String toString() {
        String res =  eventAnnot.getPicUri()+","+eventAnnot.getEventUri()+","+contactsUris+"]";
        return res;
    }
}
