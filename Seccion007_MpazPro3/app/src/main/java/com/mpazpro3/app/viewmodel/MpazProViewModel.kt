package com.mpazpro3.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mpazpro3.app.data.MockData
import com.mpazpro3.app.model.Actividad
import com.mpazpro3.app.model.Asignacion
import com.mpazpro3.app.model.Asignatura
import com.mpazpro3.app.model.Contenido
import com.mpazpro3.app.model.EstadoAprobacion
import com.mpazpro3.app.model.Estudiante
import com.mpazpro3.app.model.InformeDocente
import com.mpazpro3.app.model.Perfil
import com.mpazpro3.app.model.RecursoDidactico
import com.mpazpro3.app.model.RespuestaEstudiante
import com.mpazpro3.app.model.SeguimientoAvance
import com.mpazpro3.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
/**
 * ViewModel único, compartido por todas las pantallas (se crea una sola vez en AppNavigation).
 *
 * ESTO ES UN ESQUELETO: las pantallas ya llaman a todas estas funciones, pero la lógica de
 * negocio del caso está vacía a propósito y marcada con TODO(equipo), citando el módulo de
 * Requerimientos_Seccion007 al que corresponde. Mientras no se implemente, las listas se
 * muestran sin filtrar y las acciones (aprobar, asignar, responder, validar) no cambian nada.
 */
class MpazProViewModel : ViewModel() {

    // =====================================================================
    // Autenticación
    // =====================================================================

    private val _usuarioActivo = MutableStateFlow<Usuario?>(null)
    val usuarioActivo: StateFlow<Usuario?> = _usuarioActivo.asStateFlow()

    private val _errorLogin = MutableStateFlow<String?>(null)
    val errorLogin: StateFlow<String?> = _errorLogin.asStateFlow()

    /**
     * TODO(equipo): validar usuario y clave contra MockData.usuarios. Si son correctos, dejar
     * _usuarioActivo con ese usuario y devolver true. Si no, dejar _errorLogin con un mensaje y devolver false.
     */
    fun login(usuario: String, clave: String): Boolean {
        _errorLogin.value = "Login sin implementar — ver TODO(equipo) en MpazProViewModel.login()."
        return false
    }

    /**
     * SOLO PARA DESARROLLO: entra con el usuario de prueba del perfil indicado, sin validar
     * credenciales. TODO(equipo): eliminar esta función y los botones "Modo desarrollo".
     */
    fun entrarModoDesarrollo(perfil: Perfil) {
        _usuarioActivo.value = MockData.usuarios.first { it.perfil == perfil }
        _errorLogin.value = null
    }

    fun logout() {
        _usuarioActivo.value = null
        _errorLogin.value = null
        _contenidoSeleccionadoId.value = null
        _estudianteSeleccionadoId.value = null
    }

    // =====================================================================
    // Datos compartidos y selección (no es lógica de negocio)
    // =====================================================================

    private val _contenidos = MutableStateFlow(MockData.contenidos)
    val contenidos: StateFlow<List<Contenido>> = _contenidos.asStateFlow()

    private val _recursos = MutableStateFlow(MockData.recursos)
    private val _actividades = MutableStateFlow(MockData.actividades)

    private val _asignaciones = MutableStateFlow(MockData.asignaciones)
    val asignaciones: StateFlow<List<Asignacion>> = _asignaciones.asStateFlow()

    private val _estudiantes = MutableStateFlow(MockData.estudiantes)
    val estudiantes: StateFlow<List<Estudiante>> = _estudiantes.asStateFlow()

    private val _seguimientos = MutableStateFlow(MockData.seguimientos)
    val seguimientos: StateFlow<List<SeguimientoAvance>> = _seguimientos.asStateFlow()

    private val _informes = MutableStateFlow<List<InformeDocente>>(emptyList())
    val informes: StateFlow<List<InformeDocente>> = _informes.asStateFlow()

    private val _contenidoSeleccionadoId = MutableStateFlow<String?>(null)
    val contenidoSeleccionadoId: StateFlow<String?> = _contenidoSeleccionadoId.asStateFlow()

    private val _estudianteSeleccionadoId = MutableStateFlow<String?>(null)
    val estudianteSeleccionadoId: StateFlow<String?> = _estudianteSeleccionadoId.asStateFlow()

    fun seleccionarContenido(id: String?) {
        _contenidoSeleccionadoId.value = id
    }

    fun seleccionarEstudiante(id: String?) {
        _estudianteSeleccionadoId.value = id
    }

    fun contenidoPorId(id: String?): Contenido? = _contenidos.value.find { it.id == id }

    fun estudiantePorId(id: String?): Estudiante? = _estudiantes.value.find { it.id == id }

    fun recursosDe(contenidoId: String?): List<RecursoDidactico> =
        _recursos.value.filter { it.contenidoId == contenidoId }

    fun actividadPorId(id: String): Actividad? = _actividades.value.find { it.id == id }

    // =====================================================================
    // Módulo "Autorización de contenidos (UTP)"
    // =====================================================================

    /**
     * TODO(equipo): filtrar por asignatura y por estado (null = todos). Hoy devuelve todo.
     */
    fun contenidosFiltrados(asignatura: Asignatura?, estado: EstadoAprobacion?): List<Contenido> = _contenidos.value

    /**
     * TODO(equipo): cambiar el estado del contenido a APROBADO y guardar la observación.
     */
    fun aprobarContenido(contenidoId: String, observacion: String) {
        // Sin implementar todavía.
    }

