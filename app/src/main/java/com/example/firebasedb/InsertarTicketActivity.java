package com.example.firebasedb;

import android.os.Bundle;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Tipo;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Map;

public class InsertarTicketActivity extends AppCompatActivity {

    Spinner eTextTipoTicket, eTextSedeTicket;
    private DatabaseReference mDatabase;
    ArrayList<String> tipos_array = new ArrayList<>();
    ArrayList<String> sedes_array = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_ticket);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();

        cargarTipos();
        cargarSedes();
    }

    private void initViews(){
        eTextTipoTicket = (Spinner)findViewById(R.id.eTextTipoTicket);
        eTextSedeTicket = (Spinner)findViewById(R.id.eTextSedeTicket);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void cargarTipos() {
        final Tipo tipo;


        mDatabase = FirebaseDatabase.getInstance().getReference("tipo");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot tipos : dataSnapshot.getChildren()) {
                    final Tipo tipo = tipos.getValue(Tipo.class);
                    tipos_array.add(tipo.getTipo_nombre());
                }

                ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_expandable_list_item_1, tipos_array);
                eTextTipoTicket.setAdapter(adapter);

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


    private void cargarSedes() {
        final Sede sede;


        mDatabase = FirebaseDatabase.getInstance().getReference("sede");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot sedes : dataSnapshot.getChildren()) {
                    final Sede sede = sedes.getValue(Sede.class);

                    //Al objeto sede le obtengo el HasMap de poblacion
                    for (Map.Entry<String, Boolean> entry : sede.getPoblacion().entrySet()) {
                        String id_poblacion = entry.getKey();
                        FirebaseDatabase.getInstance().getReference("poblacion").child(id_poblacion).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Poblacion poblacion = dataSnapshot.getValue(Poblacion.class);

                                String sede_info=sede.getDireccion()+", "+poblacion.getCp()+" "+poblacion.getPoblacion()+" ("+poblacion.getProvincia()+")";
                                sedes_array.add(sede_info);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i("ERROR SEDE", databaseError.getMessage());
                            }
                        });
                    }
                    ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_expandable_list_item_1, sedes_array);
                    eTextSedeTicket.setAdapter(adapter);

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
