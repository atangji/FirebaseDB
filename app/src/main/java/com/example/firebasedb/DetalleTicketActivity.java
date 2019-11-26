package com.example.firebasedb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.firebasedb.Model.Ticket;

public class DetalleTicketActivity extends AppCompatActivity {

    Ticket t;
    EditText eTextTipoTicket;
    //...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detalle);
        initViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            t = bundle.getParcelable("TICKET");
            eTextTipoTicket.setText(t.getTipoobj().getTipo_nombre());
            //..
        }
    }


    private void initViews(){
        eTextTipoTicket = (EditText)findViewById(R.id.eTextTipoTicket);
        //...
    }
}
