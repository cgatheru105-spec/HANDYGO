package com.example.handygo

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object CloudinaryManager {

    fun init(context: Context) {

        val config = mapOf(
            "cloud_name" to "djcsosckf"
        )

        MediaManager.init(context, config)
    }

    fun uploadImage(
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        MediaManager.get()
            .upload(imageUri)
            .unsigned("handygo_uploads")
            .callback(object : com.cloudinary.android.callback.UploadCallback {

                override fun onStart(requestId: String?) {}

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {}

                override fun onSuccess(
                    requestId: String?,
                    resultData: MutableMap<Any?, Any?>?
                ) {

                    val imageUrl =
                        resultData?.get("secure_url").toString()

                    onSuccess(imageUrl)
                }

                override fun onError(
                    requestId: String?,
                    error: com.cloudinary.android.callback.ErrorInfo?
                ) {
                    onError(error?.description ?: "Upload failed")
                }

                override fun onReschedule(
                    requestId: String?,
                    error: com.cloudinary.android.callback.ErrorInfo?
                ) {}
            })
            .dispatch()
    }

    suspend fun uploadImageAsync(imageUri: Uri): String? = suspendCancellableCoroutine { continuation ->
        uploadImage(
            imageUri = imageUri,
            onSuccess = { imageUrl ->
                if (continuation.isActive) continuation.resume(imageUrl)
            },
            onError = {
                if (continuation.isActive) continuation.resume(null)
            }
        )
    }
}
