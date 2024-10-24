package com.example.rmas.repositories

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class ImageRepository {
    private val storage: FirebaseStorage = Firebase.storage

    suspend fun uploadImage(name: String, imageUri: Uri, contentResolver: ContentResolver): Uri  {
        val imageRef = storage.reference.child("images/$name.jpg")
        val stream = ByteArrayOutputStream()

        withContext(Dispatchers.IO) {
            val bitmap: Bitmap

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                bitmap = ImageDecoder.decodeBitmap(source)
            } else {
                val inputStream = contentResolver.openInputStream(imageUri)
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        }

        imageRef.putBytes(stream.toByteArray()).await()

        return imageRef.downloadUrl.await()
    }
}