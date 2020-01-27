package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petlocket2.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.net.URI;
import java.util.HashMap;

public class NewPet extends AppCompatActivity {

    Uri imageuri;
    String myurl="";
    StorageTask uploadtask;
    StorageReference storageReference;
    ImageView close,imageadded;
    TextView add;
    String country="",city="",area="";
    boolean flag = true;

    EditText name,ageyears, agemonths, breed, license, location, price,service;
    Spinner sex  , breedSpinner, serviceSpinner;
    String genderItem , breedsItem , servicesItem;
    ImageView star;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pet);
        close= findViewById(R.id.close);
        imageadded= findViewById(R.id.imageadded);
        add= findViewById(R.id.add);
        name=findViewById(R.id.nameBox);
        ageyears=findViewById(R.id.ageBox);
        agemonths = findViewById(R.id.monthsBox);
//        breed=findViewById(R.id.breedBox);
        license=findViewById(R.id.licenseBox);
        sex=findViewById(R.id.sexspinner);
//        location=findViewById(R.id.locationBox);
        price=findViewById(R.id.priceBox);
//        service=findViewById(R.id.serviceBox);

        breedSpinner = findViewById(R.id.breedSpinner);
        serviceSpinner = findViewById(R.id.typeOfService);

        storageReference= FirebaseStorage.getInstance().getReference("pets");


//        star = findViewById(R.id.favstar);
//        star.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("henaaa","henaa");
//                star.setImageResource(R.drawable.ic_star_yellow_24dp);
//
//            }
//        });

        final String gender[] = {"","Female" ,"Male"};
        final String breeds[] = {"","German" ,"Wolf" , "Lolo"};
        final String services[] = {"","Sell" ,"Adopt" , "Mate"};

        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user= dataSnapshot.getValue(Users.class);
                country=user.getCountry();
                city= user.getCity();
                area=user.getArea();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,gender);

        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(dataAdapter2);

        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderItem = gender[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,breeds);

        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breedSpinner.setAdapter(dataAdapter3);

        breedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                breedsItem = breeds[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,services);

        dataAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(dataAdapter4);

        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                servicesItem = services[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                addNewPet();
            }
        });

        imageadded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1).start(NewPet.this);

            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void addNewPet(){
        boolean register = true;

        String str_name = name.getText().toString();
        String str_age_years = ageyears.getText().toString();
        String str_age_months = agemonths.getText().toString();
        String str_license = license.getText().toString();
        String str_price= price.getText().toString();

        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Adding...");
        progressDialog.show();


        if(TextUtils.isEmpty(str_name)){
            name.setError( "This is a required field" );
            register = false;
            progressDialog.dismiss();
        }
        if(TextUtils.isEmpty(str_age_years) && TextUtils.isEmpty(str_age_months)){
            agemonths.setError( "This is a required field");
            ageyears.setError( "This is a required field");
            register = false;
            progressDialog.dismiss();
        }
        if(TextUtils.isEmpty(str_license)){
            license.setError( "This is a required field" );
            register = false;
            progressDialog.dismiss();
        }

        if(genderItem.equals("")){
            TextView errorText = (TextView)sex.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Fill");
            register = false;
            progressDialog.dismiss();
        }


        if(breedsItem.equals("")){
            TextView errorText = (TextView)breedSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Fill");
            register = false;
            progressDialog.dismiss();
        }




        if(register ==true) {


            if (imageuri != null) {
                final StorageReference filreference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageuri));
                uploadtask = filreference.putFile(imageuri);
                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filreference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloaduri = task.getResult();
                            myurl = downloaduri.toString();
                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pets");

                            String petId = databaseReference.push().getKey();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("postPetId", petId);
                            hashMap.put("postPetImage", myurl);
                            hashMap.put("postPetName", name.getText().toString());
                            hashMap.put("postPetAgeY", ageyears.getText().toString());
                            hashMap.put("postPetAgeM", agemonths.getText().toString());
                            hashMap.put("postPetBreed", breedsItem);
                            hashMap.put("postPetLicense", license.getText().toString());
                            hashMap.put("postPetSex", genderItem);
                            hashMap.put("postPetPrice", price.getText().toString());
                            hashMap.put("postPetService", servicesItem);
                            hashMap.put("postPetPublisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("postPetTimestamp", System.currentTimeMillis());
                            hashMap.put("postPetCountry", country);
                            hashMap.put("postPetCity", city);
                            hashMap.put("postPetArea", area);

                            if (servicesItem.equals(("")))
                                hashMap.put("postPetActive", "False");
                            else
                                hashMap.put("postPetActive", "True");

                            databaseReference.child(petId).setValue(hashMap);
                            progressDialog.dismiss();
//                        startActivity(new Intent(NewPet.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(NewPet.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewPet.this, "Failure Listner", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(NewPet.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }
        else {
            Toast.makeText(NewPet.this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== RESULT_OK){
            CropImage.ActivityResult res= CropImage.getActivityResult(data);
            imageuri= res.getUri();

            imageadded.setImageURI(imageuri);
        }else{
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(NewPet.this, HomeActivity.class));
            finish();
        }
    }
}
