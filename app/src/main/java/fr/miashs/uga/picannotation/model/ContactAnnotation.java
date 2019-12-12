package fr.miashs.uga.picannotation.model;


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "contact_annotation" , primaryKeys = {"picUri", "contactUri"})
public class ContactAnnotation {

    @NonNull
    private Uri picUri;
    @NonNull
    private Uri contactUri;

    public ContactAnnotation(Uri picUri, Uri contactUri){
        this.picUri=picUri;
        this.contactUri=contactUri;
    }

    public Uri getPicUri() {
        return picUri;
    }

    public Uri getContactUri() {
        return contactUri;
    }

    @NonNull
    @Override
    public String toString() {
        return "["+picUri+","+contactUri+"]";
    }
}
