package ec.edu.monster.servicio;

import ec.edu.monster.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BancoCoreService {

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

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    public RespuestaCredito verificarYCrearCredito(String cedula, double montoCompra, int plazoMeses) {
        RespuestaCredito respuesta = new RespuestaCredito();
        respuesta.setAprobado(false);
        respuesta.setMontoMaximo(0.0);

        if (plazoMeses < 3 || plazoMeses > 18) {
            respuesta.setMensaje("El plazo del credito debe ser entre 3 y 18 meses.");
            return respuesta;
        }

        try (Connection conn = getConnection()) {
            // 1. Verificar si el solicitante es cliente
            String sqlCliente = "SELECT COD_CLIENTE, NOMBRE, GENERO, FECHA_NACIMIENTO FROM CLIENTE WHERE CEDULA = ?";
            int codCliente = -1;
            String nombre = "";
            String genero = "";
            Date fechaNac = null;

            try (PreparedStatement ps = conn.prepareStatement(sqlCliente)) {
                ps.setString(1, cedula);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        codCliente = rs.getInt("COD_CLIENTE");
                        nombre = rs.getString("NOMBRE");
                        genero = rs.getString("GENERO");
                        fechaNac = rs.getDate("FECHA_NACIMIENTO");
                    }
                }
            }

            if (codCliente == -1) {
                respuesta.setMensaje("El solicitante no es cliente de la institucion financiera.");
                return respuesta;
            }

            // 2. Verificar edad (no menor de 25 si es masculino)
            Calendar birth = Calendar.getInstance();
            birth.setTime(fechaNac);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH) || 
               (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }

            if ("M".equalsIgnoreCase(genero) && age < 25) {
                respuesta.setMensaje("El solicitante de genero masculino debe tener al menos 25 anos de edad.");
                return respuesta;
            }

            // Obtener numero de cuenta del cliente
            String sqlCuenta = "SELECT NUM_CUENTA FROM CUENTA WHERE COD_CLIENTE = ?";
            String numCuenta = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlCuenta)) {
                ps.setInt(1, codCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        numCuenta = rs.getString("NUM_CUENTA");
                    }
                }
            }

            if (numCuenta == null) {
                respuesta.setMensaje("El cliente no posee ninguna cuenta activa en el banco.");
                return respuesta;
            }

            // 3. Verificar transaccion de deposito en el ultimo mes
            String sqlDep = "SELECT COUNT(*) FROM MOVIMIENTO " +
                            "WHERE NUM_CUENTA = ? AND TIPO = 'DEP' AND FECHA >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
            int depositosMes = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlDep)) {
                ps.setString(1, numCuenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        depositosMes = rs.getInt(1);
                    }
                }
            }

            if (depositosMes == 0) {
                respuesta.setMensaje("El cliente no registra depositos en el ultimo mes.");
                return respuesta;
            }

            // 4. Verificar que no tenga credito activo
            String sqlCred = "SELECT COUNT(*) FROM CREDITO WHERE COD_CLIENTE = ? AND ESTADO = 'ACTIVO'";
            int creditosActivos = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlCred)) {
                ps.setInt(1, codCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        creditosActivos = rs.getInt(1);
                    }
                }
            }

            if (creditosActivos > 0) {
                respuesta.setMensaje("El cliente ya posee un credito activo actualmente.");
                return respuesta;
            }

            // 5. Obtener promedio de depositos y retiros en los ultimos 3 meses
            String sqlPromDep = "SELECT COALESCE(SUM(VALOR), 0) FROM MOVIMIENTO " +
                                "WHERE NUM_CUENTA = ? AND TIPO = 'DEP' AND FECHA >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)";
            String sqlPromRet = "SELECT COALESCE(SUM(VALOR), 0) FROM MOVIMIENTO " +
                                "WHERE NUM_CUENTA = ? AND TIPO = 'RET' AND FECHA >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)";
            
            double sumDep = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlPromDep)) {
                ps.setString(1, numCuenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) sumDep = rs.getDouble(1);
                }
            }

            double sumRet = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlPromRet)) {
                ps.setString(1, numCuenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) sumRet = rs.getDouble(1);
                }
            }

            double avgDep = sumDep / 3.0;
            double avgRet = sumRet / 3.0;

            // Limite de credito
            double maxCredito = ((avgDep - avgRet) * 0.30) * 6;
            maxCredito = round(maxCredito);
            if (maxCredito < 0) maxCredito = 0;

            respuesta.setMontoMaximo(maxCredito);

            if (montoCompra > maxCredito) {
                respuesta.setMensaje("El monto de la compra (" + montoCompra + ") supera el limite de credito aprobado (" + maxCredito + ").");
                return respuesta;
            }

            // CREDITO APROBADO -> Generar Tabla de Amortizacion
            double tasaAnual = 16.5;
            double tasaPeriodo = (tasaAnual / 100.0) / 12.0; // 16.5% / 12 = 0.01375

            // Cuota fija
            double cuota = (montoCompra * tasaPeriodo) / (1 - Math.pow(1 + tasaPeriodo, -plazoMeses));
            cuota = round(cuota);

            List<CuotaAmortizacion> tabla = new ArrayList<>();
            double saldo = montoCompra;

            // Fila 0
            tabla.add(new CuotaAmortizacion(0, 0.00, 0.00, 0.00, saldo));

            for (int i = 1; i <= plazoMeses; i++) {
                double interes = round(saldo * tasaPeriodo);
                double capital = round(cuota - interes);
                
                // Ajustar ultima cuota para saldar centavos
                if (i == plazoMeses) {
                    capital = saldo;
                    cuota = round(capital + interes);
                    saldo = 0.00;
                } else {
                    saldo = round(saldo - capital);
                }

                tabla.add(new CuotaAmortizacion(i, cuota, interes, capital, saldo));
            }

            respuesta.setTablaAmortizacion(tabla);
            respuesta.setAprobado(true);
            respuesta.setMensaje("Credito aprobado exitosamente.");

            // Registrar en base de datos
            conn.setAutoCommit(false);
            try {
                // Insertar Credito
                String sqlInsCred = "INSERT INTO CREDITO (COD_CLIENTE, NUM_CUENTA, MONTO_PRESTAMO, INTERES_ANUAL, PLAZO_MESES, CUOTA_MENSUAL, FECHA_APROBACION, ESTADO) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, CURDATE(), 'ACTIVO')";
                int idCredito = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsCred, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, codCliente);
                    ps.setString(2, numCuenta);
                    ps.setDouble(3, montoCompra);
                    ps.setDouble(4, tasaAnual);
                    ps.setInt(5, plazoMeses);
                    ps.setDouble(6, cuota);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            idCredito = rs.getInt(1);
                        }
                    }
                }

                respuesta.setIdCredito(idCredito);

                // Insertar Amortizacion
                String sqlInsAmort = "INSERT INTO TABLA_AMORTIZACION (ID_CREDITO, NUM_CUOTA, VALOR_CUOTA, INTERES_PAGADO, CAPITAL_PAGADO, SALDO) " +
                                     "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsAmort)) {
                    for (CuotaAmortizacion c : tabla) {
                        ps.setInt(1, idCredito);
                        ps.setInt(2, c.getNumCuota());
                        ps.setDouble(3, c.getValorCuota());
                        ps.setDouble(4, c.getInteresPagado());
                        ps.setDouble(5, c.getCapitalPagado());
                        ps.setDouble(6, c.getSaldo());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error en verificarYCrearCredito: " + e.getMessage());
            respuesta.setAprobado(false);
            respuesta.setMensaje("Error interno del servidor financiero: " + e.getMessage());
        }

        return respuesta;
    }
}
