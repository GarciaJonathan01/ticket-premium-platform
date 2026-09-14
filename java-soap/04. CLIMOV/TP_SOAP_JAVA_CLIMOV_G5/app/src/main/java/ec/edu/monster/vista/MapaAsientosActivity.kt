package ec.edu.monster.vista

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import ec.edu.monster.modelo.AsientoPartido
import ec.edu.monster.modelo.LocalidadPartido
import ec.edu.monster.modelo.PeticionCompra
import ec.edu.monster.servicio.FormatUtil
import ec.edu.monster.servicio.TicketPremiumSoapService
import ec.edu.monster.ticketpremium.R

class MapaAsientosActivity : AppCompatActivity() {

    private val soapService = TicketPremiumSoapService()
    
    private lateinit var tvPartidoInfoMapa: TextView
    private lateinit var tvSeleccionResumen: TextView
    private lateinit var tvTotalMapa: TextView
    private lateinit var btnVolverMapa: MaterialButton
    private lateinit var btnContinuarMapa: MaterialButton
    private lateinit var pbMapa: ProgressBar
    
    private lateinit var gridGeneralNorte: GridLayout
    private lateinit var gridPalco: GridLayout
    private lateinit var gridTribuna: GridLayout
    private lateinit var gridGeneralSur: GridLayout

    private var codPartido = 0
    private var todosAsientos = mutableListOf<AsientoPartido>()
    private val asientosSeleccionados = mutableListOf<AsientoPartido>()

    private var precioPalco = 150.0
    private var precioTribuna = 80.0
    private var precioGeneral = 40.0
    
