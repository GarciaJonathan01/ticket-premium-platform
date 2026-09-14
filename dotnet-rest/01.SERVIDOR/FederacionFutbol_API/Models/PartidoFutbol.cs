namespace FederacionFutbol_API.Models
{
    public class PartidoFutbol
    {
        public int Codigo { get; set; }
        public string EquipoLocal { get; set; }
        public string EquipoVisita { get; set; }
        public string Fecha { get; set; }
        public string Lugar { get; set; }
    }

    public class LocalidadPartido
    {
        public int Id { get; set; }
        public int CodigoPartido { get; set; }
        public string CodigoLocalidad { get; set; }
        public int Disponibilidad { get; set; }
        public decimal Precio { get; set; }
    }

    public class Factura
    {
        public int Id { get; set; }
        public int CodigoPartido { get; set; }
        public string NombreCliente { get; set; }
        public string Fecha { get; set; }
        public decimal Subtotal { get; set; }
        public decimal Iva { get; set; }
        public decimal Total { get; set; }
    }

    public class DetalleFactura
    {
        public int Id { get; set; }
        public int IdFactura { get; set; }
        public int CodigoPartido { get; set; }
        public string CodigoLocalidad { get; set; }
        public int Cantidad { get; set; }
        public decimal PrecioUnitario { get; set; }
        public decimal Subtotal { get; set; }
    }

    public class ResumenVenta
    {
        public string CodigoLocalidad { get; set; }
        public int Vendidos { get; set; }
        public decimal TotalRecaudado { get; set; }
    }

    public class CompraRequest
    {
        public int CodigoPartido { get; set; }
        public string NombreCliente { get; set; }
        public string CodigoLocalidad { get; set; }
        public int IdLocalidad { get; set; }
        public int Cantidad { get; set; }
        public decimal PrecioUnitario { get; set; }
    }
}
