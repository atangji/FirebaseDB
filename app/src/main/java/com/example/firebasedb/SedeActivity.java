package com.example.firebasedb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Adapters.TicketAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
import com.example.firebasedb.Model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SedeActivity extends AppCompatActivity {

    RecyclerView rvSede;
    ArrayList<Sede> sedes_array = new ArrayList<Sede>();
    FloatingActionButton fabInsertarSede;

    private DatabaseReference mDatabase;
    Usuario u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sede);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
             u = bundle.getParcelable("USUARIO");
        }



        rvSede = (RecyclerView) findViewById(R.id.rvSede);
        rvSede.setHasFixedSize(true);
        rvSede.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        cargarSedeFirebase();


        FloatingActionButton fabInsertarSede = (FloatingActionButton) findViewById(R.id.fabInsertarSede);
        fabInsertarSede.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), InsertarSedeActivity.class);
                startActivity(i);
            }
        });


    }


    private void cargarSedeFirebase() {
        final Sede sede;

        fabInsertarSede = (FloatingActionButton) findViewById(R.id.fabInsertarSede);
        mDatabase = FirebaseDatabase.getInstance().getReference("sede");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot sedes : dataSnapshot.getChildren()) {
                    final Sede sede = sedes.getValue(Sede.class);
                    for (Map.Entry<String, Boolean> entryUsuario : sede.getUsuarios().entrySet()) {
                        if(entryUsuario.getKey().equals(u.getId())){


                    //Al objeto sede le obtengo el HasMap de poblacion
                    for (Map.Entry<String, Boolean> entry : sede.getPoblacion().entrySet()) {

                        String id_poblacion = entry.getKey();

                        FirebaseDatabase.getInstance().getReference("poblacion").child(id_poblacion).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Poblacion poblacion = dataSnapshot.getValue(Poblacion.class);
                                sede.setPoblobj(poblacion);

                                sedes_array.add(sede);
                                SedeAdapter sedeadapter = new SedeAdapter(sedes_array);
                                rvSede.setAdapter(sedeadapter);
                                //Al objeto sede le obtengo el HasMap de poblacion

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i("ERROR SEDE", databaseError.getMessage());
                            }
                        });
                    }

                        }
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