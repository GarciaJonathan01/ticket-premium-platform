using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Web.Mvc;
using Newtonsoft.Json;

namespace TicketPremium_Web.Controllers
{
    public class TicketController : Controller
    {
        private static readonly string BASE_URL = "http://localhost:51641/api";
        private static readonly HttpClient http = new HttpClient();

        // GET: /Ticket/ - Lista partidos disponibles
        public ActionResult Index()
        {
            try
            {
                var json = http.GetStringAsync(BASE_URL + "/partido/disponibles").GetAwaiter().GetResult();
                ViewBag.Partidos = JsonConvert.DeserializeObject<List<dynamic>>(json);
            }
            catch (Exception ex) { ViewBag.Error = ex.Message; }
            return View();
        }

        // GET: /Ticket/Localidades?codPartido=1
        public ActionResult Localidades(int codPartido)
        {
            try
            {
                var jsonP = http.GetStringAsync(BASE_URL + $"/partido/detalle?codigoPartido={codPartido}").GetAwaiter().GetResult();
                ViewBag.Partido = JsonConvert.DeserializeObject<dynamic>(jsonP);
                var jsonL = http.GetStringAsync(BASE_URL + $"/partido/localidades?codigoPartido={codPartido}").GetAwaiter().GetResult();
                ViewBag.Localidades = JsonConvert.DeserializeObject<List<dynamic>>(jsonL);
                ViewBag.CodPartido = codPartido;
            }
            catch (Exception ex) { ViewBag.Error = ex.Message; }
            return View();
        }

        // POST: /Ticket/Comprar
        [HttpPost]
        public ActionResult Comprar(int codPartido, int idLocalidad, string codigoLocalidad,
                                     int cantidad, decimal precio, string nombreCliente)
        {
            try
            {
                var body = new { CodigoPartido = codPartido, NombreCliente = nombreCliente,
                    CodigoLocalidad = codigoLocalidad, IdLocalidad = idLocalidad,
                    Cantidad = cantidad, PrecioUnitario = precio };
                var content = new StringContent(JsonConvert.SerializeObject(body), Encoding.UTF8, "application/json");
                var resp = http.PostAsync(BASE_URL + "/compra/boleto", content).GetAwaiter().GetResult();
                if (resp.IsSuccessStatusCode)
                {
                    var factura = JsonConvert.DeserializeObject<dynamic>(resp.Content.ReadAsStringAsync().GetAwaiter().GetResult());
                    ViewBag.Factura = factura;
                    ViewBag.Localidad = codigoLocalidad;
                    ViewBag.Cantidad = cantidad;
                    ViewBag.PrecioUnitario = precio;
                    return View("Factura");
                }
                ViewBag.Error = "No se pudo completar la compra.";
            }
            catch (Exception ex) { ViewBag.Error = ex.Message; }
            return RedirectToAction("Localidades", new { codPartido });
        }

        // GET: /Ticket/Reporte?codPartido=1
        public ActionResult Reporte(int codPartido)
        {
            try
            {
                var jsonP = http.GetStringAsync(BASE_URL + $"/partido/detalle?codigoPartido={codPartido}").GetAwaiter().GetResult();
                ViewBag.Partido = JsonConvert.DeserializeObject<dynamic>(jsonP);
                var jsonR = http.GetStringAsync(BASE_URL + $"/reporte/ventas?codigoPartido={codPartido}").GetAwaiter().GetResult();
                ViewBag.Resumen = JsonConvert.DeserializeObject<List<dynamic>>(jsonR);
            }
            catch (Exception ex) { ViewBag.Error = ex.Message; }
            return View();
        }
    }
}
