package com.example.moviles.ui.Home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Cambiado a Center
        ) {
            Text(
                text = "Administración de Medicamentos",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp, // Aumentado el tamaño de la fuente para mejor legibilidad
                textAlign = TextAlign.Center, // Centrado el texto
                modifier = Modifier
                    .fillMaxWidth() // Para que ocupe todo el ancho disponible
                    .padding(bottom = 32.dp) // Margen inferior
            )

            //Espacio entre el titulo y los botones
            Spacer(modifier = Modifier.height(24.dp))

            HomeButton(
                text = "Agregar Medicamento",
                icon = Icons.Filled.Add,
                color = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary
            ) { navController.navigate("add_product_screen") }

            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre botones

            HomeButton(
                text = "Listar Medicamentos",
                icon = Icons.Filled.List,
                color = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary
            ) { navController.navigate("list_products_screen") }

            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre botones

            HomeButton(
                text = "Editar / Eliminar",
                icon = Icons.Filled.Settings,
                color = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary
            ) { navController.navigate("edit_delete_product_screen") }

            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre botones

            HomeButton(
                text = "Salir",
                icon = Icons.Filled.ExitToApp,
                color = MaterialTheme.colorScheme.secondary,
                textColor = MaterialTheme.colorScheme.onSecondary
            ) {
                navController.navigate("login_screen") {
                    popUpTo("login_screen") { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun HomeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}