package ec.edu.monster.servicio;

import ec.edu.monster.modelo.CuotaAmortizacion;
import ec.edu.monster.modelo.RespuestaCredito;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class BancoSoapClient {

    private static final String ENDPOINT = "http://127.0.0.1:8080/TP_SOAP_JAVA_BANCO_G5/WSBancoCore";

    public RespuestaCredito verificarYCrearCredito(String cedula, double monto, int plazo) {
        RespuestaCredito resp = new RespuestaCredito();
        resp.setAprobado(false);
        resp.setMensaje("No se pudo conectar al servicio financiero.");

        String soapEnvelope =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">" +
            "   <soapenv:Header/>" +
            "   <soapenv:Body>" +
            "      <ws:verificarYCrearCredito>" +
            "         <cedula>" + cedula + "</cedula>" +
            "         <montoCompra>" + monto + "</montoCompra>" +
            "         <plazoMeses>" + plazo + "</plazoMeses>" +
            "      </ws:verificarYCrearCredito>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";

        try {
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            conn.setRequestProperty("SOAPAction", "");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapEnvelope.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }

                // Parse XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(sb.toString())));

                NodeList nodes = doc.getElementsByTagNameNS("*", "return");
                if (nodes.getLength() > 0) {
                    Element root = (Element) nodes.item(0);

                    String approvedStr = getElementText(root, "aprobado");
                    resp.setAprobado("true".equalsIgnoreCase(approvedStr));

                    String maxStr = getElementText(root, "montoMaximo");
                    if (!maxStr.isEmpty()) resp.setMontoMaximo(Double.parseDouble(maxStr));

                    resp.setMensaje(getElementText(root, "mensaje"));

                    String credIdStr = getElementText(root, "idCredito");
                    if (!credIdStr.isEmpty()) resp.setIdCredito(Integer.parseInt(credIdStr));

                    List<CuotaAmortizacion> tabla = new ArrayList<>();
                    NodeList cuotaNodes = root.getElementsByTagName("tablaAmortizacion");
                    for (int i = 0; i < cuotaNodes.getLength(); i++) {
                        Element cuotaEl = (Element) cuotaNodes.item(i);
                        CuotaAmortizacion c = new CuotaAmortizacion();

                        String numStr = getElementText(cuotaEl, "numCuota");
                        if (!numStr.isEmpty()) c.setNumCuota(Integer.parseInt(numStr));

                        String valStr = getElementText(cuotaEl, "valorCuota");
                        if (!valStr.isEmpty()) c.setValorCuota(Double.parseDouble(valStr));

                        String intStr = getElementText(cuotaEl, "interesPagado");
                        if (!intStr.isEmpty()) c.setInteresPagado(Double.parseDouble(intStr));

                        String capStr = getElementText(cuotaEl, "capitalPagado");
                        if (!capStr.isEmpty()) c.setCapitalPagado(Double.parseDouble(capStr));

                        String salStr = getElementText(cuotaEl, "saldo");
                        if (!salStr.isEmpty()) c.setSaldo(Double.parseDouble(salStr));

                        tabla.add(c);
                    }
                    resp.setTablaAmortizacion(tabla);
                }
            } else {
                resp.setMensaje("Error en el servidor financiero. HTTP " + responseCode);
            }
        } catch (Exception e) {
            resp.setMensaje("Error de conexion con el banco: " + e.getMessage());
        }

        return resp;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return "";
    }
}
