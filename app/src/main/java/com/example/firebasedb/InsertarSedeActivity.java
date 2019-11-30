package com.example.firebasedb;

import android.os.Bundle;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InsertarSedeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Spinner eTextCPSede,eTextProvinciaSede,eTextPoblacionSede;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_sede);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                insertarFirebaseSede();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initViews(){
        eTextCPSede = (Spinner) findViewById(R.id.eTextCPSede);
        eTextProvinciaSede = (Spinner) findViewById(R.id.eTextProvinciaSede);
        eTextPoblacionSede = (Spinner) findViewById(R.id.eTextPoblacionSede);
    }
    private void insertarFirebaseSede() {
        final Sede sede;


        mDatabase = FirebaseDatabase.getInstance().getReference("poblacion");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String cp = eTextCPSede.getSelectedItem().toString();
                String loc = eTextPoblacionSede.getSelectedItem().toString();
                String prov = eTextProvinciaSede.getSelectedItem().toString();

                for (DataSnapshot poblaciones : dataSnapshot.getChildren()) {
                    final Poblacion poblacion = poblaciones.getValue(Poblacion.class);



                    if(poblacion.getCp().equals(cp) && poblacion.getProvincia().toLowerCase().equals(prov.toLowerCase()) && poblacion.getPoblacion().toLowerCase().equals(loc.toLowerCase())){

                        HashMap<String, Boolean> idPoblacion = new HashMap<>();
                        HashMap<String, Boolean> idUsuario = new HashMap<>();
                        idPoblacion.put(poblacion.getId(),true);
                        idUsuario.put("11144477A",true);

                        String id_nueva_sede = UUID.randomUUID().toString();

                        Sede nuevaSede = new Sede(id_nueva_sede,"Nombreee sede",idPoblacion,idUsuario);

                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("sede").child(id_nueva_sede).setValue(nuevaSede);


                        //FIN
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);
    }


}
