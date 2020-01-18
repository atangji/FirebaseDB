package com.example.firebasedb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PerfilUsuario extends AppCompatActivity {



    Usuario u;
    TextView tvMail,tvErrorPerfil, tvErrorPass;
    EditText etNombre, etTelefono, etPass, etPassRep;
    Button btnEditarUsuario, btnCambiarPassword;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

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
            tvMail.setText(u.getEmail());
            etTelefono.setText(String.valueOf(u.getTelefono()));
        }


    }


    private void initViews(){

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etNombre = (EditText) findViewById(R.id.etPerfilNombre);
        tvMail = (TextView) findViewById(R.id.tvPerfilCorreo);
        etTelefono = (EditText) findViewById(R.id.etPerfilTelefono);
        etPass = (EditText) findViewById(R.id.eTPerfilPassword);
        etPassRep = (EditText) findViewById(R.id.eTPerfilRepitePassword);

        btnCambiarPassword = (Button) findViewById(R.id.btnPerfilCambiaPassword);
        btnEditarUsuario = (Button) findViewById(R.id.btnPerfilEditarUsuario);

        tvErrorPerfil = (TextView) findViewById((R.id.tVErroresPerfil));
        //ocultamos
        tvErrorPerfil.setVisibility(View.GONE);
        tvErrorPass = (TextView) findViewById(R.id.tVErroresPass);
        //ocultamos
        tvErrorPass.setVisibility(View.GONE);

        btnEditarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //campos telefono y nombre
                String strNombre = etNombre.getText().toString();
                String strTelefono = etTelefono.getText().toString();
                Boolean errorPerfil =false;
                char primerCaracter = strTelefono.charAt(0);



                //comprueba que los campos no están vacíos
                if (strNombre.isEmpty() ||strTelefono.isEmpty()){

                    //aparece mensaje conforme no pueden haber campos vacíos
                    tvErrorPerfil.setVisibility(View.VISIBLE);
                    tvErrorPerfil.setText("Los campos no pueden estar vacíos");
                    errorPerfil=true;

                }else if (strTelefono.length()!=9 ) {

                    //aparece un mensaje conforme el teléfono no es correcto
                    tvErrorPerfil.setVisibility(View.VISIBLE);
                    tvErrorPerfil.setText("El teléfono no puede ser inferior a 9 dígitos");
                    errorPerfil=true;

                }else if (strTelefono.charAt(0)!='9'&& strTelefono.charAt(0)!='7' && strTelefono.charAt(0)!='6'){

                    //aparece un mensaje conforme el teléfono no es correcto
                    tvErrorPerfil.setVisibility(View.VISIBLE);
                    tvErrorPerfil.setText("El teléfono debe empezar por 9, 7 o 6");
                    errorPerfil=true;

                }else if (strNombre!=u.getNombre() || strNombre!=String.valueOf(u.getTelefono())){

                    //aparece un mensaje conforme los valore no han cambiado
                    tvErrorPerfil.setVisibility(View.VISIBLE);
                    tvErrorPerfil.setText("No has modificado datos");
                    errorPerfil=true;
                }

                if(!errorPerfil){
                    //Guarda los cambios
                    u.setNombre(strNombre);
                    u.setTelefono(Integer.parseInt(strTelefono));


                 /*   mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("usuarios").child(u.getId()).setValue(u);
                    callback insertar
                    */


                    tvErrorPerfil.setVisibility(View.VISIBLE);
                    tvErrorPerfil.setText("Datos modificados");
                }


            }
        });

        btnCambiarPassword.setOnClickListener(new View.OnClickListener() {

            String StrPass = etPass.getText().toString();
            String StrPassRep = etPassRep.getText().toString();
            Boolean errorPass =false;

            @Override
            public void onClick(View v) {

                //Comprueba que los campos password no están vacíos
                if (StrPass.isEmpty() ||StrPassRep.isEmpty()){

                    tvErrorPass.setVisibility(View.VISIBLE);
                    tvErrorPass.setText("Los campos passwords no pueden estar vacíos");
                    errorPass=true;

                }else if (StrPass!=StrPassRep) {
                    //Comprueba que los passwords coinciden

                    tvErrorPass.setVisibility(View.VISIBLE);
                    tvErrorPass.setText("Los passwords no coinciden");
                    errorPass=true;

                }else if(StrPass!=u.getPassword().toString()) {
                    //Comprueba que el password no es igual al anterior

                    tvErrorPass.setVisibility(View.VISIBLE);
                    tvErrorPass.setText("El nuevo password debe ser distinto al anterior");
                    errorPass=true;

                }


                if(!errorPass){
                    //Guarda los cambios
                    //Guarda los cambios
                    u.setPassword(StrPass);
                     /*   mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("usuarios").child(u.getId()).setValue(u);
                    callback insertar

                    AUTH
                    */
                    tvErrorPass.setVisibility(View.VISIBLE);
                    tvErrorPass.setText("Contraseña cambiada");
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





}
