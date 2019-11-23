package com.example.firebasedb.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Ticket implements Parcelable {

    String comentario;
    String fecha_creacion;
    String id;
    HashMap<String, Boolean> sede;
    HashMap<String, Boolean> tipo;
    HashMap<String, Boolean> usuarios;



    Sede sedeobj;
    Tipo tipoobj;
    Usuario usuobj;

    public Ticket (){

    }

    public Ticket(String comentario, String fecha_creacion, String id, HashMap<String, Boolean> sede, HashMap<String, Boolean> tipo, HashMap<String, Boolean> usuario) {
        this.comentario = comentario;
        this.fecha_creacion = fecha_creacion;
        this.id = id;
        this.sede = sede;
        this.tipo = tipo;
        this.usuarios = usuario;
    }

    public Ticket(String comentario, String fecha_creacion, String id, Sede sedeobj, Tipo tipoobj, Usuario tipousu) {
        this.comentario = comentario;
        this.fecha_creacion = fecha_creacion;
        this.id = id;
        this.sedeobj = sedeobj;
        this.tipoobj = tipoobj;
        this.usuobj = tipousu;
    }

    protected Ticket(Parcel in) {
        comentario = in.readString();
        fecha_creacion = in.readString();
        id = in.readString();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
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
        dest.writeString(comentario);
        dest.writeString(fecha_creacion);
        dest.writeString(id);
    }


    public Sede getSedeobj() {
        return sedeobj;
    }

    public void setSedeobj(Sede sedeobj) {
        this.sedeobj = sedeobj;
    }

    public Tipo getTipoobj() {
        return tipoobj;
    }

    public void setTipoobj(Tipo tipoobj) {
        this.tipoobj = tipoobj;
    }

    public Usuario getUsuobj() {
        return usuobj;
    }

    public void setUsuobj(Usuario usuobj) {
        this.usuobj = usuobj;
    }
    public HashMap<String, Boolean> getSede() {
        return sede;
    }

    public void setSede(HashMap<String, Boolean> sede) {
        this.sede = sede;
    }


    public HashMap<String, Boolean> getTipo() {
        return tipo;
    }

    public void setTipo(HashMap<String, Boolean> tipo) {
        this.tipo = tipo;
    }


    public HashMap<String, Boolean> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(HashMap<String, Boolean> usuarios) {
        this.usuarios = usuarios;
    }

}
