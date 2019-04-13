package com.example.adminqism;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.adminqism.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linerbolim,linergallery,linerfoydalanuvchi,linerariza,lineryangilik,lineradmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        linerbolim=(LinearLayout)findViewById( R.id.linerbolim );
        linergallery=(LinearLayout)findViewById( R.id.linergallery );
        linerfoydalanuvchi=(LinearLayout)findViewById( R.id.linerfoydalanuvchi );
        lineradmin=(LinearLayout)findViewById( R.id.lineradmin );
        linerariza=(LinearLayout)findViewById( R.id.linerariza );
        lineryangilik=(LinearLayout)findViewById( R.id.lineryangilik );

        linerbolim.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( MainActivity.this,BolimlarActivity.class );
                startActivity(intent);
                finish();
            }
        } );

        linergallery.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent=new Intent( MainActivity.this, GallerybolimActivity.class);
                 startActivity( intent );
                 finish();
            }
        } );

        lineryangilik.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( MainActivity.this,NewsBolimActivity.class );
                startActivity( intent );
                finish();
            }
        } );
    }
}
