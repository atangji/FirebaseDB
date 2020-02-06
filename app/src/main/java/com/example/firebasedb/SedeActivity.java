package com.example.firebasedb;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Adapters.TicketAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import static android.widget.Toast.LENGTH_LONG;

public class SedeActivity extends AppCompatActivity {

    RecyclerView rvSede;
    TextView emtpyTv;
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

            u = bundle.getParcelable(RegistroActivity.EXTRA_USER);
            rvSede = (RecyclerView) findViewById(R.id.rvSede);
            emtpyTv = (TextView) findViewById(R.id.tvEmpty);

            rvSede.setHasFixedSize(true);
            rvSede.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            cargarSedeFirebase();

            FloatingActionButton fabInsertarSede = (FloatingActionButton) findViewById(R.id.fabInsertarSede);
            if(Constants.ID_ADMIN.equals(u.getId())){

             fabInsertarSede.setVisibility(View.GONE);
            }

            fabInsertarSede.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(v.getContext(), InsertarSedeActivity.class);
                    i.putExtra(Constants.EXTRA_USER, u);
                    startActivity(i);
                    finish();
                }
            });

        }else{
            Toast.makeText(getApplicationContext(), "error al cargar usuarios", LENGTH_LONG).show();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    u = data.getParcelableExtra(Constants.EXTRA_USER);
                }
            }
        }
    }

    private void cargarSedeFirebase() {

        compruebaConexion();

        final Sede sede;

        fabInsertarSede = (FloatingActionButton) findViewById(R.id.fabInsertarSede);
        //Accedemos al nodo sede de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("sede");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot sedes : dataSnapshot.getChildren()) {
                    final Sede sede = sedes.getValue(Sede.class);
                    for (Map.Entry<String, Boolean> entryUsuario : sede.getUsuarios().entrySet()) {
                        if(entryUsuario.getKey().equals(u.getId()) || Constants.ID_ADMIN.equals(u.getId())){


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

                                        if(sedes_array.size()==0){
                                            rvSede.setVisibility(View.GONE);
                                            emtpyTv.setVisibility(View.VISIBLE);
                                        }else{
                                            rvSede.setVisibility(View.VISIBLE);
                                            emtpyTv.setVisibility(View.GONE);
                                        }

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

    public void compruebaConexion(){

        if(!isOnlineNet(this)){

            Toast.makeText(SedeActivity.this, "No se ha podido conectar al servidor, revisa tu conexión a internet", Toast.LENGTH_LONG).show();
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