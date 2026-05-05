package com.example.frontpage.stepcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterManager(
    context: Context,
    private val onStepsChanged: (Int) -> Unit,
    private val onSensorAvailableChanged: (Boolean) -> Unit = {}
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var initialStepCount: Float? = null

    fun startListening() {
        if (stepCounterSensor == null) {
            onSensorAvailableChanged(false)
            return
        }

        onSensorAvailableChanged(true)

        sensorManager.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun resetSteps() {
        initialStepCount = null
        onStepsChanged(0)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val totalStepsSinceReboot = event.values[0]

        if (initialStepCount == null) {
            initialStepCount = totalStepsSinceReboot
        }

        val currentSteps = totalStepsSinceReboot - initialStepCount!!

        onStepsChanged(currentSteps.toInt())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}