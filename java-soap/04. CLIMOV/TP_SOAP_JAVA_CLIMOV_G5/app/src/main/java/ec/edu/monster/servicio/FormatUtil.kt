package ec.edu.monster.servicio

object FormatUtil {

    private val accentMap = mapOf(
        "Canada" to "Canadá",
        "Mexico" to "México",
        "Haiti" to "Haití",
        "Sudafrica" to "Sudáfrica",
        "Espana" to "España",
        "Belgica" to "Bélgica",
        "Iran" to "Irán",
        "Japon" to "Japón",
        "Tunez" to "Túnez",
        "Uzbekistan" to "Uzbekistán",
        "Paises Bajos" to "Países Bajos",
        "Corea del Sur" to "Corea del Sur",
        
        "Estadio Ciudad de Mexico" to "Estadio Ciudad de México",
        "Estadio Monterrey" to "Estadio BBVA Monterrey",
        "Estadio Guadalajara" to "Estadio Akron Guadalajara",
        "Toronto Stadium" to "BMO Field Toronto",
        "BC Place Vancouver" to "BC Place Vancouver",
        "Los Angeles Stadium" to "SoFi Stadium Los Ángeles",
        "San Francisco Area Stadium" to "Levi's Stadium San Francisco",
        "Nueva Jersey Stadium" to "MetLife Stadium Nueva Jersey",
        "Boston Stadium" to "Gillette Stadium Boston",
        "Houston Stadium" to "NRG Stadium Houston",
        "Dallas Stadium" to "AT&T Stadium Dallas",
        "Philadelphia Stadium" to "Lincoln Financial Field Filadelfia",
        "Atlanta Stadium" to "Mercedes-Benz Stadium Atlanta",
        "Seattle Stadium" to "Lumen Field Seattle",
        "Miami Stadium" to "Hard Rock Stadium Miami",
        "Kansas City Stadium" to "Arrowhead Stadium Kansas City",

        "PALCO" to "Palco",
        "TRIBUNA" to "Tribuna",
        "GENERAL" to "General"
    )

    fun tilde(text: String?): String {
        if (text == null) return ""
        val trimmed = text.trim()
        if (accentMap.containsKey(trimmed)) {
            return accentMap[trimmed]!!
        }

        var result = text!!
        for ((key, value) in accentMap) {
            if (result.contains(key)) {
                result = result.replace(key, value)
            }
        }
        return result
    }
}
