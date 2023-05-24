package com.ssmnd.studentintellect.activities.auth.data;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssmnd.studentintellect.models.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class AppUser implements Parcelable {
    private static final FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    @NotNull private UserInfo userInfo;
    @NotNull private UserType userType;
    @Nullable private String phone;
    private double balance;
    @Nullable private String imageUrl;
    private boolean isOnline;
    @NotNull private Set<Module> myModulesSet;

    public AppUser() {
        String uid = firebaseUser.getUid();
        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        this.userInfo = new UserInfo(uid, name, email);
        this.userType = UserType.STUDENT;
        this.phone = firebaseUser.getPhoneNumber();
        this.balance = 0;
        Uri photoUri = firebaseUser.getPhotoUrl();
        this.imageUrl = photoUri != null ? String.valueOf(photoUri) : null;
        this.isOnline = false;
        this.myModulesSet = new TreeSet<>();
    }

    public AppUser(@NotNull UserInfo userInfo, @NotNull UserType userType, @Nullable String phone,
                   double balance, @Nullable String imageUrl, boolean isOnline,
                   @NotNull Set<Module> myModulesSet) {
        this.userInfo = userInfo;
        this.userType = userType;
        this.phone = phone;
        this.balance = balance;
        this.imageUrl = imageUrl;
        this.isOnline = isOnline;
        this.myModulesSet = myModulesSet;
    }

    protected AppUser(Parcel in) {
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        userType = UserType.values()[in.readInt()];
        phone = in.readString();
        balance = in.readDouble();
        imageUrl = in.readString();
        isOnline = in.readByte() != 0;
        @NotNull Module[] modules;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            modules = in.readParcelableArray(Module.class.getClassLoader(), Module.class);
        } else {
            modules = (Module[]) in.readParcelableArray(Module.class.getClassLoader());
        }
        myModulesSet = new TreeSet<>(Arrays.asList(modules));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(userInfo, flags);
        dest.writeInt(userType.ordinal());
        dest.writeString(phone);
        dest.writeDouble(balance);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeParcelableArray(myModulesSet.toArray(new Module[0]), flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<AppUser> CREATOR = new Creator<>() {
        @Override
        public AppUser createFromParcel(Parcel in) {
            return new AppUser(in);
        }

        @Override
        public AppUser[] newArray(int size) {
            return new AppUser[size];
        }
    };

    public void setUserInfo(@NotNull UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    public void setUserType(@NotNull UserType userType) {
        this.userType = userType;
    }
    public void setPhone(@Nullable String phone) {
        if (phone == null || Objects.equals(phone, "null") || phone.isEmpty()) {
            this.phone = null;
        } else {
            this.phone = phone;
        }
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void setImageUrl(@Nullable String imageUrl) {
        if (imageUrl == null || Objects.equals(imageUrl, "null") || imageUrl.isEmpty()) {
            this.imageUrl = null;
        } else {
            this.imageUrl = imageUrl;
        }
    }
    public void setOnline(boolean online) {
        isOnline = online;
    }
    public void setMyModulesSet(@NotNull Set<Module> myModulesSet) {
        this.myModulesSet = myModulesSet;
    }

    @NotNull
    public UserInfo getUserInfo() {
        return userInfo;
    }
    @NotNull
    public UserType getUserType() {
        return userType;
    }
    @Nullable
    public String getPhone() {
        return phone;
    }
    public double getBalance() {
        return balance;
    }
    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }
    public boolean isOnline() {
        return isOnline;
    }
    @NonNull
    public Set<Module> getMyModulesSet() {
        return myModulesSet;
    }
}
