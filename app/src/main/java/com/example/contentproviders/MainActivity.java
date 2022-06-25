package com.example.contentproviders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.example.contentproviders.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static final int REQ_READ_CONTACTS=1;
    ArrayList<ContactItem> contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},REQ_READ_CONTACTS);
        }else {
            new MyTask().execute();
        }
    }

    class MyTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            readContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            binding.pb.setVisibility(View.GONE);
            populateDataIntoRecyclerView(contacts);

            Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("Range")
    private void readContacts() {

        contacts=new ArrayList<>();
      Uri uri=ContactsContract.Contacts.CONTENT_URI;
      Cursor cursor=getContentResolver().query(uri,null,null,null,
              ContactsContract.Contacts.DISPLAY_NAME+" ASC");
      if (cursor.moveToFirst()){
          do {
              Long id=cursor.getLong(cursor.getColumnIndex("_ID"));
              Uri cUri=ContactsContract.Data.CONTENT_URI;
              Cursor contactCursor=getContentResolver().query(cUri,null,ContactsContract.Data.CONTACT_ID+" =?",
                      new String[]{String.valueOf(id)},null);

              String displayName="";
              String nickName="";
              String homePhone="";
              String mobilPhone="";
              String workPhone="";
              String photoPath=""+R.drawable.person;
              byte[] photoByte=null;
              String homeEmail="";
              String workEmail="";
              String companyName="";
              String title="";
              String contactNumber="";
              String contactEmailAddress="";
              String contactOtherDetails="";
              if (contactCursor.moveToFirst()){
                  displayName=contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  do {
                      if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).equals
                              (ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)){
                            nickName=contactCursor.getString(contactCursor.getColumnIndex("data1"));
                            contactOtherDetails+=nickName+"\n";
                      }
                      if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).equals
                              (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){
                            switch (contactCursor.getInt(contactCursor.getColumnIndex("data2"))){
                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    homePhone=contactCursor.getString(contactCursor.getColumnIndex("data1"));
                                    contactNumber+="Home Phone : " + homePhone + "\n";
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    workPhone=contactCursor.getString(contactCursor.getColumnIndex("data1"));
                                    contactNumber+="Work Phone : " + workPhone + "\n";
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    mobilPhone=contactCursor.getString(contactCursor.getColumnIndex("data1"));
                                    contactNumber+="Mobil Phone : " + mobilPhone + "\n";
                                    break;
                            }
                      }

                      if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).equals
                              (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)){
                          switch (contactCursor.getInt(contactCursor.getColumnIndex("data2"))){
                              case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                                  homeEmail=contactCursor.getString(contactCursor.getColumnIndex("data1"));
                                  contactEmailAddress += "Home Email : " + homeEmail + "\n";
                                  break;
                              case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                                  workEmail=contactCursor.getString(contactCursor.getColumnIndex("data1"));
                                  contactEmailAddress += "Work Email : " + workEmail + "\n";
                                  break;

                          }
                      }


                      if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).equals
                              (ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)){
                          companyName=contactCursor.getString(contactCursor.getColumnIndex("data1")); // getCompany
                          // name
                          contactOtherDetails += "Company Name : " + companyName + "\n";
                          title = contactCursor.getString(contactCursor.getColumnIndex("data4")); // getCompany
                          // title
                          contactOtherDetails += "Title :" + title + " \n";


                      }

                      if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).equals
                              (ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)){
                          photoByte = contactCursor.getBlob(contactCursor.getColumnIndex("data15"));

                          if (photoByte != null){
                              Bitmap bitmap=BitmapFactory.decodeByteArray(photoByte,0,photoByte.length);
                              File cacheDirectory=getBaseContext().getCacheDir();
                              File temp=new File(cacheDirectory.getPath()+"moh"+id+".png");

                              try {
                                  FileOutputStream fileOutputStream=new FileOutputStream(temp);
                                  bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                                  fileOutputStream.flush();
                                  fileOutputStream.close();
                              } catch (IOException e) {
                                  e.printStackTrace();
                              }
                              photoPath=temp.getPath();
                          }
                      }

                  }
                  while (contactCursor.moveToNext());
              }
              contacts.add(new ContactItem(Long.toString(id),displayName,contactNumber,contactEmailAddress,photoPath,contactOtherDetails));

          }while (cursor.moveToNext());
      }

    }

    private void populateDataIntoRecyclerView(ArrayList<ContactItem> item){
        ContactsAdapter adapter=new ContactsAdapter(item);
        binding.mainRv.setAdapter(adapter);
        binding.mainRv.setLayoutManager(new LinearLayoutManager(this));
        binding.mainRv.setHasFixedSize(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQ_READ_CONTACTS && grantResults.length>0){
            new MyTask().execute();
        }
    }
}


