package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebasedb.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends AppCompatActivity {

    final static String EXTRA_USER = "USER";

    EditText etNombre, etEmail, etTelefono;
    TextInputLayout etPass, etPassRep;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        initViews();
    }


    private void initViews(){

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etNombre = (EditText) findViewById(R.id.etRegistroNombre);
        etEmail = (EditText) findViewById(R.id.etRegistroEmail);
        etTelefono = (EditText) findViewById(R.id.etRegistroTelefono);
        etPass = (TextInputLayout) findViewById(R.id.etRegistroPassword);
        etPassRep = (TextInputLayout) findViewById(R.id.etRegistroRepPassword);

        etPass.setHintEnabled(false);
        etPassRep.setHintEnabled(false);
    }

    public void clickRegistrar(View view) {

        String nombre = etNombre.getText().toString();
        String email = etEmail.getText().toString();
        String telefono_str = etTelefono.getText().toString();
        String pass = etPass.getEditText().getText().toString();
        String pass_rep = etPassRep.getEditText().getText().toString();

        if(TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(telefono_str) ||
                TextUtils.isEmpty(pass) || TextUtils.isEmpty(pass_rep)){
            Toast.makeText(getApplicationContext(),"Debes de rellenar todos los campos", Toast.LENGTH_LONG).show();
        }else{

            if(pass.equals(pass_rep)){
                int telefono = Integer.parseInt(telefono_str);
                Usuario u = new Usuario(email,pass,nombre,telefono);
                registroFirebase(u);

            }else{
                Toast.makeText(getApplicationContext(),"Las contraseñas deben de coincidir", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void registroFirebase(final Usuario user){

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    user.setId(firebaseUser.getUid());
                    mDatabase.child("usuarios").child(firebaseUser.getUid()).setValue(user);

                    Intent i=new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra(EXTRA_USER,user);
                    startActivity(i);

                }else{
                    try{
                        throw task.getException();
                    } catch(FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(getApplicationContext(),"El email / password no tiene un formato valido: "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }catch(FirebaseAuthUserCollisionException e){
                        Toast.makeText(getApplicationContext(),"El email "+ user.getEmail()+" ya está registrado", Toast.LENGTH_LONG).show();
                    } catch(Exception e){
                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error mientras se registraba", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
