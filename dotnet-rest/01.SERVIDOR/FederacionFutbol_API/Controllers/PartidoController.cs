using System.Web.Http;
using FederacionFutbol_API.Models;
using FederacionFutbol_API.Servicio;

namespace FederacionFutbol_API.Controllers
{
    /// <summary>
    /// Controlador REST para consultar partidos y localidades.
    /// </summary>
    public class PartidoController : ApiController
    {
        private readonly FederacionService _servicio = new FederacionService();

        /// <summary>GET api/partido/disponibles</summary>
        [HttpGet]
        [ActionName("disponibles")]
        public IHttpActionResult ObtenerPartidosDisponibles()
        {
            var partidos = _servicio.ObtenerPartidosDisponibles();
            return Ok(partidos);
        }

        /// <summary>GET api/partido/localidades?codigoPartido=1</summary>
        [HttpGet]
        [ActionName("localidades")]
        public IHttpActionResult ObtenerLocalidades(int codigoPartido)
        {
            var localidades = _servicio.ObtenerLocalidades(codigoPartido);
            return Ok(localidades);
        }

        /// <summary>GET api/partido/detalle?codigoPartido=1</summary>
        [HttpGet]
        [ActionName("detalle")]
        public IHttpActionResult ObtenerPartido(int codigoPartido)
        {
            var partido = _servicio.ObtenerPartido(codigoPartido);
            if (partido == null) return NotFound();
            return Ok(partido);
        }
    }
}
