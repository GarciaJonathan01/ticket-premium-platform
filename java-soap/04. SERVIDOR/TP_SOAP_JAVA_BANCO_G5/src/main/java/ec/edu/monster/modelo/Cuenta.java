package ec.edu.monster.modelo;

import java.io.Serializable;

public class Cuenta implements Serializable {
    private String numCuenta;
    private int codCliente;
    private double saldo;

    public Cuenta() {}

    public Cuenta(String numCuenta, int codCliente, double saldo) {
        this.numCuenta = numCuenta;
        this.codCliente = codCliente;
        this.saldo = saldo;
    }

    public String getNumCuenta() { return numCuenta; }
    public void setNumCuenta(String numCuenta) { this.numCuenta = numCuenta; }

    public int getCodCliente() { return codCliente; }
    public void setCodCliente(int codCliente) { this.codCliente = codCliente; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
