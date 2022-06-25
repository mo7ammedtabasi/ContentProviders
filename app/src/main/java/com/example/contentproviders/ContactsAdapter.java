package com.example.contentproviders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contentproviders.databinding.CustomContactItemBinding;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private ArrayList<ContactItem> contactItems;

    public ContactsAdapter(ArrayList<ContactItem> contactItems) {
        this.contactItems = contactItems;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomContactItemBinding binding=CustomContactItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        ContactsViewHolder holder=new ContactsViewHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        ContactItem contactItem=contactItems.get(position);
        holder.binding.imageView.setImageURI(Uri.parse(contactItem.getPhoto()));
        if (contactItem.getName() != null){
            holder.binding.contactItemTvName.setText(contactItem.getName());

        }
        if (contactItem.getNumber() != null){
            holder.binding.contactItemTvNumber.setText(contactItem.getNumber());

        }
        if (contactItem.getEmail() != null){
            holder.binding.contactItemTvEmail.setText(contactItem.getEmail());

        }
        if (contactItem.getOtherDetails() != null){
            holder.binding.contactItemTvOtherDetails.setText(contactItem.getOtherDetails());

        }

        Bitmap image=null;
        if (!contactItem.getPhoto().equals("") && contactItem.getPhoto()!= null){
            image= BitmapFactory.decodeFile(contactItem.getPhoto());
            if (image!=null){
                holder.binding.imageView.setImageBitmap(image);
            }else {
                image=BitmapFactory.decodeResource(holder.binding.getRoot().getContext().getResources(),R.drawable.person);
                holder.binding.imageView.setImageBitmap(image);
            }

        }else {
            image=BitmapFactory.decodeResource(holder.binding.getRoot().getContext().getResources(),R.drawable.person);
            holder.binding.imageView.setImageBitmap(image);
        }
    }

    @Override
    public int getItemCount() {
        return contactItems.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder{
        CustomContactItemBinding binding;
        public ContactsViewHolder(@NonNull CustomContactItemBinding itemView) {
            super(itemView.getRoot());
//            binding=CustomContactItemBinding.bind(itemView);
                binding=itemView;
        }
    }
}



