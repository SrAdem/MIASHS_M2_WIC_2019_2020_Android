package fr.miashs.uga.picannotation.ui.annotation;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import fr.miashs.uga.picannotation.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public TextView item_tittle;
    public ImageButton deleteButton;

    public ContactViewHolder(View itemView){
        super(itemView);

        item_tittle = (TextView)itemView.findViewById(R.id.item_textView);
        deleteButton = (ImageButton)itemView.findViewById(R.id.main_line_delete);
    }
}
