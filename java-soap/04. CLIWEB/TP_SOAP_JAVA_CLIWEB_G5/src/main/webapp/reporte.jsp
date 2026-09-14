<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ec.edu.monster.ws.generated.PartidoFutbol" %>
<%@ page import="ec.edu.monster.ws.generated.ResumenVenta" %>
<%@ page import="ec.edu.monster.ws.generated.DetalleVentaReporte" %>
<%@ page import="ec.edu.monster.util.FormatUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium - Reporte de Ventas</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh; }

        .header {
            background: linear-gradient(135deg, #581c87, #7e22ce, #9333ea);
            padding: 28px 40px; position: relative; overflow: hidden;
        }
        .header::after {
            content: ''; position: absolute; top: -40%; right: -5%; width: 250px; height: 250px;
            background: rgba(255,255,255,0.05); border-radius: 50%;
        }
        .back-link { color: rgba(255,255,255,0.8); text-decoration: none; font-size: 0.9rem; font-weight: 500; }
        .back-link:hover { color: #fff; }
        .header h1 { font-size: 1.8rem; font-weight: 800; color: #fff; margin-top: 8px; }
        .header .match-info { color: #e9d5ff; margin-top: 8px; font-size: 1rem; line-height: 1.6; }

        .container { max-width: 800px; margin: 0 auto; padding: 32px 24px; }

        .table-wrapper {
            background: linear-gradient(145deg, #1e293b, #1a1f35);
            border: 1px solid rgba(147, 51, 234, 0.15);
            border-radius: 16px; overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.25);
        }

        table { width: 100%; border-collapse: collapse; }
        thead th {
            background: linear-gradient(135deg, #581c87, #7e22ce);
            color: #fff; padding: 16px 24px; font-size: 0.9rem; font-weight: 700;
            text-transform: uppercase; letter-spacing: 0.8px; text-align: left;
        }
        thead th:last-child { text-align: right; }
        thead th:nth-child(2) { text-align: center; }

        tbody td { padding: 16px 24px; border-bottom: 1px solid rgba(100, 116, 139, 0.1); font-size: 0.95rem; }
        tbody td:first-child { font-weight: 700; color: #c084fc; }
        tbody td:nth-child(2) { text-align: center; color: #94a3b8; font-weight: 600; }
        tbody td:last-child { text-align: right; font-weight: 700; color: #34d399; font-size: 1.05rem; }

        tbody tr { transition: background 0.2s; }
        tbody tr:hover { background: rgba(147, 51, 234, 0.08); }

        .empty-row td { text-align: center !important; color: #64748b; padding: 40px; font-size: 1rem; }

        @keyframes fadeUp { from { opacity: 0; transform: translateY(12px); } to { opacity: 1; transform: translateY(0); } }
        .table-wrapper { animation: fadeUp 0.5s ease; }
    </style>
</head>
<body>
    <%
        PartidoFutbol partido = (PartidoFutbol) request.getAttribute("partido");
        List<ResumenVenta> resumen = (List<ResumenVenta>) request.getAttribute("resumen");
        List<DetalleVentaReporte> detalles = (List<DetalleVentaReporte>) request.getAttribute("detalles");
    %>

    <div class="header">
        <a href="ticket?accion=partidos" class="back-link">← Volver a Partidos</a>
        <h1>📊 Reporte Detallado de Ventas</h1>
        <div class="match-info">
            <% if (partido != null) { %>
                <strong>Partido:</strong> <%= FormatUtil.tilde(partido.getEquipoLocal()) %> vs <%= FormatUtil.tilde(partido.getEquipoVisita()) %><br/>
                <strong>Fecha:</strong> <%= partido.getFecha() %>
            <% } %>
        </div>
    </div>

    <div class="container" style="max-width: 1000px;">
        
        <h2 class="section-title" style="color: #fff; margin-bottom: 16px;">Detalle de Transacciones</h2>
        <div class="table-wrapper" style="margin-bottom: 32px;">
            <table>
                <thead>
                    <tr>
                        <th style="text-align: left;">Fecha Compra</th>
                        <th style="text-align: left;">Cliente</th>
                        <th style="text-align: left;">Localidad(es)</th>
                        <th style="text-align: center;">Boletos</th>
                        <th style="text-align: right;">Total ($)</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (detalles != null && !detalles.isEmpty()) {
                        for (DetalleVentaReporte d : detalles) { %>
                    <tr>
                        <td style="color: #e2e8f0; font-weight: normal;"><%= d.getFecha() %></td>
                        <td style="text-align: left; color: #fff;"><%= d.getCliente() %></td>
                        <td style="text-align: left;"><%= FormatUtil.tilde(d.getLocalidades()) %></td>
                        <td style="text-align: center;"><%= d.getBoletosTotales() %></td>
                        <td>$<%= String.format("%.2f", d.getTotalVenta()) %></td>
                    </tr>
                    <% }} else { %>
                    <tr class="empty-row">
                        <td colspan="5">No hay ventas registradas para este partido.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <h2 class="section-title" style="color: #fff; margin-bottom: 16px;">Resumen por Localidad</h2>
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th style="text-align: left;">Localidad</th>
                        <th style="text-align: center;">Vendidos</th>
                        <th style="text-align: right;">Total Recaudado</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (resumen != null && !resumen.isEmpty()) {
                        for (ResumenVenta r : resumen) { %>
                    <tr>
                        <td style="text-align: left;"><%= FormatUtil.tilde(r.getCodigoLocalidad()) %></td>
                        <td style="text-align: center;"><%= r.getVendidos() %></td>
                        <td>$<%= String.format("%.2f", r.getTotalRecaudado()) %></td>
                    </tr>
                    <% }} else { %>
                    <tr class="empty-row">
                        <td colspan="3">No hay datos de resumen.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
