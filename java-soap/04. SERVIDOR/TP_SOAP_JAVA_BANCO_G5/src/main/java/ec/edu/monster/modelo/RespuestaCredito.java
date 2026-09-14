package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.List;

public class RespuestaCredito implements Serializable {
    private boolean aprobado;
    private double montoMaximo;
    private String mensaje;
    private List<CuotaAmortizacion> tablaAmortizacion;
    private int idCredito;

    public RespuestaCredito() {}

    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

    public double getMontoMaximo() { return montoMaximo; }
    public void setMontoMaximo(double montoMaximo) { this.montoMaximo = montoMaximo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public List<CuotaAmortizacion> getTablaAmortizacion() { return tablaAmortizacion; }
    public void setTablaAmortizacion(List<CuotaAmortizacion> tablaAmortizacion) { this.tablaAmortizacion = tablaAmortizacion; }

    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
}
