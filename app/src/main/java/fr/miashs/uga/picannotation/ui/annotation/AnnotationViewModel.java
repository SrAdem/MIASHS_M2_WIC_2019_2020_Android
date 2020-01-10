package fr.miashs.uga.picannotation.ui.annotation;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import fr.miashs.uga.picannotation.model.ContactAnnotation;
import fr.miashs.uga.picannotation.model.EventAnnotation;
import fr.miashs.uga.picannotation.model.PicAnnotation;

public class AnnotationViewModel extends AndroidViewModel {

    private AnnotationRepository myRepository;

    private LiveData<List<EventAnnotation>> myAllEventAnnotations;
    private LiveData<List<ContactAnnotation>> myAllContactAnnotations;
    private LiveData<Integer> countEventAnnotation;

    private List<Uri> myContact = new ArrayList<>();
    private MutableLiveData<Uri> myPicUri = new MutableLiveData<>();
    private MutableLiveData<Uri> myEventUri = new MutableLiveData<>();

    public AnnotationViewModel(Application app) {
        super(app);
        myRepository = new AnnotationRepository(app);

        myAllEventAnnotations = myRepository.getAllEventAnnotations();
        myAllContactAnnotations = myRepository.getAllContactAnnotations();
        countEventAnnotation = myRepository.getCountEventAnnotation();
    }

    //<------------ DELETE ------------>

    //Vide la bdd
    //Pas vraiment utile
    public void deleteAllAnnotations() {myRepository.deleteAllAnnotations();}

    public void deleteAllContactAnnot(){myRepository.deleteAllContactAnnot();}


    //Supprime l'EventAnnotation d'une image
    public void deletePicEventAnnotation(){
        myRepository.deletePictureEventAnnot(getPicUri());
    }

    //Supprime les ContactAnnotation d'une image
    public void deletePicContactAnnotation(){
        myRepository.deletePictureContactAnnot(getPicUri());
    }

    //Supprime le ContactAnnotation
    public void deleteContactAnnotation(Uri picUri, Uri contactUri){
        myRepository.deleteContactAnnotation(picUri, contactUri);
    }

    //<------------ READ ------------>

    //Vérifie s'il existe un ContactAnnotation pour l'image choisie et le contact choisi
    //1 -> Existe | 0 -> N'existe pas
    public LiveData<Integer> getCountContactAnnotExist(Uri picUri, Uri contactUri){
       return myRepository.getCountContactAnnotExist(new ContactAnnotation(picUri,contactUri));
    }

    //Vérifie s'il existe un EventAnnotation pour l'image choisie
    //1 -> Existe | 0 -> N'existe pas
    public LiveData<Integer> getCountEventAnnotExist(Uri picUri){
        return myRepository.getCountEventAnnotExist(picUri);
    }

    //Récupère l'annotation correspondant à l'uri de l'image
    public LiveData<PicAnnotation> getPicAnnotation(Uri picUri){return myRepository.getPicAnnotation(picUri);}

    //Setter de la liste de contact data
    public void setContacts(List<Uri> contacts){myContact = contacts;}

    //Set notre PicUri et vérifie si déjà présente
    void setPicUri(Uri picUri){myPicUri.setValue(picUri);}

    //Récupère l'URI de la photo
    public Uri getPicUri(){return myPicUri.getValue();}

    //Set notre EventUri
    void setEventUri(Uri eventUri){myEventUri.setValue(eventUri);}

    //Récupère l'URI de l'event
    public MutableLiveData<Uri> getEventUri(){return myEventUri;}

    //Récupère la liste de tous les EventAnnotation de la bdd
    //TODO : Utiliser dans HomeFragment
    public LiveData<List<EventAnnotation>> getAllEventAnnotations() {return myAllEventAnnotations;}

    //Récupère la liste de tous les ContactAnnotation de la bdd
    //TODO : Utiliser dans SearchFragment | Useless
    public LiveData<List<ContactAnnotation>> getAllContactAnnotations() {return myAllContactAnnotations;}

    //Compte le nombre d'entrées dans EventAnnotation
    //TODO : Utiliser dans HomeFragment
    public LiveData<Integer> getCountEventAnnotation() {return countEventAnnotation;}

    //<------------ INSERT/UPDATE ------------>

    //Insère un contact dans notre liste Data
    public void addContact(Uri contact){
        if(!myContact.contains(contact)){
            myContact.add(contact);
        }
    }

    //Insère une entrée dans la table ContactAnnotation
    public void insertPictureContact(ContactAnnotation contactAnnot){
        myRepository.insertPictureContact(contactAnnot);
    }

    //Insère une entrée dans la table EventAnnotation
    public void insertPictureEvent(EventAnnotation eventAnnot){
        myRepository.insertPictureEvent(eventAnnot);
    }

    //Sauvegarde les informations lors d'une annotation
    void save() {
        Log.i("DEBUG","Ajout/Update d'une Annotation");

        //Ajoute un EventAnnotation dans notre bdd
        //Si conflit on remplace avec la nouvelle valeur
        insertPictureEvent(new EventAnnotation(getPicUri(),getEventUri().getValue()));

        //Pour chaque contact dans notre MutableLiveData<List> contacts, ajoute un ContactAnnotation dans notre bdd
        for(Uri contact : myContact){
            insertPictureContact(new ContactAnnotation(getPicUri(),contact));
        }
        Toast.makeText(getApplication().getApplicationContext(),"Annotation sauvegardée !",Toast.LENGTH_SHORT).show();
    }
}