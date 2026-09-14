package ec.edu.monster.modelo

data class DetalleVentaReporte(
    var fecha: String = "",
    var cliente: String = "",
    var localidades: String = "",
    var boletosTotales: Int = 0,
    var totalVenta: Double = 0.0
)
