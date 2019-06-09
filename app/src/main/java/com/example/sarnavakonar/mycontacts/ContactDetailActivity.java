package com.example.sarnavakonar.mycontacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.sarnavakonar.mycontacts.databinding.ActivityContactDetailBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ContactDetailActivity extends AppCompatActivity {

    Contacts contact;
    String id, email="", phone="";
    Bitmap bitmap;
    ActivityContactDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_detail);

        contact = (Contacts) getIntent().getSerializableExtra("contact");

        id = contact.getId();
        binding.name.setText(contact.getName());

        readContacts();

        bitmap = BitmapFactory.decodeStream(openPhoto(id));
        if(bitmap != null) {
            binding.img.setImageBitmap(bitmap);
        }
    }

    public void goBack(View view){
        onBackPressed();
    }

    public void readContacts(){

        ContentResolver cr = getContentResolver();

        //phone number
        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{id}, null);

        while (pCur.moveToNext()) {
            String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.e("TAG","phone :"+ phone);

            if(phone.contains("-")){
                phone = phone.replace("-","");
            }
            if(phone.contains(" ")){
                phone = phone.replace(" ", "");
            }

            if(!TextUtils.isEmpty(phone) && !this.phone.contains(phone)) {

                if(!TextUtils.isEmpty(this.phone)){

                    this.phone += "\n";
                }

                this.phone += phone;
            }
        }
        pCur.close();

        binding.number.setText(phone);

        //email
        Cursor emailCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{id}, null);

        while (emailCur.moveToNext()) {

            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            Log.e("TAG","Email " + email);

            if(!TextUtils.isEmpty(email) && !this.email.contains(email)) {

                if(!TextUtils.isEmpty(this.email)){

                    this.email += "\n";
                }

                this.email += email;
            }
        }
        emailCur.close();

        if(!TextUtils.isEmpty(email)){

            binding.llEmail.setVisibility(View.VISIBLE);
            binding.email.setText(email);
        }


        // note
        String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] noteWhereParams = new String[]{id,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
        Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
        if (noteCur.moveToFirst()) {

            String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
            Log.e("TAG", "Note " + note);

            if(!TextUtils.isEmpty(note)){

                binding.llNote.setVisibility(View.VISIBLE);
                binding.note.setText(note);
            }

        }
        noteCur.close();

        // relation
        String relationWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] relationWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE};

        Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,null, relationWhere, relationWhereParams, null);

        if (imCur.moveToFirst()) {

            String relation = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Relation.DATA));
            Log.e("TAG", "Relation "+relation);

            if(!TextUtils.isEmpty(relation)){

                binding.llRelation.setVisibility(View.VISIBLE);
                binding.relation.setText(relation);
            }
        }
        imCur.close();

        // website
        String websiteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] websiteWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE};

        Cursor webCur = cr.query(ContactsContract.Data.CONTENT_URI,null, websiteWhere, websiteWhereParams, null);

        if (webCur.moveToFirst()) {

            String website = webCur.getString(webCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.DATA));
            Log.e("TAG", "Website "+website);

            if(!TextUtils.isEmpty(website)){

                binding.llWebsite.setVisibility(View.VISIBLE);
                binding.website.setText(website);
            }
        }
        webCur.close();

        // company
        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] orgWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

        Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,null, orgWhere, orgWhereParams, null);

        if (orgCur.moveToFirst()) {

            String company = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
            Log.e("TAG", "orgName : "+ company);

            if(!TextUtils.isEmpty(company)){

                binding.llCompany.setVisibility(View.VISIBLE);
                binding.company.setText(company);
            }
        }
        orgCur.close();
    }

    public InputStream openPhoto(String id) {

        long contactId = Long.parseLong(id);

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        Cursor cursor = getContentResolver().query(
                photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO},
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

}
