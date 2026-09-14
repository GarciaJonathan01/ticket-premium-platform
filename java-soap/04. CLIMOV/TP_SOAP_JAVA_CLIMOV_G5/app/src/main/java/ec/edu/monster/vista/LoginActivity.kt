package ec.edu.monster.vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ec.edu.monster.modelo.Usuario
import ec.edu.monster.servicio.TicketPremiumSoapService
import ec.edu.monster.ticketpremium.R

class LoginActivity : AppCompatActivity() {

    private val soapService = TicketPremiumSoapService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etServerIp = findViewById<TextInputEditText>(R.id.etServerIp)
        val etUser = findViewById<TextInputEditText>(R.id.etUsername)
        val etPass = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Cargar IP guardada o usar "127.0.0.1" por defecto
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedIp = prefs.getString("server_ip", "127.0.0.1")
        etServerIp.setText(savedIp)

        btnLogin.setOnClickListener {
            val ip = etServerIp.text.toString().trim()
            val u = etUser.text.toString().trim()
            val p = etPass.text.toString().trim()
            
            if (ip.isEmpty()) {
                Toast.makeText(this, "Ingrese la IP o Host del servidor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Ingrese credenciales", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar IP en SharedPreferences y configurar el servicio
            prefs.edit().putString("server_ip", ip).apply()
            TicketPremiumSoapService.serverIp = ip

            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            soapService.login(u, p, object : TicketPremiumSoapService.SoapCallback<Usuario?> {
                override fun onSuccess(result: Usuario?) {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    if (result != null) {
                        Toast.makeText(this@LoginActivity, "Bienvenido ${result.username}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, PartidosActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: String) {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
