package com.example.a2lazy;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2lazy.Variables.Variables;
import com.example.a2lazy.fragments.MenuFragment;
import com.example.a2lazy.helpers.SwipeToDeleteCallback;
import com.example.a2lazy.holders.TaskListViewHolder;
import com.example.a2lazy.models.TaskListModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MenuFragment.MenuFragmentListener, SwipeToDeleteCallback.swipeListener {

    private String userEmail, userName, taskTitle, taskListId;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore rootRef;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CollectionReference userTaskListsRef;
    private FirestoreRecyclerAdapter<TaskListModel, TaskListViewHolder> firestoreRecyclerAdapter;
    private Context context;
    RecyclerView recyclerView;
    TextView emptyView;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    private Variables variables;

    private File mFile;

    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            userEmail = googleSignInAccount.getEmail();
            userName = googleSignInAccount.getDisplayName();
            Toast.makeText(this, "Welcome " + userName, Toast.LENGTH_LONG).show();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        };

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, Variables.getInstance().CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        variables = Variables.getInstance();



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Create new Task List");

                final EditText editText = new EditText(MainActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editText.setHint("Type a name");
                editText.setHintTextColor(Color.GRAY);
                builder.setView(editText);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String taskListName = editText.getText().toString().trim();
                        MainActivity.this.addTaskList(taskListName);
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

        userTaskListsRef = rootRef.collection("taskLists").document(userEmail).collection("userTaskLists");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar2 = findViewById(R.id.progress_bar2);

        CreateRectclerView();


    }

    public void addTaskList(String taskListName) {
        String taskListId = userTaskListsRef.document().getId();
        TaskListModel taskModel = new TaskListModel(taskListId, taskListName, userName, "");
        userTaskListsRef.document(taskListId).set(taskModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "Shopping List successfully created!");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        firebaseAuth.addAuthStateListener(authStateListener);
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_button:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        Map<String, Object> map = new HashMap<>();
        map.put("tokenId", FieldValue.delete());

        rootRef.collection("users").document(userEmail).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseAuth.signOut();

                if (googleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(googleApiClient);
                }
            }
        });
    }


    @Override
    public void editTask(final String taskListId, String taskListName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Shopping List Name");

        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setText(taskListName);
        editText.setSelection(editText.getText().length());
        editText.setHint("Type a name");
        editText.setHintTextColor(Color.GRAY);
        builder.setView(editText);

        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final Map<String, Object> map = new HashMap<>();

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newTaskListName = editText.getText().toString().trim();
                map.put("taskListName", newTaskListName);
                rootRef.collection("taskLists").document(userEmail).collection("userTaskLists").document(taskListId).update(map);

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

    public void openCamera(String taskListIds) {
        taskListId = taskListIds;
        mFile = getOutputMediaFile(); // create a file to save the image
        if (mFile == null) {
            Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show();
            return;
        }
        // create Intent to take a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));

        Log.d(variables.LOGTAG, "file uri = " + Uri.fromFile(mFile).toString());

        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d(variables.LOGTAG, "camera app will be started");
            startActivityForResult(intent, 100);
        } else
            Log.d(variables.LOGTAG, "camera app could NOT be started");
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera01");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("xyz", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String postfix = "jpg";
        String prefix = "IMG";

        File mediaFile = new File(mediaStorageDir.getPath() +
                File.separator + prefix +
                "_" + timeStamp + "." + postfix);

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                uploadPicture(mFile);
                Toast.makeText(this, "Capture Succeful... Please wait upload to finish...", Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show();
                return;

            } else
                Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadPicture(File newFile) {
        Uri file = Uri.fromFile(newFile);
        //generate new id for the image
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String generatedId = database.getReference("users").push().getKey();

        final StorageReference riversRef = storageReference.child("uploads/" + generatedId);

        // create metadata for the image
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("originalName", newFile.getName())
                .setCustomMetadata("userId", firebaseAuth.getCurrentUser().getUid())
                .build();

        // upload the image with metadata to the firestore
        riversRef.putFile(file,metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads
                        Log.d(variables.LOGTAG, "onSuccess: You just uploaded a picture to firestore");
                        Toast.makeText(MainActivity.this, "Uploading Finished... " , Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d(variables.LOGTAG, "onFailure: Something went wrong: " + exception);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar2.setVisibility(View.VISIBLE);
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressBar2.setProgress((int) progress);
                if(progress > 99){
                    progressBar2.setVisibility(View.GONE);
                }
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String imageUrl = (String) downloadUri.toString();

                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                            Map<String, Object> map = new HashMap<>();

                            map.put("imageUrl", imageUrl);
                            rootRef.collection("taskLists").document(userEmail).collection("userTaskLists").document(taskListId).update(map);

                        } else {
                            // Handle failures

                        }
                    }
                });

    }

    private void CreateRectclerView(){

        Query query = userTaskListsRef.orderBy("taskListName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TaskListModel> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<TaskListModel>()
                .setQuery(query, TaskListModel.class)
                .build();

        firestoreRecyclerAdapter =
                new FirestoreRecyclerAdapter<TaskListModel, TaskListViewHolder>(firestoreRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull TaskListViewHolder holder, int position, @NonNull TaskListModel model) {
                        holder.setTaskList(context, userEmail, model);
                    }

                    @Override
                    public TaskListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_list, parent, false);
                        return new TaskListViewHolder(view);
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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(userEmail,firestoreRecyclerAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void delete(String userEmails , String taskListIds) {
        rootRef.collection("products").document(taskListIds).delete();
        rootRef.collection("taskLists").document(userEmails).collection("userTaskLists").document(taskListIds).delete();

    }

    @Override
    public void deleteItem(String userEmails, String taskListIds) {
        rootRef.collection("products").document(taskListIds).delete();
        rootRef.collection("taskLists").document(userEmails).collection("userTaskLists").document(taskListIds).delete();
    }
}
