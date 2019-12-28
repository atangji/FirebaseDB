package com.example.firebasedb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PerfilUsuario extends AppCompatActivity {

    final static String EXTRA_USER = "USER";

    EditText etNombre, etEmail, etTelefono, etPass, etPassRep;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        initViews();
    }


    private void initViews(){

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etNombre = (EditText) findViewById(R.id.etPerfilNombre);
        etEmail = (EditText) findViewById(R.id.etPerfilCorreo);
        etTelefono = (EditText) findViewById(R.id.etPerfilTelefono);
    }
}
