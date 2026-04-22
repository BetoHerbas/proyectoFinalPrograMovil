package com.example.examen3moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.examen3moviles.ui.theme.Examen3MovilesTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Registro de Evento 'ENTRADA'
        registrarEvento("ENTRADA")

        enableEdgeToEdge()
        setContent {
            Examen3MovilesTheme {
                PantallaPrincipal()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registrarEvento(tipo: String) {
        // Usamos GlobalScope para el registro de SALIDA porque el lifecycleScope se cancela al cerrar la app
        GlobalScope.launch(Dispatchers.IO) {
            val database = AppDatabase.obtenerInstancia(applicationContext)
            val dao = database.eventoDao()
            dao.insertarEvento(RegistroEvento(tipo = tipo))
            
            // Programar sincronización con WorkManager
            val restricciones = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val peticionSincronizacion = OneTimeWorkRequestBuilder<SincronizadorEventos>()
                .setConstraints(restricciones)
                .build()

            WorkManager.getInstance(applicationContext).enqueue(peticionSincronizacion)
        }
    }

    override fun onStop() {
        super.onStop()
        // 1. Registro de Evento 'SALIDA'
        registrarEvento("SALIDA")
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun PantallaPrincipal() {
    var mensajeConfiguracion by remember { mutableStateOf("Cargando...") }
    val contexto = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        val database = AppDatabase.obtenerInstancia(contexto)
        val configDao = database.configuracionDao()

        // 1. Intentar obtener de Room primero (Caché local)
        withContext(Dispatchers.IO) {
            val localConfig = configDao.obtenerConfiguracion("mensaje_configuracion")
            if (localConfig != null) {
                withContext(Dispatchers.Main) {
                    mensajeConfiguracion = localConfig.valor
                }
            }
        }

        // 2. Configurar y obtener de Firebase Remote Config
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // Para pruebas, poner en 0. En prod usar 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val nuevoMensaje = remoteConfig.getString("mensaje_configuracion")
                if (nuevoMensaje.isNotEmpty()) {
                    mensajeConfiguracion = nuevoMensaje
                    
                    // Guardar en Room para persistencia offline
                    GlobalScope.launch(Dispatchers.IO) {
                        configDao.guardarConfiguracion(
                            ConfiguracionLocal("mensaje_configuracion", nuevoMensaje)
                        )
                    }
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Mensaje de Configuración:", fontSize = 18.sp)
            Text(text = mensajeConfiguracion, fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        }
    }
}
