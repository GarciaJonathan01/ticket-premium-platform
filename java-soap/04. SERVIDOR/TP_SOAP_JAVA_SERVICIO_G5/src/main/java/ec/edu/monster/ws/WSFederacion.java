package ec.edu.monster.ws;

import ec.edu.monster.modelo.*;
import ec.edu.monster.servicio.FederacionService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

@WebService(serviceName = "WSFederacion")
public class WSFederacion {

    private final FederacionService servicio = new FederacionService();

    // ==========================================
    // LOGIN
    // ==========================================
    @WebMethod(operationName = "login")
    public Usuario login(@WebParam(name = "username") String username,
                         @WebParam(name = "password") String password) {
        return servicio.login(username, password);
    }

    @WebMethod(operationName = "obtenerPartidosDisponibles")
    public List<PartidoFutbol> obtenerPartidosDisponibles() {
        return servicio.obtenerPartidosDisponibles();
    }

    @WebMethod(operationName = "obtenerLocalidades")
    public List<LocalidadPartido> obtenerLocalidades(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return servicio.obtenerLocalidades(codigoPartido);
    }

    @WebMethod(operationName = "decrementarDisponibilidad")
    public boolean decrementarDisponibilidad(
            @WebParam(name = "idLocalidad") int idLocalidad,
            @WebParam(name = "cantidad") int cantidad) {
        return servicio.decrementarDisponibilidad(idLocalidad, cantidad);
    }

    @WebMethod(operationName = "obtenerPartido")
    public PartidoFutbol obtenerPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return servicio.obtenerPartido(codigoPartido);
    }

    @WebMethod(operationName = "obtenerResumenVentas")
    public List<ResumenVenta> obtenerResumenVentas(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return servicio.obtenerResumenVentas(codigoPartido);
    }

    @WebMethod(operationName = "obtenerDetalleVentas")
    public List<DetalleVentaReporte> obtenerDetalleVentas(@WebParam(name = "codigoPartido") int codigoPartido) {
        return servicio.obtenerDetalleVentas(codigoPartido);
    }

    // ==========================================
    // MASHUP & SEATS
    // ==========================================
    @WebMethod(operationName = "obtenerAsientosPartido")
    public List<AsientoPartido> obtenerAsientosPartido(@WebParam(name = "codigoPartido") int codigoPartido) {
        return servicio.obtenerAsientosPartido(codigoPartido);
    }

    // ==========================================
    // REGISTRAR COMPRA MULTIPLE (FIFA 2026)
    // ==========================================
    @WebMethod(operationName = "comprarBoletosMulti")
    public Factura comprarBoletosMulti(
            @WebParam(name = "cedulaCliente") String cedulaCliente,
            @WebParam(name = "peticiones") List<PeticionCompra> peticiones,
            @WebParam(name = "formaPago") String formaPago,
            @WebParam(name = "idCredito") Integer idCredito) {
        return servicio.comprarBoletosMulti(cedulaCliente, peticiones, formaPago, idCredito);
    }

    // ==========================================
    // CRUD: PAIS
    // ==========================================
    @WebMethod(operationName = "listarPaises")
    public List<Pais> listarPaises() {
        return servicio.listarPaises();
    }

    @WebMethod(operationName = "crearPais")
    public boolean crearPais(@WebParam(name = "pais") Pais p) {
        return servicio.crearPais(p);
    }

    @WebMethod(operationName = "actualizarPais")
    public boolean actualizarPais(@WebParam(name = "pais") Pais p) {
        return servicio.actualizarPais(p);
    }

    @WebMethod(operationName = "eliminarPais")
    public boolean eliminarPais(@WebParam(name = "id") int id) {
        return servicio.eliminarPais(id);
    }

    // ==========================================
    // CRUD: ESTADIO
    // ==========================================
    @WebMethod(operationName = "listarEstadios")
    public List<Estadio> listarEstadios() {
        return servicio.listarEstadios();
    }

    @WebMethod(operationName = "crearEstadio")
    public boolean crearEstadio(@WebParam(name = "estadio") Estadio e) {
        return servicio.crearEstadio(e);
    }

    @WebMethod(operationName = "actualizarEstadio")
    public boolean actualizarEstadio(@WebParam(name = "estadio") Estadio e) {
        return servicio.actualizarEstadio(e);
    }

    @WebMethod(operationName = "eliminarEstadio")
    public boolean eliminarEstadio(@WebParam(name = "id") int id) {
        return servicio.eliminarEstadio(id);
    }

    // ==========================================
    // CRUD: EQUIPO
    // ==========================================
    @WebMethod(operationName = "listarEquipos")
    public List<Equipo> listarEquipos() {
        return servicio.listarEquipos();
    }

    @WebMethod(operationName = "crearEquipo")
    public boolean crearEquipo(@WebParam(name = "equipo") Equipo e) {
        return servicio.crearEquipo(e);
    }

    @WebMethod(operationName = "actualizarEquipo")
    public boolean actualizarEquipo(@WebParam(name = "equipo") Equipo e) {
        return servicio.actualizarEquipo(e);
    }

    @WebMethod(operationName = "eliminarEquipo")
    public boolean eliminarEquipo(@WebParam(name = "id") int id) {
        return servicio.eliminarEquipo(id);
    }

    // ==========================================
    // CRUD: CLIENTE
    // ==========================================
    @WebMethod(operationName = "listarClientes")
    public List<Cliente> listarClientes() {
        return servicio.listarClientes();
    }

    @WebMethod(operationName = "obtenerClientePorCedula")
    public Cliente obtenerClientePorCedula(@WebParam(name = "cedula") String cedula) {
        return servicio.obtenerClientePorCedula(cedula);
    }

    @WebMethod(operationName = "crearCliente")
    public boolean crearCliente(@WebParam(name = "cliente") Cliente c) {
        return servicio.crearCliente(c);
    }

    @WebMethod(operationName = "actualizarCliente")
    public boolean actualizarCliente(@WebParam(name = "cliente") Cliente c) {
        return servicio.actualizarCliente(c);
    }

    @WebMethod(operationName = "eliminarCliente")
    public boolean eliminarCliente(@WebParam(name = "id") int id) {
        return servicio.eliminarCliente(id);
    }

    // ==========================================
    // CRUD: PARTIDO
    // ==========================================
    @WebMethod(operationName = "listarPartidos")
    public List<PartidoFutbol> listarPartidos() {
        return servicio.listarPartidos();
    }

    @WebMethod(operationName = "crearPartido")
    public boolean crearPartido(@WebParam(name = "partido") PartidoFutbol p) {
        return servicio.crearPartido(p);
    }

    @WebMethod(operationName = "actualizarPartido")
    public boolean actualizarPartido(@WebParam(name = "partido") PartidoFutbol p) {
        return servicio.actualizarPartido(p);
    }

    @WebMethod(operationName = "eliminarPartido")
    public boolean eliminarPartido(@WebParam(name = "id") int id) {
        return servicio.eliminarPartido(id);
    }

    @WebMethod(operationName = "reservarAsientoTemporal")
    public boolean reservarAsientoTemporal(
            @WebParam(name = "idAsientoPartido") int idAsientoPartido,
            @WebParam(name = "reservar") boolean reservar) {
        return servicio.reservarAsientoTemporal(idAsientoPartido, reservar);
    }
}
