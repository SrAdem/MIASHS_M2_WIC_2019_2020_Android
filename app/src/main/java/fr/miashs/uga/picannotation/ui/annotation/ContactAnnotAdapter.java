package fr.miashs.uga.picannotation.ui.annotation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.miashs.uga.picannotation.R;

public class ContactAnnotAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<Uri> myContacts;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ContactAnnotAdapter(Context context, ArrayList contacts) {
        this.context = context;
        this.myContacts = contacts;
    }

    public void setContact(List<Uri> contacts){
        this.myContacts = contacts;
        this.notifyDataSetChanged();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return  new ContactViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listcontact_item,parent,false));
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Uri UriContact = myContacts.get(position);

        String name = retrieveContactName(UriContact);
        //Nom du contact
        holder.item_tittle.setText(name);

        //Bouton delete
        holder.deleteButton.setOnClickListener(view -> removeItem(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myContacts != null ? myContacts.size() : 0;
    }

    private void removeItem(int position){
        myContacts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,myContacts.size());
        //Log.i("DEBUG","Liste Uri contact length : "+this.getItemCount());
    }

    private String retrieveContactName(Uri uriContact){

        String contactName = null;

        // querying contact data store
        Cursor cursor = context.getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        return contactName;
    }
}
