package com.ssmnd.studentintellect.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.ssmnd.studentintellect.activities.auth.data.UserInfo
import java.util.Comparator
import java.util.Objects

data class Module(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val timeUpdated: Timestamp = Timestamp.now(),
    val adder: UserInfo = UserInfo(),
    @JvmField val isVerified: Boolean = false,
    val verifier: UserInfo? = null,
    @JvmField val isDeleted: Boolean = false,
    val deleter: UserInfo? = null
) : Parcelable , Comparable<Module> {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(Timestamp::class.java.classLoader, Timestamp::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(Timestamp::class.java.classLoader)!!
        },
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(UserInfo::class.java.classLoader, UserInfo::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(UserInfo::class.java.classLoader)!!
        },
        parcel.readByte() != 0.toByte(),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(UserInfo::class.java.classLoader, UserInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(UserInfo::class.java.classLoader)
        },
        parcel.readByte() != 0.toByte(),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            parcel.readParcelable(UserInfo::class.java.classLoader, UserInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            parcel.readParcelable(UserInfo::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(code)
        parcel.writeString(name)
        parcel.writeParcelable(timeUpdated, flags)
        parcel.writeParcelable(adder, flags)
        parcel.writeByte(if (isVerified) 1 else 0)
        parcel.writeParcelable(verifier, flags)
        parcel.writeByte(if (isDeleted) 1 else 0)
        parcel.writeParcelable(deleter, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Module> {
        override fun createFromParcel(parcel: Parcel): Module {
            return Module(parcel)
        }

        override fun newArray(size: Int): Array<Module?> {
            return arrayOfNulls(size)
        }
    }

    override fun compareTo(other: Module): Int {
        return compareBy<Module> { it.name }.compare(this, other)
    }
}