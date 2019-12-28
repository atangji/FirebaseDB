package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedb.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private EditText textEmail;
    private EditText textPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private TextView textViewRegistrar;
    private TextView tvRecordarPassword;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

//firebaseAuth.signOut();
        if(firebaseAuth.getCurrentUser()!=null){

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("usuarios/"+firebaseAuth.getCurrentUser().getUid());

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario user = dataSnapshot.getValue(Usuario.class);

                    Intent i=new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra(RegistroActivity.EXTRA_USER,user);
                    startActivity(i);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        textEmail = (EditText) findViewById(R.id.txtEmail);
        textPassword=(EditText) findViewById(R.id.txtPassword);
        tvRecordarPassword = (TextView) findViewById(R.id.tVrecordarPassword);

        textViewRegistrar=(TextView) findViewById(R.id.txtVregistrar);
        btnLogin=(Button) findViewById(R.id.btnlogin);

        progressDialog = new ProgressDialog(this);

        textViewRegistrar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent i=new Intent(getApplicationContext(), RegistroActivity.class);
                 startActivity(i);
             }
         });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            logar_usuario();
            }
        });
        tvRecordarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //obtenemos mail y contraseña
                email = textEmail.getText().toString().trim().toLowerCase();

                //Verificamos cajas no vacías
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Se debe insertar un usuario", Toast.LENGTH_LONG).show();
                    return;
                }else{

                    progressDialog.setMessage("Espera un momento..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    resetPassword();

                }

            }
        });
    }


    private void logar_usuario(){

        //obtenemos mail y contraseña
        final String email= textEmail.getText().toString().trim().toLowerCase();
        String password = textPassword.getText().toString().trim();

        //Verificamos cajas no vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe insertar un usuario",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Se debe insertar un password",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Realizando consulta en linea..");
        progressDialog.show();

        //Creamos un nuevo usuario
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Comprobamos si es correcto
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Bienvenido: "+ textEmail.getText(),Toast.LENGTH_LONG).show();

                            //CApturamos el usuario para mandarlo a la siguiente actividad
                            Integer posicion = email.indexOf("@");
                            String user = email.substring(0,posicion) ;
                            Intent intent = new Intent(getApplication(),MainActivity.class);
                            //intent.putExtra(MenuSlideActivity.usuario, email);
                            startActivity(intent);

                        }else{


                            //Comprobamos si el usuario existe
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {//Si el usuario ya existe

                                Toast.makeText(LoginActivity.this, "El usuario ya existe", Toast.LENGTH_LONG).show();
                            }

                            Toast.makeText(LoginActivity.this,"No se pudo logar el usuario",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void resetPassword(){

        //obtenemos mail y contraseña
        email = textEmail.getText().toString().trim().toLowerCase();

            firebaseAuth.setLanguageCode("es");
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(LoginActivity.this,"Se ha enviado un correo para reestablecer la contraseña",Toast.LENGTH_LONG).show();
                    }else{

                        Toast.makeText(LoginActivity.this,"No se pudo enviar el correo para reestablecer la contraseña",Toast.LENGTH_LONG).show();
                    }

                    progressDialog.dismiss();

                }
            });



    }

    public void onClick(View view){

        switch (view .getId()){


            case R.id.btnlogin:
                logar_usuario();

        }

    }
}