    private var idPalcoLoc = 0
    private var idTribunaLoc = 0
    private var idGeneralLoc = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_asientos)

        codPartido = intent.getIntExtra("codPartido", 0)
        val equipoLocal = intent.getStringExtra("equipoLocal") ?: ""
        val equipoVisita = intent.getStringExtra("equipoVisita") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""
        val lugar = intent.getStringExtra("lugar") ?: ""

        tvPartidoInfoMapa = findViewById(R.id.tvPartidoInfoMapa)
        tvSeleccionResumen = findViewById(R.id.tvSeleccionResumen)
        tvTotalMapa = findViewById(R.id.tvTotalMapa)
        btnVolverMapa = findViewById(R.id.btnVolverMapa)
        btnContinuarMapa = findViewById(R.id.btnContinuarMapa)
        pbMapa = findViewById(R.id.pbMapa)

        gridGeneralNorte = findViewById(R.id.gridGeneralNorte)
        gridPalco = findViewById(R.id.gridPalco)
        gridTribuna = findViewById(R.id.gridTribuna)
        gridGeneralSur = findViewById(R.id.gridGeneralSur)

        tvPartidoInfoMapa.text = "${FormatUtil.tilde(equipoLocal)} vs ${FormatUtil.tilde(equipoVisita)}\n📅 $fecha\n📍 ${FormatUtil.tilde(lugar)}"

        btnVolverMapa.setOnClickListener { finish() }
        btnContinuarMapa.setOnClickListener { continuarCompra() }

        cargarDatosLocalidades()
    }

    private fun cargarDatosLocalidades() {
        pbMapa.visibility = View.VISIBLE
        soapService.obtenerLocalidades(codPartido, object : TicketPremiumSoapService.SoapCallback<List<LocalidadPartido>> {
            override fun onSuccess(result: List<LocalidadPartido>) {
                for (l in result) {
                    if ("PALCO".equals(l.codigoLocalidad, ignoreCase = true)) {
                        precioPalco = l.precio
                        idPalcoLoc = l.id
                    } else if ("TRIBUNA".equals(l.codigoLocalidad, ignoreCase = true)) {
                        precioTribuna = l.precio
                        idTribunaLoc = l.id
                    } else if ("GENERAL".equals(l.codigoLocalidad, ignoreCase = true)) {
                        precioGeneral = l.precio
                        idGeneralLoc = l.id
                    }
                }
                cargarAsientos()
            }

            override fun onError(error: String) {
                pbMapa.visibility = View.GONE
                Toast.makeText(this@MapaAsientosActivity, "Error cargando localidades: $error", Toast.LENGTH_LONG).show()
                cargarAsientos()
            }
        })
    }

    private fun cargarAsientos() {
        pbMapa.visibility = View.VISIBLE
        soapService.obtenerAsientosPartido(codPartido, object : TicketPremiumSoapService.SoapCallback<List<AsientoPartido>> {
            override fun onSuccess(result: List<AsientoPartido>) {
                pbMapa.visibility = View.GONE
                todosAsientos.clear()
                todosAsientos.addAll(result)
                renderizarEstadio()
            }

            override fun onError(error: String) {
                pbMapa.visibility = View.GONE
                Toast.makeText(this@MapaAsientosActivity, "Error cargando asientos: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun renderizarEstadio() {
        gridGeneralNorte.removeAllViews()
        gridPalco.removeAllViews()
        gridTribuna.removeAllViews()
        gridGeneralSur.removeAllViews()

        val norteList = mutableListOf<AsientoPartido>()
        val surList = mutableListOf<AsientoPartido>()
        val palcoList = mutableListOf<AsientoPartido>()
        val tribunaList = mutableListOf<AsientoPartido>()

        var maxGeneral = 0
        for (a in todosAsientos) {
            if ("GENERAL".equals(a.seccion, ignoreCase = true)) {
                if (a.numero > maxGeneral) maxGeneral = a.numero
            }
        }
        val midGeneral = maxGeneral / 2

        for (a in todosAsientos) {
            if ("GENERAL".equals(a.seccion, ignoreCase = true)) {
                if (a.numero <= midGeneral) norteList.add(a)
                else surList.add(a)
            } else if ("PALCO".equals(a.seccion, ignoreCase = true)) {
                palcoList.add(a)
            } else if ("TRIBUNA".equals(a.seccion, ignoreCase = true)) {
                tribunaList.add(a)
            }
        }

        // Set stand titles with midpoints
        findViewById<TextView>(R.id.tvNorteTitulo).text = "General Norte (1 a $midGeneral) - $$precioGeneral c/u"
        findViewById<TextView>(R.id.tvSurTitulo).text = "General Sur (${midGeneral + 1} a $maxGeneral) - $$precioGeneral c/u"

        renderSeccion(gridGeneralNorte, norteList)
        renderSeccion(gridPalco, palcoList)
        renderSeccion(gridTribuna, tribunaList)
        renderSeccion(gridGeneralSur, surList)
    }

    private fun renderSeccion(grid: GridLayout, list: List<AsientoPartido>) {
        for (a in list) {
            val btn = MaterialButton(this).apply {
                val seatText = when {
                    "PALCO".equals(a.seccion, ignoreCase = true) -> "P${a.numero}"
                    "TRIBUNA".equals(a.seccion, ignoreCase = true) -> "T${a.numero}"
                    else -> "${a.numero}"
                }
                text = seatText
                textSize = 9f
                setTextColor(Color.WHITE)
                insetTop = 0
                insetBottom = 0
                setPadding(0, 0, 0, 0)
                
                val state = a.estado.uppercase()
                
                // Color codes
                val colorDisponible = Color.parseColor("#10b981")
                val colorSeleccionado = Color.parseColor("#f59e0b")
                val colorOcupado = Color.parseColor("#ef4444")
                val colorReservado = Color.parseColor("#d97706")

                when (state) {
                    "OCUPADO" -> {
                        backgroundTintList = ColorStateList.valueOf(colorOcupado)
                        setOnClickListener {
                            mostrarDetalleBoleto(a)
                        }
                    }
                    "RESERVADO" -> {
                        backgroundTintList = ColorStateList.valueOf(colorReservado)
                        isEnabled = false
                    }
                    else -> { // DISPONIBLE
                        val isSel = asientosSeleccionados.any { it.idAsientoPartido == a.idAsientoPartido }
                        backgroundTintList = ColorStateList.valueOf(if (isSel) colorSeleccionado else colorDisponible)

                        setOnClickListener {
                            val idx = asientosSeleccionados.indexOfFirst { it.idAsientoPartido == a.idAsientoPartido }
                            if (idx >= 0) {
                                asientosSeleccionados.removeAt(idx)
                                backgroundTintList = ColorStateList.valueOf(colorDisponible)
                            } else {
                                asientosSeleccionados.add(a)
                                backgroundTintList = ColorStateList.valueOf(colorSeleccionado)
                            }
                            actualizarTotal()
                        }
                    }
                }
            }

            val size = dpToPx(38)
            val params = GridLayout.LayoutParams().apply {
                width = size
                height = size
                setMargins(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3))
            }
            btn.layoutParams = params
            grid.addView(btn)
        }
    }

    private fun mostrarDetalleBoleto(a: AsientoPartido) {
        val msg = """
            🎫 DETALLES DEL BOLETO
            ----------------------------------
            Localidad:   ${FormatUtil.tilde(a.seccion)}
            Fila-N°:     ${a.fila}-${a.numero}
            Ocupante:    ${FormatUtil.tilde(a.nombreOcupante)}
            Comprador:   ${FormatUtil.tilde(a.nombreCliente)}
            Factura N°:  #${a.idFactura}
            Fecha:       ${a.fechaCompra}
            Total Fact.: $${String.format("%.2f", a.totalFactura)}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detalles del Boleto")
            .setMessage(msg)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun actualizarTotal() {
        if (asientosSeleccionados.isEmpty()) {
            tvSeleccionResumen.text = "Asientos seleccionados: Ninguno"
            tvTotalMapa.text = "Total Selección: $0.00 (IVA incl.)"
            return
        }

        var totalPrecio = 0.0
        val sb = StringBuilder("Asientos seleccionados: ")
        for (i in asientosSeleccionados.indices) {
            val a = asientosSeleccionados[i]
            val lbl = when {
                "PALCO".equals(a.seccion, ignoreCase = true) -> "Palco P${a.numero}"
                "TRIBUNA".equals(a.seccion, ignoreCase = true) -> "Tribuna T${a.numero}"
                else -> "Gen N°${a.numero}"
            }
            sb.append(lbl)
            if (i < asientosSeleccionados.size - 1) sb.append(", ")
            
            // Usamos los precios obtenidos
            val precio = when {
                "PALCO".equals(a.seccion, ignoreCase = true) -> precioPalco
                "TRIBUNA".equals(a.seccion, ignoreCase = true) -> precioTribuna
                else -> precioGeneral
            }
            totalPrecio += precio
        }
        
        val totalConIva = totalPrecio * 1.15
        
        tvSeleccionResumen.text = sb.toString()
        tvTotalMapa.text = String.format("Total Selección: $%.2f (IVA incl.)", totalConIva)
    }

    private fun continuarCompra() {
        if (asientosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar al menos un asiento", Toast.LENGTH_SHORT).show()
            return
        }

        val carrito = ArrayList<PeticionCompra>()
        val asientosInfo = ArrayList<String>()

        for (a in asientosSeleccionados) {
            val p = PeticionCompra().apply {
                codigoPartido = codPartido
                cantidad = 1
                codigoLocalidad = a.seccion
                precioUnitario = when {
                    "PALCO".equals(a.seccion, ignoreCase = true) -> precioPalco
                    "TRIBUNA".equals(a.seccion, ignoreCase = true) -> precioTribuna
                    else -> precioGeneral
                }
                idAsientoPartido = a.idAsientoPartido
                idLocalidad = when {
                    "PALCO".equals(a.seccion, ignoreCase = true) -> idPalcoLoc
                    "TRIBUNA".equals(a.seccion, ignoreCase = true) -> idTribunaLoc
                    else -> idGeneralLoc
                }
            }
            carrito.add(p)

            val info = when {
                "PALCO".equals(a.seccion, ignoreCase = true) -> "Palco VIP P${a.numero}"
                "TRIBUNA".equals(a.seccion, ignoreCase = true) -> "Tribuna T${a.numero}"
                else -> {
                    // Check if North or South General
                    var maxGeneral = 0
                    for (seat in todosAsientos) {
                        if ("GENERAL".equals(seat.seccion, ignoreCase = true)) {
                            if (seat.numero > maxGeneral) maxGeneral = seat.numero
                        }
                    }
                    val midGeneral = maxGeneral / 2
                    if (a.numero <= midGeneral) "General Norte N°${a.numero}"
                    else "General Sur N°${a.numero}"
                }
            }
            asientosInfo.add(info)
        }

        val intent = Intent(this, CompraActivity::class.java).apply {
            putExtra("codPartido", codPartido)
            putExtra("equipoLocal", this@MapaAsientosActivity.intent.getStringExtra("equipoLocal"))
            putExtra("equipoVisita", this@MapaAsientosActivity.intent.getStringExtra("equipoVisita"))
            putExtra("carrito", carrito)
            putStringArrayListExtra("asientosInfo", asientosInfo)
        }
        startActivity(intent)
        finish()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    
    private val Int.sp: Float
        get() = this * resources.displayMetrics.scaledDensity
}
