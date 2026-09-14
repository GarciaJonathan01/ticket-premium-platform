package ec.edu.monster.modelo

import java.io.Serializable

data class RespuestaCredito(
    var aprobado: Boolean = false,
    var mensaje: String = "",
    var montoMaximo: Double = 0.0,
    var idCredito: Int = 0,
    var tablaAmortizacion: List<CuotaAmortizacion> = emptyList()
) : Serializable
