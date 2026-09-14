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
import ec.edu.monster.modelo.LocalidadPartido
import ec.edu.monster.modelo.PeticionCompra
import ec.edu.monster.servicio.TicketPremiumSoapService
import ec.edu.monster.servicio.FormatUtil
import ec.edu.monster.ticketpremium.R
import android.widget.ImageButton

class LocalidadesActivity : AppCompatActivity() {

    private val soapService = TicketPremiumSoapService()
    private lateinit var rvLocalidades: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPartidoInfo: TextView
    private var codPartido = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localidades)

        codPartido = intent.getIntExtra("codPartido", 0)
        val equipoLocal = intent.getStringExtra("equipoLocal") ?: ""
        val equipoVisita = intent.getStringExtra("equipoVisita") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""
        val lugar = intent.getStringExtra("lugar") ?: ""

        tvPartidoInfo = findViewById(R.id.tvPartidoInfo)
        rvLocalidades = findViewById(R.id.rvLocalidades)
        progressBar = findViewById(R.id.progressBar)

        tvPartidoInfo.text = "${FormatUtil.tilde(equipoLocal)} vs ${FormatUtil.tilde(equipoVisita)}\n📅 $fecha\n📍 ${FormatUtil.tilde(lugar)}"

        rvLocalidades.layoutManager = LinearLayoutManager(this)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVolver)
            .setOnClickListener { finish() }

        cargarLocalidades()
    }

    override fun onResume() {
        super.onResume()
        cargarLocalidades()
    }

    private var adapter: LocalidadesAdapter? = null

    private fun cargarLocalidades() {
        progressBar.visibility = View.VISIBLE

        soapService.obtenerLocalidades(codPartido, object : TicketPremiumSoapService.SoapCallback<List<LocalidadPartido>> {
            override fun onSuccess(result: List<LocalidadPartido>) {
                progressBar.visibility = View.GONE
                adapter = LocalidadesAdapter(result)
                rvLocalidades.adapter = adapter
            }

            override fun onError(error: String) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@LocalidadesActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnIrCompra).setOnClickListener {
            val carrito = adapter?.getCarrito(codPartido) ?: emptyList()
            if (carrito.isEmpty()) {
                Toast.makeText(this, "Seleccione al menos una localidad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this@LocalidadesActivity, CompraActivity::class.java)
            intent.putExtra("codPartido", codPartido)
            intent.putExtra("equipoLocal", getIntent().getStringExtra("equipoLocal"))
            intent.putExtra("equipoVisita", getIntent().getStringExtra("equipoVisita"))
            intent.putExtra("carrito", ArrayList(carrito))
            startActivity(intent)
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVerMapa).setOnClickListener {
            val intent = Intent(this@LocalidadesActivity, MapaAsientosActivity::class.java)
            intent.putExtra("codPartido", codPartido)
            intent.putExtra("equipoLocal", getIntent().getStringExtra("equipoLocal"))
            intent.putExtra("equipoVisita", getIntent().getStringExtra("equipoVisita"))
            intent.putExtra("fecha", getIntent().getStringExtra("fecha"))
            intent.putExtra("lugar", getIntent().getStringExtra("lugar"))
            startActivity(intent)
        }
    }

    // ============================
    // Adapter
    // ============================
    class LocalidadesAdapter(
        private val localidades: List<LocalidadPartido>
    ) : RecyclerView.Adapter<LocalidadesAdapter.ViewHolder>() {

        private val cantidades = mutableMapOf<Int, Int>()

        fun getCarrito(codPartido: Int): List<PeticionCompra> {
            val list = mutableListOf<PeticionCompra>()
            for (loc in localidades) {
                val cant = cantidades[loc.id] ?: 0
                if (cant > 0) {
                    list.add(PeticionCompra(loc.id, loc.codigoLocalidad, cant, loc.precio, codPartido))
                }
            }
            return list
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvLocalidad: TextView = view.findViewById(R.id.tvLocalidad)
            val tvDisponibles: TextView = view.findViewById(R.id.tvDisponibles)
            val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
            val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
            val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
            val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_localidad, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val loc = localidades[position]
            holder.tvLocalidad.text = FormatUtil.tilde(loc.codigoLocalidad)
            holder.tvDisponibles.text = "Disponibles: ${loc.disponibilidad}"
            holder.tvPrecio.text = "$${String.format("%.2f", loc.precio)}"

            val cant = cantidades[loc.id] ?: 0
            holder.tvCantidad.text = cant.toString()

            holder.btnMinus.setOnClickListener {
                var c = cantidades[loc.id] ?: 0
                if (c > 0) {
                    c--
                    cantidades[loc.id] = c
                    holder.tvCantidad.text = c.toString()
                }
            }

            holder.btnPlus.setOnClickListener {
                var c = cantidades[loc.id] ?: 0
                if (c < loc.disponibilidad) {
                    c++
                    cantidades[loc.id] = c
                    holder.tvCantidad.text = c.toString()
                }
            }
        }

        override fun getItemCount() = localidades.size
    }
}
