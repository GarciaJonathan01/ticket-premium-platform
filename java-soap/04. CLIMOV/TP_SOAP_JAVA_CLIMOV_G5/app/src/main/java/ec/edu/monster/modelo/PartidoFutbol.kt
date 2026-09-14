package ec.edu.monster.modelo

data class PartidoFutbol(
    var codigo: Int = 0,
    var equipoLocal: String = "",
    var equipoVisita: String = "",
    var fecha: String = "",
    var lugar: String = ""
)
