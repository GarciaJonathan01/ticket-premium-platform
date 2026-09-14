using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Windows.Forms;
using Newtonsoft.Json;

namespace TicketPremium_Desktop
{
    public class VentanaPrincipal : Form
    {
        private static readonly string BASE_URL = "http://localhost:51641/api";
        private static readonly HttpClient http = new HttpClient();

        private TabControl tabs;
        private DataGridView dgvPartidos, dgvLocalidades, dgvReporte;
        private Button btnRefrescar, btnVerLoc, btnComprar, btnReporte;
        private Label lblPartido, lblReporte;
        private int partidoSeleccionado = -1;

        public VentanaPrincipal()
        {
            Text = "TicketPremium - Venta de Boletos (.NET)";
            Size = new System.Drawing.Size(1100, 650);
            StartPosition = FormStartPosition.CenterScreen;

            tabs = new TabControl { Dock = DockStyle.Fill };

            // Tab Partidos
            var tabPartidos = new TabPage("Partidos");
            dgvPartidos = CrearGrid();
            btnRefrescar = new Button { Text = "Refrescar", Dock = DockStyle.Bottom, Height = 35 };
            btnVerLoc = new Button { Text = "Ver Localidades", Dock = DockStyle.Bottom, Height = 35 };
            btnReporte = new Button { Text = "Ver Reporte", Dock = DockStyle.Bottom, Height = 35 };
            tabPartidos.Controls.AddRange(new Control[] { dgvPartidos, btnReporte, btnVerLoc, btnRefrescar });

            // Tab Localidades
            var tabLocalidades = new TabPage("Localidades");
            lblPartido = new Label { Text = "Seleccione un partido", Dock = DockStyle.Top, Height = 30, TextAlign = System.Drawing.ContentAlignment.MiddleCenter };
            dgvLocalidades = CrearGrid();
            btnComprar = new Button { Text = "🛒 Comprar Boletos", Dock = DockStyle.Bottom, Height = 40, BackColor = System.Drawing.Color.Green, ForeColor = System.Drawing.Color.White };
            tabLocalidades.Controls.AddRange(new Control[] { dgvLocalidades, btnComprar, lblPartido });

            // Tab Reporte
            var tabReporte = new TabPage("Reporte de Ventas");
            lblReporte = new Label { Text = "Reporte", Dock = DockStyle.Top, Height = 30, TextAlign = System.Drawing.ContentAlignment.MiddleCenter };
            dgvReporte = CrearGrid();
            tabReporte.Controls.AddRange(new Control[] { dgvReporte, lblReporte });

            tabs.TabPages.AddRange(new[] { tabPartidos, tabLocalidades, tabReporte });
            Controls.Add(tabs);

            // Eventos
            btnRefrescar.Click += (s, e) => CargarPartidos();
            btnVerLoc.Click += (s, e) => {
                if (dgvPartidos.CurrentRow == null) return;
                partidoSeleccionado = Convert.ToInt32(dgvPartidos.CurrentRow.Cells["Codigo"].Value);
                lblPartido.Text = $"{dgvPartidos.CurrentRow.Cells["EquipoLocal"].Value} vs {dgvPartidos.CurrentRow.Cells["EquipoVisita"].Value}";
                CargarLocalidades(partidoSeleccionado);
                tabs.SelectedIndex = 1;
            };
            btnComprar.Click += (s, e) => RealizarCompra();
            btnReporte.Click += (s, e) => {
                if (dgvPartidos.CurrentRow == null) return;
                int cod = Convert.ToInt32(dgvPartidos.CurrentRow.Cells["Codigo"].Value);
                lblReporte.Text = $"Partido: {dgvPartidos.CurrentRow.Cells["EquipoLocal"].Value} vs {dgvPartidos.CurrentRow.Cells["EquipoVisita"].Value}";
                CargarReporte(cod);
                tabs.SelectedIndex = 2;
            };

            CargarPartidos();
        }

        DataGridView CrearGrid()
        {
            return new DataGridView
            {
                Dock = DockStyle.Fill, ReadOnly = true, AllowUserToAddRows = false,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                RowHeadersVisible = false
            };
        }

