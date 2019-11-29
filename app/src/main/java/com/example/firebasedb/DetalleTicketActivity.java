package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Resolucion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DetalleTicketActivity extends AppCompatActivity {

    Ticket t;
    TextView tvTipoTicket;
    TextView tvSedeTicket;
    TextView tvFechaCreacion;
    TextView tvDetalleTicket;
    TextView tvFechaSolTicket;
    TextView tvSolucionTicket;
    EditText etSolucion;
    Button btnInsertar;
    CheckBox ckboxResuelto;

    private DatabaseReference mDatabase;
    private String fecha = "1900-01-01";
    private boolean isBackend = true;
    private boolean resuelto = false;
    //...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detalle);
        initViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            t = bundle.getParcelable("TICKET");
            tvTipoTicket.setText(t.getTipoobj().getTipo_nombre());
            tvSedeTicket.setText((t.getSedeobj().getDireccion()));
            tvFechaCreacion.setText(t.getFecha_creacion());
            tvDetalleTicket.setText(t.getComentario());

            cargarResolucion();
        }
    }


    private void initViews(){
        tvTipoTicket = (TextView) findViewById(R.id.tvTipoTicket);
        tvSedeTicket = (TextView) findViewById(R.id.tvSedeTicket);
        tvFechaCreacion = (TextView) findViewById(R.id.tvFechaCreacion);
        tvFechaSolTicket= (TextView) findViewById(R.id.tvFechaSolTicket);
        tvSolucionTicket  = (TextView) findViewById(R.id.tvSolucionTicket);
        tvDetalleTicket = (TextView) findViewById(R.id.tvDetalleTicket);
        etSolucion = (EditText) findViewById(R.id.etTextSolucion);
        btnInsertar = (Button) findViewById(R.id.btnInsertarSol);
       // ckboxResuelto = (CheckBox) findViewById(R.id.ckboxResuelto);

        //if(ckboxResuelto.isChecked()){
          //  resuelto = true;
        //}

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etSolucion.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Debes de rellenar la solucion", Toast.LENGTH_LONG).show();

                }else{
                    HashMap<String, Boolean> idTicket = new HashMap<>();
                    idTicket.put(t.getId(),true);
                    Resolucion nuevaResolucion = new Resolucion(fecha, etSolucion.getText().toString(),"backend",idTicket);

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("resolucion").child(UUID.randomUUID().toString()).setValue(nuevaResolucion);

                }
            }
        });
    }

    private void initResolucion(Resolucion r){

        tvFechaSolTicket.setText(r.getFecha());
        tvSolucionTicket.setText(r.getComentario());
        //ckboxResuelto.setChecked(r.getResuelto());

    }

    private void setFechaHora(){
        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            fecha = dtf.format(now);
        }else{
            fecha = "2019-11-28";
        }

    }
    private void enabledResolucion(){


       // tvFechaSolTicket.setText("Pendiente");
       // tvSolucionTicket.setText("Pendiente de revisión");
        if(isBackend){
           etSolucion.setVisibility(View.VISIBLE);
           btnInsertar.setVisibility(View.VISIBLE);
           ckboxResuelto.setVisibility(View.VISIBLE);

           setFechaHora();
           tvFechaSolTicket.setText(fecha);

        }else{

            etSolucion.setVisibility(View.GONE);
            btnInsertar.setVisibility(View.GONE);
            ckboxResuelto.setVisibility(View.GONE);

            tvFechaSolTicket.setText("Pendiente");
            tvSolucionTicket.setText("Pendiente de revisión");
        }
    }


    private void cargarResolucion() {
       // final Resolucion resolucion;



        mDatabase = FirebaseDatabase.getInstance().getReference("resolucion");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean noTieneResolucion = true;
                for (DataSnapshot resoluciones : dataSnapshot.getChildren()) {
                    final Resolucion resolucion = resoluciones.getValue(Resolucion.class);
                    //Al objeto sede le obtengo el HasMap de poblacion
                    for(Map.Entry<String,Boolean> entry: resolucion.getTicket().entrySet()) {

                        String id_ticket = entry.getKey();

                        if(id_ticket.equals(t.getId())){
                            initResolucion(resolucion);
                            noTieneResolucion = false;
                        }
                    }
                }

                if(noTieneResolucion){
                    enabledResolucion();
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
