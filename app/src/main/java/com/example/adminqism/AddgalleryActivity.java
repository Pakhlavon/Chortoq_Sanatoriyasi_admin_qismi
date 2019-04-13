package com.example.adminqism;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddgalleryActivity extends AppCompatActivity {
    private String Rasmname,Description,Name,savecurrentdate,savecurrenttime;

    private ImageView gallery_add_image;
    private EditText rasm_name,rasm_descriontion;
    private Button add_new_rasm;

    private static final int GalleryPick=1;
    private Uri Imageuri;
    private String RasmRandomKey,downloadImageUri;

    private StorageReference Rasmimageref;
    private DatabaseReference RasmlarRef;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_addgallery );



        Rasmname=getIntent().getStringExtra("Gallery");
        Rasmimageref= FirebaseStorage.getInstance().getReference().child( "gallery Images" );
        RasmlarRef= FirebaseDatabase.getInstance().getReference().child( "Gallery" );


        gallery_add_image=(ImageView)findViewById( R.id.gallery_add_image );

        rasm_name=(EditText)findViewById( R.id.rasm_name );
        rasm_descriontion=(EditText)findViewById( R.id.rasm_descriontion );

        add_new_rasm=(Button)findViewById( R.id.add_new_rasm );

        loading=new ProgressDialog( this );

        gallery_add_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengallery();
            }
        } );
        add_new_rasm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateSectiondata();
            }
        } );
    }

    private void opengallery()
    {
        Intent galleryintent =new Intent(  );
        galleryintent.setAction( Intent.ACTION_GET_CONTENT );
        galleryintent.setType( "image/" );
        startActivityForResult( galleryintent,GalleryPick );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data !=null)
        {
            Imageuri=data.getData();
            gallery_add_image.setImageURI( Imageuri );

        }
    }

    private void  ValidateSectiondata()
    {
        Description=rasm_descriontion.getText().toString();
        Name=rasm_name.getText().toString();

        if (Imageuri==null)
        {
            Toast.makeText( this, "Rasm tanlanishi kerak...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Name ))
        {
            Toast.makeText( this, "Iltimos Rasm nomini kiriting...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Description ))
        {
            Toast.makeText( this, "Iltimos Rasm haqida ma'lumot kiriting", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            StorageRasmMalumotlari();
        }
    }

    private void StorageRasmMalumotlari()

    {
        loading.setTitle( "Yangi Rasm qo'shildi..." );
        loading.setMessage( "Iltimos kuting, yangi rasm qo'shilyapti" );
        loading.setCanceledOnTouchOutside( false );
        loading.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat( "MMM dd,yyyy" );
        savecurrentdate=currentdate.format( calendar.getTime() );

        SimpleDateFormat currenttime=new SimpleDateFormat( "HH:mm:ss a");
        savecurrenttime=currenttime.format( calendar.getTime() );

        RasmRandomKey=savecurrentdate+savecurrenttime;

        final StorageReference filepath=Rasmimageref.child( Imageuri.getLastPathSegment()+RasmRandomKey +".jpg");
        final UploadTask uploadTask=filepath.putFile(  Imageuri);

        uploadTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message =e.toString();
                Toast.makeText( AddgalleryActivity.this, "Error:"+message, Toast.LENGTH_SHORT ).show();
                loading.dismiss();

            }
        } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText( AddgalleryActivity.this, "Rasm muvaffaqiyatli yakunlandi", Toast.LENGTH_SHORT ).show();

                Task<Uri> uriTask =uploadTask.continueWithTask( new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw  task.getException();
                        }
                        downloadImageUri=filepath.getDownloadUrl().toString();
                        return  filepath.getDownloadUrl();
                    }
                } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {

                            downloadImageUri=task.getResult().toString();
                            Toast.makeText( AddgalleryActivity.this, "Rasmi bazaga muvafaqqiyatli saqlandi...", Toast.LENGTH_SHORT ).show();

                            Bolimmalumotlarinibazagasaqlash();
                        }
                    }
                } );
            }
        } );

    }

    private void Bolimmalumotlarinibazagasaqlash()
    {
        HashMap<String,Object> RasmMap =new HashMap<>();
        RasmMap.put("pid",RasmRandomKey);
        RasmMap.put("date",savecurrentdate);
        RasmMap.put("time",savecurrenttime);
        RasmMap.put("ma'lumot",Description);
        RasmMap.put("image",downloadImageUri);
        RasmMap.put("Rasmlar",Rasmname);
        RasmMap.put("Name",Name);

        RasmlarRef.child( RasmRandomKey).updateChildren( RasmMap )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Intent intent=new Intent( AddgalleryActivity.this,GallerybolimActivity.class );
                            startActivity( intent );
                            finish();

                            loading.dismiss();
                            Toast.makeText( AddgalleryActivity.this, "Rasm qo'shildi...", Toast.LENGTH_SHORT ).show();
                        }

                        else {
                            loading.dismiss();
                            String massage =task.getException().toString();
                            Toast.makeText( AddgalleryActivity.this, "Xatolik:"+massage, Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

    }

    }

