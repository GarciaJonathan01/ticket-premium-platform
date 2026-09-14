package ec.edu.monster.vista

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ec.edu.monster.modelo.Factura
import ec.edu.monster.modelo.PeticionCompra
import ec.edu.monster.modelo.RespuestaCredito
import ec.edu.monster.servicio.TicketPremiumSoapService
import ec.edu.monster.ticketpremium.R

class CompraActivity : AppCompatActivity() {

    private val soapService = TicketPremiumSoapService()
    private lateinit var etNombre: TextInputEditText
    private lateinit var etOcupante: TextInputEditText
    private lateinit var rgFormaPago: RadioGroup
    private lateinit var layoutPlazo: LinearLayout
    private lateinit var spPlazo: Spinner
    private lateinit var tvDetallesCarrito: TextView
    private lateinit var tvPartidoInfo: TextView
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var btnVolver: MaterialButton
    private lateinit var progressBar: ProgressBar

    // Factura panel
    private lateinit var panelFactura: View
    private lateinit var tvFacturaId: TextView
    private lateinit var tvFacturaCliente: TextView
    private lateinit var tvFacturaDetalle: TextView
    private lateinit var tvFacturaSubtotal: TextView
    private lateinit var tvFacturaIva: TextView
    private lateinit var tvFacturaTotal: TextView

    private var codPartido = 0
    private var carrito: List<PeticionCompra> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        codPartido = intent.getIntExtra("codPartido", 0)
        carrito = intent.getSerializableExtra("carrito") as? List<PeticionCompra> ?: emptyList()
        val equipoLocal = intent.getStringExtra("equipoLocal") ?: ""
        val equipoVisita = intent.getStringExtra("equipoVisita") ?: ""

        // Form views
        tvPartidoInfo = findViewById(R.id.tvPartidoInfo)
        tvDetallesCarrito = findViewById(R.id.tvDetallesCarrito)
        etNombre = findViewById(R.id.etNombre)
        etOcupante = findViewById(R.id.etOcupante)
        rgFormaPago = findViewById(R.id.rgFormaPago)
        layoutPlazo = findViewById(R.id.layoutPlazo)
        spPlazo = findViewById(R.id.spPlazo)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnVolver = findViewById(R.id.btnVolver)
        progressBar = findViewById(R.id.progressBar)

        // Factura views
        panelFactura = findViewById(R.id.panelFactura)
        tvFacturaId = findViewById(R.id.tvFacturaId)
        tvFacturaCliente = findViewById(R.id.tvFacturaCliente)
        tvFacturaDetalle = findViewById(R.id.tvFacturaDetalle)
        tvFacturaSubtotal = findViewById(R.id.tvFacturaSubtotal)
        tvFacturaIva = findViewById(R.id.tvFacturaIva)
        tvFacturaTotal = findViewById(R.id.tvFacturaTotal)

        tvPartidoInfo.text = "$equipoLocal vs $equipoVisita"

