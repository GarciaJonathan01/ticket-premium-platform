package ec.edu.monster.modelo

data class Factura(
    var id: Int = 0,
    var codigoPartido: Int = 0,
    var nombreCliente: String = "",
    var fecha: String = "",
    var subtotal: Double = 0.0,
    var iva: Double = 0.0,
    var total: Double = 0.0
)
