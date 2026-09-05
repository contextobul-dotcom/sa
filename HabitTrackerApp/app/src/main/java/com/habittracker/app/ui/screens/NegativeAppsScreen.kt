package com.habittracker.app.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habittracker.app.AppContainer
import com.habittracker.app.ui.viewmodel.InstalledAppInfo
import com.habittracker.app.ui.viewmodel.NegativeAppsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegativeAppsScreen(application: Application, container: AppContainer, onBack: () -> Unit) {
    val viewModel: NegativeAppsViewModel = viewModel(
        factory = NegativeAppsViewModel.Factory(application, container.usageRepository)
    )
    val trackedApps by viewModel.trackedApps.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<InstalledAppInfo?>(null) }
    var pointsText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apps negativas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Elige apps a trackear y sus puntos negativos por minuto")
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedApp?.label ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("App instalada") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    installedApps.forEach { appInfo ->
                        DropdownMenuItem(
                            text = { Text(appInfo.label) },
                            onClick = {
                                selectedApp = appInfo
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pointsText,
                onValueChange = { pointsText = it.filter { c -> c.isDigit() } },
                label = { Text("Puntos negativos por minuto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val app = selectedApp
                    val points = pointsText.toIntOrNull() ?: 0
                    if (app != null && points > 0) {
                        viewModel.addNegativeApp(app, points)
                        selectedApp = null
                        pointsText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Apps trackeadas", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(trackedApps) { app ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${app.appLabel} — ${app.pointsPerMinute} pts/min")
                            IconButton(onClick = { viewModel.removeNegativeApp(app) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
