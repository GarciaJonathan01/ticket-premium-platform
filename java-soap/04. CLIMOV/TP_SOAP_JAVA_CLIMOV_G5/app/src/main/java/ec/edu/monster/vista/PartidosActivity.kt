package ec.edu.monster.vista

import android.content.Intent
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
import com.google.android.material.card.MaterialCardView
import ec.edu.monster.modelo.PartidoFutbol
import ec.edu.monster.servicio.TicketPremiumSoapService
import ec.edu.monster.servicio.FormatUtil
import ec.edu.monster.ticketpremium.R

class PartidosActivity : AppCompatActivity() {

    private val soapService = TicketPremiumSoapService()
    private lateinit var rvPartidos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partidos)

        rvPartidos = findViewById(R.id.rvPartidos)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)

        rvPartidos.layoutManager = LinearLayoutManager(this)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRefrescar)
            .setOnClickListener { cargarPartidos() }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnLogout)
            .setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        cargarPartidos()
    }

    private fun cargarPartidos() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        soapService.obtenerPartidosDisponibles(object : TicketPremiumSoapService.SoapCallback<List<PartidoFutbol>> {
            override fun onSuccess(result: List<PartidoFutbol>) {
                progressBar.visibility = View.GONE
                if (result.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    tvEmpty.text = "No hay partidos disponibles"
                }
                rvPartidos.adapter = PartidosAdapter(result,
                    onVerLocalidades = { partido ->
                        val intent = Intent(this@PartidosActivity, LocalidadesActivity::class.java)
                        intent.putExtra("codPartido", partido.codigo)
                        intent.putExtra("equipoLocal", partido.equipoLocal)
                        intent.putExtra("equipoVisita", partido.equipoVisita)
                        intent.putExtra("fecha", partido.fecha)
                        intent.putExtra("lugar", partido.lugar)
                        startActivity(intent)
                    },
                    onVerReporte = { partido ->
                        val intent = Intent(this@PartidosActivity, ReporteActivity::class.java)
                        intent.putExtra("codPartido", partido.codigo)
                        intent.putExtra("equipoLocal", partido.equipoLocal)
                        intent.putExtra("equipoVisita", partido.equipoVisita)
                        intent.putExtra("fecha", partido.fecha)
                        startActivity(intent)
                    }
                )
            }

            override fun onError(error: String) {
                progressBar.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = "Error al conectar con el servidor"
                Toast.makeText(this@PartidosActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    // ============================
    // Adapter
    // ============================
    class PartidosAdapter(
        private val partidos: List<PartidoFutbol>,
        private val onVerLocalidades: (PartidoFutbol) -> Unit,
        private val onVerReporte: (PartidoFutbol) -> Unit
    ) : RecyclerView.Adapter<PartidosAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvEquipos: TextView = view.findViewById(R.id.tvEquipos)
            val tvFecha: TextView = view.findViewById(R.id.tvFecha)
            val tvLugar: TextView = view.findViewById(R.id.tvLugar)
            val btnLocalidades: com.google.android.material.button.MaterialButton = view.findViewById(R.id.btnLocalidades)
            val btnReporte: com.google.android.material.button.MaterialButton = view.findViewById(R.id.btnReporte)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_partido, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val partido = partidos[position]
            holder.tvEquipos.text = "${FormatUtil.tilde(partido.equipoLocal)}  vs  ${FormatUtil.tilde(partido.equipoVisita)}"
            holder.tvFecha.text = "📅 ${partido.fecha}"
            holder.tvLugar.text = "📍 ${FormatUtil.tilde(partido.lugar)}"
            holder.btnLocalidades.setOnClickListener { onVerLocalidades(partido) }
            holder.btnReporte.setOnClickListener { onVerReporte(partido) }
        }

        override fun getItemCount() = partidos.size
    }
}
