package ec.edu.monster.modelo;

import java.io.Serializable;

public class AsientoPartido implements Serializable {
    private int idAsientoPartido;
    private int codigoPartido;
    private String seccion; // PALCO, TRIBUNA, GENERAL
    private String fila;
    private int numero;
    private String estado; // DISPONIBLE, RESERVADO, OCUPADO
    private Integer idDetalleFactura;
    private String nombreOcupante;
    private String nombreCliente;
    private Integer idFactura;
    private String fechaCompra;
    private double totalFactura;
    private double precio;

    public AsientoPartido() {}

    public int getIdAsientoPartido() { return idAsientoPartido; }
    public void setIdAsientoPartido(int idAsientoPartido) { this.idAsientoPartido = idAsientoPartido; }

    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getIdDetalleFactura() { return idDetalleFactura; }
    public void setIdDetalleFactura(Integer idDetalleFactura) { this.idDetalleFactura = idDetalleFactura; }

    public String getNombreOcupante() { return nombreOcupante; }
    public void setNombreOcupante(String nombreOcupante) { this.nombreOcupante = nombreOcupante; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }

    public String getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(String fechaCompra) { this.fechaCompra = fechaCompra; }

    public double getTotalFactura() { return totalFactura; }
    public void setTotalFactura(double totalFactura) { this.totalFactura = totalFactura; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
