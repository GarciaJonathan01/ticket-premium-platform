package ec.edu.monster.servicio;

import ec.edu.monster.ws.generated.*;
import java.util.List;

/**
 * Cliente SOAP que consume el WebService de la Federación de Fútbol.
 * Envuelve las llamadas al stub generado por JAX-WS.
 *
 * INSTRUCCIONES PARA GENERAR EL STUB EN NETBEANS:
 * 1. Desplegar primero el proyecto 01.SERVIDOR en Payara.
 * 2. En este proyecto, clic derecho -> New -> Web Service Client.
 * 3. En WSDL URL: http://127.0.0.1:8080/FederacionFutbol_WS/WSFederacion?wsdl
 * 4. Package: ec.edu.monster.ws.generated
 * 5. NetBeans generará automáticamente las clases stub.
 */
public class ClienteFederacion {

    private WSFederacion port;

    public ClienteFederacion() {
        try {
            WSFederacion_Service service = new WSFederacion_Service();
            this.port = service.getWSFederacionPort();
            if (this.port != null) {
                jakarta.xml.ws.BindingProvider bp = (jakarta.xml.ws.BindingProvider) this.port;
                bp.getRequestContext().put(jakarta.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
                    "http://127.0.0.1:8080/TP_SOAP_JAVA_SERVICIO_G5/WSFederacion");
            }
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo conectar al WS de la Federación");
            System.err.println("Asegúrese de que el servidor esté encendido.");
            System.err.println("Detalle: " + e.getMessage());
            this.port = null;
        }
    }

    public Usuario login(String username, String password) {
        if (port == null) return null;
        return port.login(username, password);
    }

    public List<PartidoFutbol> obtenerPartidosDisponibles() {
        if (port == null) return null;
        return port.obtenerPartidosDisponibles();
    }

    public List<LocalidadPartido> obtenerLocalidades(int codigoPartido) {
        if (port == null) return null;
        return port.obtenerLocalidades(codigoPartido);
    }

    public boolean decrementarDisponibilidad(int idLocalidad, int cantidad) {
        if (port == null) return false;
        return port.decrementarDisponibilidad(idLocalidad, cantidad);
    }

    public Factura comprarBoleto(int codigoPartido, String nombreCliente,
                                  String codigoLocalidad, int idLocalidad,
                                  int cantidad, double precioUnitario) {
        if (port == null) return null;
        PeticionCompra p = new PeticionCompra();
        p.setIdLocalidad(idLocalidad);
        p.setCodigoLocalidad(codigoLocalidad);
        p.setPrecioUnitario(precioUnitario);
        p.setCantidad(cantidad);
        p.setCodigoPartido(codigoPartido);
        p.setIdAsientoPartido(0);
        p.setNombreOcupante(nombreCliente);
        List<PeticionCompra> peticiones = new java.util.ArrayList<>();
        peticiones.add(p);
        return port.comprarBoletosMulti("1726354712", peticiones, "EFECTIVO", null);
    }

    public Factura comprarBoletos(int codigoPartido, String nombreCliente, List<PeticionCompra> peticiones) {
        if (port == null) return null;
        if (peticiones != null) {
            for (PeticionCompra p : peticiones) {
                p.setCodigoPartido(codigoPartido);
                p.setIdAsientoPartido(0);
                p.setNombreOcupante(nombreCliente);
            }
        }
        return port.comprarBoletosMulti("1726354712", peticiones, "EFECTIVO", null);
    }

    public PartidoFutbol obtenerPartido(int codigoPartido) {
        if (port == null) return null;
        return port.obtenerPartido(codigoPartido);
    }

    public List<ResumenVenta> obtenerResumenVentas(int codigoPartido) {
        if (port == null) return null;
        return port.obtenerResumenVentas(codigoPartido);
    }

    public List<DetalleVentaReporte> obtenerDetalleVentas(int codigoPartido) {
        if (port == null) return null;
        return port.obtenerDetalleVentas(codigoPartido);
    }

    public Factura comprarBoletosMulti(String cedulaCliente, List<PeticionCompra> peticiones, String formaPago, Integer idCredito) {
        if (port == null) return null;
        return port.comprarBoletosMulti(cedulaCliente, peticiones, formaPago, idCredito);
    }
}
