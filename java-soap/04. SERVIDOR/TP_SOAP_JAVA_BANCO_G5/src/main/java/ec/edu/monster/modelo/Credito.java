package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Date;

public class Credito implements Serializable {
    private int idCredito;
    private int codCliente;
    private String numCuenta;
    private double montoPrestamo;
    private double interesAnual;
    private int plazoMeses;
    private double cuotaMensual;
    private Date fechaAprobacion;
    private String estado; // APROBADO, ACTIVO, etc.

    public Credito() {}

    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }

    public int getCodCliente() { return codCliente; }
    public void setCodCliente(int codCliente) { this.codCliente = codCliente; }

    public String getNumCuenta() { return numCuenta; }
    public void setNumCuenta(String numCuenta) { this.numCuenta = numCuenta; }

    public double getMontoPrestamo() { return montoPrestamo; }
    public void setMontoPrestamo(double montoPrestamo) { this.montoPrestamo = montoPrestamo; }

    public double getInteresAnual() { return interesAnual; }
    public void setInteresAnual(double interesAnual) { this.interesAnual = interesAnual; }

    public int getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(int plazoMeses) { this.plazoMeses = plazoMeses; }

    public double getCuotaMensual() { return cuotaMensual; }
    public void setCuotaMensual(double cuotaMensual) { this.cuotaMensual = cuotaMensual; }

    public Date getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(Date fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
