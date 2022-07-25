package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.Models.UserGroups;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {


    FirebaseUser fuser;
    FirebaseDatabase fb;
    DatabaseReference reference;
    StorageReference storageReference;
    TextView usernametb;
    EditText showfirstname;
    EditText showlastname;
    EditText showemail;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    String ogImageLoc;
    ProgressDialog pd;
    Chip editmode;
    Button saveedit;
    CircleImageView showpimage;

    Intent intent;
    String userid;

    User openUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        filePath = Uri.EMPTY;
        editmode = findViewById(R.id.chip);
        showfirstname = findViewById(R.id.showfirstname);
        showlastname = findViewById(R.id.showlastname);
        showemail = findViewById(R.id.showemail);
        showpimage = findViewById(R.id.showprofileimage1);
        showpimage.setEnabled(false);
        saveedit = findViewById(R.id.saveedituser);
        pd = new ProgressDialog(UserActivity.this);
        pd.setMessage("Uploading...");
        usernametb = findViewById(R.id.user_name);
        Toolbar toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent1 = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });

        // get the passed user data

        intent = getIntent();
        userid = intent.getStringExtra("id");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        loaduser();

        // update the profile image
        showpimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showpimage.isEnabled()){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                }
            }
        });


        editmode.setOnClickListener(v -> {
            if (editmode.isChecked()){
                //if edit mode is on
                saveedit.setVisibility(View.VISIBLE);
                showfirstname.setFocusableInTouchMode(true);
                showfirstname.setTypeface(null, Typeface.BOLD_ITALIC);
                showlastname.setFocusableInTouchMode(true);
                showlastname.setTypeface(null, Typeface.BOLD_ITALIC);
                showpimage.setEnabled(true);


            }else{
                Toast.makeText(UserActivity.this, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                //if edit mode if turned off
                loaduser();
                saveedit.setVisibility(View.INVISIBLE);
                showfirstname.setFocusableInTouchMode(false);
                showfirstname.clearFocus();
                showfirstname.setTypeface(null, Typeface.NORMAL);
                showlastname.setFocusableInTouchMode(false);
                showlastname.clearFocus();
                showlastname.setTypeface(null, Typeface.NORMAL);
                showpimage.setEnabled(false);
            }
        });

        saveedit.setOnClickListener(v -> {
            // ask the user if they want to confirm the update. if they do then you can look to update

            AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
            builder.setCancelable(true);
            builder.setTitle("User Details Editing");
            builder.setMessage("Do you want to confirm your changes?");
            builder.setPositiveButton("Continue",
                    (dialog, which) -> {
                        // update the database
                        fb = FirebaseDatabase.getInstance();
                        reference = fb.getReference("Users");
                        reference.child(openUser.getId()).child("firstname").setValue(showfirstname.getText().toString());

                        fb = FirebaseDatabase.getInstance();
                        reference = fb.getReference("Users");
                        reference.child(openUser.getId()).child("lastname").setValue(showlastname.getText().toString());

                        // store the new image

                        // check that a new image has been uploaded
                        if (!filePath.equals(Uri.EMPTY)){
                            uploadImageToFirebase(openUser.getId(), filePath);
                        }else{
                            Toast.makeText(UserActivity.this, "User Edit success!", Toast.LENGTH_SHORT).show();
                            // go back to the main page
                            Intent intent = new Intent(UserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.setNegativeButton("Cancel",
                    (dialog, which) -> {
                        Toast.makeText(UserActivity.this, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                        //if edit mode if turned off
                        setUserInfo(openUser);
                        editmode.setChecked(false);
                        saveedit.setVisibility(View.INVISIBLE);
                        showfirstname.setFocusableInTouchMode(false);
                        showfirstname.clearFocus();
                        showfirstname.setTypeface(null, Typeface.NORMAL);
                        showlastname.setFocusableInTouchMode(false);
                        showlastname.clearFocus();
                        showlastname.setTypeface(null, Typeface.NORMAL);

                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                showpimage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        pd.show();
        final StorageReference image = storageReference.child("userimages/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fb = FirebaseDatabase.getInstance();
                        reference = fb.getReference("Users");
                        reference.child(openUser.getId()).child("imageLoc").setValue(uri.toString());

                        Toast.makeText(UserActivity.this, "Image Upload Success!", Toast.LENGTH_SHORT).show();
                        // go back to the main page
                        Intent intent = new Intent(UserActivity.this, MainActivity.class);
                        startActivity(intent);
                        pd.dismiss();
                        finish();
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UserActivity.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loaduser(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                openUser = user;
                // set toolbar data
                usernametb.setText(user.getFirstname() + " " + user.getLastname());
                ogImageLoc = user.getImageLoc();
                showfirstname.setText(user.getFirstname());
                showlastname.setText(user.getLastname());
                showemail.setText(user.getEmail());
                if (user.getImageLoc().equals("default")){
                    showpimage.setImageResource(R.mipmap.ic_launcher);
                } else{
                    // load image from whatever
                    Glide.with(UserActivity.this)
                            .load(user.getImageLoc())
                            .into(showpimage);
                }

                if (!(fuser.getUid().equals(openUser.getId()))){
                    // if its NOT the logged in user we are opening.
                    editmode.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setUserInfo(User inUser){
        showfirstname.setText(inUser.getFirstname());
        showlastname.setText(inUser.getLastname());
        showemail.setText(inUser.getEmail());
        Glide.with(UserActivity.this)
                .load(ogImageLoc)
                .into(showpimage);
    }
}
