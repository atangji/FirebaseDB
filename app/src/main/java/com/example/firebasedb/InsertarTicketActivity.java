package com.example.firebasedb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
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

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class InsertarTicketActivity extends AppCompatActivity {

    Spinner eTextTipoTicket, eTextSedeTicket;
    EditText eTextDetalleTicket;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    ArrayList<Tipo> tipos_obj_array = new ArrayList<>();
    ArrayList<String> tipos_array = new ArrayList<>();
    ArrayList<Sede> sedes_obj_array = new ArrayList<>();
    ArrayList<String> sedes_array = new ArrayList<>();
    Usuario u;
    String ticket_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_ticket);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            u = bundle.getParcelable(Constants.EXTRA_USER);
            cargarTipos();
            cargarSedes();
        }

    }

    private void initViews(){

        //Método para inicializar la vista
        eTextTipoTicket = (Spinner)findViewById(R.id.eTextTipoTicket);
        eTextSedeTicket = (Spinner)findViewById(R.id.eTextSedeTicket);
        eTextDetalleTicket = (EditText) findViewById(R.id.eTextDetalleTicket);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                compruebaConexion();

                //Comentario
                String comentario = eTextDetalleTicket.getText().toString();

                //Comprobamos que el comentario y la sede estén rellenos
                if (TextUtils.isEmpty(comentario) || TextUtils.isEmpty(eTextSedeTicket.getSelectedItem().toString()) || eTextSedeTicket.getSelectedItem()==null) {
                    Toast.makeText(getApplicationContext(), "El comentario y la sede son obligatorios", Toast.LENGTH_LONG).show();

                }else{

                    String sede = eTextSedeTicket.getSelectedItem().toString();
                    //Sede
                    String idSede = getSede(sede);
                    HashMap<String, Boolean> ticket_sede = new HashMap<>();
                    ticket_sede.put(idSede,true);
                    //Tipo
                    String tipo = eTextTipoTicket.getSelectedItem().toString();
                    String idTipo = getTipo(tipo);
                    HashMap<String, Boolean> ticket_tipo = new HashMap<>();
                    ticket_tipo.put(idTipo,true);
                    //Usuario
                    HashMap<String, Boolean> ticket_usuario = new HashMap<>();
                    ticket_usuario.put(u.getId(),true);
                    //ID ticket
                    ticket_id = UUID.randomUUID().toString();
                    ticket_id = ticket_id.substring(0,6);

                    //fecha
                    String fecha ="1900-01-01";
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    cal.add(Calendar.HOUR, +1);
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    fecha = format1.format(cal.getTime());

                    Ticket ticket = new Ticket(comentario, fecha, ticket_id,ticket_sede, ticket_tipo, ticket_usuario);

                    //Conectamos a firebase para insertar el ticket
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    progressDialog.setMessage("Insertando ticket..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mDatabase.child("ticket").child(ticket_id).setValue(ticket,new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Se insertó el ticket con id:"+ticket_id +" con éxito", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                                i.putExtra(Constants.EXTRA_USER, u);
                                startActivity(i);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Ups¡ No se ha podido guardar el ticket", Toast.LENGTH_LONG).show();

                            }

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

    private String getTipo(String nombre){
        for (Tipo tipo: tipos_obj_array){
            if(tipo.getTipo_nombre().equals(nombre)){
                return tipo.getId();
            }
        }
        return null;
    }

    private String getSede(String nombre){
        for (Sede sede: sedes_obj_array){
            if(sede.getDireccion().equals(nombre)){
                return sede.getId();
            }
        }
        return null;
    }

    private void cargarTipos() {


        //Accedemos al nodo tipo de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("tipo");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot tipos : dataSnapshot.getChildren()) {
                    final Tipo tipo = tipos.getValue(Tipo.class);
                    tipos_obj_array.add(tipo);
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

        //Accedemos al nodo sede de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("sede");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot sedes : dataSnapshot.getChildren()) {
                    final Sede sede = sedes.getValue(Sede.class);
                    String usuario_id_sede="";
                    for(Map.Entry<String,Boolean> entry: sede.getUsuarios().entrySet()) {
                        usuario_id_sede= entry.getKey();
                        if(usuario_id_sede.equals(u.getId())){
                            sedes_obj_array.add(sede);
                            sedes_array.add(sede.getDireccion());

                        }
                    }
                }
                ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_expandable_list_item_1, sedes_array);
                eTextSedeTicket.setAdapter(adapter);


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

        //Comprobamos que haya conexión a internet
        if(!isOnlineNet(this)){

            Toast.makeText(InsertarTicketActivity.this, "No se ha podido conectar al servidor, revisa tu conexión a internet", Toast.LENGTH_LONG).show();
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
