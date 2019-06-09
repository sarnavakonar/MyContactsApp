package com.example.sarnavakonar.mycontacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{

    ArrayList<Contacts> contacts;
    mListener listener;

    public ContactsAdapter(ArrayList<Contacts> contacts, mListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.name.setText(contacts.get(position).getName());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);

            view.findViewById(R.id.ll_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.next(v, contacts.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public interface mListener{

        void next(View view, Contacts contact);
    }
}

