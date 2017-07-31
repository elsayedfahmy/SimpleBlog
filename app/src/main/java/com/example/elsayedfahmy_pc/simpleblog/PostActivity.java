package com.example.elsayedfahmy_pc.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.net.URI;

public class PostActivity extends AppCompatActivity {

    private ImageButton img_post;
    private EditText edt_PostTitle ,edt_Postdescription;
    Button btnsumbit;
    private Uri imageuri=null;
    private static  int gallaryRequest=1;

    private StorageReference mstorage;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;

    private FirebaseAuth mfirebaseAuth;
    private FirebaseUser mcirrentUser;
    private DatabaseReference mdatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mfirebaseAuth=FirebaseAuth.getInstance();
        mcirrentUser=mfirebaseAuth.getCurrentUser();
        mdatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mcirrentUser.getUid());

        mstorage = FirebaseStorage.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Blog");

        mprogress=new ProgressDialog(this);
        edt_Postdescription=(EditText)findViewById(R.id.edtpost_description);
        edt_PostTitle=(EditText)findViewById(R.id.edtpost_title);
        btnsumbit=(Button)findViewById(R.id.btnsummbit);

        img_post =(ImageButton) findViewById(R.id.imgpost);
        img_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,gallaryRequest);
            }
        });


        btnsumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }
    public void  startPosting()
    {
        mprogress.setMessage("Posting to Blog ...");
        mprogress.show();
        final String title=edt_PostTitle.getText().toString().trim();
        final String description=edt_Postdescription.getText().toString().trim();
        if (!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(description)&&imageuri!=null)
        {
            StorageReference filepath=mstorage.child("Blog_image").child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUri = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newpost=mdatabase.push();

                    mdatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newpost.child("title").setValue(title);
                            newpost.child("description").setValue(description);
                            newpost.child("image").setValue(downloadUri.toString());
                            newpost.child("uid").setValue(mcirrentUser.getUid());
                            newpost.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));

                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mprogress.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==gallaryRequest && resultCode==RESULT_OK)
        {
             imageuri=data.getData();
            img_post.setImageURI(imageuri);

        }

    }
}
