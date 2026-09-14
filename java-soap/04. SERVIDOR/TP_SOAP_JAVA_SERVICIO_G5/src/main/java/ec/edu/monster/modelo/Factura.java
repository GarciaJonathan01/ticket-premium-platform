package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Factura implements Serializable {

    private int id;
    private int codigoPartido; // compatibility
    private String nombreCliente;
    private String fecha;
    private double subtotal;
    private double iva;
    private double total;
    private List<DetalleFactura> detalles;
    
    // FIFA 2026 Payment and Clients fields
    private int idCliente;
    private String cedulaCliente;
    private double descuento;
    private String formaPago; // EFECTIVO, CREDITO
    private Integer idCredito;

    public Factura() {
        this.detalles = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleFactura> detalles) { this.detalles = detalles; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getCedulaCliente() { return cedulaCliente; }
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public Integer getIdCredito() { return idCredito; }
    public void setIdCredito(Integer idCredito) { this.idCredito = idCredito; }
}
