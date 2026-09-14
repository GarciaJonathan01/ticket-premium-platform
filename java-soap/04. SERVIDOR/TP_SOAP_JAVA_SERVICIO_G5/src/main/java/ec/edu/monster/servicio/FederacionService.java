package ec.edu.monster.servicio;

import ec.edu.monster.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FederacionService {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASSWORD");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ==========================================
    // LOGIN
    // ==========================================
    public Usuario login(String username, String password) {
        String sql = "SELECT ID, USERNAME, PASSWORD FROM USUARIO WHERE USERNAME = ? AND PASSWORD = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("ID"), rs.getString("USERNAME"), rs.getString("PASSWORD"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error login: " + e.getMessage());
        }
        return null;
    }

    // ==========================================
    // PARTIDOS DISPONIBLES (fecha >= ahora)
    // ==========================================
    public List<PartidoFutbol> obtenerPartidosDisponibles() {
        List<PartidoFutbol> partidos = new ArrayList<>();
        String sql = "SELECT p.CODIGO, el.NOMBRE AS EQUIPO_LOCAL, ev.NOMBRE AS EQUIPO_VISITA, "
                   + "DATE_FORMAT(p.FECHA, '%Y-%m-%d %H:%i') AS FECHA, est.NOMBRE AS LUGAR, "
                   + "p.ID_EQUIPO_LOCAL, p.ID_EQUIPO_VISITA, p.ID_ESTADIO, p.GRUPO "
                   + "FROM PARTIDO_FUTBOL p "
                   + "JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO "
                   + "JOIN EQUIPO ev ON p.ID_EQUIPO_VISITA = ev.ID_EQUIPO "
                   + "JOIN ESTADIO est ON p.ID_ESTADIO = est.ID_ESTADIO "
                   + "WHERE p.FECHA >= NOW() ORDER BY p.FECHA";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PartidoFutbol p = new PartidoFutbol();
                p.setCodigo(rs.getInt("CODIGO"));
                p.setEquipoLocal(rs.getString("EQUIPO_LOCAL"));
                p.setEquipoVisita(rs.getString("EQUIPO_VISITA"));
                p.setFecha(rs.getString("FECHA"));
                p.setLugar(rs.getString("LUGAR"));
                p.setIdEquipoLocal(rs.getInt("ID_EQUIPO_LOCAL"));
                p.setIdEquipoVisita(rs.getInt("ID_EQUIPO_VISITA"));
                p.setIdEstadio(rs.getInt("ID_ESTADIO"));
                p.setGrupo(rs.getString("GRUPO"));
                partidos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPartidosDisponibles: " + e.getMessage());
        }
        return partidos;
    }

    // ==========================================
    // LOCALIDADES DISPONIBLES PARA UN PARTIDO
    // ==========================================
    public List<LocalidadPartido> obtenerLocalidades(int codigoPartido) {
        List<LocalidadPartido> localidades = new ArrayList<>();
        String sql = "SELECT ID, CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO "
                   + "FROM LOCALIDAD_PARTIDO "
                   + "WHERE CODIGO_PARTIDO = ? AND DISPONIBILIDAD > 0";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigoPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalidadPartido l = new LocalidadPartido();
                    l.setId(rs.getInt("ID"));
                    l.setCodigoPartido(rs.getInt("CODIGO_PARTIDO"));
                    l.setCodigoLocalidad(rs.getString("CODIGO_LOCALIDAD"));
                    l.setDisponibilidad(rs.getInt("DISPONIBILIDAD"));
                    l.setPrecio(rs.getDouble("PRECIO"));
                    localidades.add(l);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerLocalidades: " + e.getMessage());
        }
        return localidades;
    }

    // ==========================================
    // OBTENER PARTIDO POR CÓDIGO
    // ==========================================
    public PartidoFutbol obtenerPartido(int codigoPartido) {
        String sql = "SELECT p.CODIGO, el.NOMBRE AS EQUIPO_LOCAL, ev.NOMBRE AS EQUIPO_VISITA, "
                   + "DATE_FORMAT(p.FECHA, '%Y-%m-%d %H:%i') AS FECHA, est.NOMBRE AS LUGAR, "
                   + "p.ID_EQUIPO_LOCAL, p.ID_EQUIPO_VISITA, p.ID_ESTADIO, p.GRUPO "
                   + "FROM PARTIDO_FUTBOL p "
                   + "JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO "
                   + "JOIN EQUIPO ev ON p.ID_EQUIPO_VISITA = ev.ID_EQUIPO "
                   + "JOIN ESTADIO est ON p.ID_ESTADIO = est.ID_ESTADIO "
                   + "WHERE p.CODIGO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigoPartido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PartidoFutbol p = new PartidoFutbol();
                    p.setCodigo(rs.getInt("CODIGO"));
                    p.setEquipoLocal(rs.getString("EQUIPO_LOCAL"));
                    p.setEquipoVisita(rs.getString("EQUIPO_VISITA"));
                    p.setFecha(rs.getString("FECHA"));
                    p.setLugar(rs.getString("LUGAR"));
                    p.setIdEquipoLocal(rs.getInt("ID_EQUIPO_LOCAL"));
                    p.setIdEquipoVisita(rs.getInt("ID_EQUIPO_VISITA"));
                    p.setIdEstadio(rs.getInt("ID_ESTADIO"));
                    p.setGrupo(rs.getString("GRUPO"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPartido: " + e.getMessage());
        }
        return null;
    }

    // ==========================================
    // DECREMENTAR DISPONIBILIDAD (LEGACY COMPATIBILITY)
    // ==========================================
    public boolean decrementarDisponibilidad(int idLocalidad, int cantidad) {
        String sql = "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - ? WHERE ID = ? AND DISPONIBILIDAD >= ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idLocalidad);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error decrementarDisponibilidad: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // RESUMEN DE VENTAS DE UN PARTIDO
    // ==========================================
    public List<ResumenVenta> obtenerResumenVentas(int codigoPartido) {
        List<ResumenVenta> resumen = new ArrayList<>();
        String sql = "SELECT CODIGO_LOCALIDAD, "
                   + "SUM(CANTIDAD) AS VENDIDOS, "
                   + "SUM(SUBTOTAL) AS TOTAL_RECAUDADO "
                   + "FROM DETALLE_FACTURA "
                   + "WHERE CODIGO_PARTIDO = ? "
                   + "GROUP BY CODIGO_LOCALIDAD";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigoPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResumenVenta r = new ResumenVenta();
                    r.setCodigoLocalidad(rs.getString("CODIGO_LOCALIDAD"));
                    r.setVendidos(rs.getInt("VENDIDOS"));
                    r.setTotalRecaudado(rs.getDouble("TOTAL_RECAUDADO"));
                    resumen.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerResumenVentas: " + e.getMessage());
        }
        return resumen;
    }

    // ==========================================
    // DETALLE DE VENTAS DE UN PARTIDO
    // ==========================================
    public List<DetalleVentaReporte> obtenerDetalleVentas(int codigoPartido) {
        List<DetalleVentaReporte> detalles = new ArrayList<>();
        String sql = "SELECT f.ID AS FACTURA_ID, "
                   + "DATE_FORMAT(f.FECHA, '%Y-%m-%d %H:%i') AS FECHA, "
                   + "CONCAT(el.NOMBRE, ' vs ', ev.NOMBRE) AS PARTIDO, "
                   + "c.NOMBRE AS NOMBRE_CLIENTE, "
                   + "GROUP_CONCAT(CONCAT(d.CANTIDAD, 'x ', d.CODIGO_LOCALIDAD) SEPARATOR ', ') AS LOCALIDADES, "
                   + "SUM(d.CANTIDAD) AS BOLETOS_TOTALES, "
                   + "f.TOTAL "
                   + "FROM FACTURA f "
                   + "JOIN CLIENTE c ON f.ID_CLIENTE = c.ID_CLIENTE "
                   + "JOIN DETALLE_FACTURA d ON f.ID = d.ID_FACTURA "
                   + "JOIN PARTIDO_FUTBOL p ON d.CODIGO_PARTIDO = p.CODIGO "
                   + "JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO "
                   + "JOIN EQUIPO ev ON p.ID_EQUIPO_VISITA = ev.ID_EQUIPO "
                   + "WHERE d.CODIGO_PARTIDO = ? "
                   + "GROUP BY f.ID, f.FECHA, el.NOMBRE, ev.NOMBRE, c.NOMBRE, f.TOTAL "
                   + "ORDER BY f.FECHA DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigoPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVentaReporte d = new DetalleVentaReporte();
                    d.setFecha(rs.getString("FECHA"));
                    d.setPartido(rs.getString("PARTIDO"));
                    d.setCliente(rs.getString("NOMBRE_CLIENTE"));
                    d.setLocalidades(rs.getString("LOCALIDADES"));
                    d.setBoletosTotales(rs.getInt("BOLETOS_TOTALES"));
                    d.setTotalVenta(rs.getDouble("TOTAL"));
                    detalles.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerDetalleVentas: " + e.getMessage());
        }
        return detalles;
    }

    // ==========================================
    // MASHUP: OBTENER ASIENTOS DE UN PARTIDO
    // ==========================================
    public List<AsientoPartido> obtenerAsientosPartido(int codigoPartido) {
        String sqlCleanup = "UPDATE ASIENTO_PARTIDO SET ESTADO = 'DISPONIBLE', FECHA_RESERVA = NULL "
                           + "WHERE CODIGO_PARTIDO = ? AND ESTADO = 'RESERVADO' "
                           + "AND FECHA_RESERVA < DATE_SUB(NOW(), INTERVAL 10 MINUTE)";
        try (Connection conn = getConnection();
             PreparedStatement psCleanup = conn.prepareStatement(sqlCleanup)) {
            psCleanup.setInt(1, codigoPartido);
            psCleanup.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error cleaning expired reservations: " + e.getMessage());
        }

        List<AsientoPartido> asientos = new ArrayList<>();
        String sql = "SELECT ap.ID_ASIENTO_PARTIDO, ap.CODIGO_PARTIDO, a.SECCION, a.FILA, a.NUMERO, ap.ESTADO, "
                   + "ap.ID_DETALLE_FACTURA, ap.NOMBRE_OCUPANTE, c.NOMBRE AS NOMBRE_CLIENTE, f.ID AS FACTURA_ID, "
                   + "DATE_FORMAT(f.FECHA, '%Y-%m-%d %H:%i') AS FECHA_COMPRA, f.TOTAL, lp.PRECIO "
                   + "FROM ASIENTO_PARTIDO ap "
                   + "JOIN ASIENTO a ON ap.ID_ASIENTO = a.ID_ASIENTO "
                   + "JOIN PARTIDO_FUTBOL p ON ap.CODIGO_PARTIDO = p.CODIGO "
                   + "JOIN LOCALIDAD_PARTIDO lp ON p.CODIGO = lp.CODIGO_PARTIDO AND a.SECCION = lp.CODIGO_LOCALIDAD "
                   + "LEFT JOIN DETALLE_FACTURA df ON ap.ID_DETALLE_FACTURA = df.ID "
                   + "LEFT JOIN FACTURA f ON df.ID_FACTURA = f.ID "
                   + "LEFT JOIN CLIENTE c ON f.ID_CLIENTE = c.ID_CLIENTE "
                   + "WHERE ap.CODIGO_PARTIDO = ? "
                   + "ORDER BY a.SECCION, a.FILA, a.NUMERO";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigoPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsientoPartido ap = new AsientoPartido();
                    ap.setIdAsientoPartido(rs.getInt("ID_ASIENTO_PARTIDO"));
                    ap.setCodigoPartido(rs.getInt("CODIGO_PARTIDO"));
                    ap.setSeccion(rs.getString("SECCION"));
                    ap.setFila(rs.getString("FILA"));
                    ap.setNumero(rs.getInt("NUMERO"));
                    ap.setEstado(rs.getString("ESTADO"));
                    
                    int detId = rs.getInt("ID_DETALLE_FACTURA");
                    if (!rs.wasNull()) {
                        ap.setIdDetalleFactura(detId);
                        ap.setNombreOcupante(rs.getString("NOMBRE_OCUPANTE"));
                        ap.setNombreCliente(rs.getString("NOMBRE_CLIENTE"));
                        ap.setIdFactura(rs.getInt("FACTURA_ID"));
                        ap.setFechaCompra(rs.getString("FECHA_COMPRA"));
                        ap.setTotalFactura(rs.getDouble("TOTAL"));
                    }
                    ap.setPrecio(rs.getDouble("PRECIO"));
                    asientos.add(ap);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerAsientosPartido: " + e.getMessage());
        }
        return asientos;
    }

    // ==========================================
    // COMPRA DE BOLETOS MULTIPLE Y MULTIPARTIDO
    // ==========================================
    public Factura comprarBoletosMulti(String cedulaCliente, List<PeticionCompra> peticiones, String formaPago, Integer idCredito) {
        if (peticiones == null || peticiones.isEmpty()) return null;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Obtener o crear Cliente
                int idCliente = -1;
                String nombreCliente = "";
                String sqlGetClient = "SELECT ID_CLIENTE, NOMBRE FROM CLIENTE WHERE CEDULA = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlGetClient)) {
                    ps.setString(1, cedulaCliente);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idCliente = rs.getInt("ID_CLIENTE");
                            nombreCliente = rs.getString("NOMBRE");
                        }
                    }
                }

                if (idCliente == -1) {
                    // Auto-crear cliente generico si no existe
                    String sqlInsClient = "INSERT INTO CLIENTE (CEDULA, NOMBRE, EMAIL, TELEFONO) VALUES (?, ?, ?, '0999999999')";
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsClient, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, cedulaCliente);
                        ps.setString(2, "Cliente C.I. " + cedulaCliente);
                        ps.setString(3, "cliente." + cedulaCliente + "@temp.com");
                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) idCliente = rs.getInt(1);
                        }
                    }
                    nombreCliente = "Cliente C.I. " + cedulaCliente;
                }

                // 2. Calcular subtotal y descuento
                double subtotal = 0;
                for (PeticionCompra p : peticiones) {
                    subtotal += p.getCantidad() * p.getPrecioUnitario();
                }

                double descuento = 0.00;
                if ("EFECTIVO".equalsIgnoreCase(formaPago)) {
                    descuento = Math.round(subtotal * 0.12 * 100.0) / 100.0;
                }

                double subtotalConDesc = subtotal - descuento;
                double iva = Math.round(subtotalConDesc * 0.15 * 100.0) / 100.0;
                double total = Math.round((subtotalConDesc + iva) * 100.0) / 100.0;

                // 3. Crear Factura
                Factura factura = new Factura();
                factura.setIdCliente(idCliente);
                factura.setNombreCliente(nombreCliente);
                factura.setSubtotal(subtotal);
                factura.setDescuento(descuento);
                factura.setIva(iva);
                factura.setTotal(total);
                factura.setFormaPago(formaPago);
                factura.setIdCredito(idCredito);

                String sqlInsFact = "INSERT INTO FACTURA (ID_CLIENTE, FECHA, SUBTOTAL, DESCUENTO, IVA, TOTAL, FORMA_PAGO, ID_CREDITO) "
                                  + "VALUES (?, NOW(), ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsFact, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente);
                    ps.setDouble(2, subtotal);
                    ps.setDouble(3, descuento);
                    ps.setDouble(4, iva);
                    ps.setDouble(5, total);
                    ps.setString(6, formaPago);
                    if (idCredito != null) {
                        ps.setInt(7, idCredito);
                    } else {
                        ps.setNull(7, Types.INTEGER);
                    }
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) factura.setId(rs.getInt(1));
                    }
                }

                // 4. Crear Detalles y actualizar asientos
                String sqlInsDet = "INSERT INTO DETALLE_FACTURA (ID_FACTURA, CODIGO_PARTIDO, CODIGO_LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) "
                                 + "VALUES (?, ?, ?, ?, ?, ?)";
                String sqlUpdateSeat = "UPDATE ASIENTO_PARTIDO SET ESTADO = 'OCUPADO', ID_DETALLE_FACTURA = ?, NOMBRE_OCUPANTE = ?, FECHA_RESERVA = NULL "
                                     + "WHERE ID_ASIENTO_PARTIDO = ? AND ESTADO IN ('DISPONIBLE', 'RESERVADO')";
                String sqlRestarDisp = "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - ? "
                                     + "WHERE CODIGO_PARTIDO = ? AND CODIGO_LOCALIDAD = ? AND DISPONIBILIDAD >= ?";

                try (PreparedStatement psDet = conn.prepareStatement(sqlInsDet, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement psSeat = conn.prepareStatement(sqlUpdateSeat);
                     PreparedStatement psRestar = conn.prepareStatement(sqlRestarDisp)) {

                    for (PeticionCompra p : peticiones) {
                        // Insertar Detalle Factura
                        psDet.setInt(1, factura.getId());
                        psDet.setInt(2, p.getCodigoPartido());
                        psDet.setString(3, p.getCodigoLocalidad());
                        psDet.setInt(4, p.getCantidad());
                        psDet.setDouble(5, p.getPrecioUnitario());
                        psDet.setDouble(6, p.getCantidad() * p.getPrecioUnitario());
                        psDet.executeUpdate();
                        
                        int idDetalle = -1;
                        try (ResultSet rs = psDet.getGeneratedKeys()) {
                            if (rs.next()) idDetalle = rs.getInt(1);
                        }

                        // Restar disponibilidad
                        psRestar.setInt(1, p.getCantidad());
                        psRestar.setInt(2, p.getCodigoPartido());
                        psRestar.setString(3, p.getCodigoLocalidad());
                        psRestar.setInt(4, p.getCantidad());
                        int updatedDisp = psRestar.executeUpdate();
                        if (updatedDisp == 0) {
                            throw new SQLException("Disponibilidad insuficiente para la localidad: " + p.getCodigoLocalidad() + " en partido " + p.getCodigoPartido());
                        }

                        // Ocupar Asiento
                        if (p.getIdAsientoPartido() > 0) {
                            psSeat.setInt(1, idDetalle);
                            psSeat.setString(2, p.getNombreOcupante() != null ? p.getNombreOcupante() : nombreCliente);
                            psSeat.setInt(3, p.getIdAsientoPartido());
                            int updatedSeat = psSeat.executeUpdate();
                            if (updatedSeat == 0) {
                                throw new SQLException("El asiento seleccionado ya esta ocupado o no existe.");
                            }
                        } else {
                            // Auto-asignar 'n' asientos de manera balanceada si se hizo una compra sin mapa interactivo (consola, móvil o escritorio)
                            List<Integer> listNorte = new ArrayList<>();
                            List<Integer> listSur = new ArrayList<>();
                            List<Integer> listOthers = new ArrayList<>();

                            int maxGeneral = 0;
                            String sqlMax = "SELECT MAX(a.NUMERO) FROM ASIENTO_PARTIDO ap "
                                          + "JOIN ASIENTO a ON ap.ID_ASIENTO = a.ID_ASIENTO "
                                          + "WHERE ap.CODIGO_PARTIDO = ? AND a.SECCION = ?";
                            try (PreparedStatement psMax = conn.prepareStatement(sqlMax)) {
                                psMax.setInt(1, p.getCodigoPartido());
                                psMax.setString(2, p.getCodigoLocalidad());
                                try (ResultSet rs = psMax.executeQuery()) {
                                    if (rs.next()) {
                                        maxGeneral = rs.getInt(1);
                                    }
                                }
                            }
                            int midGeneral = maxGeneral / 2;

                            String sqlFind = "SELECT ap.ID_ASIENTO_PARTIDO, a.NUMERO FROM ASIENTO_PARTIDO ap "
                                           + "JOIN ASIENTO a ON ap.ID_ASIENTO = a.ID_ASIENTO "
                                           + "WHERE ap.CODIGO_PARTIDO = ? AND a.SECCION = ? AND ap.ESTADO = 'DISPONIBLE' "
                                           + "FOR UPDATE";

                            try (PreparedStatement psFind = conn.prepareStatement(sqlFind)) {
                                psFind.setInt(1, p.getCodigoPartido());
                                psFind.setString(2, p.getCodigoLocalidad());
                                try (ResultSet rs = psFind.executeQuery()) {
                                    while (rs.next()) {
                                        int idAP = rs.getInt("ID_ASIENTO_PARTIDO");
                                        int num = rs.getInt("NUMERO");
                                        if ("GENERAL".equalsIgnoreCase(p.getCodigoLocalidad())) {
                                            if (num <= midGeneral) {
                                                listNorte.add(idAP);
                                            } else {
                                                listSur.add(idAP);
                                            }
                                        } else {
                                            listOthers.add(idAP);
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < p.getCantidad(); i++) {
                                int idToOccupy = -1;
                                if ("GENERAL".equalsIgnoreCase(p.getCodigoLocalidad())) {
                                    if (!listNorte.isEmpty() && !listSur.isEmpty()) {
                                        if (listNorte.size() >= listSur.size()) {
                                            idToOccupy = listNorte.remove(0);
                                        } else {
                                            idToOccupy = listSur.remove(0);
                                        }
                                    } else if (!listNorte.isEmpty()) {
                                        idToOccupy = listNorte.remove(0);
                                    } else if (!listSur.isEmpty()) {
                                        idToOccupy = listSur.remove(0);
                                    }
                                } else {
                                    if (!listOthers.isEmpty()) {
                                        idToOccupy = listOthers.remove(0);
                                    }
                                }

                                if (idToOccupy == -1) {
                                    throw new SQLException("No se encontraron suficientes asientos libres en la seccion " + p.getCodigoLocalidad());
                                }

                                psSeat.setInt(1, idDetalle);
                                psSeat.setString(2, p.getNombreOcupante() != null ? p.getNombreOcupante() : nombreCliente);
                                psSeat.setInt(3, idToOccupy);
                                int updatedSeat = psSeat.executeUpdate();
                                if (updatedSeat == 0) {
                                    throw new SQLException("El asiento autoseleccionado con ID " + idToOccupy + " ya esta ocupado o no existe.");
                                }
                            }
                        }
                    }
                }

                conn.commit();
                return factura;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error transaccional en comprarBoletosMulti (rollback): " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos en comprarBoletosMulti: " + e.getMessage());
            return null;
        }
    }

    // =========================================================================
    // CRUDS METODOS PARA PAISES, ESTADIOS, EQUIPOS, CLIENTES, PARTIDOS
    // =========================================================================

    // PAIS
    public List<Pais> listarPaises() {
        List<Pais> list = new ArrayList<>();
        String sql = "SELECT ID_PAIS, NOMBRE FROM PAIS ORDER BY NOMBRE";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Pais(rs.getInt("ID_PAIS"), rs.getString("NOMBRE")));
            }
        } catch (SQLException e) {
            System.err.println("Error listarPaises: " + e.getMessage());
        }
        return list;
    }

    public boolean crearPais(Pais p) {
        String sql = "INSERT INTO PAIS (NOMBRE) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error crearPais: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPais(Pais p) {
        String sql = "UPDATE PAIS SET NOMBRE = ? WHERE ID_PAIS = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getIdPais());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizarPais: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPais(int id) {
        String sql = "DELETE FROM PAIS WHERE ID_PAIS = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarPais: " + e.getMessage());
            return false;
        }
    }

    // ESTADIO
    public List<Estadio> listarEstadios() {
        List<Estadio> list = new ArrayList<>();
        String sql = "SELECT e.ID_ESTADIO, e.NOMBRE, e.CIUDAD, e.ID_PAIS, p.NOMBRE AS PAIS_NOMBRE, e.CAPACIDAD " +
                     "FROM ESTADIO e JOIN PAIS p ON e.ID_PAIS = p.ID_PAIS ORDER BY e.NOMBRE";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Estadio e = new Estadio();
                e.setIdEstadio(rs.getInt("ID_ESTADIO"));
                e.setNombre(rs.getString("NOMBRE"));
                e.setCiudad(rs.getString("CIUDAD"));
                e.setIdPais(rs.getInt("ID_PAIS"));
                e.setNombrePais(rs.getString("PAIS_NOMBRE"));
                e.setCapacidad(rs.getInt("CAPACIDAD"));
                list.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Error listarEstadios: " + e.getMessage());
        }
        return list;
    }

    public boolean crearEstadio(Estadio e) {
        String sql = "INSERT INTO ESTADIO (NOMBRE, CIUDAD, ID_PAIS, CAPACIDAD) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idEstadio = -1;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, e.getNombre());
                    ps.setString(2, e.getCiudad());
                    ps.setInt(3, e.getIdPais());
                    ps.setInt(4, e.getCapacidad());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) idEstadio = rs.getInt(1);
                    }
                }
                
                // Generar automaticamente 50 asientos escalados para este estadio
                String sqlInsSeat = "INSERT INTO ASIENTO (ID_ESTADIO, SECCION, FILA, NUMERO) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsSeat)) {
                    // 10 Palco
                    for (int i = 1; i <= 10; i++) {
                        ps.setInt(1, idEstadio);
                        ps.setString(2, "PALCO");
                        ps.setString(3, "P");
                        ps.setInt(4, i);
                        ps.addBatch();
                    }
                    // 15 Tribuna
                    for (int i = 1; i <= 15; i++) {
                        ps.setInt(1, idEstadio);
                        ps.setString(2, "TRIBUNA");
                        ps.setString(3, "T");
                        ps.setInt(4, i);
                        ps.addBatch();
                    }
                    // 25 General
                    for (int i = 1; i <= 25; i++) {
                        ps.setInt(1, idEstadio);
                        ps.setString(2, "GENERAL");
                        ps.setString(3, "G");
                        ps.setInt(4, i);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            System.err.println("Error crearEstadio: " + ex.getMessage());
            return false;
        }
    }

    public boolean actualizarEstadio(Estadio e) {
        String sql = "UPDATE ESTADIO SET NOMBRE = ?, CIUDAD = ?, ID_PAIS = ?, CAPACIDAD = ? WHERE ID_ESTADIO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getCiudad());
            ps.setInt(3, e.getIdPais());
            ps.setInt(4, e.getCapacidad());
            ps.setInt(5, e.getIdEstadio());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error actualizarEstadio: " + ex.getMessage());
            return false;
        }
    }

    public boolean eliminarEstadio(int id) {
        String sql = "DELETE FROM ESTADIO WHERE ID_ESTADIO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarEstadio: " + e.getMessage());
            return false;
        }
    }

    // EQUIPO
    public List<Equipo> listarEquipos() {
        List<Equipo> list = new ArrayList<>();
        String sql = "SELECT e.ID_EQUIPO, e.NOMBRE, e.ID_PAIS, p.NOMBRE AS PAIS_NOMBRE " +
                     "FROM EQUIPO e JOIN PAIS p ON e.ID_PAIS = p.ID_PAIS ORDER BY e.NOMBRE";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Equipo eq = new Equipo();
                eq.setIdEquipo(rs.getInt("ID_EQUIPO"));
                eq.setNombre(rs.getString("NOMBRE"));
                eq.setIdPais(rs.getInt("ID_PAIS"));
                eq.setNombrePais(rs.getString("PAIS_NOMBRE"));
                list.add(eq);
            }
        } catch (SQLException e) {
            System.err.println("Error listarEquipos: " + e.getMessage());
        }
        return list;
    }

    public boolean crearEquipo(Equipo e) {
        String sql = "INSERT INTO EQUIPO (NOMBRE, ID_PAIS) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getIdPais());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error crearEquipo: " + ex.getMessage());
            return false;
        }
    }

    public boolean actualizarEquipo(Equipo e) {
        String sql = "UPDATE EQUIPO SET NOMBRE = ?, ID_PAIS = ? WHERE ID_EQUIPO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getIdPais());
            ps.setInt(3, e.getIdEquipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error actualizarEquipo: " + ex.getMessage());
            return false;
        }
    }

    public boolean eliminarEquipo(int id) {
        String sql = "DELETE FROM EQUIPO WHERE ID_EQUIPO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarEquipo: " + e.getMessage());
            return false;
        }
    }

    // CLIENTE
    public List<Cliente> listarClientes() {
        List<Cliente> list = new ArrayList<>();
        String sql = "SELECT ID_CLIENTE, CEDULA, NOMBRE, EMAIL, TELEFONO FROM CLIENTE ORDER BY NOMBRE";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Cliente(
                    rs.getInt("ID_CLIENTE"),
                    rs.getString("CEDULA"),
                    rs.getString("NOMBRE"),
                    rs.getString("EMAIL"),
                    rs.getString("TELEFONO")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error listarClientes: " + e.getMessage());
        }
        return list;
    }

    public Cliente obtenerClientePorCedula(String cedula) {
        String sql = "SELECT ID_CLIENTE, CEDULA, NOMBRE, EMAIL, TELEFONO FROM CLIENTE WHERE CEDULA = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("ID_CLIENTE"),
                        rs.getString("CEDULA"),
                        rs.getString("NOMBRE"),
                        rs.getString("EMAIL"),
                        rs.getString("TELEFONO")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerClientePorCedula: " + e.getMessage());
        }
        return null;
    }

    public boolean crearCliente(Cliente c) {
        String sql = "INSERT INTO CLIENTE (CEDULA, NOMBRE, EMAIL, TELEFONO) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCedula());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error crearCliente: " + ex.getMessage());
            return false;
        }
    }

    public boolean actualizarCliente(Cliente c) {
        String sql = "UPDATE CLIENTE SET NOMBRE = ?, EMAIL = ?, TELEFONO = ? WHERE ID_CLIENTE = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getTelefono());
            ps.setInt(4, c.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error actualizarCliente: " + ex.getMessage());
            return false;
        }
    }

    public boolean eliminarCliente(int id) {
        String sql = "DELETE FROM CLIENTE WHERE ID_CLIENTE = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarCliente: " + e.getMessage());
            return false;
        }
    }

    // PARTIDO_FUTBOL
    public List<PartidoFutbol> listarPartidos() {
        List<PartidoFutbol> list = new ArrayList<>();
        String sql = "SELECT p.CODIGO, el.NOMBRE AS EQUIPO_LOCAL, ev.NOMBRE AS EQUIPO_VISITA, " +
                     "DATE_FORMAT(p.FECHA, '%Y-%m-%d %H:%i') AS FECHA, est.NOMBRE AS LUGAR, " +
                     "p.ID_EQUIPO_LOCAL, p.ID_EQUIPO_VISITA, p.ID_ESTADIO, p.GRUPO " +
                     "FROM PARTIDO_FUTBOL p " +
                     "JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO " +
                     "JOIN EQUIPO ev ON p.ID_EQUIPO_VISITA = ev.ID_EQUIPO " +
                     "JOIN ESTADIO est ON p.ID_ESTADIO = est.ID_ESTADIO ORDER BY p.FECHA";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                PartidoFutbol p = new PartidoFutbol();
                p.setCodigo(rs.getInt("CODIGO"));
                p.setEquipoLocal(rs.getString("EQUIPO_LOCAL"));
                p.setEquipoVisita(rs.getString("EQUIPO_VISITA"));
                p.setFecha(rs.getString("FECHA"));
                p.setLugar(rs.getString("LUGAR"));
                p.setIdEquipoLocal(rs.getInt("ID_EQUIPO_LOCAL"));
                p.setIdEquipoVisita(rs.getInt("ID_EQUIPO_VISITA"));
                p.setIdEstadio(rs.getInt("ID_ESTADIO"));
                p.setGrupo(rs.getString("GRUPO"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error listarPartidos: " + e.getMessage());
        }
        return list;
    }

    public boolean crearPartido(PartidoFutbol p) {
        String sql = "INSERT INTO PARTIDO_FUTBOL (ID_EQUIPO_LOCAL, ID_EQUIPO_VISITA, FECHA, ID_ESTADIO, GRUPO) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                int codigoPartido = -1;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, p.getIdEquipoLocal());
                    ps.setInt(2, p.getIdEquipoVisita());
                    ps.setString(3, p.getFecha());
                    ps.setInt(4, p.getIdEstadio());
                    ps.setString(5, p.getGrupo());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) codigoPartido = rs.getInt(1);
                    }
                }

                // Inicializar disponibilidad agregada
                String sqlInsLoc = "INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsLoc)) {
                    // Palco
                    ps.setInt(1, codigoPartido);
                    ps.setString(2, "PALCO");
                    ps.setInt(3, 10);
                    ps.setDouble(4, 150.00);
                    ps.executeUpdate();
                    // Tribuna
                    ps.setInt(1, codigoPartido);
                    ps.setString(2, "TRIBUNA");
                    ps.setInt(3, 15);
                    ps.setDouble(4, 80.00);
                    ps.executeUpdate();
                    // General
                    ps.setInt(1, codigoPartido);
                    ps.setString(2, "GENERAL");
                    ps.setInt(3, 25);
                    ps.setDouble(4, 40.00);
                    ps.executeUpdate();
                }

                // Generar los ASIENTO_PARTIDO
                String sqlInsSeatPart = "INSERT INTO ASIENTO_PARTIDO (CODIGO_PARTIDO, ID_ASIENTO, ESTADO) VALUES (?, ?, 'DISPONIBLE')";
                String sqlGetSeats = "SELECT ID_ASIENTO FROM ASIENTO WHERE ID_ESTADIO = ?";
                List<Integer> asientos = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(sqlGetSeats)) {
                    ps.setInt(1, p.getIdEstadio());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) asientos.add(rs.getInt("ID_ASIENTO"));
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlInsSeatPart)) {
                    for (int idAsiento : asientos) {
                        ps.setInt(1, codigoPartido);
                        ps.setInt(2, idAsiento);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            System.err.println("Error crearPartido: " + ex.getMessage());
            return false;
        }
    }

    public boolean actualizarPartido(PartidoFutbol p) {
        String sql = "UPDATE PARTIDO_FUTBOL SET ID_EQUIPO_LOCAL = ?, ID_EQUIPO_VISITA = ?, FECHA = ?, ID_ESTADIO = ?, GRUPO = ? WHERE CODIGO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdEquipoLocal());
            ps.setInt(2, p.getIdEquipoVisita());
            ps.setString(3, p.getFecha());
            ps.setInt(4, p.getIdEstadio());
            ps.setString(5, p.getGrupo());
            ps.setInt(6, p.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error actualizarPartido: " + ex.getMessage());
            return false;
        }
    }

    public boolean eliminarPartido(int id) {
        String sql = "DELETE FROM PARTIDO_FUTBOL WHERE CODIGO = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarPartido: " + e.getMessage());
            return false;
        }
    }

    public boolean reservarAsientoTemporal(int idAsientoPartido, boolean reservar) {
        String sql;
        if (reservar) {
            sql = "UPDATE ASIENTO_PARTIDO SET ESTADO = 'RESERVADO', FECHA_RESERVA = NOW() "
                + "WHERE ID_ASIENTO_PARTIDO = ? AND ESTADO = 'DISPONIBLE'";
        } else {
            sql = "UPDATE ASIENTO_PARTIDO SET ESTADO = 'DISPONIBLE', FECHA_RESERVA = NULL "
                + "WHERE ID_ASIENTO_PARTIDO = ? AND ESTADO = 'RESERVADO'";
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAsientoPartido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error reservarAsientoTemporal: " + e.getMessage());
            return false;
        }
    }
}
