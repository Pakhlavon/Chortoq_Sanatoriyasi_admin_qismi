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
import android.widget.Toast;

import com.example.adminqism.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity
{
    private EditText et_login,et_parol;
    private Button btn_login;
    private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        et_login=(EditText)findViewById( R.id.et_login );
        et_parol=(EditText)findViewById( R.id.et_parol );

        btn_login=(Button)findViewById( R.id.btn_login );

        loading=new ProgressDialog(this);

        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();
            }
        } );

    }

    private void loginuser()
    {

        String phone =et_login.getText().toString();
        String parol =et_parol.getText().toString();

        if (TextUtils.isEmpty(phone  ))
        {
            Toast.makeText( this, "Iltimos telefon raqamni kiriting", Toast.LENGTH_SHORT ).show();

        }
        else if (TextUtils.isEmpty( parol ))
        {
            Toast.makeText( this, "Iltimos parolni kiriting", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            loading.setTitle("Admin oynasiga kirish");
            loading.setMessage(" Iltimos kuting, sizning ma'lumotlaringiz tekshirilmoqda");
            loading.setCanceledOnTouchOutside( false );
            loading.show();

            accoutgaruxsat(phone,parol);

        }

    }



    private void accoutgaruxsat(final String phone, final String parol)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child( "Admins" ).child( phone ).exists())
                {

                    Users userdata= dataSnapshot.child( "Admins" ).child( phone ).getValue(Users.class);
                    if (userdata.getPhone().equals( phone ))
                    {
                        if (userdata.getParol().equals( parol ))
                        {
                            Toast.makeText( LoginActivity.this, "Kirish muvaffaqiyatli bajarildi", Toast.LENGTH_SHORT ).show();
                            loading.dismiss();

                            Intent intent = new Intent( LoginActivity.this,MainActivity.class );
                            startActivity( intent );
                        }
                    }

                }
                else
                {
                    Toast.makeText( LoginActivity.this, "Profilda bu"+phone+"numer mavjud", Toast.LENGTH_SHORT ).show();
                    loading.dismiss();
                    Toast.makeText( LoginActivity.this, "Siz yangi accoun yaratishingiz kerak", Toast.LENGTH_SHORT ).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }
}
