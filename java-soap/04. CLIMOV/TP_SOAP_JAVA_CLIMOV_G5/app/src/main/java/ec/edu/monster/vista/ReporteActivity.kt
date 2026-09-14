package ec.edu.monster.vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ec.edu.monster.modelo.ResumenVenta
import ec.edu.monster.modelo.DetalleVentaReporte
import ec.edu.monster.servicio.TicketPremiumSoapService
import ec.edu.monster.ticketpremium.R

class ReporteActivity : AppCompatActivity() {

    private val soapService = TicketPremiumSoapService()
    private lateinit var rvResumen: RecyclerView
    private lateinit var rvDetalles: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPartidoInfo: TextView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte)

        val codPartido = intent.getIntExtra("codPartido", 0)
        val equipoLocal = intent.getStringExtra("equipoLocal") ?: ""
        val equipoVisita = intent.getStringExtra("equipoVisita") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""

        tvPartidoInfo = findViewById(R.id.tvPartidoInfo)
        rvResumen = findViewById(R.id.rvResumen)
        rvDetalles = findViewById(R.id.rvDetalles)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)

        tvPartidoInfo.text = "Partido: $equipoLocal vs $equipoVisita\nFecha: $fecha"

        rvResumen.layoutManager = LinearLayoutManager(this)
        rvDetalles.layoutManager = LinearLayoutManager(this)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVolver)
            .setOnClickListener { finish() }

        cargarReporte(codPartido)
    }

    private fun cargarReporte(codPartido: Int) {
        progressBar.visibility = View.VISIBLE

        // Cargar resumen
        soapService.obtenerResumenVentas(codPartido, object : TicketPremiumSoapService.SoapCallback<List<ResumenVenta>> {
            override fun onSuccess(result: List<ResumenVenta>) {
                if (result.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    rvResumen.adapter = ResumenAdapter(result)
                }
                
                // Cargar detalle
                soapService.obtenerDetalleVentas(codPartido, object: TicketPremiumSoapService.SoapCallback<List<DetalleVentaReporte>> {
                    override fun onSuccess(detalles: List<DetalleVentaReporte>) {
                        progressBar.visibility = View.GONE
                        rvDetalles.adapter = DetalleAdapter(detalles)
                    }

                    override fun onError(error: String) {
                        progressBar.visibility = View.GONE
                    }
                })
            }

            override fun onError(error: String) {
                progressBar.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = "Error al cargar reporte"
                Toast.makeText(this@ReporteActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    // ============================
    // Adapters
    // ============================
    class DetalleAdapter(
        private val detalles: List<DetalleVentaReporte>
    ) : RecyclerView.Adapter<DetalleAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvFecha: TextView = view.findViewById(R.id.tvFecha)
            val tvCliente: TextView = view.findViewById(R.id.tvCliente)
            val tvLocalidades: TextView = view.findViewById(R.id.tvLocalidades)
            val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detalle_venta, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val d = detalles[position]
            holder.tvFecha.text = d.fecha
            holder.tvCliente.text = d.cliente
            holder.tvLocalidades.text = d.localidades
            holder.tvTotal.text = "$${String.format("%.2f", d.totalVenta)}"
        }

        override fun getItemCount() = detalles.size
    }

    class ResumenAdapter(
        private val resumen: List<ResumenVenta>
    ) : RecyclerView.Adapter<ResumenAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvLocalidad: TextView = view.findViewById(R.id.tvLocalidad)
            val tvVendidos: TextView = view.findViewById(R.id.tvVendidos)
            val tvRecaudado: TextView = view.findViewById(R.id.tvRecaudado)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_resumen, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val r = resumen[position]
            holder.tvLocalidad.text = r.codigoLocalidad
            holder.tvVendidos.text = "Vendidos: ${r.vendidos}"
            holder.tvRecaudado.text = "$${String.format("%.2f", r.totalRecaudado)}"
        }

        override fun getItemCount() = resumen.size
    }
}
