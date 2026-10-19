package com.mpazpro3.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class Rf05IntentosDao {
    @Query("SELECT * FROM rf05_intentos WHERE curso = :curso")
    abstract fun observarPorCurso(curso: String): Flow<List<Rf05IntentosEntity>>

    @Query("""SELECT * FROM rf05_intentos
        WHERE curso = :curso AND contenidoId = :contenidoId LIMIT 1""")
    abstract fun observarUno(
        curso: String,
        contenidoId: String
    ): Flow<Rf05IntentosEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun guardarUno(valor: Rf05IntentosEntity)

    @Transaction
    open suspend fun guardarVarios(valores: List<Rf05IntentosEntity>) {
        valores.forEach { guardarUno(it) }
    }
}