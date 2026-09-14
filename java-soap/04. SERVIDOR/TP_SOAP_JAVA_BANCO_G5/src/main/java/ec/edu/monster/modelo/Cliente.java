package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Date;

public class Cliente implements Serializable {
    private int codCliente;
    private String cedula;
    private String nombre;
    private String genero;
    private Date fechaNacimiento;

    public Cliente() {}

    public Cliente(int codCliente, String cedula, String nombre, String genero, Date fechaNacimiento) {
        this.codCliente = codCliente;
        this.cedula = cedula;
        this.nombre = nombre;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getCodCliente() { return codCliente; }
    public void setCodCliente(int codCliente) { this.codCliente = codCliente; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
