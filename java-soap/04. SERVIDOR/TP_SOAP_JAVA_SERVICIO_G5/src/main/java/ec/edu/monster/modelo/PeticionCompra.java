package ec.edu.monster.modelo;

import java.io.Serializable;

public class PeticionCompra implements Serializable {
    private int idLocalidad;
    private String codigoLocalidad;
    private int cantidad;
    private double precioUnitario;
    
    // Extended fields for FIFA 2026 seats and multiple matches
    private int codigoPartido;
    private int idAsientoPartido;
    private String nombreOcupante;

    public PeticionCompra() {}

    public PeticionCompra(int idLocalidad, String codigoLocalidad, int cantidad, double precioUnitario) {
        this.idLocalidad = idLocalidad;
        this.codigoLocalidad = codigoLocalidad;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdLocalidad() { return idLocalidad; }
    public void setIdLocalidad(int idLocalidad) { this.idLocalidad = idLocalidad; }
    public String getCodigoLocalidad() { return codigoLocalidad; }
    public void setCodigoLocalidad(String codigoLocalidad) { this.codigoLocalidad = codigoLocalidad; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public int getIdAsientoPartido() { return idAsientoPartido; }
    public void setIdAsientoPartido(int idAsientoPartido) { this.idAsientoPartido = idAsientoPartido; }
    public String getNombreOcupante() { return nombreOcupante; }
    public void setNombreOcupante(String nombreOcupante) { this.nombreOcupante = nombreOcupante; }
}