//    @SuppressLint("Range")
//    private void readContacts() {
//        contacts=new ArrayList<>();
//        Uri uri= ContactsContract.Contacts.CONTENT_URI;
//        Cursor cursor=getContentResolver().query(uri,null,null,null,
//                ContactsContract.Contacts.DISPLAY_NAME+" ASC");
//        if (cursor.moveToFirst()){
//            do {
//                @SuppressLint("Range")
//                long id=cursor.getLong(cursor.getColumnIndex("_ID"));
//                Uri cUri=ContactsContract.Contacts.CONTENT_URI;
//                Cursor contactCursor=getContentResolver().query(cUri,null,
//                        ContactsContract.Data.CONTACT_ID+" =?",new String[]{String.valueOf(id)},null);
//
//                String displayName="";
//                String nickName="";
//                String homePhone="";
//                String mobilPhone="";
//                String workPhone="";
//                String photoPath=""+R.drawable.person;
//                byte[] photoByte=null;
//                String homeEmail="";
//                String workEmail="";
//                String companyName="";
//                String title="";
//                String contactNumber="";
//                String contactEmailAddress="";
//                String contactOtherDetails="";
//
//                if (contactCursor.moveToFirst()){
//                    displayName=contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//
//                    do {
//                        if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).
//                                equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)){
//                            nickName=contactCursor.getString(contactCursor.getColumnIndex("data1"));
//                            contactOtherDetails += nickName+"\n";
//                        }
//
//                        if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).
//                                equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){
//                            switch (contactCursor.getInt(contactCursor.getColumnIndex("data2"))){
//                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                                    homePhone=contactCursor.getString(contactCursor.getColumnIndex("data1"));
//                                    contactNumber+="Home phone : "+homePhone+"\n";
//                                    break;
//                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                                    workPhone=contactCursor.getString(contactCursor.getColumnIndex("data2"));
//                                    contactNumber+="Work phone : "+workPhone+"\n";
//                                    break;
//                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                                    homePhone=contactCursor.getString(contactCursor.getColumnIndex("data3"));
//                                    contactNumber+="Mobil phone : "+mobilPhone+"\n";
//                                    break;
//                            }
//                        }
//
//                        if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).
//                                equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)){
//                            switch (contactCursor.getInt(contactCursor.getColumnIndex("data2"))){
//                                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
//                                    homeEmail=contactCursor.getString(contactCursor.getColumnIndex("data1"));
//                                    contactEmailAddress += "Home email : "+homeEmail+"\n";
//                                    break;
//                                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
//                                    workEmail=contactCursor.getString(contactCursor.getColumnIndex("data2"));
//                                    contactEmailAddress += "Work email : "+workEmail+"\n";
//                                    break;
//                            }
//                        }
//
//                        if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).
//                                equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)){
//
//                            companyName=contactCursor.getString(contactCursor.getColumnIndex("data1"));
//                            contactOtherDetails +="Company Name :"+ companyName+"\n";
//
//                            title=contactCursor.getString(contactCursor.getColumnIndex("data1"));
//                            contactOtherDetails +="Title :"+ title+"\n";
//                        }
//
//                        if (contactCursor.getString(contactCursor.getColumnIndex("mimetype")).
//                                equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
//
//                            photoByte = contactCursor.getBlob(contactCursor.getColumnIndex("data15"));
//
//                            if(photoByte != null){
//                                Bitmap bitmap= BitmapFactory.decodeByteArray(photoByte,0,photoByte.length);
//                                File cacheDirectory=getBaseContext().getCacheDir();
//                                File temp=new File(cacheDirectory.getPath()+"/_androhub"+id+".png");
//
//                                try {
//                                    FileOutputStream fileOutputStream=new FileOutputStream(temp);
//                                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
//                                    fileOutputStream.flush();
//                                    fileOutputStream.close();
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                photoPath=temp.getPath();
//
//                            }
//                        }
//
//                    }while (contactCursor.moveToNext());
//                    contacts.add(new ContactItem(Long.toString(id),displayName,contactNumber,contactEmailAddress,photoPath,contactOtherDetails));
//
//                }
//
//            }while (cursor.moveToNext());
//        }
//
//    }