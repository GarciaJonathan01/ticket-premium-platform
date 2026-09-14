package ec.edu.monster.servicio

import android.os.AsyncTask
import android.util.Log
import ec.edu.monster.modelo.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.concurrent.TimeUnit

/**
 * Cliente SOAP manual para consumir el WebService de la Federación de Fútbol.
 * Construye envelopes SOAP XML y parsea las respuestas con XmlPullParser.
 * Utiliza OkHttp para las peticiones HTTP.
 */
class TicketPremiumSoapService {

    companion object {
        private const val TAG = "TicketPremiumSOAP"
        private const val NAMESPACE = "http://ws.monster.edu.ec/"
        private const val DEFAULT_IP = "127.0.0.1"
        private const val PORT = "8080"
        private const val SERVICE_PATH = "/TP_SOAP_JAVA_SERVICIO_G5/WSFederacion"
        private const val CONNECT_TIMEOUT = 30L
        private const val READ_TIMEOUT = 30L
        private val SOAP_MEDIA_TYPE = "text/xml; charset=utf-8".toMediaType()

        // IP configurable
        var serverIp: String = DEFAULT_IP

        fun escapeXml(text: String): String {
            return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
        }
    }

    private val client: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "OkHttp: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun getBaseUrl(): String {
        return "http://$serverIp:$PORT$SERVICE_PATH"
    }

    // ============================
    // Callback interface
    // ============================
    interface SoapCallback<T> {
        fun onSuccess(result: T)
        fun onError(error: String)
    }

    // ============================
    // SOAP Envelope Builder
    // ============================
    private fun createSoapRequest(methodName: String, vararg params: Pair<String, Any>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
        sb.append("xmlns:ser=\"$NAMESPACE\">")
        sb.append("<soapenv:Header/>")
        sb.append("<soapenv:Body>")
        sb.append("<ser:$methodName>")
        params.forEach { (name, value) ->
            sb.append("<$name>${escapeXml(value.toString())}</$name>")
        }
        sb.append("</ser:$methodName>")
        sb.append("</soapenv:Body>")
        sb.append("</soapenv:Envelope>")
        val xml = sb.toString()
        Log.d(TAG, "SOAP Request: $xml")
        return xml
    }


    private fun executeSoapRequest(soapBody: String, customPath: String? = null): String? {
        val path = customPath ?: SERVICE_PATH
        val baseUrl = "http://$serverIp:$PORT$path"
        try {
            Log.d(TAG, "URL: $baseUrl")
            val requestBody = soapBody.toRequestBody(SOAP_MEDIA_TYPE)
            val request = Request.Builder()
                .url(baseUrl)
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .addHeader("Accept", "text/xml")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            Log.d(TAG, "HTTP ${response.code}")

            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "Sin detalles"
                Log.e(TAG, "Error HTTP ${response.code}: $errorBody")
                throw Exception("Error HTTP ${response.code}: ${response.message}")
            }

            return response.body?.string()
        } catch (e: Exception) {
            Log.e(TAG, "Error SOAP", e)
            throw e
        }
    }

    // ============================
    // PUBLIC API METHODS
    // ============================

    fun obtenerPartidosDisponibles(callback: SoapCallback<List<PartidoFutbol>>) {
        ObtenerPartidosTask(this, callback).execute()
    }

    fun obtenerLocalidades(codigoPartido: Int, callback: SoapCallback<List<LocalidadPartido>>) {
        ObtenerLocalidadesTask(this, callback).execute(codigoPartido.toString())
    }

    fun comprarBoleto(
        codigoPartido: Int, nombreCliente: String,
        codigoLocalidad: String, idLocalidad: Int,
        cantidad: Int, precioUnitario: Double,
        callback: SoapCallback<Factura?>
    ) {
        ComprarBoletoTask(this, callback).execute(
            codigoPartido.toString(), nombreCliente,
            codigoLocalidad, idLocalidad.toString(),
            cantidad.toString(), precioUnitario.toString()
        )
    }

    fun obtenerPartido(codigoPartido: Int, callback: SoapCallback<PartidoFutbol?>) {
        ObtenerPartidoTask(this, callback).execute(codigoPartido.toString())
    }

    fun obtenerResumenVentas(codigoPartido: Int, callback: SoapCallback<List<ResumenVenta>>) {
        ObtenerResumenVentasTask(this, callback).execute(codigoPartido.toString())
    }

    fun obtenerDetalleVentas(codigoPartido: Int, callback: SoapCallback<List<DetalleVentaReporte>>) {
        ObtenerDetalleVentasTask(this, callback).execute(codigoPartido.toString())
    }

    fun obtenerAsientosPartido(codigoPartido: Int, callback: SoapCallback<List<AsientoPartido>>) {
        ObtenerAsientosTask(this, callback).execute(codigoPartido.toString())
    }

    fun login(username: String, clave: String, callback: SoapCallback<Usuario?>) {
        LoginTask(this, callback).execute(username, clave)
    }

    fun comprarBoletos(
        codigoPartido: Int, nombreCliente: String, peticiones: List<PeticionCompra>,
        formaPago: String, idCredito: Int?,
        callback: SoapCallback<Factura?>
    ) {
        val idCredStr = idCredito?.toString() ?: ""
        ComprarBoletosTask(this, callback, peticiones).execute(codigoPartido.toString(), nombreCliente, formaPago, idCredStr)
    }

    fun verificarYCrearCredito(
        cedula: String, monto: Double, plazo: Int,
        callback: SoapCallback<RespuestaCredito>
    ) {
        VerificarCreditoTask(this, callback).execute(cedula, monto.toString(), plazo.toString())
    }

    // ============================
    // XML PARSERS
    // ============================

    private fun parsePartidosResponse(xml: String): List<PartidoFutbol> {
        val partidos = mutableListOf<PartidoFutbol>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var currentPartido: PartidoFutbol? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            currentPartido = PartidoFutbol()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (currentPartido != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "codigo" -> currentPartido.codigo = text.toIntOrNull() ?: 0
                                    "equipoLocal" -> currentPartido.equipoLocal = text
                                    "equipoVisita" -> currentPartido.equipoVisita = text
                                    "fecha" -> currentPartido.fecha = text
                                    "lugar" -> currentPartido.lugar = text
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return" && currentPartido != null) {
                            if (currentPartido.codigo > 0) {
                                partidos.add(currentPartido)
                            }
                            currentPartido = null
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando partidos", e)
        }
        return partidos
    }

    private fun parseAsientosResponse(xml: String): List<AsientoPartido> {
        val list = mutableListOf<AsientoPartido>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var current: AsientoPartido? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            current = AsientoPartido()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (current != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "idAsientoPartido" -> current.idAsientoPartido = text.toIntOrNull() ?: 0
                                    "codigoPartido" -> current.codigoPartido = text.toIntOrNull() ?: 0
                                    "seccion" -> current.seccion = text
                                    "fila" -> current.fila = text
                                    "numero" -> current.numero = text.toIntOrNull() ?: 0
                                    "estado" -> current.estado = text
                                    "idDetalleFactura" -> current.idDetalleFactura = text.toIntOrNull()
                                    "nombreOcupante" -> current.nombreOcupante = text
                                    "nombreCliente" -> current.nombreCliente = text
                                    "idFactura" -> current.idFactura = text.toIntOrNull()
                                    "fechaCompra" -> current.fechaCompra = text
                                    "totalFactura" -> current.totalFactura = text.toDoubleOrNull() ?: 0.0
                                    "precio" -> current.precio = text.toDoubleOrNull() ?: 0.0
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return" && current != null) {
                            list.add(current)
                            current = null
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando asientos", e)
        }
        return list
    }

    private fun parseLocalidadesResponse(xml: String): List<LocalidadPartido> {
        val localidades = mutableListOf<LocalidadPartido>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var current: LocalidadPartido? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            current = LocalidadPartido()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (current != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "id" -> current.id = text.toIntOrNull() ?: 0
                                    "codigoPartido" -> current.codigoPartido = text.toIntOrNull() ?: 0
                                    "codigoLocalidad" -> current.codigoLocalidad = text
                                    "disponibilidad" -> current.disponibilidad = text.toIntOrNull() ?: 0
                                    "precio" -> current.precio = text.toDoubleOrNull() ?: 0.0
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return" && current != null) {
                            if (current.id > 0) {
                                localidades.add(current)
                            }
                            current = null
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando localidades", e)
        }
        return localidades
    }

    private fun parseFacturaResponse(xml: String): Factura? {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var factura: Factura? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            factura = Factura()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (factura != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "id" -> factura.id = text.toIntOrNull() ?: 0
                                    "codigoPartido" -> factura.codigoPartido = text.toIntOrNull() ?: 0
                                    "nombreCliente" -> factura.nombreCliente = text
                                    "fecha" -> factura.fecha = text
                                    "subtotal" -> factura.subtotal = text.toDoubleOrNull() ?: 0.0
                                    "iva" -> factura.iva = text.toDoubleOrNull() ?: 0.0
                                    "total" -> factura.total = text.toDoubleOrNull() ?: 0.0
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return") {
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
            return factura
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando factura", e)
            return null
        }
    }

    private fun parsePartidoResponse(xml: String): PartidoFutbol? {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var partido: PartidoFutbol? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            partido = PartidoFutbol()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (partido != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "codigo" -> partido.codigo = text.toIntOrNull() ?: 0
                                    "equipoLocal" -> partido.equipoLocal = text
                                    "equipoVisita" -> partido.equipoVisita = text
                                    "fecha" -> partido.fecha = text
                                    "lugar" -> partido.lugar = text
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return") {
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
            return partido
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando partido", e)
            return null
        }
    }

    private fun parseResumenVentasResponse(xml: String): List<ResumenVenta> {
        val resumen = mutableListOf<ResumenVenta>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var current: ResumenVenta? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            current = ResumenVenta()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (current != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "codigoLocalidad" -> current.codigoLocalidad = text
                                    "vendidos" -> current.vendidos = text.toIntOrNull() ?: 0
                                    "totalRecaudado" -> current.totalRecaudado = text.toDoubleOrNull() ?: 0.0
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return" && current != null) {
                            if (current.codigoLocalidad.isNotEmpty()) {
                                resumen.add(current)
                            }
                            current = null
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando resumen ventas", e)
        }
        return resumen
    }

    private fun parseDetalleVentasResponse(xml: String): List<DetalleVentaReporte> {
        val detalles = mutableListOf<DetalleVentaReporte>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var current: DetalleVentaReporte? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            current = DetalleVentaReporte()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (current != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "fecha" -> current.fecha = text
                                    "cliente" -> current.cliente = text
                                    "localidades" -> current.localidades = text
                                    "boletosTotales" -> current.boletosTotales = text.toIntOrNull() ?: 0
                                    "totalVenta" -> current.totalVenta = text.toDoubleOrNull() ?: 0.0
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return" && current != null) {
                            if (current.cliente.isNotEmpty()) {
                                detalles.add(current)
                            }
                            current = null
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando detalle ventas", e)
        }
        return detalles
    }

    private fun parseUsuarioResponse(xml: String): Usuario? {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var usuario: Usuario? = null
            var currentTag: String? = null
            var insideReturn = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            usuario = Usuario()
                            insideReturn = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (usuario != null && currentTag != null) {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "username" -> usuario.username = text
                                    "rol" -> usuario.rol = text
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "return") {
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
            return usuario
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando usuario", e)
            return null
        }
    }

    // ============================
    // ASYNC TASKS
    // ============================

    private class ObtenerPartidosTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<List<PartidoFutbol>>
    ) : AsyncTask<Void, Void, Pair<List<PartidoFutbol>?, String?>>() {
        override fun doInBackground(vararg params: Void?): Pair<List<PartidoFutbol>?, String?> {
            return try {
                val soapBody = service.createSoapRequest("obtenerPartidosDisponibles")
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parsePartidosResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<List<PartidoFutbol>?, String?>) {
            if (result.first != null) callback.onSuccess(result.first!!)
            else callback.onError(result.second ?: "Error desconocido")
        }
    }

    private class ObtenerLocalidadesTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<List<LocalidadPartido>>
    ) : AsyncTask<String, Void, Pair<List<LocalidadPartido>?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<List<LocalidadPartido>?, String?> {
            return try {
                val codigoPartido = params[0] ?: return Pair(null, "Código no válido")
                val soapBody = service.createSoapRequest(
                    "obtenerLocalidades",
                    "codigoPartido" to codigoPartido
                )
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parseLocalidadesResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<List<LocalidadPartido>?, String?>) {
            if (result.first != null) callback.onSuccess(result.first!!)
            else callback.onError(result.second ?: "Error desconocido")
        }
    }

    private class ComprarBoletoTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<Factura?>
    ) : AsyncTask<String, Void, Pair<Factura?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<Factura?, String?> {
            return try {
                val soapBody = service.createSoapRequest(
                    "comprarBoleto",
                    "codigoPartido" to (params[0] ?: "0"),
                    "nombreCliente" to (params[1] ?: ""),
                    "codigoLocalidad" to (params[2] ?: ""),
                    "idLocalidad" to (params[3] ?: "0"),
                    "cantidad" to (params[4] ?: "0"),
                    "precioUnitario" to (params[5] ?: "0.0")
                )
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parseFacturaResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<Factura?, String?>) {
            if (result.first != null) callback.onSuccess(result.first)
            else callback.onError(result.second ?: "Error: no se pudo completar la compra")
        }
    }

    private class ObtenerPartidoTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<PartidoFutbol?>
    ) : AsyncTask<String, Void, Pair<PartidoFutbol?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<PartidoFutbol?, String?> {
            return try {
                val soapBody = service.createSoapRequest(
                    "obtenerPartido",
                    "codigoPartido" to (params[0] ?: "0")
                )
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parsePartidoResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<PartidoFutbol?, String?>) {
            if (result.first != null) callback.onSuccess(result.first)
            else callback.onError(result.second ?: "Error desconocido")
        }
    }

    private class ObtenerResumenVentasTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<List<ResumenVenta>>
    ) : AsyncTask<String, Void, Pair<List<ResumenVenta>?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<List<ResumenVenta>?, String?> {
            return try {
                val soapBody = service.createSoapRequest(
                    "obtenerResumenVentas",
                    "codigoPartido" to (params[0] ?: "0")
                )
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parseResumenVentasResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<List<ResumenVenta>?, String?>) {
            if (result.first != null) callback.onSuccess(result.first!!)
            else callback.onError(result.second ?: "Error desconocido")
        }
    }

    private class ObtenerDetalleVentasTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<List<DetalleVentaReporte>>
    ) : AsyncTask<String, Void, Pair<List<DetalleVentaReporte>?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<List<DetalleVentaReporte>?, String?> {
            return try {
                val soapBody = service.createSoapRequest(
                    "obtenerDetalleVentas",
                    "codigoPartido" to (params[0] ?: "0")
                )
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parseDetalleVentasResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<List<DetalleVentaReporte>?, String?>) {
            if (result.first != null) callback.onSuccess(result.first!!)
            else callback.onError(result.second ?: "Error desconocido")
        }
    }

    private class ObtenerAsientosTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<List<AsientoPartido>>
    ) : AsyncTask<String, Void, Pair<List<AsientoPartido>?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<List<AsientoPartido>?, String?> {
            return try {
                val codigoPartido = params[0] ?: return Pair(null, "Código no válido")
                val soapBody = service.createSoapRequest(
                    "obtenerAsientosPartido",
                    "codigoPartido" to codigoPartido
                )
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parseAsientosResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<List<AsientoPartido>?, String?>) {
            if (result.first != null) callback.onSuccess(result.first!!)
            else callback.onError(result.second ?: "Error desconocido")
        }
    }

    private class LoginTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<Usuario?>
    ) : AsyncTask<String, Void, Pair<Usuario?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<Usuario?, String?> {
            return try {
                val soapBody = service.createSoapRequest(
                    "login",
                    "username" to (params[0] ?: ""),
                    "password" to (params[1] ?: "")
                )
                val response = service.executeSoapRequest(soapBody)
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parseUsuarioResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<Usuario?, String?>) {
            if (result.first != null) callback.onSuccess(result.first)
            else callback.onError(result.second ?: "Error en login")
        }
    }

    private class ComprarBoletosTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<Factura?>,
        private val peticiones: List<PeticionCompra>
    ) : AsyncTask<String, Void, Pair<Factura?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<Factura?, String?> {
            return try {
                // Manually build SOAP because it has a list
                val sb = java.lang.StringBuilder()
                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
                sb.append("xmlns:ser=\"http://ws.monster.edu.ec/\">")
                sb.append("<soapenv:Header/>")
                sb.append("<soapenv:Body>")
                sb.append("<ser:comprarBoletosMulti>")
                sb.append("<cedulaCliente>${params[1] ?: ""}</cedulaCliente>")
                peticiones.forEach { p ->
                    sb.append("<peticiones>")
                    sb.append("<cantidad>${p.cantidad}</cantidad>")
                    sb.append("<codigoLocalidad>${p.codigoLocalidad}</codigoLocalidad>")
                    sb.append("<idLocalidad>${p.idLocalidad}</idLocalidad>")
                    sb.append("<precioUnitario>${p.precioUnitario}</precioUnitario>")
                    sb.append("<codigoPartido>${p.codigoPartido}</codigoPartido>")
                    sb.append("<nombreOcupante>${escapeXml(p.nombreOcupante)}</nombreOcupante>")
                    sb.append("<idAsientoPartido>${p.idAsientoPartido}</idAsientoPartido>")
                    sb.append("</peticiones>")
                }
                sb.append("<formaPago>${params[2] ?: "EFECTIVO"}</formaPago>")
                val idCred = params[3] ?: ""
                if (idCred.isNotEmpty()) {
                    sb.append("<idCredito>$idCred</idCredito>")
                }
                sb.append("</ser:comprarBoletosMulti>")
                sb.append("</soapenv:Body>")
                sb.append("</soapenv:Envelope>")

                val response = service.executeSoapRequest(sb.toString())
                    ?: return Pair(null, "Respuesta vacía del servidor")
                Pair(service.parseFacturaResponse(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<Factura?, String?>) {
            if (result.first != null) callback.onSuccess(result.first)
            else callback.onError(result.second ?: "Error: no se pudo completar la compra múltiple")
        }
    }

    private fun parseRespuestaCredito(xml: String): RespuestaCredito {
        val resp = RespuestaCredito()
        val tabla = mutableListOf<CuotaAmortizacion>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var currentTag: String? = null
            var insideReturn = false
            var currentCuota: CuotaAmortizacion? = null
            var insideCuota = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "return") {
                            insideReturn = true
                        } else if (tag == "tablaAmortizacion") {
                            currentCuota = CuotaAmortizacion()
                            insideCuota = true
                        } else if (insideReturn) {
                            currentTag = tag
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text.trim()
                        if (text.isNotEmpty() && currentTag != null) {
                            if (insideCuota && currentCuota != null) {
                                when (currentTag) {
                                    "numCuota" -> currentCuota.numCuota = text.toIntOrNull() ?: 0
                                    "valorCuota" -> currentCuota.valorCuota = text.toDoubleOrNull() ?: 0.0
                                    "interesPagado" -> currentCuota.interesPagado = text.toDoubleOrNull() ?: 0.0
                                    "capitalPagado" -> currentCuota.capitalPagado = text.toDoubleOrNull() ?: 0.0
                                    "saldo" -> currentCuota.saldo = text.toDoubleOrNull() ?: 0.0
                                }
                            } else {
                                when (currentTag) {
                                    "aprobado" -> resp.aprobado = text.toBoolean()
                                    "mensaje" -> resp.mensaje = text
                                    "montoMaximo" -> resp.montoMaximo = text.toDoubleOrNull() ?: 0.0
                                    "idCredito" -> resp.idCredito = text.toIntOrNull() ?: 0
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tag = parser.name
                        if (tag == "tablaAmortizacion" && currentCuota != null) {
                            tabla.add(currentCuota)
                            currentCuota = null
                            insideCuota = false
                        } else if (tag == "return") {
                            insideReturn = false
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
            resp.tablaAmortizacion = tabla
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando respuesta credito", e)
        }
        return resp
    }

    private class VerificarCreditoTask(
        private val service: TicketPremiumSoapService,
        private val callback: SoapCallback<RespuestaCredito>
    ) : AsyncTask<String, Void, Pair<RespuestaCredito?, String?>>() {
        override fun doInBackground(vararg params: String?): Pair<RespuestaCredito?, String?> {
            return try {
                val cedula = params[0] ?: ""
                val monto = params[1] ?: "0.0"
                val plazo = params[2] ?: "0"

                val sb = java.lang.StringBuilder()
                sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">")
                sb.append("<soapenv:Header/>")
                sb.append("<soapenv:Body>")
                sb.append("<ws:verificarYCrearCredito>")
                sb.append("<cedula>$cedula</cedula>")
                sb.append("<montoCompra>$monto</montoCompra>")
                sb.append("<plazoMeses>$plazo</plazoMeses>")
                sb.append("</ws:verificarYCrearCredito>")
                sb.append("</soapenv:Body>")
                sb.append("</soapenv:Envelope>")

                val response = service.executeSoapRequest(sb.toString(), "/TP_SOAP_JAVA_BANCO_G5/WSBancoCore")
                    ?: return Pair(null, "Respuesta vacía del banco")
                Pair(service.parseRespuestaCredito(response), null)
            } catch (e: Exception) {
                Pair(null, e.message ?: "Error desconocido")
            }
        }

        override fun onPostExecute(result: Pair<RespuestaCredito?, String?>) {
            if (result.first != null) callback.onSuccess(result.first!!)
            else callback.onError(result.second ?: "Error en verificación de crédito")
        }
    }
}
