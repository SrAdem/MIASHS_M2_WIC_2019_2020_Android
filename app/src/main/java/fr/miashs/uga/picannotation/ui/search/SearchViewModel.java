package fr.miashs.uga.picannotation.ui.search;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private List<Uri> contacts = new ArrayList<>();
    private MutableLiveData<Uri> myEventUri = new MutableLiveData<>();

    public SearchViewModel(Application app) {
        super(app);
    }

    //Lance la recherche
    public void search(){
        Log.i("DEBUG","Taille Liste de contacts actuelle : "+contacts.size());
        Log.i("DEBUG","Event Uri actuel : "+getEventUri().getValue());
        //TODO : Faire les tests pour appeler la bonne fonction requête
        if(contacts.size() > 0 && getEventUri().getValue() == null){
            //TODO : Fonction de recherche par Contact seulement
        }
        if(contacts.size() == 0 && getEventUri().getValue() != null){
            //TODO : Fonction de recherche par Event seulement
        }
        if(contacts.size() > 0 && getEventUri().getValue() != null){
            //TODO : Fonction de recherche par Event et Contact
        }
    }

    //Ajoute un contact dans contacts
    public void insertContact(Uri contact){
        if(!contacts.contains(contact)){
            contacts.add(contact);
        }
    }

    //Supprime un contact dans contacts
    public void removeContact(Uri contact){
        contacts.remove(contact);
    }

    //Récupère la liste de contacts
    public List<Uri> getContacts(){return contacts;}

    //Récupère Event Uri
    public MutableLiveData<Uri> getEventUri(){return myEventUri;}

    //Setter de Event Uri
    public void setEventUri(Uri eventUri){myEventUri.setValue(eventUri);}


}