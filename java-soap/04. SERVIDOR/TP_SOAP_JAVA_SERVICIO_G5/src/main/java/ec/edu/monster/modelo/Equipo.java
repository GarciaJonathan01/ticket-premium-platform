package ec.edu.monster.modelo;

import java.io.Serializable;

public class Equipo implements Serializable {
    private int idEquipo;
    private String nombre;
    private int idPais;
    private String nombrePais; // Joined for display

    public Equipo() {}

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdPais() { return idPais; }
    public void setIdPais(int idPais) { this.idPais = idPais; }

    public String getNombrePais() { return nombrePais; }
    public void setNombrePais(String nombrePais) { this.nombrePais = nombrePais; }
}
