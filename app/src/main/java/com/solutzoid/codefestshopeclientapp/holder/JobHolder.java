package com.solutzoid.codefestshopeclientapp.holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.solutzoid.codefestshopeclientapp.FCMHelper.FcmClient;
import com.solutzoid.codefestshopeclientapp.R;
import com.solutzoid.codefestshopeclientapp.model.Admin;
import com.solutzoid.codefestshopeclientapp.model.Product;


public class JobHolder extends RecyclerView.ViewHolder{

    public TextView productNameTxt,productPriceTxt;
    public ImageView productImg;
    public Button buyProduct;
    public Product product;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference productCollection = db.collection("Product");;
    public CollectionReference adminCollection = db.collection("Admin");;

    public JobHolder(View itemView){
        super(itemView);

        productNameTxt = itemView.findViewById(R.id.t_v_productName);
        productPriceTxt = itemView.findViewById(R.id.t_v_productPrice);

        productImg = itemView.findViewById(R.id.i_v_productImg);

        buyProduct = itemView.findViewById(R.id.btn_buy_product);


        buyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference document = productCollection.document(product.getProductDocId());
                document.update("productState","Sold")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                DocumentReference document1 = adminCollection.document(product.getAdminDocId());
                                document1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Admin admin = documentSnapshot.toObject(Admin.class);
                                        Toast.makeText(itemView.getContext(), "Book Sold Out", Toast.LENGTH_LONG).show();
                                        new FcmClient().execute(admin.getFcmId(),"Book Name : "+product.getProductName()+" "+"Price :"+product.getProductPrice(),"Book Sold Out");
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        });
    }
}
