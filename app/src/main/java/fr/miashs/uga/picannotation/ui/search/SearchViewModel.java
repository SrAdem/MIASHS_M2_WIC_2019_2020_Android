package fr.miashs.uga.picannotation.ui.search;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.miashs.uga.picannotation.model.PicAnnotation;

public class SearchViewModel extends AndroidViewModel {

    private SearchRepository searchRepository;
    private List<Uri> contacts = new ArrayList<>();
    private MutableLiveData<Uri> myEventUri = new MutableLiveData<>();
    private MediatorLiveData liveDataMerger = new MediatorLiveData();

    private LiveData<List<PicAnnotation>> resultEvent;
    private LiveData<List<PicAnnotation>> resultContact;

    public SearchViewModel(Application app) {
        super(app);
        searchRepository = new SearchRepository(app);
    }

    //Lance la recherche
    public LiveData<List<PicAnnotation>> search(){

        //Tests pour appeler la bonne fonction de requête
        if(getContacts().size() == 0 && getEventUri().getValue() != null){
            //Appel fonction de recherche par Event seulement
            return getResultSearchByGivenEvent(getEventUri().getValue());
        }else if(getContacts().size() != 0 && getEventUri().getValue() == null){
            //Appel fonction de recherche par Contact seulement
            return getResultSearchByGivenContacts(getContacts());
        }else {
            //Appel fonction de recherche par Event et Contact
            return getResultSearchByEventAndContact(getEventUri().getValue(),getContacts());
        }
    }

    //Fonction de recherche par Event
    public LiveData<List<PicAnnotation>> getResultSearchByGivenEvent(Uri eventUri){
        return searchRepository.getResultSearchByGivenEvent(getEventUri().getValue());
        //return searchRepository.getCountResultSearchGivenEvent(eventUri);
    }

    //Fonction de recherche par Contact
    public LiveData<List<PicAnnotation>> getResultSearchByGivenContacts(List<Uri> contacts){
        if(contacts.size() == 1){
            return searchRepository.getResultSearchByGivenOneContact(contacts.get(0));
        }else {
            return searchRepository.getResultSearchByGivenContacts(contacts);
        }
        //return searchRepository.getCountResultSearchGivenContacts(contacts);
    }

    //Fonction de recherche par Event et Contact
    public LiveData<List<PicAnnotation>> getResultSearchByEventAndContact(Uri eventUri, List<Uri> contacts){

        return searchRepository.getResultSearchByEventAndContacts(eventUri,contacts);
        //return searchRepository.getCountResultSearchEventAndContacts(eventUri, contacts);

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