package com.example.moviles.ui.login.ui

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moviles.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    val navigateToHome by viewModel.navigateToHome.collectAsState(initial = false)

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navController.navigate("home_screen") {
                // Evitar volver a la pantalla de login con el botón "Atrás"
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Añade un padding general
        ) {
            Login(Modifier.align(Alignment.Center), viewModel, navController)
        }
    }
}

@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel, navController: NavHostController) {
    Column(
        modifier = modifier
            .fillMaxWidth() // Permite que la columna ocupe el ancho completo disponible
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp) // Aumenta el espacio entre elementos
    ) {
        HeaderImage(Modifier.size(120.dp)) // Tamaño fijo para la imagen
        Text(
            text = "Bienvenido", // Título más llamativo
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary // Color del tema
        )
        Text(
            text = "Inicia sesión para continuar", // Subtítulo
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Color atenuado
        )
        EmailField(viewModel)
        PasswordField(viewModel)
        LoginButton(viewModel)
        NoAccountButton(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(viewModel: LoginViewModel) {
    val errorEmail = viewModel.errorEmail
    OutlinedTextField(
        value = viewModel.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Correo electrónico") }, // Label más amigable
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        isError = errorEmail != null,
        supportingText = {
            if (errorEmail != null) {
                Text(text = errorEmail, color = MaterialTheme.colorScheme.error)
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
        ),
        shape = RoundedCornerShape(8.dp) // Bordes más suaves
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(viewModel: LoginViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = viewModel.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Contraseña") }, // Label más amigable
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = if (passwordVisible) painterResource(id = R.drawable.ic_visibility_off) else painterResource(
                        id = R.drawable.ic_visibility
                    ),
                    contentDescription = description,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Icono más sutil
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
        ),
        shape = RoundedCornerShape(8.dp) // Bordes más suaves
    )
}

@Composable
fun LoginButton(viewModel: LoginViewModel) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            scope.launch {
                viewModel.onLoginButtonClicked()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // Altura ligeramente mayor
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)), // Sombra sutil
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = "Iniciar sesión",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp // Tamaño de fuente más grande
        )
    }
}

@Composable
fun NoAccountButton(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = { navController.navigate("register_screen") }) {
            Text(text = "¿No tienes cuenta? Regístrate")
        }
    }
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Header",
        modifier = modifier,
    )
}