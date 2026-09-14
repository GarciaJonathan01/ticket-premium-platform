package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * Modelo para el reporte de resumen de ventas por localidad.
 */
public class ResumenVenta implements Serializable {

    private String codigoLocalidad;
    private int vendidos;
    private double totalRecaudado;

    public ResumenVenta() {}

    public ResumenVenta(String codigoLocalidad, int vendidos, double totalRecaudado) {
        this.codigoLocalidad = codigoLocalidad;
        this.vendidos = vendidos;
        this.totalRecaudado = totalRecaudado;
    }

    public String getCodigoLocalidad() { return codigoLocalidad; }
    public void setCodigoLocalidad(String codigoLocalidad) { this.codigoLocalidad = codigoLocalidad; }
    public int getVendidos() { return vendidos; }
    public void setVendidos(int vendidos) { this.vendidos = vendidos; }
    public double getTotalRecaudado() { return totalRecaudado; }
    public void setTotalRecaudado(double totalRecaudado) { this.totalRecaudado = totalRecaudado; }
}
