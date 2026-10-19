package com.mpazpro3.app.data.local

import androidx.room.Entity

@Entity(
    tableName = "rf05_intentos",
    primaryKeys = ["curso", "contenidoId"]
)
data class Rf05IntentosEntity(
    val curso: String,
    val contenidoId: String,
    val maxIntentos: Int
)