package io.fotoapparat.characteristic

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics.LENS_FACING
import android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Build
import androidx.annotation.RequiresApi
import io.fotoapparat.hardware.orientation.toOrientation

/**
 * Returns the [Characteristics] for the given `cameraId`.
 */
@Suppress("DEPRECATION")
internal fun getCharacteristics(cameraId: Int): Characteristics {
    val info = Camera.CameraInfo()
    Camera.getCameraInfo(cameraId, info)
    val lensPosition = info.facing.toLensPosition()
    return Characteristics(
            cameraId = cameraId,
            lensPosition = lensPosition,
            cameraOrientation = info.orientation.toOrientation(),
            isMirrored = lensPosition == LensPosition.Front
    )
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal fun Context.getCameraManager() = getSystemService(Context.CAMERA_SERVICE) as CameraManager

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal fun CameraManager.getCharacteristicsByCameraId(cameraId: String): Characteristics {
    val characteristics = getCameraCharacteristics(cameraId)

    val lensPosition: LensPosition = when(characteristics.get(LENS_FACING)) {
        CameraMetadata.LENS_FACING_FRONT -> LensPosition.Front
        CameraMetadata.LENS_FACING_BACK -> LensPosition.Back
        CameraMetadata.LENS_FACING_EXTERNAL -> LensPosition.External
        else -> throw IllegalStateException("Lens position not found.")
    }

    val cameraOrientation: Int = characteristics.get(SENSOR_ORIENTATION)
            ?: throw IllegalStateException("Camera orientation not found.")

    return Characteristics(
            cameraId = cameraId.toInt(),
            lensPosition = lensPosition,
            cameraOrientation = cameraOrientation.toOrientation(),
            isMirrored = lensPosition == LensPosition.Front
    )
}
