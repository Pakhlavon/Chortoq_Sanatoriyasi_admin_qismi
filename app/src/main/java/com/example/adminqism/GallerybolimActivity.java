package com.example.adminqism;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GallerybolimActivity extends AppCompatActivity {
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gallerybolim );

        btn_add=(Button)findViewById( R.id.btn_add );


        btn_add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( GallerybolimActivity.this,AddgalleryActivity.class );
                startActivity( intent );
                finish();
            }
        } );
    }
}
