using System;
using System.Collections.Generic;
using System.Net.Http;
using Newtonsoft.Json;

namespace TicketPremium_Console
{
    class Program
    {
        static readonly string BASE_URL = "http://localhost:51641/api";
        static readonly HttpClient http = new HttpClient();

        static void Main(string[] args)
        {
            Console.OutputEncoding = System.Text.Encoding.UTF8;
            Console.WriteLine("==============================================");
            Console.WriteLine("   TICKET PREMIUM - Venta de Boletos (.NET)");
            Console.WriteLine("==============================================");

            int opcion;
            do
            {
                Console.WriteLine("\n--- MENÚ PRINCIPAL ---");
                Console.WriteLine("1. Ver partidos disponibles");
                Console.WriteLine("2. Ver localidades de un partido");
                Console.WriteLine("3. Comprar boletos");
                Console.WriteLine("4. Reporte: Resumen de Ventas");
                Console.WriteLine("0. Salir");
                Console.Write("Opción: ");
                int.TryParse(Console.ReadLine(), out opcion);

                switch (opcion)
                {
                    case 1: MostrarPartidos(); break;
                    case 2: MostrarLocalidades(); break;
                    case 3: ComprarBoletos(); break;
                    case 4: MostrarReporte(); break;
                }
            } while (opcion != 0);
        }

        static List<dynamic> Get(string url)
        {
            try
            {
                var resp = http.GetStringAsync(BASE_URL + url).GetAwaiter().GetResult();
                return JsonConvert.DeserializeObject<List<dynamic>>(resp);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error de conexión: " + ex.Message);
                return null;
            }
        }

        static void MostrarPartidos()
        {
            var partidos = Get("/partido/disponibles");
            if (partidos == null || partidos.Count == 0) { Console.WriteLine("No hay partidos disponibles."); return; }
            Console.WriteLine("\n--- PARTIDOS DISPONIBLES ---");
            Console.WriteLine($"{"COD",-6} {"EQUIPO LOCAL",-25} {"EQUIPO VISITA",-25} {"FECHA",-20} {"LUGAR"}");
            Console.WriteLine(new string('-', 120));
            foreach (var p in partidos)
                Console.WriteLine($"{p.Codigo,-6} {p.EquipoLocal,-25} {p.EquipoVisita,-25} {p.Fecha,-20} {p.Lugar}");
        }

        static void MostrarLocalidades()
        {
            Console.Write("Código del partido: ");
            int cod = int.Parse(Console.ReadLine());
            var locs = Get($"/partido/localidades?codigoPartido={cod}");
            if (locs == null || locs.Count == 0) { Console.WriteLine("No hay localidades."); return; }
            Console.WriteLine($"\n{"ID",-6} {"LOCALIDAD",-20} {"DISPONIBLES",-15} {"PRECIO"}");
            Console.WriteLine(new string('-', 55));
            foreach (var l in locs)
                Console.WriteLine($"{l.Id,-6} {l.CodigoLocalidad,-20} {l.Disponibilidad,-15} ${l.Precio}");
        }

        static void ComprarBoletos()
        {
            MostrarPartidos();
            Console.Write("\nCódigo del partido: ");
            int codPartido = int.Parse(Console.ReadLine());
            var locs = Get($"/partido/localidades?codigoPartido={codPartido}");
            if (locs == null || locs.Count == 0) { Console.WriteLine("No hay localidades."); return; }

            foreach (var l in locs)
                Console.WriteLine($"  ID: {l.Id} | {l.CodigoLocalidad} | Disp: {l.Disponibilidad} | ${l.Precio}");

            Console.Write("ID de localidad: ");
            int idLoc = int.Parse(Console.ReadLine());
            Console.Write("Cantidad: ");
            int cantidad = int.Parse(Console.ReadLine());
            Console.Write("Nombre del cliente: ");
            string nombre = Console.ReadLine();

            dynamic selec = null;
            foreach (var l in locs) if ((int)l.Id == idLoc) { selec = l; break; }
            if (selec == null) { Console.WriteLine("Localidad no encontrada."); return; }

            var body = new
            {
                CodigoPartido = codPartido,
                NombreCliente = nombre,
                CodigoLocalidad = (string)selec.CodigoLocalidad,
                IdLocalidad = idLoc,
                Cantidad = cantidad,
                PrecioUnitario = (decimal)selec.Precio
            };

            try
            {
                var json = new StringContent(JsonConvert.SerializeObject(body), System.Text.Encoding.UTF8, "application/json");
                var resp = http.PostAsync(BASE_URL + "/compra/boleto", json).GetAwaiter().GetResult();
                if (resp.IsSuccessStatusCode)
                {
                    var factura = JsonConvert.DeserializeObject<dynamic>(resp.Content.ReadAsStringAsync().GetAwaiter().GetResult());
                    Console.WriteLine("\n========================================");
                    Console.WriteLine("        FACTURA DE COMPRA");
                    Console.WriteLine("========================================");
                    Console.WriteLine($"Factura N°:  {factura.Id}");
                    Console.WriteLine($"Cliente:     {factura.NombreCliente}");
                    Console.WriteLine($"Subtotal:    ${factura.Subtotal}");
                    Console.WriteLine($"IVA (15%):   ${factura.Iva}");
                    Console.WriteLine($"TOTAL:       ${factura.Total}");
                    Console.WriteLine("========================================");
                }
                else
                {
                    Console.WriteLine("Error: " + resp.Content.ReadAsStringAsync().GetAwaiter().GetResult());
                }
            }
            catch (Exception ex) { Console.WriteLine("Error: " + ex.Message); }
        }

        static void MostrarReporte()
        {
            Console.Write("Código del partido: ");
            int cod = int.Parse(Console.ReadLine());
            var resumen = Get($"/reporte/ventas?codigoPartido={cod}");

            Console.WriteLine("\n========================================");
            Console.WriteLine("   RESUMEN DE VENTAS DE UN PARTIDO");
            Console.WriteLine("========================================");
            Console.WriteLine($"{"Localidad",-20} {"Vendidos",-12} {"Total Recaudado"}");
            Console.WriteLine(new string('-', 50));
            if (resumen != null)
                foreach (var r in resumen)
                    Console.WriteLine($"{r.CodigoLocalidad,-20} {r.Vendidos,-12} ${r.TotalRecaudado}");
            else
                Console.WriteLine("No hay ventas registradas.");
            Console.WriteLine("========================================");
        }
    }
}
