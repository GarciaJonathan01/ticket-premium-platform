package ec.edu.monster.modelo;

import java.io.Serializable;

public class Cliente implements Serializable {
    private int idCliente;
    private String cedula;
    private String nombre;
    private String email;
    private String telefono;

    public Cliente() {}

    public Cliente(int idCliente, String cedula, String nombre, String email, String telefono) {
        this.idCliente = idCliente;
        this.cedula = cedula;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
