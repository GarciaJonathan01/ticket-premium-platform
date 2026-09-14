package ec.edu.monster.modelo;

import java.io.Serializable;

public class CuotaAmortizacion implements Serializable {
    private int numCuota;
    private double valorCuota;
    private double interesPagado;
    private double capitalPagado;
    private double saldo;

    public CuotaAmortizacion() {}

    public CuotaAmortizacion(int numCuota, double valorCuota, double interesPagado, double capitalPagado, double saldo) {
        this.numCuota = numCuota;
        this.valorCuota = valorCuota;
        this.interesPagado = interesPagado;
        this.capitalPagado = capitalPagado;
        this.saldo = saldo;
    }

    public int getNumCuota() { return numCuota; }
    public void setNumCuota(int numCuota) { this.numCuota = numCuota; }

    public double getValorCuota() { return valorCuota; }
    public void setValorCuota(double valorCuota) { this.valorCuota = valorCuota; }

    public double getInteresPagado() { return interesPagado; }
    public void setInteresPagado(double interesPagado) { this.interesPagado = interesPagado; }

    public double getCapitalPagado() { return capitalPagado; }
    public void setCapitalPagado(double capitalPagado) { this.capitalPagado = capitalPagado; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
