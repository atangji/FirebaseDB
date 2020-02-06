package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class PerfilUsuario extends AppCompatActivity {



    Usuario u;
    EditText etNombre, etTelefono;
    TextInputLayout etPass,etPassRep;
    TextInputEditText eTextPass, eTextPassRep;
    Button btnEditarUsuario, btnCambiarPassword;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    final static String EXTRA_USER = "USER";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        initViews();

        //Recibimos los extras de la activity anterior:
        Bundle bundle = getIntent().getExtras();

        //Rellenamos los edit text con la información del usuario
        if(bundle!=null){
            u = bundle.getParcelable(Constants.EXTRA_USER);
            etNombre.setText(u.getNombre());
            //tvMail.setText(u.getEmail());
            etTelefono.setText(String.valueOf(u.getTelefono()));
            setTitle("Perfil: "+u.getEmail());
        }


    }

    private void initViews(){

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etNombre = (EditText) findViewById(R.id.etPerfilNombre);
        //tvMail = (TextView) findViewById(R.id.tvPerfilCorreo);
        etTelefono = (EditText) findViewById(R.id.etPerfilTelefono);
        etPass = (TextInputLayout) findViewById(R.id.eTPerfilPassword);
        etPass.setHintEnabled(false);
        etPassRep = (TextInputLayout) findViewById(R.id.eTPerfilRepitePassword);
        etPassRep.setHintEnabled(false);
        eTextPass =(TextInputEditText) findViewById(R.id.textInputPass);
        eTextPassRep =(TextInputEditText) findViewById(R.id.textInputPassRep);

        btnCambiarPassword = (Button) findViewById(R.id.btnPerfilCambiaPassword);
        btnEditarUsuario = (Button) findViewById(R.id.btnPerfilEditarUsuario);

        progressDialog = new ProgressDialog(this);

        btnEditarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                compruebaConexion();
                //campos telefono y nombre
                String strNombre = etNombre.getText().toString();
                String strTelefono = etTelefono.getText().toString();
                Boolean errorPerfil =false;
                char primerCaracter = strTelefono.charAt(0);

                //comprueba que los campos no están vacíos
                if (strNombre.isEmpty() ||strTelefono.isEmpty()){

                    //aparece mensaje conforme no pueden haber campos vacíos
                    Toast.makeText(getApplicationContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_LONG).show();
                    errorPerfil=true;

                }else if (strTelefono.length()!=9 ) {

                    //aparece un mensaje conforme el teléfono no es correcto
                    Toast.makeText(getApplicationContext(), "El teléfono no puede ser inferior a 9 dígitos", Toast.LENGTH_LONG).show();
                    errorPerfil=true;

                }else if (strTelefono.charAt(0)!='9'&& strTelefono.charAt(0)!='7' && strTelefono.charAt(0)!='6'){

                    //aparece un mensaje conforme el teléfono no es correcto
                    Toast.makeText(getApplicationContext(), "El teléfono debe empezar por 9, 7 o 6", Toast.LENGTH_LONG).show();
                    errorPerfil=true;

                }else if (strNombre.equalsIgnoreCase(u.getNombre()) || strNombre.equalsIgnoreCase(String.valueOf(u.getTelefono()))){

                    //aparece un mensaje conforme los valore no han cambiado
                    Toast.makeText(getApplicationContext(), "No has modificado ningún dato", Toast.LENGTH_LONG).show();
                    errorPerfil=true;
                }

                if(!errorPerfil){
                    //Guarda los cambios
                    u.setNombre(strNombre);
                    u.setTelefono(Integer.parseInt(strTelefono));

                    progressDialog.setMessage("Modificando datos del usuario..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();



                    mDatabase.child("usuarios").child(u.getId()).setValue(u,new DatabaseReference.CompletionListener() {



                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                progressDialog.dismiss();

                                Toast.makeText(getApplicationContext(), "Se han modificado los datos del usuario", Toast.LENGTH_LONG).show();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Ups¡ No se han podido guardar los datos", Toast.LENGTH_LONG).show();
                            }

                        }


                        //CALLBACK NO ACTUALIZA DATOS DEL OBJETO USUARIO ???
                    });

                }


            }
        });

        btnCambiarPassword.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                compruebaConexion();

                etPass.setPasswordVisibilityToggleEnabled(true);
                etPassRep.setPasswordVisibilityToggleEnabled(true);


                final String StrPass = etPass.getEditText().getText().toString();
                String StrPassRep = etPassRep.getEditText().getText().toString();
                Boolean errorPass =false;


                //Comprueba que los campos password no están vacíos
                if (StrPass.isEmpty() ||StrPassRep.isEmpty()){

                    Toast.makeText(getApplicationContext(), "Los campos passwords no pueden estar vacíos", Toast.LENGTH_LONG).show();
                    errorPass=true;

                }else if (!StrPass.equals(StrPassRep)) {
                    //Comprueba que los passwords coinciden

                    Toast.makeText(getApplicationContext(), "Los passwords no coinciden", Toast.LENGTH_LONG).show();
                    errorPass=true;

                }else if(StrPass.equals(u.getPassword())) {
                    //Comprueba que el password no es igual al anterior

                    Toast.makeText(getApplicationContext(), "El nuevo password debe ser distinto al anterior", Toast.LENGTH_LONG).show();
                    errorPass=true;

                }


                if(!errorPass){

                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!= null){


                        user.updatePassword(StrPass)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            //Guarda los cambios en el objeto del nuevo pass
                                            u.setPassword(StrPass);


                                            //Accedemos al nodo usuarios de firebase
                                            mDatabase.child("usuarios").child(u.getId()).setValue(u,new DatabaseReference.CompletionListener() {



                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        progressDialog.dismiss();

                                                        Toast.makeText(getApplicationContext(), "Nuevo password guardado", Toast.LENGTH_LONG).show();

                                                    } else {
                                                        progressDialog.dismiss();

                                                    }
                                                }


                                                //CALLBACK NO ACTUALIZA DATOS DEL OBJETO USUARIO ???
                                            });

                                        }else{

                                            Toast.makeText(getApplicationContext(), "Ups¡ No se han podido guardar los datos", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });

                    }
                }

            }
        });

        eTextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPass.setPasswordVisibilityToggleEnabled(true);
            }
        });

        eTextPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(!isFocused){
                    etPass.setPasswordVisibilityToggleEnabled(false);
                }else{

                    eTextPass.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if(eTextPass.getText().toString().length() > 0) {
                                etPass.setPasswordVisibilityToggleEnabled(true);
                            }
                            else{
                                etPass.setPasswordVisibilityToggleEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            etPass.setPasswordVisibilityToggleEnabled(true);
                        }
                    });

                }
            }
        });


        eTextPassRep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(!isFocused){
                    etPassRep.setPasswordVisibilityToggleEnabled(false);
                }else{

                    eTextPassRep.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if(eTextPassRep.getText().toString().length() > 0) {
                                etPassRep.setPasswordVisibilityToggleEnabled(true);
                            }
                            else{
                                etPassRep.setPasswordVisibilityToggleEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            etPassRep.setPasswordVisibilityToggleEnabled(true);
                        }
                    });

                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                Intent back = getIntent();
                back.putExtra(Constants.EXTRA_USER, u);
                setResult(RESULT_OK, back);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void compruebaConexion(){

        if(!isOnlineNet(this)){

            Toast.makeText(PerfilUsuario.this, "No se ha podido conectar al servidor, revisa tu conexión a internet", Toast.LENGTH_LONG).show();
        }
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

}
