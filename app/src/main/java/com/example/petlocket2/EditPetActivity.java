package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petlocket2.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class EditPetActivity extends AppCompatActivity {

    Uri imageuri;
    String myurl = "";
    StorageTask uploadtask;
    StorageReference storageReference;
    ImageView close;
    TextView add;
    String country="",city="",area="";
    Button inactiveButton;

    EditText name,age, location, price, service;
    Spinner serviceSpinner;
    String genderItem , item,item2,item3 , breedsItem , servicesItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);
        close= findViewById(R.id.close);
        add= findViewById(R.id.add);
        name=findViewById(R.id.nameBox);
        age=findViewById(R.id.ageBox);
        price=findViewById(R.id.priceBox);
        inactiveButton = findViewById(R.id.inactiveButton);

        serviceSpinner = findViewById(R.id.typeOfService);

        storageReference= FirebaseStorage.getInstance().getReference("pets");


        final String id = getIntent().getStringExtra("id");
        final String oldName = getIntent().getStringExtra("name");
        final String oldAge = getIntent().getStringExtra("age");
        final String oldPrice = getIntent().getStringExtra("price");
        final String oldService = getIntent().getStringExtra("service");

        name.setText(oldName);
        age.setText(oldAge);
        price.setText(oldPrice);
        if(oldService.toLowerCase().equals("adopt"))
            serviceSpinner.setSelection(0);
        else if (oldService.toLowerCase().equals("mate"))
            serviceSpinner.setSelection(1);
        else
            serviceSpinner.setSelection(2);

        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,services);

//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        serviceSpinner.setAdapter(dataAdapter);

//        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                servicesItem = services[i];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(NewPet.this,HomeActivity.class));
                finish();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPet();
            }
        });

        inactiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert= new AlertDialog.Builder(EditPetActivity.this).setTitle("Set Pet Inactive").setMessage("Are you sure you want to set your pet Inactive?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("pets").child(id).child("postPetActive");
                        reference.setValue("False");

                        finish();

                    }
                }).setNegativeButton("No",null).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });
    }

    private void editPet(){
        boolean register = true;

        final String id = getIntent().getStringExtra("id");

        final String str_name = name.getText().toString();
        final String str_age = age.getText().toString();
        final String str_price= price.getText().toString();
        final String str_serv= serviceSpinner.getSelectedItem().toString();

        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Adding...");
        progressDialog.show();

        if(TextUtils.isEmpty(str_name)){
            name.setError( "This is a required field" );
            register = false;
            progressDialog.dismiss();
        }
        if(TextUtils.isEmpty(str_age)){
            age.setError( "This is a required field");
            register = false;
            progressDialog.dismiss();
        }

        if(register) {

            DatabaseReference nameReference = FirebaseDatabase.getInstance().getReference("pets").child(id).child("postPetName");
            nameReference.setValue(str_name).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference ageReference = FirebaseDatabase.getInstance().getReference("pets").child(id).child("postPetAge");
                    ageReference.setValue(str_age);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference priceReference = FirebaseDatabase.getInstance().getReference("pets").child(id).child("postPetPrice");
                    priceReference.setValue(str_price);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("pets").child(id).child("postPetService");
                    serviceReference.setValue(str_serv);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    finish();
                }
            });
        }else{
            Toast.makeText(this, "Can't leave empty field",Toast.LENGTH_SHORT);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
