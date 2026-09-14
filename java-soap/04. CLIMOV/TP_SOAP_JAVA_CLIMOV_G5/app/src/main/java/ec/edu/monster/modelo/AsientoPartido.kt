package ec.edu.monster.modelo

import java.io.Serializable

data class AsientoPartido(
    var idAsientoPartido: Int = 0,
    var codigoPartido: Int = 0,
    var seccion: String = "",
    var fila: String = "",
    var numero: Int = 0,
    var estado: String = "",
    var idDetalleFactura: Int? = null,
    var nombreOcupante: String = "",
    var nombreCliente: String = "",
    var idFactura: Int? = null,
    var fechaCompra: String = "",
    var totalFactura: Double = 0.0,
    var precio: Double = 0.0
) : Serializable
