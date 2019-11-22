package com.example.firebasedb.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Backend implements Parcelable {

    String id;
    String password;
    String user;

    public Backend() {
    }

    public Backend(String id, String password, String user) {
        this.id = id;
        this.password = password;
        this.user = user;
    }

    protected Backend(Parcel in) {
        id = in.readString();
        password = in.readString();
        user = in.readString();
    }

    public static final Creator<Backend> CREATOR = new Creator<Backend>() {
        @Override
        public Backend createFromParcel(Parcel in) {
            return new Backend(in);
        }

        @Override
        public Backend[] newArray(int size) {
            return new Backend[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(password);
        dest.writeString(user);
    }
}
