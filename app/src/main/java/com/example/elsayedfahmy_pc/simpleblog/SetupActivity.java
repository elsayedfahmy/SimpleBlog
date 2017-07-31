package com.example.elsayedfahmy_pc.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton img_setup_image_button;
   private EditText edt_setupName;
    private Button btn_finishSetUp;
    private Uri  mimageUri=null;
   private   static final int Gallaryrequest =1;

    private DatabaseReference mdatabaseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mstorageimage;

    private ProgressDialog mprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth=FirebaseAuth.getInstance();
        mstorageimage= FirebaseStorage.getInstance().getReference().child("profile_images");

        mdatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
       // mdatabaseUsers.keepSynced(true);

        mprogress=new ProgressDialog(this);

        img_setup_image_button=(ImageButton)findViewById(R.id.setup_image_button);
        edt_setupName=(EditText)findViewById(R.id.edt_setupName);
        btn_finishSetUp=(Button)findViewById(R.id.btn_setup);

        img_setup_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent=new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent,Gallaryrequest);

            }
        });

        btn_finishSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StartSetUpaccount();
            }
        });


    }
    private  void  StartSetUpaccount()
    {
        final String name_setup=edt_setupName.getText().toString().trim();
        final String user_id=mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(name_setup)&&mimageUri!=null)
        {
            mprogress.setMessage("Finishing Setup ...");
            mprogress.show();

            StorageReference filepath=mstorageimage.child(mimageUri.getLastPathSegment());

            filepath.putFile(mimageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloasUri=taskSnapshot.getDownloadUrl().toString();

                    mdatabaseUsers.child(user_id).child("name").setValue(name_setup);
                    mdatabaseUsers.child(user_id).child("image").setValue(name_setup);

                    mprogress.dismiss();

                    Intent mainIntent=new Intent(SetupActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallaryrequest && requestCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mimageUri = result.getUri();
                img_setup_image_button.setImageURI(mimageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
