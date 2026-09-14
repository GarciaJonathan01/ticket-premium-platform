<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ec.edu.monster.ws.generated.Factura" %>
<%@ page import="ec.edu.monster.ws.generated.PeticionCompra" %>
<%@ page import="ec.edu.monster.ws.generated.PartidoFutbol" %>
<%@ page import="ec.edu.monster.util.FormatUtil" %>
<%@ page import="ec.edu.monster.modelo.RespuestaCredito" %>
<%@ page import="ec.edu.monster.modelo.CuotaAmortizacion" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium - Factura</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh;
               display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40px 20px; gap: 20px; }

        .receipt {
            background: linear-gradient(145deg, #1e293b, #1a1f35);
            border: 1px solid rgba(16, 185, 129, 0.2);
            border-radius: 20px; max-width: 480px; width: 100%;
            overflow: hidden; box-shadow: 0 25px 60px rgba(0,0,0,0.4);
            animation: popIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
        }

        .receipt-header {
            background: linear-gradient(135deg, #059669, #10b981);
            padding: 32px 28px; text-align: center;
        }
        .receipt-header .check {
            width: 60px; height: 60px; background: rgba(255,255,255,0.2);
            border-radius: 50%; display: flex; align-items: center; justify-content: center;
            margin: 0 auto 12px; font-size: 2rem;
        }
        .receipt-header h1 { color: #fff; font-size: 1.5rem; font-weight: 800; }
        .receipt-header .id { color: #d1fae5; font-size: 1.1rem; margin-top: 4px; font-weight: 600; }

        .receipt-body { padding: 28px; }

        .receipt-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid rgba(100, 116, 139, 0.15); }
        .receipt-row .label { color: #94a3b8; font-size: 0.9rem; }
        .receipt-row .value { color: #e2e8f0; font-weight: 600; font-size: 0.95rem; text-align: right; }

        .divider { height: 2px; background: repeating-linear-gradient(90deg, #334155 0px, #334155 6px, transparent 6px, transparent 12px); margin: 16px 0; }

        .receipt-row.highlight .label { color: #94a3b8; font-size: 0.95rem; }
        .receipt-row.highlight .value { color: #e2e8f0; font-size: 1rem; }

        .receipt-total {
            display: flex; justify-content: space-between; align-items: center;
            padding: 16px 0; margin-top: 8px;
            border-top: 2px solid rgba(16, 185, 129, 0.3);
        }
        .receipt-total .label { color: #10b981; font-size: 1rem; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; }
        .receipt-total .value { color: #34d399; font-size: 1.8rem; font-weight: 800; }

        .receipt-footer { padding: 0 28px 28px; text-align: center; }
        .btn-back {
            display: inline-flex; align-items: center; gap: 8px;
            background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white;
            padding: 12px 28px; border-radius: 12px; text-decoration: none;
            font-weight: 600; font-size: 0.95rem; transition: all 0.3s;
        }
        .btn-back:hover { transform: scale(1.03); box-shadow: 0 8px 20px rgba(99, 102, 241, 0.3); }

        @keyframes popIn { from { opacity: 0; transform: scale(0.9) translateY(20px); } to { opacity: 1; transform: scale(1) translateY(0); } }
    </style>
</head>
<body>
    <%
        Factura factura = (Factura) request.getAttribute("factura");
        List<PeticionCompra> carrito = (List<PeticionCompra>) request.getAttribute("carrito");
    %>

    <div class="receipt">
        <div class="receipt-header">
            <div class="check">✅</div>
            <h1>Compra Exitosa</h1>
            <div class="id">FACTURA N° <%= factura.getId() %></div>
        </div>

        <div class="receipt-body">
            <div class="receipt-row">
                <span class="label">Cliente</span>
                <span class="value"><%= factura.getNombreCliente() %></span>
            </div>
            
            <div class="divider"></div>
            
            <div style="margin-bottom: 16px;">
                <span class="label" style="font-weight: bold; color: #10b981;">Detalle de Compra</span>
                <% 
                    java.util.Map<Integer, PartidoFutbol> matchMap = (java.util.Map<Integer, PartidoFutbol>) request.getAttribute("matchMap");
                    if (carrito != null) { 
                        for (PeticionCompra p : carrito) { 
                            PartidoFutbol match = (matchMap != null) ? matchMap.get(p.getCodigoPartido()) : null;
                            String matchName = "";
                            if (match != null) {
                                matchName = FormatUtil.tilde(match.getEquipoLocal()) + " vs " + FormatUtil.tilde(match.getEquipoVisita()) + " (Asiento " + p.getIdAsientoPartido() + ")";
                            } else {
                                matchName = "Partido #" + p.getCodigoPartido() + " (Asiento " + p.getIdAsientoPartido() + ")";
                            }
                %>
                <div class="receipt-row" style="padding: 6px 0; border-bottom: 1px solid rgba(255,255,255,0.04); display: flex; flex-direction: column; align-items: flex-start; gap: 4px;">
                    <div style="display: flex; justify-content: space-between; width: 100%;">
                        <span class="label" style="font-weight: 600; color: #fff;"><%= p.getCantidad() %>x <%= FormatUtil.tilde(p.getCodigoLocalidad()) %></span>
                        <span class="value" style="font-weight: 700; color: #cbd5e1;">$<%= String.format("%.2f", p.getCantidad() * p.getPrecioUnitario()) %></span>
                    </div>
                    <div style="font-size: 0.8rem; color: #94a3b8;"><%= matchName %></div>
                    <div style="font-size: 0.75rem; color: #c084fc; font-style: italic;">Ocupante: <%= FormatUtil.tilde(p.getNombreOcupante()) %></div>
                </div>
                <% }} %>
            </div>

            <div class="divider"></div>

            <div class="receipt-row highlight">
                <span class="label">Subtotal</span>
                <span class="value">$<%= String.format("%.2f", factura.getSubtotal()) %></span>
            </div>
            <div class="receipt-row highlight">
                <span class="label">IVA (15%)</span>
                <span class="value">$<%= String.format("%.2f", factura.getIva()) %></span>
            </div>

            <div class="receipt-total">
                <span class="label">Total</span>
                <span class="value">$<%= String.format("%.2f", factura.getTotal()) %></span>
            </div>
        </div>

        <div class="receipt-footer">
            <a href="ticket?accion=partidos" class="btn-back">← Volver a Partidos</a>
        </div>
    </div>

    <%
        RespuestaCredito res = (RespuestaCredito) request.getAttribute("respuestaCredito");
        if (res != null) {
    %>
        <div class="amortization-container" style="margin-top: 10px; padding: 20px; background: linear-gradient(145deg, #1e293b, #151d30); border: 1px solid rgba(16, 185, 129, 0.2); border-radius: 16px; width: 100%; max-width: 480px; box-shadow: 0 25px 60px rgba(0,0,0,0.4);">
            <h2 style="color: #34d399; font-size: 1.1rem; font-weight: 700; margin-bottom: 12px; text-align: center; display: flex; align-items: center; justify-content: center; gap: 8px;">
                📊 Tabla de Amortización Francesa (16.5% Interés)
            </h2>
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse; font-size: 0.8rem; color: #cbd5e1;">
                    <thead>
                        <tr style="border-bottom: 2px solid rgba(255,255,255,0.1); text-align: left;">
                            <th style="padding: 8px 4px; color: #94a3b8; font-weight: 600;">Cuota</th>
                            <th style="padding: 8px 4px; color: #94a3b8; font-weight: 600; text-align: right;">Valor</th>
                            <th style="padding: 8px 4px; color: #94a3b8; font-weight: 600; text-align: right;">Interés</th>
                            <th style="padding: 8px 4px; color: #94a3b8; font-weight: 600; text-align: right;">Capital</th>
                            <th style="padding: 8px 4px; color: #94a3b8; font-weight: 600; text-align: right;">Saldo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<CuotaAmortizacion> tabla = res.getTablaAmortizacion();
                            if (tabla != null) {
                                for (CuotaAmortizacion c : tabla) {
                        %>
                        <tr style="border-bottom: 1px solid rgba(255,255,255,0.05);">
                            <td style="padding: 6px 4px; font-weight: 600; color: #fff;">Cuota <%= c.getNumCuota() %></td>
                            <td style="padding: 6px 4px; text-align: right;">$<%= String.format("%.2f", c.getValorCuota()) %></td>
                            <td style="padding: 6px 4px; text-align: right; color: #ef4444;">$<%= String.format("%.2f", c.getInteresPagado()) %></td>
                            <td style="padding: 6px 4px; text-align: right; color: #10b981;">$<%= String.format("%.2f", c.getCapitalPagado()) %></td>
                            <td style="padding: 6px 4px; text-align: right; font-weight: 600; color: #34d399;">$<%= String.format("%.2f", c.getSaldo()) %></td>
                        </tr>
                        <%
                                }
                            }
                        %>
                    </tbody>
                </table>
            </div>
            <div style="margin-top: 12px; font-size: 0.75rem; color: #94a3b8; text-align: center;">
                Crédito Financiero N° <%= res.getIdCredito() %> aprobado de forma automática.
            </div>
        </div>
    <%
        }
    %>
</body>
</html>
