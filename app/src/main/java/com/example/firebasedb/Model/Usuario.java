package com.example.firebasedb.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable {

    String email;
    String password;
    String id;
    String nombre;
    int telefono;


    public Usuario() {


    }
    public Usuario(String email, String password,  String nombre, Integer telefono) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.telefono =telefono;
    }


    protected Usuario(Parcel in) {
        email = in.readString();
        password = in.readString();
        id = in.readString();
        nombre = in.readString();
        telefono = in.readInt();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeInt(telefono);
    }
}
