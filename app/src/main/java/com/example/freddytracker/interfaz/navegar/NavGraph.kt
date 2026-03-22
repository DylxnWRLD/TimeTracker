package com.example.freddytracker.interfaz.navegar

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.freddytracker.interfaz.pantallas.AñadirTareaPantalla
import com.example.freddytracker.interfaz.pantallas.PantallaPrincipal
import com.example.freddytracker.viewModel.TareaViewModel
import com.example.freddytracker.interfaz.pantallas.EditarTarea
import com.example.freddytracker.interfaz.pantallas.PantallaIntervalo

@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val viewModel: TareaViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            PantallaPrincipal(navController, viewModel)
        }

        composable("addTask") {
            AñadirTareaPantalla(navController, viewModel)
        }

        composable("interval/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: 0
            PantallaIntervalo(
                navController = navController,
                viewModel = viewModel,
                taskId = taskId
            )
        }


        composable("editTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: 0
            EditarTarea(
                navController = navController,
                viewModel = viewModel,
                taskId = taskId
            )
        }

    }

}