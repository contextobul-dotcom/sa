package com.habittracker.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habittracker.app.AppContainer
import com.habittracker.app.data.entity.PositiveHabit
import com.habittracker.app.ui.theme.NegativeRed
import com.habittracker.app.ui.theme.PositiveGreen
import com.habittracker.app.ui.viewmodel.HomeViewModel
import com.habittracker.app.usage.UsagePermissionHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    container: AppContainer,
    onNavigateToHabits: () -> Unit,
    onNavigateToNegativeApps: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(container.habitRepository, container.usageRepository)
    )

    var hasUsageAccess by remember { mutableStateOf(UsagePermissionHelper.hasUsageAccess(context)) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasUsageAccess = UsagePermissionHelper.hasUsageAccess(context)
                if (hasUsageAccess) viewModel.refreshUsage()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val habits by viewModel.habits.collectAsState()
    val netPoints by viewModel.netPoints.collectAsState()
    val positivePoints by viewModel.positivePoints.collectAsState()
    val negativePoints by viewModel.negativePoints.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Habit Tracker") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!hasUsageAccess) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Para restar puntos automáticamente por apps como TikTok, activa el acceso de uso.",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(onClick = { context.startActivity(UsagePermissionHelper.buildSettingsIntent()) }) {
                            Text("Activar acceso de uso")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "$netPoints pts",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)) {
                Text("+$positivePoints", color = PositiveGreen, modifier = Modifier.padding(end = 16.dp))
                Text("-$negativePoints", color = NegativeRed)
            }

            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                OutlinedButton(onClick = onNavigateToHabits, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Hábitos")
                }
                OutlinedButton(onClick = onNavigateToNegativeApps) {
                    Text("Apps negativas")
                }
            }

            Text("Hábitos de hoy", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            if (habits.isEmpty()) {
                Text("Aún no has creado hábitos. Ve a 'Hábitos' para añadir uno.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(habits) { habit ->
                        HabitRow(habit = habit, onToggle = { viewModel.toggleHabit(habit) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitRow(habit: PositiveHabit, onToggle: () -> Unit) {
    val isRunning = habit.activeSessionStart != null
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(habit.name, fontWeight = FontWeight.Bold)
                Text("${habit.pointsPerMinute} pts/min")
            }
            Button(onClick = onToggle) {
                Text(if (isRunning) "Detener" else "Iniciar")
            }
        }
    }
}
