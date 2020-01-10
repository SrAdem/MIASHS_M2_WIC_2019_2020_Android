package fr.miashs.uga.picannotation.ui.annotation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;



import fr.miashs.uga.picannotation.R;

public class ContactAnnotAdapter extends RecyclerView.Adapter<ContactAnnotAdapter.ContactViewHolder> {

    private Context context;
    private List<Uri> myContacts = new ArrayList<>();
    private MutableLiveData<List<Uri>> myContactsLive = new MutableLiveData<>();
    private MutableLiveData<Uri> myContactDelete = new MutableLiveData<>();

    // Provide a suitable constructor (depends on the kind of dataset)
    public ContactAnnotAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return  new ContactViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listcontact_item,parent,false));
    }

    public void setContacts(List<Uri> contacts){
        myContacts.clear();
        myContacts.addAll(contacts);
        myContactsLive.setValue(myContacts);
        this.notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        private final TextView item_tittle;
        private final ImageButton deleteButton;
        int position;
        long contactID;

        public ContactViewHolder(View itemView){
            super(itemView);

            item_tittle = itemView.findViewById(R.id.item_textView);
            deleteButton = itemView.findViewById(R.id.main_line_delete);
        }

        public ItemDetailsLookup.ItemDetails getItemDetails(){
            return new ContactAnnotAdapter.ContactDetails(this);
        }
    }

    static final class ContactDetails extends ItemDetailsLookup.ItemDetails{
        ContactViewHolder selected;

        ContactDetails(ContactViewHolder holder){
            selected = holder;
        }

        @Override
        public int getPosition(){
            return selected.position;
        }

        @Nullable
        @Override
        public Object getSelectionKey(){
            return selected.contactID;
        }
    }

    //Remplace les champs de la view avec nos données
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Uri UriContact = myContacts.get(position);

        String name = retrieveContactName(UriContact);
        //Nom du contact
        holder.item_tittle.setText(name);

        //Position du contact
        holder.position = position;

        //Bouton delete
        holder.deleteButton.setOnClickListener(view -> {
            removeContact(UriContact);
        });
    }

    // Retourne la taille de notre liste
    @Override
    public int getItemCount() {
        return myContacts != null ? myContacts.size() : 0;
    }

    //Insère un contact dans notre liste UI
    public void addContact(Uri contactUri){
        if(!myContacts.contains(contactUri)){
            myContacts.add(contactUri);
            myContactsLive.setValue(myContacts);
            this.notifyDataSetChanged();
        }
    }

    //Supprime un contact dans notre liste UI et met à jour
    public void removeContact(Uri contactUri){
        myContactDelete.setValue(contactUri);

        myContacts.remove(contactUri);

        myContactsLive.setValue(myContacts);
        this.notifyDataSetChanged();
    }

    //Récupère l'Uri du contact supprimé
    public MutableLiveData<Uri> getMyContactDelete(){return myContactDelete;}

    //Récupère la liste des contacts UI
    public MutableLiveData<List<Uri>> getMyContactsLive(){return myContactsLive;}

    //Récupère le nom du Contact par son Uri
    private String retrieveContactName(Uri uriContact){

        String contactName = null;

        //Query du contact stocké
        Cursor cursor = context.getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        return contactName;
    }
}
