package ec.edu.monster.modelo;

import java.io.Serializable;

public class Estadio implements Serializable {
    private int idEstadio;
    private String nombre;
    private String ciudad;
    private int idPais;
    private String nombrePais; // Joined for display
    private int capacidad;

    public Estadio() {}

    public int getIdEstadio() { return idEstadio; }
    public void setIdEstadio(int idEstadio) { this.idEstadio = idEstadio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public int getIdPais() { return idPais; }
    public void setIdPais(int idPais) { this.idPais = idPais; }

    public String getNombrePais() { return nombrePais; }
    public void setNombrePais(String nombrePais) { this.nombrePais = nombrePais; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
}
