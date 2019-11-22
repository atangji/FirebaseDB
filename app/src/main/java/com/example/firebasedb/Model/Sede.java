package com.example.firebasedb.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Sede implements Parcelable{

    String id;
    String direccion;
    HashMap<String, Boolean> poblacion;

    public Sede() {
    }

    public Sede(String id, String direccion, HashMap<String, Boolean> poblacion) {
        this.id = id;
        this.direccion = direccion;
        this.poblacion = poblacion;
    }


    protected Sede(Parcel in) {
        id = in.readString();
        direccion = in.readString();
    }

    public static final Creator<Sede> CREATOR = new Creator<Sede>() {
        @Override
        public Sede createFromParcel(Parcel in) {
            return new Sede(in);
        }

        @Override
        public Sede[] newArray(int size) {
            return new Sede[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(direccion);
    }

    public HashMap<String, Boolean> getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(HashMap<String, Boolean> poblacion) {
        this.poblacion = poblacion;
    }
}
