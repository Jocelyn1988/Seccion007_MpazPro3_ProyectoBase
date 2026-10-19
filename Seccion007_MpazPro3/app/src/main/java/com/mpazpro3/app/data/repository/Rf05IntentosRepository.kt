package com.mpazpro3.app.data.repository

import com.mpazpro3.app.data.local.Rf05IntentosDao
import com.mpazpro3.app.data.local.Rf05IntentosEntity
import kotlinx.coroutines.flow.Flow

class Rf05IntentosRepository(private val dao: Rf05IntentosDao) {
    fun observarPorCurso(curso: String): Flow<List<Rf05IntentosEntity>> =
        dao.observarPorCurso(curso)

    fun observarUno(
        curso: String,
        contenidoId: String
    ): Flow<Rf05IntentosEntity?> = dao.observarUno(curso, contenidoId)

    suspend fun guardar(
        curso: String,
        contenidoIds: List<String>,
        maxIntentos: Int
    ) {
        val valores = contenidoIds.distinct().map { id ->
            Rf05IntentosEntity(curso, id, maxIntentos)
        }
        dao.guardarVarios(valores)
    }
}