package ec.edu.monster.modelo

import java.io.Serializable

data class PeticionCompra(
    var idLocalidad: Int = 0,
    var codigoLocalidad: String = "",
    var cantidad: Int = 0,
    var precioUnitario: Double = 0.0,
    var codigoPartido: Int = 0,
    var nombreOcupante: String = "",
    var idAsientoPartido: Int = 0
) : Serializable
