package com.example.moviles

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviles.apiService.MyFirebaseMessagingService
import com.example.moviles.ui.Home.ui.HomeScreen
import com.example.moviles.ui.Products.ui.AddProductScreen
import com.example.moviles.ui.Products.ui.EditDeleteProductScreen
import com.example.moviles.ui.Products.ui.EditSingleProductScreen
import com.example.moviles.ui.Products.ui.ListProductsScreen
import com.example.moviles.ui.login.ui.LoginScreen
import com.example.moviles.ui.register.ui.RegisterScreen
import com.example.moviles.ui.theme.MovilesTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val notificationService = MyFirebaseMessagingService()
    private val REQUEST_CODE_POST_NOTIFICATIONS = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission()
        }
        else {
            getToken()
        }

        setContent {
            val notificationMessage = remember { mutableStateOf("") }
            // Usa la recolección del flujo con LaunchedEffect para un ciclo de vida seguro
            LaunchedEffect(Unit) {
                notificationService.receivedMessage.collect { (title, message) ->
                    notificationMessage.value = "Título: $title, Mensaje: $message"
                    Log.d("FCM_UI", "Mensaje de UI: ${notificationMessage.value}") // Log para depuración
                }
            }

            MovilesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column {
                        if (notificationMessage.value.isNotEmpty()) {
                            Text(
                                text = "Mensaje Push: ${notificationMessage.value}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "login_screen"
                        ) {
                            composable("login_screen") {
                                LoginScreen(navController = navController)
                            }
                            composable("register_screen") {
                                RegisterScreen(navigateToLogin = {
                                    navController.navigate("login_screen")
                                })
                            }
                            composable("home_screen") {
                                HomeScreen(navController = navController)
                            }
                            composable("add_product_screen") {
                                AddProductScreen(navController = navController)
                            }
                            composable("list_products_screen") {
                                ListProductsScreen(navController = navController)
                            }
                            composable("edit_delete_product_screen") {
                                EditDeleteProductScreen(navController = navController)
                            }
                            composable(
                                route = "edit_single_product_screen/{productId}",
                                arguments = listOf(navArgument("productId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                EditSingleProductScreen(
                                    navController = navController,
                                    productId = backStackEntry.arguments?.getString("productId")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no está garantizado, solicitarlo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, // Especifica el tipo como Array<String>
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permiso concedido
                    Log.d("FCM", "Permiso de notificaciones concedido")
                    getToken()
                } else {
                    // Permiso denegado
                    Log.d("FCM", "Permiso de notificaciones denegado")
                    // Puedes mostrar un mensaje al usuario explicando por qué necesitas el permiso
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("FCM", "FCM Token: $token")
            // Guarda este token y envíalo a tu servidor (NodeJS)
            // para que puedas enviar notificaciones dirigidas a este dispositivo.
            sendRegistrationToServer(token)

            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implementar la lógica para enviar el token al servidor.
        // Esto permite a tu servidor enviar notificaciones dirigidas a este dispositivo.
        // Puedes usar Retrofit para enviar el token a tu API en NodeJS.
        Log.d("FCM Token", "Sending token to server: $token")
        // Si necesitas enviar el token a tu servidor NodeJS, usa Retrofit aquí.
        // Ejemplo:
        // val apiService = RetrofitClient.instance.create(YourApiService::class.java)
        // val call = apiService.sendToken(token)
        // call.enqueue(object : Callback<YourResponse> { ... })
    }
}