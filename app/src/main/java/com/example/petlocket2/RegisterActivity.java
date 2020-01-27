package com.example.petlocket2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    EditText username, email, password;
    Button register;
    TextView txt_login;
    Spinner spinner1,spinner2,spinner3;
    String item;
    String item2;
    String item3;
    int itemid;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);


        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


        final String countries [] ={"","Egypt"};
        final String cities[] ={ "","Cairo" , "Alexandria" , "Giza" , "October" };
        final String areasCairo[]= {"","Rehab", "Nasr City" };
        final String areasAlex [] = {"","Somoha" , "Gleem"};
        final String areasOctober[] = {"","Sheikh Zayed"};
        final String areasGiza [] ={"","Pyramids"};

        ArrayAdapter<String>  dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,countries);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                item = countries[position];
                if(position == 1){
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_dropdown_item,cities);
                    spinner2.setAdapter(adapter1);
                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            item2 = cities[i];
                            if(i==1){
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_dropdown_item,areasCairo);
                                spinner3.setAdapter(adapter2);

                                spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        item3 = areasCairo[i];
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });


                            }
                            if(i==2){
                                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_dropdown_item,areasAlex);
                                spinner3.setAdapter(adapter3);
                                spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        item3 = areasAlex[i];
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }
                            if(i==3){
                                ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_dropdown_item,areasGiza);
                                spinner3.setAdapter(adapter4);
                                spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        item3 = areasGiza[i];
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }
                            if(i==4){
                                ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_dropdown_item,areasOctober);
                                spinner3.setAdapter(adapter5);
                                spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        item3 = areasOctober[i];
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();




                if(TextUtils.isEmpty(str_username)){
                    username.setError( "This is a required field" );
                    pd.dismiss();
                }

                if(TextUtils.isEmpty(str_email)){
                    email.setError( "This is a required field" );
                    pd.dismiss();
                }

                if(str_password.length() < 6 || TextUtils.isEmpty(str_password)){
                    password.setError( "Password must be more than 6 digits" );
                    pd.dismiss();
                }
                if(item.equals("") ){
                    TextView errorText = (TextView)spinner1.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Fill");
                    pd.dismiss();
                }

                else if(item2.equals("")){
                    TextView errorText2 = (TextView)spinner2.getSelectedView();
                    errorText2.setError("");
                    errorText2.setTextColor(Color.RED);
                    errorText2.setText("Fill");
                    pd.dismiss();
                }
                else if(item3.equals("")){
                    TextView errorText3 = (TextView)spinner3.getSelectedView();
                    errorText3.setError("");
                    errorText3.setTextColor(Color.RED);
                    errorText3.setText("Fill");
                    pd.dismiss();
                }

                else
                    register(str_username, str_email, str_password ,item ,item2,item3);

            }
        });
    }

    private void register(final String username, String email, String password, final String item , final String item2 , final String item3){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("country" , item);
                            hashMap.put("city" , item2);
                            hashMap.put("area", item3);
                            hashMap.put("imageUrl","");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
