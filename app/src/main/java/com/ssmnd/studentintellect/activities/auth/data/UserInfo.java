package com.ssmnd.studentintellect.activities.auth.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UserInfo implements Parcelable {
    private static final FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    @NotNull private String uid;
    @Nullable private String name;
    @Nullable private String email;

    public UserInfo() {
        uid = firebaseUser.getUid();
        name = firebaseUser.getDisplayName();
        email = firebaseUser.getEmail();
    }
    public UserInfo(@NotNull String uid, @Nullable String name, @Nullable String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }
    protected UserInfo(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(email);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<UserInfo> CREATOR = new Creator<>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public void setUid(@NotNull String uid) {
        this.uid = uid;
    }
    public void setName(@Nullable String name) {
        if (name == null || Objects.equals(name, "null") || name.isEmpty()) {
            this.name = null;
        } else {
            this.name = name;
        }
    }
    public void setEmail(@Nullable String email) {
        if (email == null || Objects.equals(email, "null") || email.isEmpty()) {
            this.email = null;
        } else {
            this.email = email;
        }
    }

    @NotNull
    public String getUid() {
        return uid;
    }
    @Nullable
    public String getName() {
        return name;
    }
    @Nullable
    public String getEmail() {
        return email;
    }
}
