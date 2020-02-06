package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedb.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextInputLayout textPasswordLayout;
    private TextInputEditText eTextPassword;
    private EditText textEmail;
    private Button btnLogin;
    private TextView textViewRegistrar, tvRecordarPassword;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Método para iniciarlizar vista
        initViews();

        //Comprobamos si hay conexión a internet
        if (!isOnlineNet(this)) {

            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "No se ha podido conectar al servidor, revisa tu conexión a internet", Toast.LENGTH_LONG).show();

        } else {

            //Autologamos al usuario si ha realizado un login anterior sin cerrar sesión
            if (firebaseAuth.getCurrentUser() != null) {

                //Mostramos progress dialog
                progressDialog.setMessage("Autenticando..");
                progressDialog.show();

                //Conectamos a firebase y vamos al nodo usuarios para obtener el usuario
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("usuarios/" + firebaseAuth.getCurrentUser().getUid());
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario user = dataSnapshot.getValue(Usuario.class);

                        //Logamos al usuario y vamos a la activity main, incluimos en extra los datos del objeto usuario
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra(RegistroActivity.EXTRA_USER, user);
                        progressDialog.dismiss();
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
            }

        }
    }



    private void initViews() {
        //Método para inicializar los elementos de la activity

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        textPasswordLayout = (TextInputLayout) findViewById(R.id.textLayoutPassword);
        textPasswordLayout.setHintEnabled(false);

        eTextPassword = (TextInputEditText) findViewById(R.id.textInputPassword);


        textEmail = (EditText) findViewById(R.id.txtEmail);
        tvRecordarPassword = (TextView) findViewById(R.id.tVrecordarPassword);
        textViewRegistrar = (TextView) findViewById(R.id.txtVregistrar);
        btnLogin = (Button) findViewById(R.id.btnlogin);

        //Evento para restablecer el password del usuario
        tvRecordarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //obtenemos mail y contraseña
                email = textEmail.getText().toString().trim().toLowerCase();

                //Verificamos cajas no vacías
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Se debe insertar un usuario", Toast.LENGTH_LONG).show();
                    return;
                } else {

                    progressDialog.setMessage("Espera un momento..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    resetPassword();

                }

            }
        });

        //Funcionaildad toggle password para mostrar input show eyes
        eTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (!isFocused) {
                    textPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                } else {

                    eTextPassword.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if (eTextPassword.getText().toString().length() > 0) {
                                textPasswordLayout.setPasswordVisibilityToggleEnabled(true);
                            } else {
                                textPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            textPasswordLayout.setPasswordVisibilityToggleEnabled(true);
                        }
                    });

                }
            }
        });

        //Click registrar nuevo usuario
        textViewRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(i);
            }
        });

        //Click logar usuario
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                logar_usuario();
            }
        });



    }

    private void logar_usuario(){

        //Comprobamos si hay conexión a internet
        if (!isOnlineNet(this)) {

            //En caso de que no haya avisamos al usuario
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "No se ha podido conectar al servidor, revisa tu conexión a internet", Toast.LENGTH_LONG).show();
        }else {

            //Obtenemos mail y contraseña que ha insertado el usuario:
            final String email= textEmail.getText().toString().trim().toLowerCase();
            String password = textPasswordLayout.getEditText().getText().toString().trim();

            //Verificamos cajas no vacías
            if(TextUtils.isEmpty(email)){
                Toast.makeText(this,"Se debe insertar un usuario",Toast.LENGTH_LONG).show();
                return;
            }

            //Comprobamos el formato del correo
            if(!email.contains("@")){
                Toast.makeText(this,"Se debe insertar un correo electrónico",Toast.LENGTH_LONG).show();
                return;
            }

            //Comprobamos el campo password no esté vacío
            if(TextUtils.isEmpty(password)){
                Toast.makeText(this,"Se debe insertar un password",Toast.LENGTH_LONG).show();
                return;
            }


            progressDialog.setMessage("Autenticando...");
            progressDialog.show();


            //Nos logamos con el usuario en firebase
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //Comprobamos si es correcto
                            if (task.isSuccessful()) {

                                Toast.makeText(LoginActivity.this, "Bienvenido: " + textEmail.getText(), Toast.LENGTH_LONG).show();

                                //Accedemos al nodo usuarios de firebase
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("usuarios/" + firebaseAuth.getCurrentUser().getUid());

                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Usuario user = dataSnapshot.getValue(Usuario.class);

                                        //Iniciamos activity principal con los datos del usuario en extras
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.putExtra(RegistroActivity.EXTRA_USER, user);
                                        startActivity(i);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {

                                //Comprobamos los errores en la autenticación para personalizar el mensaje de error
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(LoginActivity.this, "El email introducido tiene un formato erróneo.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_WRONG_PASSWORD":
                                        Toast.makeText(LoginActivity.this, "El password es erróneo.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_MISMATCH":
                                        Toast.makeText(LoginActivity.this, "El usuario no existe", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_NOT_FOUND":
                                        Toast.makeText(LoginActivity.this, "El usuario no existe", Toast.LENGTH_LONG).show();
                                        break;

                                    default:

                                        Toast.makeText(LoginActivity.this, "No se ha podido logar al usuario.", Toast.LENGTH_SHORT).show();
                                        break;

                                }

                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void resetPassword(){

        progressDialog.setMessage("Restableciendo password...");
        progressDialog.show();

        //obtenemos mail
        email = textEmail.getText().toString().trim().toLowerCase();

            //Mandamos correo de reseteo
            firebaseAuth.setLanguageCode("es");
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    progressDialog.dismiss();
                    if(task.isSuccessful()) {

                        //Lanzamos mensaje de éxito
                        Toast.makeText(LoginActivity.this, "No se pudo enviar el correo para reestablecer la contraseña", Toast.LENGTH_LONG).show();
                    }else{

                        //Comprobamos los errores en la autenticación para personalizar el mensaje de error
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        switch (errorCode) {

                            case "ERROR_USER_NOT_FOUND":
                                Toast.makeText(LoginActivity.this, "No se pudo enviar el correo, el usuario no está registrado.", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_USER_MISMATCH":
                                Toast.makeText(LoginActivity.this, "El usuario no existe", Toast.LENGTH_LONG).show();
                                break;

                            case "ERROR_INVALID_EMAIL":
                                Toast.makeText(LoginActivity.this, "El email introducido tiene un formato erróneo.", Toast.LENGTH_LONG).show();
                                break;

                            default:

                                Toast.makeText(LoginActivity.this,"No se pudo enviar el correo para reestablecer la contraseña",Toast.LENGTH_LONG).show();
                                break;
                        }

                        Toast.makeText(LoginActivity.this,"Se ha enviado un correo para reestablecer la contraseña",Toast.LENGTH_LONG).show();
                    }


                }

            });



    }

    public Boolean isOnlineNet(Context context)
    {
        boolean isOnlineNet = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                isOnlineNet = true;
            }
        }
        return  isOnlineNet;
    }

    public void onClick(View view){

        switch (view .getId()){


            case R.id.btnlogin:
                logar_usuario();

        }

    }
}
