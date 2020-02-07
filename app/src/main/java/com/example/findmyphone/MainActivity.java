package com.example.findmyphone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.example.findmyphone.Adapters.TrustedNumbersAdapter;
import com.example.findmyphone.DB_VM.AppViewModel;
import com.example.findmyphone.DB_VM.TrustedPerson;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int READ_CONTACT_PERMISSION_KEY = 1;
    private static final int READ_CONTACT_REQUEST_KEY = 2;
    private static final String ACTIVATE_FUNCTIONALITY = "active_receiver";


    SwitchMaterial mSwitchMaterial;
    TextView mAddContact;
    SharedPreferences mSharePreference;
    RecyclerView mRecycler;
    TrustedNumbersAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar())
                .setTitle(Html.fromHtml("<font color='#FFFFFF'>Find Me</font>"));

        mSwitchMaterial = findViewById(R.id.switch_active_preference);
        mAddContact = findViewById(R.id.add_contact_text);
        mSharePreference = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        adapter = new TrustedNumbersAdapter();
        ViewModelProviders.of(MainActivity.this)
                .get(AppViewModel.class).getAllTrusted().observe(
                        this, new Observer<List<TrustedPerson>>() {
            @Override
            public void onChanged(List<TrustedPerson> trustedPeople) {
                adapter.setList(trustedPeople);
            }
        });


        mRecycler = findViewById(R.id.trusted_numbers);
        mRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecycler.setSaveEnabled(true);
        mRecycler.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new CustomAsyncTask(MainActivity.this, "DELETE",adapter.getPerson(viewHolder.getAdapterPosition())).execute();
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mRecycler);


        mSwitchMaterial.setChecked(mSharePreference.getBoolean(ACTIVATE_FUNCTIONALITY, false));


        mSwitchMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean state = mSwitchMaterial.isChecked();
                mSwitchMaterial.setChecked(!state);

                SharedPreferences.Editor editor = mSharePreference.edit();
                editor.putBoolean(ACTIVATE_FUNCTIONALITY, state);
                editor.apply();

                mSwitchMaterial.setChecked(state);

            }
        });


        mAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_PICK);

                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivityForResult(intent, READ_CONTACT_REQUEST_KEY);
                }
                else{
                    checkPermission();
                }

            }
        });





    }

    private void unkillFunctionality() {
        mAddContact.setEnabled(true);
        mSwitchMaterial.setEnabled(true);
        mRecycler.setVisibility(View.VISIBLE);
    }

    private void killFunctionality() {
        mAddContact.setEnabled(false);
        mSwitchMaterial.setEnabled(false);
        mRecycler.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_CONTACT_REQUEST_KEY && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String number="";
            String name="";



            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.Contacts.DISPLAY_NAME};

            Cursor cursor = null;

            if (contactUri != null) {
                cursor = getContentResolver().query(contactUri, projection,
                        null, null, null);
            }
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);
                number = cursor.getString(numberIndex);
                // TODO something with the phone number

                int nameIndex = cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME);
                name = cursor.getString(nameIndex);
                // TODO something with the name

                new CustomAsyncTask(MainActivity.this,
                        contactUri,"INSERT", name, number.replaceAll(" ","")).execute();
            }


            if (cursor != null) {
                cursor.close();
            }
        }
    }





    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){



            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)){
                /*ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        READ_CONTACT_PERMISSION_KEY);*/
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        READ_CONTACT_PERMISSION_KEY);
            }

        }
        else {
            unkillFunctionality();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case READ_CONTACT_PERMISSION_KEY:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_PHONE_STATE)
                            == PackageManager.PERMISSION_GRANTED){
                        //Toast.makeText(getApplicationContext(), "Granted!", Toast.LENGTH_LONG).show();
                        unkillFunctionality();
                        Intent intent = new Intent(Intent.ACTION_PICK);

                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

                        if (intent.resolveActivity(getPackageManager()) != null)
                            startActivityForResult(intent, READ_CONTACT_REQUEST_KEY);
                    }
                    else{
                        killFunctionality();
                        //Toast.makeText(getApplicationContext(), "Denied!", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }
}