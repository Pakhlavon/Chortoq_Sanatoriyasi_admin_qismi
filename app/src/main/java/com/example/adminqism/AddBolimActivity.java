package com.example.adminqism;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
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

import com.google.android.gms.auth.api.signin.internal.Storage;
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

public class AddBolimActivity extends AppCompatActivity
{
    private String Bolimname,Description,Name,savecurrentdate,savecurrenttime;
    private ImageView select_image;
    private EditText bolim_name,bolim_descriontion;
    private Button add_new_bolim;
    private static final int GalleryPick=1;
    private Uri Imageuri;
    private String BolimRandomKey,downloadImageUri;

    private StorageReference Bolimimageref;
    private DatabaseReference BolimlarRef;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_bolim );

        Bolimname=getIntent().getStringExtra("Bolimlar");
        Bolimimageref= FirebaseStorage.getInstance().getReference().child( "Bolim Images" );
        BolimlarRef= FirebaseDatabase.getInstance().getReference().child( "Bo'limlar" );

        select_image=(ImageView)findViewById( R.id.select_image );

        bolim_name=(EditText)findViewById( R.id.bolim_name );
        bolim_descriontion=(EditText)findViewById( R.id.bolim_descriontion );

        add_new_bolim=(Button)findViewById( R.id.add_new_bolim );

        loading=new ProgressDialog( this );

        select_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengallery();
            }
        } );
            add_new_bolim.setOnClickListener( new View.OnClickListener() {
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
            select_image.setImageURI( Imageuri );

        }
    }

    private void  ValidateSectiondata()
    {
        Description=bolim_descriontion.getText().toString();
        Name=bolim_name.getText().toString();

        if (Imageuri==null)
        {
            Toast.makeText( this, "Rasm tanlanishi kerak...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Name ))
        {
            Toast.makeText( this, "Iltimos Bo'lim nomini kiriting...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Description ))
        {
            Toast.makeText( this, "Iltimos Bo'lim haqida ma'lumot kiriting", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            StorageBolimMalumotlari();
        }
    }

    private void StorageBolimMalumotlari()

    {
        loading.setTitle( "Yangi bo'lim qo'shildi..." );
        loading.setMessage( "Iltimos kuting, yangi bo'lim qo'shilyapti" );
        loading.setCanceledOnTouchOutside( false );
        loading.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat( "MMM dd,yyyy" );
        savecurrentdate=currentdate.format( calendar.getTime() );

        SimpleDateFormat currenttime=new SimpleDateFormat( "HH:mm:ss a");
        savecurrenttime=currenttime.format( calendar.getTime() );

        BolimRandomKey=savecurrentdate+savecurrenttime;

        final StorageReference filepath=Bolimimageref.child( Imageuri.getLastPathSegment()+BolimRandomKey +".jpg");
        final UploadTask uploadTask=filepath.putFile(  Imageuri);

        uploadTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message =e.toString();
                Toast.makeText( AddBolimActivity.this, "Error:"+message, Toast.LENGTH_SHORT ).show();
                loading.dismiss();

            }
        } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText( AddBolimActivity.this, "Rasm muvaffaqiyatli yakunlandi", Toast.LENGTH_SHORT ).show();

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
                        Toast.makeText( AddBolimActivity.this, "Bo'lim rasmi bazaga muvafaqqiyatli saqlandi...", Toast.LENGTH_SHORT ).show();

                   Bolimmalumotlarinibazagasaqlash();
                    }
                    }
                } );
            }
        } );

    }

    private void Bolimmalumotlarinibazagasaqlash()
    {
        HashMap<String,Object> bolimMap =new HashMap<>();
        bolimMap.put("pid",BolimRandomKey);
        bolimMap.put("date",savecurrentdate);
        bolimMap.put("time",savecurrenttime);
        bolimMap.put("ma'lumot",Description);
        bolimMap.put("image",downloadImageUri);
        bolimMap.put("Bolimlar",Bolimname);
        bolimMap.put("Name",Name);

        BolimlarRef.child( BolimRandomKey).updateChildren( bolimMap )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Intent intent=new Intent( AddBolimActivity.this,BolimlarActivity.class );
                            startActivity( intent );
                            finish();

                            loading.dismiss();
                            Toast.makeText( AddBolimActivity.this, "Bo'lim qo'shildi...", Toast.LENGTH_SHORT ).show();
                        }

                        else {
                            loading.dismiss();
                            String massage =task.getException().toString();
                            Toast.makeText( AddBolimActivity.this, "Xatolik:"+massage, Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

    }
}

