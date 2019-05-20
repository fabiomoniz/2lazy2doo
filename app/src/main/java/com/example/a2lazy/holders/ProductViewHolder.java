package com.example.a2lazy.holders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a2lazy.R;
import com.example.a2lazy.TaskListActivity;
import com.example.a2lazy.models.NotificationModel;
import com.example.a2lazy.models.ProductModel;
import com.example.a2lazy.models.TaskListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    private TextView productNameTextView;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productNameTextView = itemView.findViewById(R.id.product_name_text_view);
    }

    public void setProduct(final Context context, final View taskListViewFragment, final String userEmail, final String userName, final TaskListModel taskModel, ProductModel productModel) {
        final String taskListId = taskModel.getTaskListId();
        final String taskListName = taskModel.getTaskListName();
        String productId = productModel.getProductId();
        final String productName = productModel.getProductName();
        final Boolean izCompleted = productModel.getIzCompleted();
        productNameTextView.setText(productName);

        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final DocumentReference productIdRef = rootRef.collection("products").document(taskListId)
                .collection("taskListProducts").document(productId);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<>();
                if(izCompleted) {
                    map.put("izCompleted", false);
                }else{
                    map.put("izCompleted",true);
                }
                productIdRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(izCompleted){
                            rootRef.collection("taskLists").document(userEmail)
                                    .collection("userTaskLists").document(taskListId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Map<String, Object> map = (Map<String, Object>) task.getResult().get("users");
                                    String notificationMessege = userName + "just Completed" + productName + " from" + taskListName + "´s list!";
                                    NotificationModel notificationModel = new NotificationModel(notificationMessege,userEmail);

                                    for(Map.Entry<String,Object> entry : map.entrySet()){
                                        String sharedUserEmail = entry.getKey();

                                        //if(!sharedUserEmail.equals(userEmail)){
                                            rootRef.collection("notifications").document(sharedUserEmail)
                                                    .collection("userNotifications").document()
                                                    .set(notificationModel);
                                        //}
                                    }

                                }
                            });

                        }
                    }
                });
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit / Delete Name");

                final EditText editText = new EditText(context);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editText.setText(productName);
                editText.setSelection(editText.getText().length());
                editText.setHint("Type a name");
                editText.setHintTextColor(Color.GRAY);
                builder.setView(editText);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newProductName = editText.getText().toString().trim();
                        Map<String, Object> map = new HashMap<>();
                        map.put("productName", newProductName);
                        productIdRef.update(map);

                    }
                });

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        productIdRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(taskListViewFragment, "Product Deleted!", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
    }

}
