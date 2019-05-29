package com.fernandotenorio.volvifinalapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Data.BlogsAdapter;
import com.fernandotenorio.volvifinalapp.Listeners.OnUserLoadListener;
import com.fernandotenorio.volvifinalapp.Model.Blog;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.Model.Users;
import com.fernandotenorio.volvifinalapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@IgnoreExtraProperties
public class PostListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private BlogsAdapter blogRecycleAdapter;


    private EditText search;

    private List<Blog> blogList;
    private User currentUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Blog, BlogsAdapter.ViewHolder> firebaseRecyclerAdapter;
    private FirebaseDatabase mDataBase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDataBase  = FirebaseDatabase.getInstance();
        mDatabaseReference = mDataBase.getReference().child("Volvi Blog");
        mDatabaseReference.keepSynced(true);


        search = findViewById(R.id.action_search);
        recyclerView = findViewById(R.id.recyclerView);

        blogList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        new Users().loadCurrentUser(new OnUserLoadListener() {
            @Override
            public void onLoad(User user) {
                currentUser = user;
            }

            @Override
            public void onFailed(String error) {
                currentUser = null;
                Toast.makeText(PostListActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_manu, menu);

        final MenuItem searchtItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchtItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                if(mUser !=null && mAuth != null){
                    startActivity(new Intent(PostListActivity.this, AddPostActivity.class));
                    finish();

                }
                break;
            case R.id.action_signout:

                if(mUser !=null && mAuth != null){
                    mAuth.signOut();

                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();

                }
                break;
            case R.id.action_event:
                if(mUser !=null && mAuth != null){

                    startActivity(new Intent(PostListActivity.this, EventListActivity.class));
                    finish();
                }
                break;

            case R.id.action_calendar:
                if(mUser !=null && mAuth != null){

                    startActivity(new Intent(PostListActivity.this, CalendarActivity.class));
                    finish();

                }
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot data : dataSnapshot.getChildren()){

                            try {
                                Blog blog = new Blog(data, currentUser);
                                blogList.add(blog);
                                Collections.reverse(blogList);

                            }catch (DatabaseException e){
                                data.getKey();
                            }
                            BlogsAdapter blogRecycleAdapter = new BlogsAdapter(PostListActivity.this.getApplicationContext(), PostListActivity.this, currentUser, blogList);
                            recyclerView.setAdapter(blogRecycleAdapter);
                            blogRecycleAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
