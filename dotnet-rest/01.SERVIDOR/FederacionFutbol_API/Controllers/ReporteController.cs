using System.Web.Http;
using FederacionFutbol_API.Servicio;

namespace FederacionFutbol_API.Controllers
{
    /// <summary>
    /// Controlador REST para el reporte de ventas.
    /// </summary>
    public class ReporteController : ApiController
    {
        private readonly FederacionService _servicio = new FederacionService();

        /// <summary>GET api/reporte/ventas?codigoPartido=1</summary>
        [HttpGet]
        [ActionName("ventas")]
        public IHttpActionResult ObtenerResumenVentas(int codigoPartido)
        {
            var resumen = _servicio.ObtenerResumenVentas(codigoPartido);
            return Ok(resumen);
        }
    }
}
