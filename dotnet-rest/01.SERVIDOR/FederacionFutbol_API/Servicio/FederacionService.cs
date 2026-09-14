using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data.SqlClient;
using FederacionFutbol_API.Models;

namespace FederacionFutbol_API.Servicio
{
    /// <summary>
    /// Capa de servicio/DAO para acceso a SQL Server.
    /// </summary>
    public class FederacionService
    {
        private static readonly string ConnStr =
            ConfigurationManager.ConnectionStrings["TicketPremiumDB"].ConnectionString;

        // ==========================================
        // PARTIDOS DISPONIBLES
        // ==========================================
        public List<PartidoFutbol> ObtenerPartidosDisponibles()
        {
            var lista = new List<PartidoFutbol>();
            using (var conn = new SqlConnection(ConnStr))
            {
                conn.Open();
                var cmd = new SqlCommand(
                    "SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITA, " +
                    "FORMAT(FECHA, 'yyyy-MM-dd HH:mm') AS FECHA, LUGAR " +
                    "FROM PARTIDO_FUTBOL WHERE FECHA >= GETDATE() ORDER BY FECHA", conn);
                using (var rd = cmd.ExecuteReader())
                {
                    while (rd.Read())
                    {
                        lista.Add(new PartidoFutbol
                        {
                            Codigo = rd.GetInt32(0),
                            EquipoLocal = rd.GetString(1),
                            EquipoVisita = rd.GetString(2),
                            Fecha = rd.GetString(3),
                            Lugar = rd.GetString(4)
                        });
                    }
                }
            }
            return lista;
        }

        // ==========================================
        // LOCALIDADES DISPONIBLES
        // ==========================================
        public List<LocalidadPartido> ObtenerLocalidades(int codigoPartido)
        {
            var lista = new List<LocalidadPartido>();
            using (var conn = new SqlConnection(ConnStr))
            {
                conn.Open();
                var cmd = new SqlCommand(
                    "SELECT ID, CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO " +
                    "FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO = @cod AND DISPONIBILIDAD > 0", conn);
                cmd.Parameters.AddWithValue("@cod", codigoPartido);
                using (var rd = cmd.ExecuteReader())
                {
                    while (rd.Read())
                    {
                        lista.Add(new LocalidadPartido
                        {
                            Id = rd.GetInt32(0),
                            CodigoPartido = rd.GetInt32(1),
                            CodigoLocalidad = rd.GetString(2),
                            Disponibilidad = rd.GetInt32(3),
                            Precio = rd.GetDecimal(4)
                        });
                    }
                }
            }
            return lista;
        }

        // ==========================================
        // DECREMENTAR DISPONIBILIDAD
        // ==========================================
        public bool DecrementarDisponibilidad(int idLocalidad, int cantidad)
        {
            using (var conn = new SqlConnection(ConnStr))
            {
                conn.Open();
                var cmd = new SqlCommand(
                    "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - @cant " +
                    "WHERE ID = @id AND DISPONIBILIDAD >= @cant", conn);
                cmd.Parameters.AddWithValue("@cant", cantidad);
                cmd.Parameters.AddWithValue("@id", idLocalidad);
                return cmd.ExecuteNonQuery() > 0;
            }
        }

