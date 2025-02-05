package com.example.moviles.ui.Products.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.moviles.apiService.RetroClient
import com.example.moviles.utils.bitmapToUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavHostController) {
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val viewModel: AddProductViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AddProductViewModel(RetroClient.instance) as T
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            productImageUri = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            productImageUri = bitmapToUri(context, bitmap)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val resetFields: () -> Unit = {
        productName = ""
        productPrice = ""
        productImageUri = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Cambiado a Center
        ) {
            Text(
                text = "Agregar Nuevo Producto",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (viewModel.isLoading) CircularProgressIndicator()
            viewModel.error?.let {
                Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            }
            if (viewModel.success) {
                Text(text = "Producto agregado con éxito", color = MaterialTheme.colorScheme.primary)
                LaunchedEffect(Unit) { resetFields() }
            }

            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = productPrice,
                onValueChange = {
                    // Solo permitir números y un punto decimal
                    val newPrice = it.filter { char -> char.isDigit() || char == '.' }

                    // Asegurarse de que solo haya un punto decimal
                    if (newPrice.count { char -> char == '.' } <= 1) {
                        productPrice = newPrice
                    }
                },
                label = { Text("Precio del Producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            productImageUri?.let {
                Image(
                    painter = rememberImagePainter(data = it),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryLauncher.launch(intent)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Seleccionar Imagen")
                }

                Button(
                    onClick = {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            cameraLauncher.launch(null)
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tomar Foto")
                }
            }


            // Botones en la parte inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Distribuye el espacio uniformemente
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f) // Hace que los botones ocupen el mismo espacio
                ) {
                    Text("Regresar al menu", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre botones

                Button(
                    onClick = {
                        viewModel.addProduct(productName, productPrice, productImageUri, context)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    modifier = Modifier.weight(1f) // Hace que los botones ocupen el mismo espacio
                ) {
                    Text("Agregar Producto", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}