package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Date;

public class Movimiento implements Serializable {
    private int codMovimiento;
    private String numCuenta;
    private String tipo; // DEP o RET
    private double valor;
    private Date fecha;

    public Movimiento() {}

    public Movimiento(int codMovimiento, String numCuenta, String tipo, double valor, Date fecha) {
        this.codMovimiento = codMovimiento;
        this.numCuenta = numCuenta;
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = fecha;
    }

    public int getCodMovimiento() { return codMovimiento; }
    public void setCodMovimiento(int codMovimiento) { this.codMovimiento = codMovimiento; }

    public String getNumCuenta() { return numCuenta; }
    public void setNumCuenta(String numCuenta) { this.numCuenta = numCuenta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
