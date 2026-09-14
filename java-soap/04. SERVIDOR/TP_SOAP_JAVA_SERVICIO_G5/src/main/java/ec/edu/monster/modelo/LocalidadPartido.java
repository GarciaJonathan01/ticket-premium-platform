package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * Modelo que representa una localidad disponible para un partido.
 */
public class LocalidadPartido implements Serializable {

    private int id;
    private int codigoPartido;
    private String codigoLocalidad;
    private int disponibilidad;
    private double precio;

    public LocalidadPartido() {}

    public LocalidadPartido(int id, int codigoPartido, String codigoLocalidad, int disponibilidad, double precio) {
        this.id = id;
        this.codigoPartido = codigoPartido;
        this.codigoLocalidad = codigoLocalidad;
        this.disponibilidad = disponibilidad;
        this.precio = precio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public String getCodigoLocalidad() { return codigoLocalidad; }
    public void setCodigoLocalidad(String codigoLocalidad) { this.codigoLocalidad = codigoLocalidad; }
    public int getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(int disponibilidad) { this.disponibilidad = disponibilidad; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
