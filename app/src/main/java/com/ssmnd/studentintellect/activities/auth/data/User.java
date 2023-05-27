package com.ssmnd.studentintellect.activities.auth.data;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.ssmnd.studentintellect.models.Module;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class User implements Parcelable {
    @NotNull private String uid = "null";
    @NotNull private String names = "null";
    @Nullable private String lastName = null;
    @NotNull private String email = "null";
    @Nullable private String phone = null;
    @Nullable private String imageUrl = null;
    @NotNull private UserType userType = UserType.STUDENT;
    private double balance = 0.0;
    private boolean isOnline = false;
    @NotNull private Set<Module> modulesSet = new TreeSet<>();

    public User(){}
    public User(@NonNull FirebaseUser firebaseUser) {
        this.uid = firebaseUser.getUid();
        String[] names = {"null",null};
        String displayName = firebaseUser.getDisplayName();
        if (displayName != null) {
            String[] split = displayName.split(";");
            System.arraycopy(split, 0, names, 0, split.length);
        }
        this.names = names[0];
        this.lastName = names[1];
        this.email = Objects.requireNonNull(firebaseUser.getEmail());
        this.phone = firebaseUser.getPhoneNumber();
        Uri photoUri = firebaseUser.getPhotoUrl();
        this.imageUrl = photoUri != null ? String.valueOf(photoUri) : null;
    }
    public User(@NotNull String uid, @NotNull String names, @Nullable String lastName, @NotNull String email,
                @Nullable String phone, @Nullable String imageUrl, @NotNull UserType userType, double balance,
                boolean isOnline, @NotNull Set<Module> modulesSet) {
        this.uid = uid;
        this.names = names;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.userType = userType;
        this.balance = balance;
        this.isOnline = isOnline;
        this.modulesSet = modulesSet;
    }
    protected User(@NonNull Parcel in) {
        this.uid = in.readString();
        this.names = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.imageUrl = in.readString();
        this.userType = UserType.values()[in.readInt()];
        this.balance = in.readDouble();
        this.isOnline = in.readByte() != 0;
        @NotNull Module[] modules;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            modules = in.readArray(Module.class.getClassLoader(), Module.class);
        } else {
            Object[] newModules = in.readArray(Module.class.getClassLoader());
            modules = new Module[newModules.length];
            for (int i = 0; i < newModules.length; i++) {
                modules[i] = (Module) newModules[i];
            }
        }
        this.modulesSet = new TreeSet<>(Arrays.asList(modules));
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(names);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(imageUrl);
        dest.writeInt(userType.ordinal());
        dest.writeDouble(balance);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeArray(modulesSet.toArray(new Module[0]));
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<User> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setUid(@NotNull String uid) {
        this.uid = uid;
    }
    public void setNames(@NotNull String names) {
        this.names = names;
    }
    public void setLastName(@Nullable String lastName) {
        if (lastName == null || Objects.equals(lastName, "null") || lastName.isEmpty())
            this.lastName = null; else this.lastName = lastName;
    }
    public void setEmail(@NotNull String email) {
        this.email = email;
    }
    public void setPhone(@Nullable String phone) {
        if (phone == null || Objects.equals(phone, "null") || phone.isEmpty())
            this.phone = null; else this.phone = phone;
    }
    public void setImageUrl(@Nullable String imageUrl) {
        if (imageUrl == null || Objects.equals(imageUrl, "null") || imageUrl.isEmpty())
            this.imageUrl = null; else this.imageUrl = imageUrl;
    }
    public void setUserType(@NotNull UserType userType) {
        this.userType = userType;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void setOnline(boolean online) {
        isOnline = online;
    }
    public void setModulesSet(@NotNull Set<Module> modulesSet) {
        this.modulesSet = modulesSet;
    }


    @NotNull
    public String getUid() {
        return uid;
    }
    @NotNull
    public String getNames() {
        return names;
    }
    @Nullable
    public String getLastName() {
        return lastName;
    }
    @NotNull
    public String getEmail() {
        return email;
    }
    @Nullable
    public String getPhone() {
        return phone;
    }
    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }
    @NotNull
    public UserType getUserType() {
        return userType;
    }
    public double getBalance() {
        return balance;
    }
    public boolean isOnline() {
        return isOnline;
    }
    @NotNull
    public Set<Module> getModulesSet() {
        return modulesSet;
    }
}
