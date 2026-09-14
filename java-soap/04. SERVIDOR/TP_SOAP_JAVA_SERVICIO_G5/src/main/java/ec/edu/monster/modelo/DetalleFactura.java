package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * Modelo que representa un detalle de factura.
 */
public class DetalleFactura implements Serializable {

    private int id;
    private int idFactura;
    private int codigoPartido;
    private String codigoLocalidad;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleFactura() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public String getCodigoLocalidad() { return codigoLocalidad; }
    public void setCodigoLocalidad(String codigoLocalidad) { this.codigoLocalidad = codigoLocalidad; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
