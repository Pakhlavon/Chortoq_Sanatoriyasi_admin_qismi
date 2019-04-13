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

public class AddNewsActivity extends AppCompatActivity {

    private String Newsname,Description,Name,savecurrentdate,savecurrenttime;
    private ImageView news_add_image;
    private EditText news_name,news_descriontion;
    private Button add_new_news_rasm;
    private static final int GalleryPick=1;
    private Uri Imageuri;
    private String NewsRandomKey,downloadImageUri;

    private StorageReference Newsimageref;
    private DatabaseReference NewsRef;
    private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_news );

        Newsname=getIntent().getStringExtra("News");
        Newsimageref= FirebaseStorage.getInstance().getReference().child( "new Images" );
        NewsRef= FirebaseDatabase.getInstance().getReference().child( "News" );

        news_add_image=(ImageView)findViewById( R.id.news_add_image);

        news_name=(EditText)findViewById( R.id.news_name );
        news_descriontion=(EditText)findViewById( R.id.news_descriontion );

        add_new_news_rasm=(Button)findViewById( R.id.add_new_news_rasm );

        loading=new ProgressDialog( this );

        news_add_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengallery();
            }
        } );
        add_new_news_rasm.setOnClickListener( new View.OnClickListener() {
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
            news_add_image.setImageURI( Imageuri );

        }
    }

    private void  ValidateSectiondata()
    {
        Description=news_descriontion.getText().toString();
        Name=news_name.getText().toString();

        if (Imageuri==null)
        {
            Toast.makeText( this, "Rasm tanlanishi kerak...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Name ))
        {
            Toast.makeText( this, "Iltimos Yangilik nomini kiriting...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Description ))
        {
            Toast.makeText( this, "Iltimos Yangilik haqida ma'lumot kiriting", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            StorageBolimMalumotlari();
        }
    }

    private void StorageBolimMalumotlari()

    {
        loading.setTitle( "Yangi yangilik qo'shildi..." );
        loading.setMessage( "Iltimos kuting, yangi yangilik qo'shilyapti" );
        loading.setCanceledOnTouchOutside( false );
        loading.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat( "MMM dd,yyyy" );
        savecurrentdate=currentdate.format( calendar.getTime() );

        SimpleDateFormat currenttime=new SimpleDateFormat( "HH:mm:ss a");
        savecurrenttime=currenttime.format( calendar.getTime() );

        NewsRandomKey=savecurrentdate+savecurrenttime;

        final StorageReference filepath=Newsimageref.child( Imageuri.getLastPathSegment()+NewsRandomKey +".jpg");
        final UploadTask uploadTask=filepath.putFile(  Imageuri);

        uploadTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message =e.toString();
                Toast.makeText( AddNewsActivity.this, "Error:"+message, Toast.LENGTH_SHORT ).show();
                loading.dismiss();

            }
        } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText( AddNewsActivity.this, "Rasm muvaffaqiyatli yakunlandi", Toast.LENGTH_SHORT ).show();

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
                            Toast.makeText( AddNewsActivity.this, "Yangilik rasmi bazaga muvafaqqiyatli saqlandi...", Toast.LENGTH_SHORT ).show();

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
        bolimMap.put("pid",NewsRandomKey);
        bolimMap.put("date",savecurrentdate);
        bolimMap.put("time",savecurrenttime);
        bolimMap.put("ma'lumot",Description);
        bolimMap.put("image",downloadImageUri);
        bolimMap.put("Yangiliklar nomi",Newsname);
        bolimMap.put("Name",Name);

        NewsRef.child( NewsRandomKey).updateChildren( bolimMap )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Intent intent=new Intent( AddNewsActivity.this,NewsBolimActivity.class );
                            startActivity( intent );
                            finish();

                            loading.dismiss();
                            Toast.makeText( AddNewsActivity.this, "Yangilik qo'shildi...", Toast.LENGTH_SHORT ).show();
                        }

                        else {
                            loading.dismiss();
                            String massage =task.getException().toString();
                            Toast.makeText( AddNewsActivity.this, "Xatolik:"+massage, Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

    }
}

