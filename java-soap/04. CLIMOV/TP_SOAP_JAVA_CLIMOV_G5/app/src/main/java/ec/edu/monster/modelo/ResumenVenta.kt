package ec.edu.monster.modelo

data class ResumenVenta(
    var codigoLocalidad: String = "",
    var vendidos: Int = 0,
    var totalRecaudado: Double = 0.0
)
