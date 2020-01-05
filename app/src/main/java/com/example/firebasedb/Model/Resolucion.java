package com.example.firebasedb.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Resolucion implements Parcelable {

    String fecha;
    String comentario;
    String backend;
    String id;
    HashMap<String, Boolean> ticket;
    Boolean resuelto;


    public Resolucion(){

    }


    public Resolucion(String id, String fecha, String comentario, String backend, HashMap<String, Boolean> ticket, Boolean resuelto)  {
        this.id = id;
        this.fecha = fecha;
        this.comentario = comentario;
        this.backend = backend;
        this.ticket = ticket;
        this.resuelto =resuelto;
    }



    protected Resolucion(Parcel in) {
        id = in.readString();
        fecha = in.readString();
        comentario = in.readString();
        backend = in.readString();
        byte resuelto = in.readByte();

    }


    public static final Creator<Resolucion> CREATOR = new Creator<Resolucion>() {
        @Override
        public Resolucion createFromParcel(Parcel in) {
            return new Resolucion(in);
        }

        @Override
        public Resolucion[] newArray(int size) {
            return new Resolucion[size];
        }
    };

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public Boolean getResuelto() {
        return resuelto;
    }

    public void setResuelto(Boolean resuelto) {
        this.resuelto = resuelto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(fecha);
        dest.writeString(comentario);
        dest.writeString(backend);
        dest.writeString(String.valueOf(ticket));
        dest.writeByte((byte) (resuelto ? 1 : 0));
    }

    public HashMap<String, Boolean> getTicket() {
        return ticket;
    }

    public void setTicket(HashMap<String, Boolean> ticket) {
        this.ticket = ticket;
    }

}