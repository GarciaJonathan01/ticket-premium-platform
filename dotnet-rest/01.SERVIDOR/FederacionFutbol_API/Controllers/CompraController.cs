using System.Web.Http;
using FederacionFutbol_API.Models;
using FederacionFutbol_API.Servicio;

namespace FederacionFutbol_API.Controllers
{
    /// <summary>
    /// Controlador REST para registrar compras de boletos.
    /// </summary>
    public class CompraController : ApiController
    {
        private readonly FederacionService _servicio = new FederacionService();

        /// <summary>POST api/compra/boleto</summary>
        [HttpPost]
        [ActionName("boleto")]
        public IHttpActionResult ComprarBoleto([FromBody] CompraRequest request)
        {
            if (request == null) return BadRequest("Datos de compra requeridos.");

            var factura = _servicio.RegistrarCompra(request);
            if (factura == null)
                return BadRequest("No se pudo realizar la compra. Verifique disponibilidad.");

            return Ok(factura);
        }
    }
}
