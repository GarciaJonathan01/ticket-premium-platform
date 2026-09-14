package ec.edu.monster.modelo;

public class DetalleVentaReporte {
    private String fecha;
    private String partido;
    private String cliente;
    private String localidades;
    private int boletosTotales;
    private double totalVenta;

    public DetalleVentaReporte() {}

    public DetalleVentaReporte(String fecha, String partido, String cliente, String localidades, int boletosTotales, double totalVenta) {
        this.fecha = fecha;
        this.partido = partido;
        this.cliente = cliente;
        this.localidades = localidades;
        this.boletosTotales = boletosTotales;
        this.totalVenta = totalVenta;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getPartido() { return partido; }
    public void setPartido(String partido) { this.partido = partido; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getLocalidades() { return localidades; }
    public void setLocalidades(String localidades) { this.localidades = localidades; }
    public int getBoletosTotales() { return boletosTotales; }
    public void setBoletosTotales(int boletosTotales) { this.boletosTotales = boletosTotales; }
    public double getTotalVenta() { return totalVenta; }
    public void setTotalVenta(double totalVenta) { this.totalVenta = totalVenta; }
}
