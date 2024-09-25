package com.example.rmas.repositories

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class ImageRepository {
    private val storage: FirebaseStorage = Firebase.storage

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun uploadImage(user: FirebaseUser, imageUri: Uri, contentResolver: ContentResolver): Uri {
        val source = ImageDecoder.createSource(contentResolver, imageUri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        val imageRef = storage.reference.child("images/user_${user.uid}.jpg")

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

        imageRef.putBytes(stream.toByteArray()).await()

        return imageRef.downloadUrl.await()
    }
}