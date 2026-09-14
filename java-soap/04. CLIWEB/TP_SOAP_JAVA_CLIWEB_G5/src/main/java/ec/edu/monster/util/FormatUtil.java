package ec.edu.monster.util;

import java.util.HashMap;
import java.util.Map;

public class FormatUtil {

    private static final Map<String, String> ACCENT_MAP = new HashMap<>();

    static {
        // Países y Equipos
        ACCENT_MAP.put("Canada", "Canadá");
        ACCENT_MAP.put("Mexico", "México");
        ACCENT_MAP.put("Haiti", "Haití");
        ACCENT_MAP.put("Sudafrica", "Sudáfrica");
        ACCENT_MAP.put("Espana", "España");
        ACCENT_MAP.put("Belgica", "Bélgica");
        ACCENT_MAP.put("Iran", "Irán");
        ACCENT_MAP.put("Japon", "Japón");
        ACCENT_MAP.put("Tunez", "Túnez");
        ACCENT_MAP.put("Uzbekistan", "Uzbekistán");
        ACCENT_MAP.put("Paises Bajos", "Países Bajos");
        ACCENT_MAP.put("Corea del Sur", "Corea del Sur");
        ACCENT_MAP.put("Repechaje 1", "Repechaje 1");
        ACCENT_MAP.put("Repechaje 2", "Repechaje 2");
        ACCENT_MAP.put("UEFA A", "UEFA Stand A");
        ACCENT_MAP.put("UEFA B", "UEFA Stand B");
        ACCENT_MAP.put("UEFA C", "UEFA Stand C");
        ACCENT_MAP.put("UEFA D", "UEFA Stand D");

        // Estadios
        ACCENT_MAP.put("Estadio Ciudad de Mexico", "Estadio Ciudad de México");
        ACCENT_MAP.put("Estadio Monterrey", "Estadio BBVA Monterrey");
        ACCENT_MAP.put("Estadio Guadalajara", "Estadio Akron Guadalajara");
        ACCENT_MAP.put("Toronto Stadium", "BMO Field Toronto");
        ACCENT_MAP.put("BC Place Vancouver", "BC Place Vancouver");
        ACCENT_MAP.put("Los Angeles Stadium", "SoFi Stadium Los Ángeles");
        ACCENT_MAP.put("San Francisco Area Stadium", "Levi's Stadium San Francisco");
        ACCENT_MAP.put("Nueva Jersey Stadium", "MetLife Stadium Nueva Jersey");
        ACCENT_MAP.put("Boston Stadium", "Gillette Stadium Boston");
        ACCENT_MAP.put("Houston Stadium", "NRG Stadium Houston");
        ACCENT_MAP.put("Dallas Stadium", "AT&T Stadium Dallas");
        ACCENT_MAP.put("Philadelphia Stadium", "Lincoln Financial Field Filadelfia");
        ACCENT_MAP.put("Atlanta Stadium", "Mercedes-Benz Stadium Atlanta");
        ACCENT_MAP.put("Seattle Stadium", "Lumen Field Seattle");
        ACCENT_MAP.put("Miami Stadium", "Hard Rock Stadium Miami");
        ACCENT_MAP.put("Kansas City Stadium", "Arrowhead Stadium Kansas City");

        // Secciones
        ACCENT_MAP.put("PALCO", "Palco");
        ACCENT_MAP.put("TRIBUNA", "Tribuna");
        ACCENT_MAP.put("GENERAL", "General");
    }

    /**
     * Retorna el texto formateado con acentos o el mismo texto si no se encuentra mapeado.
     */
    public static String tilde(String text) {
        if (text == null) return "";
        String trimmed = text.trim();
        if (ACCENT_MAP.containsKey(trimmed)) {
            return ACCENT_MAP.get(trimmed);
        }
        
        // Búsqueda parcial / subcadenas (por si viene concatenado o en reportes)
        String result = text;
        for (Map.Entry<String, String> entry : ACCENT_MAP.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
