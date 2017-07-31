package com.example.elsayedfahmy_pc.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

   private EditText edt_username,edt_password,edtemail;
   private Button btn_register;
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;

    private ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("users");

        mprogress=new ProgressDialog(this);
        edt_username=(EditText)findViewById(R.id.edt_username);
        edtemail=(EditText)findViewById(R.id.edt_mail);
        edt_password=(EditText)findViewById(R.id.edt_password);
        btn_register=(Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startregister();
            }
        });


    }
    public  void startregister()
    {
        mprogress.setMessage(" Signing Up  ...");
        mprogress.show();
         final String username=edt_username.getText().toString().trim();
         String emai=edtemail.getText().toString().trim();
        String password=edt_password.getText().toString().trim();
        if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(emai)&&!TextUtils.isEmpty(password))
        {
            mAuth.createUserWithEmailAndPassword(emai,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String user_id=mAuth.getCurrentUser().getUid();

                    DatabaseReference current_user_id=mdatabase.child(user_id);
                    current_user_id.child("username").setValue(username);
                    current_user_id.child("image").setValue("default");

                    mprogress.dismiss();
                    Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);



                }
            });

        }
    }

}