        // Configurar Spinner de plazos
        val plazos = arrayOf(3, 6, 9, 12, 18)
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, plazos)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlazo.adapter = spAdapter

        // Toggle visibilidad del plazo
        rgFormaPago.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbCredito) {
                layoutPlazo.visibility = View.VISIBLE
            } else {
                layoutPlazo.visibility = View.GONE
            }
        }
        
        val asientosInfo = intent.getStringArrayListExtra("asientosInfo")
        var detallesStr = ""
        var totalEstimado = 0.0
        if (asientosInfo != null && asientosInfo.size == carrito.size) {
            for (i in carrito.indices) {
                val p = carrito[i]
                val info = asientosInfo[i]
                detallesStr += "1x $info ($${String.format("%.2f", p.precioUnitario)})\n"
                totalEstimado += p.precioUnitario
            }
        } else {
            carrito.forEach { p ->
                val sub = p.cantidad * p.precioUnitario
                detallesStr += "${p.cantidad}x ${p.codigoLocalidad} ($${String.format("%.2f", p.precioUnitario)}) = $${String.format("%.2f", sub)}\n"
                totalEstimado += sub
            }
        }
        detallesStr += "\nSubtotal aprox: $${String.format("%.2f", totalEstimado)}"
        tvDetallesCarrito.text = detallesStr

        btnConfirmar.setOnClickListener { realizarCompra() }
        btnVolver.setOnClickListener { finish() }
    }

    private fun realizarCompra() {
        val cedula = etNombre.text.toString().trim()
        val ocupante = etOcupante.text.toString().trim()

        if (cedula.isEmpty()) {
            etNombre.error = "Ingrese su cédula"
            return
        }
        if (ocupante.isEmpty()) {
            etOcupante.error = "Ingrese el nombre del ocupante"
            return
        }

        if (carrito.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // Configurar ocupante en las peticiones
        carrito.forEach { p ->
            p.nombreOcupante = ocupante
        }

        val isCredito = findViewById<RadioButton>(R.id.rbCredito).isChecked
        val formaPago = if (isCredito) "CREDITO" else "EFECTIVO"

        progressBar.visibility = View.VISIBLE
        btnConfirmar.isEnabled = false

        if (isCredito) {
            val plazo = spPlazo.selectedItem as Int
            var subtotal = 0.0
            carrito.forEach { p ->
                subtotal += p.cantidad * p.precioUnitario
            }
            val total = Math.round(subtotal * 1.15 * 100.0) / 100.0 // +15% recargo

            soapService.verificarYCrearCredito(cedula, total, plazo, object : TicketPremiumSoapService.SoapCallback<RespuestaCredito> {
                override fun onSuccess(result: RespuestaCredito) {
                    if (result.aprobado) {
                        mostrarTablaAmortizacion(cedula, formaPago, result)
                    } else {
                        progressBar.visibility = View.GONE
                        btnConfirmar.isEnabled = true
                        Toast.makeText(this@CompraActivity, "Crédito Rechazado: ${result.mensaje}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(error: String) {
                    progressBar.visibility = View.GONE
                    btnConfirmar.isEnabled = true
                    Toast.makeText(this@CompraActivity, "Error consultando crédito: $error", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            procederCompraSOAP(cedula, formaPago, null, null)
        }
    }

    private fun mostrarTablaAmortizacion(cedula: String, formaPago: String, res: RespuestaCredito) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("✅ Crédito Aprobado")
        
        val sb = StringBuilder()
        sb.append(String.format("%-8s %-12s %-12s %-12s %-10s\n", "# Cuota", "Val. Cuota", "Interés", "Capital", "Saldo"))
        sb.append("-".repeat(55)).append("\n")
        res.tablaAmortizacion.forEach { c ->
            if (c.numCuota > 0) {
                sb.append(String.format("%-8d $%-11.2f $%-11.2f $%-11.2f $%-9.2f\n",
                    c.numCuota, c.valorCuota, c.interesPagado, c.capitalPagado, c.saldo))
            }
        }
        
        val view = TextView(this)
        view.text = sb.toString()
        view.typeface = android.graphics.Typeface.MONOSPACE
        view.setPadding(32, 16, 32, 16)
        
        val scroll = ScrollView(this)
        scroll.addView(view)
        
        builder.setView(scroll)
        builder.setPositiveButton("Proceder Compra") { _, _ ->
            procederCompraSOAP(cedula, formaPago, res.idCredito, res)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
            progressBar.visibility = View.GONE
            btnConfirmar.isEnabled = true
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun procederCompraSOAP(cedula: String, formaPago: String, idCredito: Int?, resCredito: RespuestaCredito?) {
        soapService.comprarBoletos(
            codPartido, cedula, carrito, formaPago, idCredito,
            object : TicketPremiumSoapService.SoapCallback<Factura?> {
                override fun onSuccess(result: Factura?) {
                    progressBar.visibility = View.GONE
                    if (result != null && result.id > 0) {
                        mostrarFactura(result, formaPago, resCredito)
                    } else {
                        btnConfirmar.isEnabled = true
                        Toast.makeText(this@CompraActivity, "No se pudo completar la compra", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(error: String) {
                    progressBar.visibility = View.GONE
                    btnConfirmar.isEnabled = true
                    Toast.makeText(this@CompraActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun mostrarFactura(factura: Factura, formaPago: String, resCredito: RespuestaCredito?) {
        panelFactura.visibility = View.VISIBLE
        btnConfirmar.visibility = View.GONE
        findViewById<View>(R.id.tvDetallesCarrito).visibility = View.GONE
        findViewById<View>(R.id.etNombre).isEnabled = false
        findViewById<View>(R.id.etOcupante).isEnabled = false
        findViewById<View>(R.id.rgFormaPago).isEnabled = false
        findViewById<View>(R.id.spPlazo).isEnabled = false

        tvFacturaId.text = "FACTURA N° ${factura.id}"
        tvFacturaCliente.text = "Comprador: ${factura.nombreCliente}\nForma Pago: $formaPago"
        
        val asientosInfo = intent.getStringArrayListExtra("asientosInfo")
        var detalleStr = ""
        if (asientosInfo != null && asientosInfo.size == carrito.size) {
            for (i in carrito.indices) {
                val p = carrito[i]
                val info = asientosInfo[i]
                detalleStr += "1x $info - Ocupante: ${p.nombreOcupante}\n"
            }
        } else {
            carrito.forEach { p ->
                detalleStr += "${p.cantidad}x ${p.codigoLocalidad} - Ocupante: ${p.nombreOcupante}\n"
            }
        }
        
        if (formaPago == "CREDITO" && resCredito != null) {
            detalleStr += "\n📋 MÓDULO FINANCIERO (Crédito Aprobado)\n"
            val cuotaVal = resCredito.tablaAmortizacion.firstOrNull { it.numCuota == 1 }?.valorCuota ?: 0.0
            detalleStr += "Plazo: ${resCredito.tablaAmortizacion.size - 1} meses\n"
            detalleStr += "Valor cuota mensual: $${String.format("%.2f", cuotaVal)}\n"
        }
        
        tvFacturaDetalle.text = detalleStr.trim()
        
        tvFacturaSubtotal.text = "Subtotal: $${String.format("%.2f", factura.subtotal)}"
        tvFacturaIva.text = "IVA (15%): $${String.format("%.2f", factura.iva)}"
        tvFacturaTotal.text = "TOTAL: $${String.format("%.2f", factura.total)}"
    }
}
