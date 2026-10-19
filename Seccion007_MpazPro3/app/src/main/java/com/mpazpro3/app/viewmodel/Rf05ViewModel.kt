package com.mpazpro3.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mpazpro3.app.data.local.Rf05Database
import com.mpazpro3.app.data.local.Rf05IntentosEntity
import com.mpazpro3.app.data.repository.Rf05IntentosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

sealed interface Rf05Estado {
    data object Inicial : Rf05Estado
    data object Guardando : Rf05Estado
    data class Exito(val mensaje: String) : Rf05Estado
    data class Error(val mensaje: String) : Rf05Estado
}

class Rf05ViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Rf05IntentosRepository(
        Rf05Database.obtener(application).intentosDao()
    )

    private val _estado = MutableStateFlow<Rf05Estado>(Rf05Estado.Inicial)
    val estado: StateFlow<Rf05Estado> = _estado.asStateFlow()

    fun observarPorCurso(curso: String): Flow<List<Rf05IntentosEntity>> =
        repository.observarPorCurso(curso)

    fun observarUno(
        curso: String,
        contenidoId: String
    ): Flow<Rf05IntentosEntity?> = repository.observarUno(curso, contenidoId)

    fun guardarMaximo(
        curso: String,
        contenidoIds: List<String>,
        maxIntentos: Int
    ) {
        val ids = contenidoIds.distinct()
        if (curso.isBlank() || ids.isEmpty() ||
            ids.any { it.isBlank() } || maxIntentos !in 1..10
        ) {
            _estado.value = Rf05Estado.Error("Curso, contenido o intentos inválidos")
            return
        }
        viewModelScope.launch {
            _estado.value = Rf05Estado.Guardando
            try {
                repository.guardar(curso, ids, maxIntentos)
                _estado.value = Rf05Estado.Exito(
                    "Máximo de $maxIntentos intentos guardado correctamente"
                )
            } catch (e: Exception) {
                _estado.value = Rf05Estado.Error("No fue posible guardar los intentos")
            }
        }
    }

    fun limpiarEstado() {
        _estado.value = Rf05Estado.Inicial
    }
}