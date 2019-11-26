package com.example.firebasedb;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Adapters.TicketAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
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


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sede);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvSede = (RecyclerView) findViewById(R.id.rvSede);
        rvSede.setHasFixedSize(true);
        rvSede.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        cargarSedeFirebase();




    }


    private void cargarSedeFirebase() {
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