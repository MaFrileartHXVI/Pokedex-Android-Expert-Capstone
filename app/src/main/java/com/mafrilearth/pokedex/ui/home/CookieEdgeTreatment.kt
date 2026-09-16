package com.mafrilearth.pokedex.ui.home

import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath

@Suppress("unused")
class CookieEdgeTreatment(private val inset: Float, private val waves: Int = 3) : EdgeTreatment() {
    override fun getEdgePath(
        length: Float,
        center: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val actualInset = inset * interpolation
        val waveLength = length / waves
        
        shapePath.lineTo(0f, 0f)
        for (i in 0 until waves) {
            val startX = i * waveLength
            val endX = (i + 1) * waveLength
            
            shapePath.cubicToPoint(
                startX + waveLength / 4, actualInset,
                endX - waveLength / 4, actualInset,
                endX, 0f
            )
        }
    }
}
