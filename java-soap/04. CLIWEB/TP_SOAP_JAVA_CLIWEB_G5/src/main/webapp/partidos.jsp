<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ec.edu.monster.ws.generated.PartidoFutbol" %>
<%@ page import="ec.edu.monster.ws.generated.Usuario" %>
<%@ page import="ec.edu.monster.ws.generated.PeticionCompra" %>
<%@ page import="ec.edu.monster.util.FormatUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium - Partidos Disponibles</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh; }

        .header {
            background: linear-gradient(135deg, #1e293b 0%, #0f172a 50%, #1a1a2e 100%);
            border-bottom: 1px solid rgba(99, 102, 241, 0.2);
            padding: 32px 40px;
            text-align: center;
        }
        .header h1 {
            font-size: 2.2rem; font-weight: 800; color: #fff;
            background: linear-gradient(90deg, #818cf8, #a78bfa, #c084fc);
            -webkit-background-clip: text; -webkit-text-fill-color: transparent;
        }
        .header p { color: #94a3b8; margin-top: 8px; font-size: 1rem; }

        .container { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }

        .section-title {
            font-size: 1.3rem; font-weight: 700; color: #e2e8f0; margin-bottom: 24px;
            display: flex; align-items: center; gap: 10px;
        }
        .section-title span { font-size: 1.6rem; }

        .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 20px; }

        .card {
            background: linear-gradient(145deg, #1e293b, #1a1f35);
            border: 1px solid rgba(99, 102, 241, 0.15);
            border-radius: 16px; padding: 24px;
            transition: all 0.3s ease; position: relative; overflow: hidden;
        }
        .card::before {
            content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px;
            background: linear-gradient(90deg, #818cf8, #a78bfa);
            opacity: 0; transition: opacity 0.3s;
        }
        .card:hover { transform: translateY(-4px); border-color: rgba(99, 102, 241, 0.4); box-shadow: 0 20px 40px rgba(0,0,0,0.3); }
        .card:hover::before { opacity: 1; }

        .card-teams { font-size: 1.2rem; font-weight: 700; color: #f1f5f9; margin-bottom: 12px; }
        .card-vs { color: #818cf8; font-weight: 800; }
        .card-info { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
        .card-info span { font-size: 0.9rem; color: #94a3b8; }
        .card-info .icon { font-size: 1rem; }

        .card-actions { display: flex; gap: 10px; }
        .btn {
            display: inline-flex; align-items: center; gap: 6px;
            padding: 10px 20px; border-radius: 10px; font-size: 0.85rem;
            font-weight: 600; text-decoration: none; transition: all 0.3s; cursor: pointer; border: none;
        }
        .btn-primary {
            background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white;
        }
        .btn-primary:hover { background: linear-gradient(135deg, #4f46e5, #7c3aed); transform: scale(1.03); }
        .btn-outline {
            background: transparent; color: #a78bfa;
            border: 1px solid rgba(167, 139, 250, 0.3);
        }
        .btn-outline:hover { background: rgba(167, 139, 250, 0.1); border-color: #a78bfa; }

        .empty { text-align: center; padding: 60px; color: #64748b; font-size: 1.1rem; }

        @keyframes fadeIn { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }
        .card { animation: fadeIn 0.5s ease forwards; }
        .card:nth-child(2) { animation-delay: 0.1s; }
        .card:nth-child(3) { animation-delay: 0.2s; }
        .card:nth-child(4) { animation-delay: 0.3s; }
        .card:nth-child(5) { animation-delay: 0.4s; }

        @media (max-width: 768px) {
            .header h1 { font-size: 1.6rem; }
            .grid { grid-template-columns: 1fr; }
            .container { padding: 20px 16px; }
        }
    </style>
</head>
<body>
    <div class="header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px;">
        <div style="text-align: left;">
            <h1>🎟️ TICKET PREMIUM</h1>
            <p>Sistema de Venta de Boletos — Copa Mundial de Fútbol FIFA 2026</p>
        </div>
        <div style="display: flex; align-items: center; gap: 16px; flex-wrap: wrap;">
            <%
                List<PeticionCompra> cart = null;
                HttpSession sess = request.getSession(false);
                if (sess != null) {
                    cart = (List<PeticionCompra>) sess.getAttribute("carrito_compras");
                }
                int cartSize = (cart != null) ? cart.size() : 0;
            %>
            <a href="ticket?accion=verCarrito" class="btn btn-primary" style="background: linear-gradient(135deg, #10b981, #059669); position: relative; padding: 10px 20px; font-size: 0.9rem; text-decoration: none; color: white;">
                🛒 Ver Carrito <span style="background: #ef4444; color: white; border-radius: 50%; padding: 2px 6px; font-size: 0.75rem; margin-left: 4px;"><%= cartSize %></span>
            </a>
            <% if (sess != null && sess.getAttribute("usuario") != null) { 
                Usuario u = (Usuario) sess.getAttribute("usuario");
            %>
                <span style="color: #cbd5e1; font-size: 0.9rem; font-weight: 500;">👤 <%= FormatUtil.tilde(u.getUsername()) %></span>
                <a href="ticket?accion=logout" class="btn btn-outline" style="font-size: 0.85rem; padding: 8px 16px; text-decoration: none;">Cerrar Sesión</a>
            <% } %>
        </div>
    </div>

    <div class="container">
        <div class="section-title"><span>⚽</span> Partidos de Fútbol Disponibles</div>

        <%
            List<PartidoFutbol> partidos = (List<PartidoFutbol>) request.getAttribute("partidos");
        %>

        <% if (partidos != null && !partidos.isEmpty()) { %>
        <div class="grid">
            <% for (PartidoFutbol p : partidos) { %>
            <div class="card">
                <div class="card-teams">
                    <%= FormatUtil.tilde(p.getEquipoLocal()) %> <span class="card-vs">vs</span> <%= FormatUtil.tilde(p.getEquipoVisita()) %>
                </div>
                <div class="card-info">
                    <span><span class="icon">📅</span> <%= p.getFecha() %></span>
                    <span><span class="icon">📍</span> <%= FormatUtil.tilde(p.getLugar()) %></span>
                </div>
                <div class="card-actions">
                    <a href="ticket?accion=localidades&codPartido=<%= p.getCodigo() %>" class="btn btn-primary">
                        🏟️ Ver Localidades
                    </a>
                    <a href="ticket?accion=reporte&codPartido=<%= p.getCodigo() %>" class="btn btn-outline">
                        📊 Reporte
                    </a>
                </div>
            </div>
            <% } %>
        </div>
        <% } else { %>
        <div class="empty">No hay partidos disponibles en este momento.</div>
        <% } %>
    </div>
</body>
</html>
