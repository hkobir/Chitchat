package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.chitchat.adapters.CustomAdapter;
import com.example.chitchat.databinding.ActivityMainBinding;
import com.example.chitchat.models.ChatModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CustomAdapter adapter;
    private List<ChatModel> list_chat_models;
    private static int SIGN_IN_REQUEST_CODE = 1;
    private AlertDialog progressDialog;
    private boolean isEmpty = true;
    private SoftInputAssist softInputAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        softInputAssist = new SoftInputAssist(this);

        //progress dialogue set up

        progressDialog = new AlertDialog.Builder(MainActivity.this).create();
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.progress_diaog_layout, null);
        progressDialog.setCancelable(false);
        progressDialog.setView(view);

        list_chat_models = new ArrayList<>();
        adapter = new CustomAdapter(list_chat_models, MainActivity.this);
        binding.listOfMessage.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        binding.listOfMessage.setAdapter(adapter);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty){
                    FirebaseDatabase.getInstance().getReference().child("message").push().setValue(new ChatModel(
                            "Hi!",
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                    ));
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("message").push().setValue(new ChatModel(
                            binding.userMessage.getText().toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                    ));
                }
                binding.userMessage.setText("");
                binding.userMessage.requestFocus();
            }
        });
        binding.userMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.userMessage.getText().toString().equals("") || binding.userMessage.getText().toString().length() == 0) {
                    binding.fab.setImageResource(R.drawable.ic_hand);
                    isEmpty =true;
                } else {
                    binding.fab.setImageResource(R.drawable.ic_send);
                    isEmpty = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(binding.mainLayout, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
        //Check if not sign-in then navigate Signin page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(binding.mainLayout, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //Load content
            setImage();
            setMessage();
        }


    }

    private void setImage() {
        Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.ic_add_profile_pic).into(binding.circleImageView);
    }

    private void setMessage() {
        progressDialog.show();
        DatabaseReference msgReffeReference = FirebaseDatabase.getInstance().getReference().child("message");
        msgReffeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_chat_models.clear();
                for (DataSnapshot pSnapshot : snapshot.getChildren()) {
                    ChatModel model = pSnapshot.getValue(ChatModel.class);
                    list_chat_models.add(model);
                    adapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(binding.mainLayout, "Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                setMessage();
            } else {
                Snackbar.make(binding.mainLayout, "We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        softInputAssist.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        softInputAssist.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        softInputAssist.onDestroy();
        super.onDestroy();
    }
}