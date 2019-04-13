package com.example.adminqism;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewsBolimActivity extends AppCompatActivity {
    private Button btn_add_new,btn_ochir_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_news_bolim );

        btn_add_new=(Button)findViewById( R.id.btn_add_new );
        btn_ochir_new=(Button)findViewById( R.id.btn_ochir_new );

        btn_add_new.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent( NewsBolimActivity.this,AddNewsActivity.class );
                startActivity( intent );
                finish();
            }
        } );

    }
}