    /** TODO(equipo): igual que aprobarContenido(), pero con estado RECHAZADO. Hoy no hace nada. */
    fun rechazarContenido(contenidoId: String, observacion: String) {
        // Sin implementar todavía.
    }

    // =====================================================================
    // Módulo "Planificación docente"
    // =====================================================================

    /**
     * TODO(equipo): devolver SOLO los contenidos APROBADOS por UTP (regla de negocio: un
     * contenido no autorizado no puede asignarse). Hoy devuelve todos.
     */
    fun contenidosDisponiblesParaAsignar(): List<Contenido> = _contenidos.value

    /**
     * Crea las asignaciones del curso para los contenidos elegidos, fijando el máximo
     * de intentos de la evaluación final (RF-05, RF-04, RN-02, RN-03).
     */
    fun asignarAlCurso(curso: String, contenidoIds: List<String>, maxIntentos: Int) {
        // Regla RN-03: Validar que el máximo de intentos sea al menos 1
        if (maxIntentos < 1) return
        if (curso.isBlank()) return

        val listaActual = _asignaciones.value.toMutableList()

        for (contenidoId in contenidoIds) {
            // Regla RN-02: Verificar que el contenido exista y esté APROBADO por UTP
            val contenido = contenidoPorId(contenidoId)
            if (contenido == null || contenido.estado != EstadoAprobacion.APROBADO) {
                continue
            }

            // Buscar si ya existe una asignación para este curso y contenido
            val index = listaActual.indexOfFirst { it.curso == curso && it.contenidoId == contenidoId }
            if (index != -1) {
                // Actualizar el máximo de intentos sin duplicar
                val asignacionExistente = listaActual[index]
                listaActual[index] = asignacionExistente.copy(maxIntentosEvaluacion = maxIntentos)
            } else {
                // Crear nueva asignación con el máximo de intentos definido
                val nuevaAsignacion = Asignacion(
                    id = "AS_${System.currentTimeMillis()}_$contenidoId",
                    curso = curso,
                    contenidoId = contenidoId,
                    maxIntentosEvaluacion = maxIntentos
                )
                listaActual.add(nuevaAsignacion)
            }
        }
        _asignaciones.value = listaActual
    }

    /** TODO(equipo): devolver solo las asignaciones del curso indicado. Hoy devuelve todas. */
    fun asignacionesDelCurso(curso: String): List<Asignacion> = _asignaciones.value

    /**
     * TODO(equipo): calcular el avance del estudiante (0.0 a 1.0) a partir de sus SeguimientoAvance.
     */
    fun progresoGeneral(estudianteId: String): Float = 0f

    /**
     * TODO(equipo): exportar los resultados del curso (por ejemplo a un archivo CSV o PDF).
     */
    fun descargarResultados(curso: String) {
        // Sin implementar todavía.
    }

    // =====================================================================
    // Módulo "Informe pedagógico"
    // =====================================================================

    fun informeDe(estudianteId: String?): InformeDocente? = _informes.value.find { it.estudianteId == estudianteId }

    /**
     * TODO(equipo): generar el informe del estudiante a partir de sus respuestas, en estado BORRADOR.
     */
    fun generarInforme(estudianteId: String) {
        // Sin implementar todavía.
    }

    /**
     * TODO(equipo): pasar el informe de BORRADOR a VALIDADO.
     */
    fun validarInforme(informeId: String) {
        // Sin implementar todavía.
    }

    // =====================================================================
    // Módulo "Experiencia del estudiante"
    // =====================================================================

    /**
     * TODO(equipo): devolver solo los contenidos asignados al curso del estudiante.
     */
    fun contenidosDelEstudiante(usuario: Usuario?): List<Contenido> = _contenidos.value

    /** TODO(equipo): avance del estudiante en el contenido (0.0 a 1.0). */
    fun progreso(estudianteId: String, contenidoId: String): Float = 0f

    /**
     * TODO(equipo): guardar que el estudiante terminó de revisar el material.
     */
    fun confirmarMaterialRevisado(estudianteId: String, contenidoId: String) {
        // Sin implementar todavía.
    }

    /**
     * TODO(equipo): devolver los desafíos del contenido (sin la evaluación final), ordenados por dificultad.
     */
    fun desafiosDe(contenidoId: String?): List<Actividad> = _actividades.value

    /**
     * TODO(equipo): registrar la respuesta en el seguimiento del estudiante y devolver si fue correcta.
     */
    fun responderDesafio(estudianteId: String, actividad: Actividad, alternativa: Int): Boolean? = null

    /** TODO(equipo): devolver solo las preguntas de la evaluación final del contenido. */
    fun evaluacionFinalDe(contenidoId: String?): List<Actividad> = _actividades.value

    /**
     * TODO(equipo): intentos que le quedan al estudiante en la evaluación final.
     */
    fun intentosRestantes(estudianteId: String, contenidoId: String): Int? = null

    /**
     * TODO(equipo): registrar las respuestas de la evaluación final como un nuevo intento.
     */
    fun enviarEvaluacion(estudianteId: String, contenidoId: String, respuestas: Map<String, Int>) {
        // Sin implementar todavía.
    }

    /**
     * TODO(equipo): devolver las respuestas del estudiante en el contenido.
     */
    fun respuestasDe(estudianteId: String, contenidoId: String): List<RespuestaEstudiante> = emptyList()
}