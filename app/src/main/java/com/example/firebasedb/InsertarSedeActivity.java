package com.example.firebasedb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InsertarSedeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private Spinner eTextPoblacionSede;
    private EditText eTextCPSede, etDireccion;
    private TextView eTextProvinciaSede;
    String cp_seleccionado;
    String poblacion_seleccionado;
    String provincia_seleccionado;
    String id_poblacion_seleccionado;
    Usuario u;
    ArrayList<Poblacion> poblaciones_seleccionda=new ArrayList<Poblacion>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_sede);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            u = bundle.getParcelable(Constants.EXTRA_USER);

            initViews();
        }
    }
    private void initViews(){
        progressDialog = new ProgressDialog(this);
        eTextCPSede = (EditText) findViewById(R.id.etCP);
        etDireccion = (EditText) findViewById(R.id.eTextSedeDir);
        eTextProvinciaSede = (TextView) findViewById(R.id.eTextProvinciaSede);
        eTextPoblacionSede = (Spinner) findViewById(R.id.eTextPoblacionSede);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Poblacion poblacion_guardar = null;
                String cp_poblacion_seleccionada = eTextPoblacionSede.getSelectedItem().toString();
                for (Poblacion p: poblaciones_seleccionda){
                    if(p.getPoblacion().equals(cp_poblacion_seleccionada)){
                          poblacion_guardar = p;
                    }
                }

                if (poblacion_guardar != null) {
                    String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String dir = etDireccion.getText().toString();

                    if (TextUtils.isEmpty(dir)) {

                        Toast.makeText(getApplicationContext(),"Debes de insertar una direccion",Toast.LENGTH_LONG).show();
                    }else{
                        String sede_id = UUID.randomUUID().toString();
                        HashMap<String, Boolean> poblacion_sede = new HashMap<>();
                        poblacion_sede.put(poblacion_guardar.getId(),true);
                        HashMap<String, Boolean> usuarios_sede = new HashMap<>();
                        usuarios_sede.put(idUser,true);
                        Sede sede_nueva = new Sede(sede_id, dir,poblacion_sede,usuarios_sede);
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        progressDialog.setMessage("Insertando sede..");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        mDatabase.child("sede").child(sede_id).setValue(sede_nueva,new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Se insertó la sede con éxito", Toast.LENGTH_LONG).show();
                                    /*Intent i=new Intent(getApplicationContext(), SedeActivity.class);
                                    i.putExtra(Constants.EXTRA_USER, u);
                                    startActivity(i);
                                    finish();*/
                                    Intent back = getIntent();
                                    back.putExtra(Constants.EXTRA_USER, u);
                                    setResult(RESULT_OK, back);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Ups¡ No se ha podido guardar la sede", Toast.LENGTH_LONG).show();

                                }

                            }

                        });
                    }

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

    private void cargarSpinnerPoblacion() {
        final Sede sede;
        final ArrayList<String> poblaciones_nombres = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("poblacion");
        Query queryRef = mDatabase.orderByChild("cp").equalTo(cp_seleccionado);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot poblaciones : dataSnapshot.getChildren()) {

                    final Poblacion poblacion = poblaciones.getValue(Poblacion.class);
                    poblaciones_nombres.add(poblacion.getPoblacion());
                    provincia_seleccionado = poblacion.getProvincia();
                    poblaciones_seleccionda.add(poblacion);
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,  poblaciones_nombres);
                eTextPoblacionSede.setAdapter(adapter);
                eTextProvinciaSede.setText(provincia_seleccionado);
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        queryRef.addValueEventListener(postListener);
    }


    public void clickVerPoblaciones(View view) {

        String cp = eTextCPSede.getText().toString();
        if(cp.length()<=5 &&  !TextUtils.isEmpty(cp) && TextUtils.isDigitsOnly(cp)){
            progressDialog.setMessage("Cargando Poblaciones...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            cp_seleccionado = cp;
            cargarSpinnerPoblacion();
        }else{
            Toast.makeText(getApplicationContext(), "Revisa el CP, debe de estar relleno por un formato válido", Toast.LENGTH_SHORT).show();
        }
    }
}
