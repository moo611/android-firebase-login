package com.example.macos.firebaselogin;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {


    EditText id,name,password;
    ImageView profile;
    Button btn;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id=findViewById(R.id.id);
        password=findViewById(R.id.password);
        name=findViewById(R.id.name);
        profile=findViewById(R.id.img_profile);
        btn=findViewById(R.id.signup);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                signup();


            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload();

            }
        });



    }

    private void upload() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 10);



    }

    private void signup() {


            FirebaseAuth.getInstance().createUserWithEmailAndPassword(id.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()){

                               final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

                        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("users").child(uid);

                        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()){

                                     storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Uri> task) {

                                             String imageurl=task.toString();

                                                         UserModel userModel=new UserModel();
                                                         userModel.name=name.getText().toString();
                                                         userModel.uid=uid;
                                                         userModel.imageurl=imageurl;


                                             FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);


                                         }
                                     });






                                }else{


                                    Toast.makeText(RegisterActivity.this,"error",Toast.LENGTH_SHORT).show();
                                }


                            }
                        });








                    }else{

                        Toast.makeText(RegisterActivity.this,"error",Toast.LENGTH_SHORT).show();

                    }
                }
            });












    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 10 && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData());
            imageUri = data.getData();
        }
    }
}
