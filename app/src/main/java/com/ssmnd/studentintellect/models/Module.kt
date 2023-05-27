package com.ssmnd.studentintellect.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Module(
    var id: String = "",
    var code: String = "",
    var name: String = "",
    var timeUpdated: Timestamp = Timestamp.now(),
    var adderUid: String = "",
    var adderName: String = "",
    var adderEmail: String = "",
    @JvmField var isVerified: Boolean = false,
    var verifierUid: String? = null,
    var verifierName: String? = null,
    var verifierEmail: String? = null,
    @JvmField val isDeleted: Boolean = false,
    val deleterUid: String? = null,
    val deleterName: String? = null,
    val deleterEmail: String? = null
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
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(code)
        parcel.writeString(name)
        parcel.writeParcelable(timeUpdated, flags)
        parcel.writeString(adderUid)
        parcel.writeString(adderName)
        parcel.writeString(adderEmail)
        parcel.writeByte(if (isVerified) 1 else 0)
        parcel.writeString(verifierUid)
        parcel.writeString(verifierName)
        parcel.writeString(verifierEmail)
        parcel.writeByte(if (isDeleted) 1 else 0)
        parcel.writeString(deleterUid)
        parcel.writeString(deleterName)
        parcel.writeString(deleterEmail)
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