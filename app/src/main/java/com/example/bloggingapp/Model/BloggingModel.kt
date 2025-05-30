package com.example.bloggingapp.Model

import android.os.Parcel
import android.os.Parcelable

data class BloggingModel(
    val heading: String? = "null",
    val username: String? = "null",
    val date: String? = "null",
    val post: String? = "null",
    var likeCount: Int = 0,
    val imageUrl: String? = "null",
    var postId: String? = "null",
    val likedBy: MutableList<String>? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "null",
        parcel.readString() ?: "null",
        parcel.readString() ?: "null",
        parcel.readString() ?: "null",
        parcel.readInt(),
        parcel.readString() ?: "null",
        parcel.readString() ?: "null"
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(heading)
        dest.writeString(username)
        dest.writeString(date)
        dest.writeString(post)
        dest.writeInt(likeCount)
        dest.writeString(imageUrl)
        dest.writeString(postId)
    }

    companion object CREATOR : Parcelable.Creator<BloggingModel> {
        override fun createFromParcel(parcel: Parcel): BloggingModel {
            return BloggingModel(parcel)
        }

        override fun newArray(size: Int): Array<BloggingModel?> {
            return arrayOfNulls(size)
        }
    }
}
