package com.example.a2lazy.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2lazy.R;
import com.example.a2lazy.TaskListActivity;
import com.example.a2lazy.holders.ProductViewHolder;
import com.example.a2lazy.holders.TaskListViewHolder;
import com.example.a2lazy.models.ProductModel;
import com.example.a2lazy.models.TaskListModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TaskListFragment extends Fragment {
    private String taskListId;
    private FirebaseFirestore rootRef;
    private CollectionReference taskListProductsRef;
    private Boolean izCompleted;
    private String userEmail, userName;
    private GoogleApiClient googleApiClient;
    RecyclerView recyclerView;
    TextView emptyView;
    ProgressBar progressBar;
    private FirestoreRecyclerAdapter<ProductModel, ProductViewHolder> firestoreRecyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View taskListViewFragment = inflater.inflate(R.layout.fragment_task_list, container, false);
        Bundle bundle = getArguments();
        izCompleted = bundle.getBoolean("izCompleted");

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if (googleSignInAccount != null) {
            userEmail = googleSignInAccount.getEmail();
            userName = googleSignInAccount.getDisplayName();
        }

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        final TaskListModel taskModel = ((TaskListActivity) getActivity()).getTaskListModel();
        taskListId = taskModel.getTaskListId();

        FloatingActionButton fab = taskListViewFragment.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskListFragment.this.getContext());
                builder.setTitle("Create Product");

                final EditText editText = new EditText(TaskListFragment.this.getContext());
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editText.setHint("Type a name");
                editText.setHintTextColor(Color.GRAY);
                builder.setView(editText);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String productName = editText.getText().toString().trim();
                                TaskListFragment.this.addProduct(productName);
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        rootRef = FirebaseFirestore.getInstance();
        taskListProductsRef = rootRef.collection("products").document(taskListId).collection("taskListProducts");

        recyclerView = taskListViewFragment.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyView = taskListViewFragment.findViewById(R.id.empty_view);
        progressBar = taskListViewFragment.findViewById(R.id.progress_bar);

        Query query = taskListProductsRef.whereEqualTo("izCompleted", izCompleted)
                .orderBy("productName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ProductModel> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        firestoreRecyclerAdapter =
                new FirestoreRecyclerAdapter<ProductModel, ProductViewHolder>(firestoreRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductModel model) {
                        holder.setProduct(getContext(), taskListViewFragment, userEmail, userName, taskModel,  model);
                    }

                    @Override
                    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                        return new ProductViewHolder(view);
                }

                    @Override
                    public void onDataChanged() {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if (getItemCount() == 0) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public int getItemCount() {
                        return super.getItemCount();
                    }
                };
        recyclerView.setAdapter(firestoreRecyclerAdapter);


        return taskListViewFragment;
    }

    private void addProduct(String productName) {
        String productId = taskListProductsRef.document().getId();
        ProductModel productModel = new ProductModel(productId, productName, izCompleted);
        taskListProductsRef.document(productId).set(productModel);
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }

}