        // ==========================================
        // REGISTRAR COMPRA
        // ==========================================
        public Factura RegistrarCompra(CompraRequest req)
        {
            decimal subtotal = req.Cantidad * req.PrecioUnitario;
            decimal iva = subtotal * 0.15m;
            decimal total = subtotal + iva;

            using (var conn = new SqlConnection(ConnStr))
            {
                conn.Open();
                var tran = conn.BeginTransaction();
                try
                {
                    // Decrementar
                    var cmdDec = new SqlCommand(
                        "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - @cant " +
                        "WHERE ID = @id AND DISPONIBILIDAD >= @cant", conn, tran);
                    cmdDec.Parameters.AddWithValue("@cant", req.Cantidad);
                    cmdDec.Parameters.AddWithValue("@id", req.IdLocalidad);
                    if (cmdDec.ExecuteNonQuery() <= 0) { tran.Rollback(); return null; }

                    // Insertar Factura
                    var cmdFact = new SqlCommand(
                        "INSERT INTO FACTURA (CODIGO_PARTIDO, NOMBRE_CLIENTE, FECHA, SUBTOTAL, IVA, TOTAL) " +
                        "VALUES (@cod, @nom, GETDATE(), @sub, @iva, @tot); SELECT SCOPE_IDENTITY();", conn, tran);
                    cmdFact.Parameters.AddWithValue("@cod", req.CodigoPartido);
                    cmdFact.Parameters.AddWithValue("@nom", req.NombreCliente);
                    cmdFact.Parameters.AddWithValue("@sub", subtotal);
                    cmdFact.Parameters.AddWithValue("@iva", iva);
                    cmdFact.Parameters.AddWithValue("@tot", total);
                    int idFactura = Convert.ToInt32(cmdFact.ExecuteScalar());

                    // Insertar Detalle
                    var cmdDet = new SqlCommand(
                        "INSERT INTO DETALLE_FACTURA (ID_FACTURA, CODIGO_PARTIDO, CODIGO_LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) " +
                        "VALUES (@idf, @cod, @loc, @cant, @pu, @sub)", conn, tran);
                    cmdDet.Parameters.AddWithValue("@idf", idFactura);
                    cmdDet.Parameters.AddWithValue("@cod", req.CodigoPartido);
                    cmdDet.Parameters.AddWithValue("@loc", req.CodigoLocalidad);
                    cmdDet.Parameters.AddWithValue("@cant", req.Cantidad);
                    cmdDet.Parameters.AddWithValue("@pu", req.PrecioUnitario);
                    cmdDet.Parameters.AddWithValue("@sub", subtotal);
                    cmdDet.ExecuteNonQuery();

                    tran.Commit();
                    return new Factura
                    {
                        Id = idFactura,
                        CodigoPartido = req.CodigoPartido,
                        NombreCliente = req.NombreCliente,
                        Subtotal = subtotal,
                        Iva = iva,
                        Total = total
                    };
                }
                catch
                {
                    tran.Rollback();
                    return null;
                }
            }
        }

        // ==========================================
        // OBTENER PARTIDO
        // ==========================================
        public PartidoFutbol ObtenerPartido(int codigo)
        {
            using (var conn = new SqlConnection(ConnStr))
            {
                conn.Open();
                var cmd = new SqlCommand(
                    "SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITA, " +
                    "FORMAT(FECHA, 'yyyy-MM-dd HH:mm') AS FECHA, LUGAR " +
                    "FROM PARTIDO_FUTBOL WHERE CODIGO = @cod", conn);
                cmd.Parameters.AddWithValue("@cod", codigo);
                using (var rd = cmd.ExecuteReader())
                {
                    if (rd.Read())
                    {
                        return new PartidoFutbol
                        {
                            Codigo = rd.GetInt32(0),
                            EquipoLocal = rd.GetString(1),
                            EquipoVisita = rd.GetString(2),
                            Fecha = rd.GetString(3),
                            Lugar = rd.GetString(4)
                        };
                    }
                }
            }
            return null;
        }

        // ==========================================
        // RESUMEN DE VENTAS
        // ==========================================
        public List<ResumenVenta> ObtenerResumenVentas(int codigoPartido)
        {
            var lista = new List<ResumenVenta>();
            using (var conn = new SqlConnection(ConnStr))
            {
                conn.Open();
                var cmd = new SqlCommand(
                    "SELECT CODIGO_LOCALIDAD, SUM(CANTIDAD) AS VENDIDOS, " +
                    "SUM(SUBTOTAL) AS TOTAL_RECAUDADO " +
                    "FROM DETALLE_FACTURA WHERE CODIGO_PARTIDO = @cod " +
                    "GROUP BY CODIGO_LOCALIDAD", conn);
                cmd.Parameters.AddWithValue("@cod", codigoPartido);
                using (var rd = cmd.ExecuteReader())
                {
                    while (rd.Read())
                    {
                        lista.Add(new ResumenVenta
                        {
                            CodigoLocalidad = rd.GetString(0),
                            Vendidos = rd.GetInt32(1),
                            TotalRecaudado = rd.GetDecimal(2)
                        });
                    }
                }
            }
            return lista;
        }
    }
}
