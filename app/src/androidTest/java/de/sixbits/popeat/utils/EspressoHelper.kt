package de.sixbits.popeat.utils

import android.app.Instrumentation
import android.os.SystemClock
import android.view.MotionEvent


object EspressoHelper {
    fun drag(inst: Instrumentation, fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val stepCount = 10

        val downTime = SystemClock.uptimeMillis()
        var eventTime = SystemClock.uptimeMillis()

        var y: Float = fromY.toFloat()
        var x: Float = fromX.toFloat()

        val yStep: Float = (toY.toFloat() - fromY) / stepCount
        val xStep: Float = (toX.toFloat() - fromX) / stepCount

        var event = MotionEvent.obtain(
            downTime, eventTime,
            MotionEvent.ACTION_DOWN, x, y, 0
        )
        inst.sendPointerSync(event)
        for (i in 0 until stepCount) {
            y += yStep
            x += xStep
            eventTime = SystemClock.uptimeMillis()
            event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, 0)
            inst.sendPointerSync(event)
        }

        eventTime = SystemClock.uptimeMillis()
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0)
        inst.sendPointerSync(event)
        inst.waitForIdleSync()
    }
}