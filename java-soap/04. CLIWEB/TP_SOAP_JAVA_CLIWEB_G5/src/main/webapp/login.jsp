<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión - TicketPremium</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; }
    </style>
</head>
<body class="bg-gray-900 text-white min-h-screen flex items-center justify-center">

    <div class="bg-gray-800 p-8 rounded-xl shadow-2xl w-full max-w-md border border-gray-700">
        <div class="text-center mb-8">
            <h1 class="text-3xl font-bold text-indigo-400 mb-2">🎟️ TicketPremium</h1>
            <p class="text-gray-400">Ingreso al Sistema</p>
        </div>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
            <div class="bg-red-500/20 border border-red-500 text-red-300 p-3 rounded-lg mb-6 text-sm">
                <%= error %>
            </div>
        <%
            }
        %>

        <form action="ticket" method="post" class="space-y-6">
            <input type="hidden" name="accion" value="login">
            
            <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Usuario</label>
                <input type="text" name="username" required
                       class="w-full bg-gray-700 border border-gray-600 rounded-lg px-4 py-3 text-white focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-colors">
            </div>

            <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Contraseña</label>
                <input type="password" name="password" required
                       class="w-full bg-gray-700 border border-gray-600 rounded-lg px-4 py-3 text-white focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-colors">
            </div>

            <button type="submit"
                    class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition-all duration-200 shadow-lg shadow-indigo-500/30 transform hover:-translate-y-0.5">
                Iniciar Sesión
            </button>
        </form>
    </div>

</body>
</html>
