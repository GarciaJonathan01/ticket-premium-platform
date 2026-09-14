package ec.edu.monster.modelo

import java.io.Serializable

data class CuotaAmortizacion(
    var numCuota: Int = 0,
    var valorCuota: Double = 0.0,
    var interesPagado: Double = 0.0,
    var capitalPagado: Double = 0.0,
    var saldo: Double = 0.0
) : Serializable
