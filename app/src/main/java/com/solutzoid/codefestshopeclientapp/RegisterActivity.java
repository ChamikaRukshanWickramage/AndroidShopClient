package com.solutzoid.codefestshopeclientapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.solutzoid.codefestshopeclientapp.model.Customer;
import com.squareup.picasso.Picasso;

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText nicTxt,nameTxt,emailTxt,mobileTxt,addressTxt;
    Button registerButton, profile_btn;
    ImageView profileImageView;
    StorageReference storageReference;

    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;

    private int Profile_Pic_Req_Code = 105;

    Uri profileImageUri;
    private String selectedRbText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        String authId = bundle.getString("authId");
        String email = bundle.getString("email");
        String name = bundle.getString("name");

        profileImageView = findViewById(R.id.imageViewprofile);

        radioGroup = findViewById(R.id.radioGroup1);
        nicTxt = findViewById(R.id.editTextNic);
        nameTxt = findViewById(R.id.editTextName);
        emailTxt = findViewById(R.id.editTextEmail);
        mobileTxt = findViewById(R.id.editTextMobile);
        addressTxt = findViewById(R.id.editTextAddress);

        registerButton = findViewById(R.id.btn_sign_up);
        profile_btn = findViewById(R.id.btn_profile_pic);

        nameTxt.setText(name);
        emailTxt.setText(email);

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileChooser = new Intent();
                fileChooser.setAction(Intent.ACTION_GET_CONTENT);
                fileChooser.setType("image/*");
                startActivityForResult(Intent.createChooser(fileChooser, "Select Profile Photo"), Profile_Pic_Req_Code);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nic = nicTxt.getText().toString();
                String name = nameTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String mobile = mobileTxt.getText().toString();
                String address = addressTxt.getText().toString();

                // Customer(String authId, String name, String email, String mobile, String address, String userStatus)

                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) {
                    selectedRadioButton = findViewById(selectedRadioButtonId);
                     selectedRbText = selectedRadioButton.getText().toString();
                }

                Customer customer = new Customer();
                customer.setNic(nic);
                customer.setName(name);
                customer.setEmail(email);
                customer.setGender(selectedRbText);
                customer.setMobile(mobile);
                customer.setAddress(address);
                customer.setUserStatus("1");
                customer.setAuthId(authId);

                String profileImageUrlPath = "Customer_image_"+nic;
                setProfileImage(customer,profileImageUrlPath);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Profile_Pic_Req_Code){
            if(resultCode == RESULT_OK){
                profileImageUri = data.getData();
                Picasso.with(RegisterActivity.this).load(profileImageUri).into(profileImageView);
            }
        }

    }

    public void setProfileImage(Customer customer, String imgUrl){

        storageReference.child("CustomersImage").child(imgUrl).putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegisterActivity.this, "Customer Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Customer Image Upload Error", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.child("CustomersImage/"+imgUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        customer.setImageUrl(uri.toString());
                        registerCustomer(customer);
                    }
                });
            }
        });

    }

    public void registerCustomer(Customer customer) {

        db.collection("Customer").add(customer).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                String id = documentReference.getId();

                documentReference.update("googleId",id).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(RegisterActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}