package com.solutzoid.codefestshopeclientapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.solutzoid.codefestshopeclientapp.holder.JobHolder;
import com.solutzoid.codefestshopeclientapp.model.Product;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    Button btn_logout;
    EditText searchText;
    ImageButton search_btn;
    ImageView profileImg;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView productLList;

    private CollectionReference productCollectionReference;

    private FirestoreRecyclerAdapter<Product, JobHolder> fsRecyclerAdapter;
    private FirestoreRecyclerAdapter<Product, JobHolder> fsRecyclerAdapter_1;

    private String image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();
        image_url = extras.getString("image_url");

        profileImg = findViewById(R.id.img_profile);

        btn_logout = findViewById(R.id.btn_log_out);
        searchText = findViewById(R.id.editTextSearch);

        search_btn = findViewById(R.id.img_btn_search);

        productLList = findViewById(R.id.recycle_view_product);
        productLList.setLayoutManager(new LinearLayoutManager(this));

        productCollectionReference = db.collection("Product");

        Picasso.with(HomeActivity.this).load(image_url).into(profileImg);

                setItem(null);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = searchText.getText().toString().trim();
                setItem(name);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fsRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fsRecyclerAdapter.startListening();
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent loginIntent = new Intent(HomeActivity.this,MainActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(loginIntent);
                        finish();
                    }
                });
        // [END auth_fui_signout]
    }

    public void setItem(String item) {

        if(item == null){

            FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(productCollectionReference, Product.class).build();

            fsRecyclerAdapter = new FirestoreRecyclerAdapter<Product,JobHolder>(recyclerOptions) {

                @NonNull
                @Override
                public JobHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
                    return new JobHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull JobHolder holder, int position, @NonNull Product model) {
                    holder.productNameTxt.setText("Name : "+model.getProductName());
                    holder.productPriceTxt.setText("Price : "+model.getProductPrice());

                    Picasso.with(HomeActivity.this).load(model.getProductImageUrl()).into(holder.productImg);

                    holder.product = model;

                }
            };
            productLList.setAdapter(fsRecyclerAdapter);
        }else {
            Query loadJobs = db.collection("Product").whereEqualTo("productName", item);

            FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(loadJobs, Product.class).build();

            fsRecyclerAdapter_1 = new FirestoreRecyclerAdapter<Product,JobHolder>(recyclerOptions) {

                @NonNull
                @Override
                public JobHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
                    return new JobHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull JobHolder holder, int position, @NonNull Product model) {
                    holder.productNameTxt.setText("Name : "+model.getProductName());
                    holder.productPriceTxt.setText("Price : "+model.getProductPrice());

                    Picasso.with(HomeActivity.this).load(model.getProductImageUrl()).into(holder.productImg);

                    holder.product = model;

                }
            };
            fsRecyclerAdapter_1.startListening();
            productLList.setAdapter(fsRecyclerAdapter_1);
        }

    }

}