package com.example.mypocketguide.ui.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * ImageProxy → Bitmap → JPEG ByteArray.
 * Причина конвертации: ImageProxy отдаёт YUV/NV21, а нам нужен JPEG.
 */
fun ImageProxy.toJpegByteArray(quality: Int = 90): ByteArray? {
    val bitmap = toBitmap() ?: return null
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    val bytes = stream.toByteArray()
    bitmap.recycle()
    return bytes
}

fun ImageProxy.toBitmap(): Bitmap? {
    // Плоскость 0 — обычно Y (яркость). Для JPEG-потока (если format == JPEG)
    // там уже лежит готовый JPEG.
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}