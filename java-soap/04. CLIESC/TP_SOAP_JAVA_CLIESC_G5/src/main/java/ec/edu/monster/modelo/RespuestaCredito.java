package ec.edu.monster.modelo;

import java.util.List;

public class RespuestaCredito {
    private boolean aprobado;
    private String mensaje;
    private double montoMaximo;
    private int idCredito;
    private List<CuotaAmortizacion> tablaAmortizacion;

    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public double getMontoMaximo() { return montoMaximo; }
    public void setMontoMaximo(double montoMaximo) { this.montoMaximo = montoMaximo; }
    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
    public List<CuotaAmortizacion> getTablaAmortizacion() { return tablaAmortizacion; }
    public void setTablaAmortizacion(List<CuotaAmortizacion> tablaAmortizacion) { this.tablaAmortizacion = tablaAmortizacion; }
}
