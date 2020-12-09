package com.example.project.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "email_address") val emailAddress: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "profile_picture",typeAffinity = ColumnInfo.BLOB) val picture: ByteArray?,
    @ColumnInfo(name = "phone") val phone: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (uid != other.uid) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (emailAddress != other.emailAddress) return false
        if (address != other.address) return false
        if (picture != null) {
            if (other.picture == null) return false
            if (!picture.contentEquals(other.picture)) return false
        } else if (other.picture != null) return false
        if (phone != other.phone) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid
        result = 31 * result + (firstName?.hashCode() ?: 0)
        result = 31 * result + (lastName?.hashCode() ?: 0)
        result = 31 * result + (emailAddress?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (picture?.contentHashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "User(uid=$uid, firstName=$firstName, lastName=$lastName, emailAddress=$emailAddress, address=$address, picture=${picture?.contentToString()}, phone=$phone)"
    }
}

@Entity
class UserUpdate (
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "email_address") val emailAddress: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "profile_picture",typeAffinity = ColumnInfo.BLOB) val picture: ByteArray?,
    @ColumnInfo(name = "phone") val phone: String?,
)