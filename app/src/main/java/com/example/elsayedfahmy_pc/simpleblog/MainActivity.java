package com.example.elsayedfahmy_pc.simpleblog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mbloglist;
    private DatabaseReference mdatabase;

    private DatabaseReference mdatabaseUsers;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mauthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth=FirebaseAuth.getInstance();
        mauthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null)
                {
                   Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mdatabase=FirebaseDatabase.getInstance().getReference().child("Blog");
        mdatabase.keepSynced(true);

        mdatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabaseUsers.keepSynced(true);



        mbloglist=(RecyclerView)findViewById(R.id.blogList_recycleview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mbloglist.setHasFixedSize(true);
        mbloglist.setLayoutManager(layoutManager);


        checkuserExit();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // checkuserExit();
      mAuth.addAuthStateListener(mauthStateListener);

        FirebaseRecyclerAdapter<blog,blogHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<blog, blogHolder>(
                blog.class,
                R.layout.blog_row,
                blogHolder.class,
                mdatabase
        ) {
            @Override
            protected void populateViewHolder(blogHolder viewHolder, blog model, int position) {
                final String post_Key=getRef(position).toString();

                viewHolder.settitle(model.getTitle());
                viewHolder.setdescription(model.getDescription());
                viewHolder.setusername(model.getUsername());
                viewHolder.setimage(getApplicationContext(), model.getImage());

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"Post Key" + post_Key,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mbloglist.setAdapter(firebaseRecyclerAdapter);

    }



    public  void  checkuserExit() {
        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mdatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }



    public  static class blogHolder extends RecyclerView.ViewHolder
    {
        View mview;
        public blogHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void settitle(String title)
        {
            TextView txt_posttitle=(TextView)mview.findViewById(R.id.post_title);
            txt_posttitle.setText(title);
        }
        public void setdescription(String desc)
        {
            TextView txt_postdeacriptione=(TextView)mview.findViewById(R.id.post_deacription);
            txt_postdeacriptione.setText(desc);
        }
        public void setimage(Context context, String imageuri)
        {
            ImageView imagepost=(ImageView)mview.findViewById(R.id.post_image);
            Picasso.with(context).load(imageuri).into(imagepost);
        }
        public void setusername( String username)
        {
            EditText edt_username=(EditText) mview.findViewById(R.id.edt_username_hint_Blogrow);
            edt_username.setText(username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_add)
        {startActivity(new Intent(MainActivity.this,PostActivity.class));

        }
        if (item.getItemId()==R.id.logout)
        {
            logout();

        }

        return super.onOptionsItemSelected(item);
    }

public  void logout()
{
    mAuth.signOut();
}

}
