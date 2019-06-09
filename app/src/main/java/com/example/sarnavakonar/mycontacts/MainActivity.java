package com.example.sarnavakonar.mycontacts;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ArrayList<Contacts> contacts;
    ContactsAdapter contactsAdapter;
    RecyclerView recyclerView;
    String[] Permissions = {Manifest.permission.READ_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv);
        contacts = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Syncing your contacts...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        contactsAdapter = new ContactsAdapter(contacts, new ContactsAdapter.mListener() {
            @Override
            public void next(View view, Contacts contact) {

                Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                intent.putExtra("contact", contact);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactsAdapter);

        if(!hasPermissions(this, Permissions)){

            ActivityCompat.requestPermissions(this, Permissions, 1);
        }
        else {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    readContacts();
                }
            });
            thread.start();
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what == 1){

                contactsAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
            return true;
        }
    });

    public static boolean hasPermissions(Context context, String... permissions){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case 1:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            readContacts();
                        }
                    });
                    thread.start();
                }
        }
    }


    public void readContacts(){

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Log.e("TAG","name : id - " + name + " : " + id);

                    contacts.add(new Contacts(name, id));
                }
            }

            handler.sendEmptyMessage(1);
        }
    }
}