        void CargarPartidos()
        {
            try
            {
                var json = http.GetStringAsync(BASE_URL + "/partido/disponibles").GetAwaiter().GetResult();
                dgvPartidos.DataSource = JsonConvert.DeserializeObject<List<PartidoFutbol>>(json);
            }
            catch (Exception ex) { MessageBox.Show("Error: " + ex.Message); }
        }

        void CargarLocalidades(int cod)
        {
            try
            {
                var json = http.GetStringAsync(BASE_URL + $"/partido/localidades?codigoPartido={cod}").GetAwaiter().GetResult();
                dgvLocalidades.DataSource = JsonConvert.DeserializeObject<List<LocalidadPartido>>(json);
            }
            catch (Exception ex) { MessageBox.Show("Error: " + ex.Message); }
        }

        void CargarReporte(int cod)
        {
            try
            {
                var json = http.GetStringAsync(BASE_URL + $"/reporte/ventas?codigoPartido={cod}").GetAwaiter().GetResult();
                dgvReporte.DataSource = JsonConvert.DeserializeObject<List<ResumenVenta>>(json);
            }
            catch (Exception ex) { MessageBox.Show("Error: " + ex.Message); }
        }

        void RealizarCompra()
        {
            if (dgvLocalidades.CurrentRow == null || partidoSeleccionado < 0) return;
            var row = dgvLocalidades.CurrentRow;
            int idLoc = Convert.ToInt32(row.Cells["Id"].Value);
            string codLoc = row.Cells["CodigoLocalidad"].Value.ToString();
            int disp = Convert.ToInt32(row.Cells["Disponibilidad"].Value);
            decimal precio = Convert.ToDecimal(row.Cells["Precio"].Value);

            string cantStr = Microsoft.VisualBasic.Interaction.InputBox($"Cantidad (máx {disp}):", "Comprar Boletos", "1");
            if (string.IsNullOrEmpty(cantStr)) return;
            int cantidad = int.Parse(cantStr);

            string nombre = Microsoft.VisualBasic.Interaction.InputBox("Nombre del cliente:", "Cliente", "");
            if (string.IsNullOrEmpty(nombre)) return;

            var body = new { CodigoPartido = partidoSeleccionado, NombreCliente = nombre, CodigoLocalidad = codLoc, IdLocalidad = idLoc, Cantidad = cantidad, PrecioUnitario = precio };
            try
            {
                var content = new StringContent(JsonConvert.SerializeObject(body), System.Text.Encoding.UTF8, "application/json");
                var resp = http.PostAsync(BASE_URL + "/compra/boleto", content).GetAwaiter().GetResult();
                if (resp.IsSuccessStatusCode)
                {
                    var f = JsonConvert.DeserializeObject<dynamic>(resp.Content.ReadAsStringAsync().GetAwaiter().GetResult());
                    MessageBox.Show($"FACTURA N° {f.Id}\nCliente: {nombre}\nLocalidad: {codLoc}\nCantidad: {cantidad}\n\nSubtotal: ${f.Subtotal}\nIVA (15%): ${f.Iva}\nTOTAL: ${f.Total}", "Compra Exitosa", MessageBoxButtons.OK, MessageBoxIcon.Information);
                    CargarLocalidades(partidoSeleccionado);
                }
                else MessageBox.Show("Error al comprar: " + resp.ReasonPhrase);
            }
            catch (Exception ex) { MessageBox.Show("Error: " + ex.Message); }
        }

        // Models internos
        class PartidoFutbol { public int Codigo { get; set; } public string EquipoLocal { get; set; } public string EquipoVisita { get; set; } public string Fecha { get; set; } public string Lugar { get; set; } }
        class LocalidadPartido { public int Id { get; set; } public int CodigoPartido { get; set; } public string CodigoLocalidad { get; set; } public int Disponibilidad { get; set; } public decimal Precio { get; set; } }
        class ResumenVenta { public string CodigoLocalidad { get; set; } public int Vendidos { get; set; } public decimal TotalRecaudado { get; set; } }

        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new VentanaPrincipal());
        }
    }
}
