package io.fotoapparat.routine.camera

import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.error.CameraDeviceException
import io.fotoapparat.hardware.CameraDevice
import io.fotoapparat.hardware.Device
import io.fotoapparat.routine.capability.getCapabilities
import io.fotoapparat.test.testCameraParameters
import io.fotoapparat.test.testFrameProcessor
import io.fotoapparat.test.willReturn
import io.fotoapparat.test.willThrow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
internal class UpdateCameraConfigurationRoutineTest {

    @Mock
    lateinit var device: Device
    @Mock
    lateinit var cameraDevice: CameraDevice

    @Test
    fun `Update device configuration`() = runBlocking {
        // Given
        val configuration = UpdateConfiguration()
        device.getSelectedCamera() willReturn cameraDevice
        device.getCameraParameters(cameraDevice) willReturn testCameraParameters
        device.getFrameProcessor() willReturn testFrameProcessor

        // When
        device.updateDeviceConfiguration(configuration)

        // Then
        val inOrder = inOrder(
                device,
                cameraDevice
        )

        inOrder.verify(device).updateConfiguration(configuration)
        inOrder.verify(device).updateCameraConfiguration(cameraDevice)
    }

    @Test
    fun `Update camera configuration`() = runBlocking {
        // Given
        device.getCameraParameters(cameraDevice) willReturn testCameraParameters
        device.getFrameProcessor() willReturn testFrameProcessor

        // When
        device.updateCameraConfiguration(
                cameraDevice = cameraDevice
        )

        // Then
        val inOrder = inOrder(cameraDevice)

        inOrder.verify(cameraDevice).updateParameters(testCameraParameters)
        inOrder.verify(cameraDevice).updateFrameProcessor(testFrameProcessor)
    }

    @Test
    fun `Throws correct exception`() {
        val e = try {
            runBlocking {
                // Given
                device.getCameraParameters(cameraDevice) willThrow VerifyError()

                // When
                device.updateCameraConfiguration(cameraDevice)
            }
            Exception()
        } catch (e: Throwable) { e }

        // Then
        assertTrue { e is CameraDeviceException }
    }
}

inline infix fun <reified T> T.willThrow(item: Throwable) {
    BDDMockito.given(this).willThrow(item)
}
