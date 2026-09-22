package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.presentation.components.ErrorView
import pe.upeu.andinasalud.presentation.components.LoadingView
import pe.upeu.andinasalud.presentation.theme.StatusAtendida
import pe.upeu.andinasalud.presentation.theme.StatusCancelada
import pe.upeu.andinasalud.presentation.theme.StatusProgramada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    citaId: Long,
    viewModel: DetalleViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(citaId) {
        viewModel.cargarCita(citaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Cita") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingView(modifier = Modifier.padding(paddingValues))
            state.error != null -> ErrorView(
                mensaje = state.error ?: "Error desconocido",
                onReintentar = { viewModel.cargarCita(citaId) },
                modifier = Modifier.padding(paddingValues)
            )
            state.cita != null -> {
                val cita = state.cita!!
                val (estadoTexto, estadoColor) = when (cita.estado) {
                    is EstadoCita.Programada -> "Programada" to StatusProgramada
                    is EstadoCita.Atendida -> "Atendida" to StatusAtendida
                    is EstadoCita.Cancelada -> "Cancelada" to StatusCancelada
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Estado visual con distintivo de color
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = estadoColor.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estado de Atención",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(estadoColor)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = estadoTexto,
                                    color = MaterialTheme.colorScheme.surface,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Información general de la cita médica
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            FilaDetalle(Icons.Default.MedicalServices, "Especialidad", cita.especialidad)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                            FilaDetalle(Icons.Default.Person, "Médico Responsable", cita.medico)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                            FilaDetalle(Icons.Default.LocationOn, "Sede Hospitalaria", cita.sede)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                            FilaDetalle(Icons.Default.CalendarToday, "Fecha", cita.fecha)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                            FilaDetalle(Icons.Default.Schedule, "Horario", cita.hora)

                            // Detalles específicos según el estado real de EstadoCita
                            when (val estado = cita.estado) {
                                is EstadoCita.Programada -> {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                                    FilaDetalle(
                                        Icons.Default.Info,
                                        "Recordatorio",
                                        if (estado.recordatorioActivo) "Activado por SMS/Email" else "Desactivado"
                                    )
                                }
                                is EstadoCita.Atendida -> {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                                    FilaDetalle(
                                        Icons.Default.CheckCircle,
                                        "Indicaciones Médicas",
                                        estado.indicaciones
                                    )
                                }
                                is EstadoCita.Cancelada -> {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                                    FilaDetalle(
                                        Icons.Default.Info,
                                        "Motivo de Cancelación",
                                        estado.motivo
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para cancelar (solo visible si está programada)
                    if (cita.estado is EstadoCita.Programada) {
                        Button(
                            onClick = { viewModel.mostrarDialogoCancelar() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancelar Cita Médica")
                        }
                    }
                }

                // Diálogo modal de cancelación (RN-03 y RN-04)
                if (state.mostrarDialogoCancelar) {
                    AlertDialog(
                        onDismissRequest = { if (!state.isCancelando) viewModel.ocultarDialogoCancelar() },
                        title = { Text("Cancelar Cita") },
                        text = {
                            Column {
                                Text(
                                    text = "Indique el motivo de cancelación (de 10 a 200 caracteres):",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = state.motivoCancelacion,
                                    onValueChange = { viewModel.onMotivoCambiado(it) },
                                    label = { Text("Motivo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    isError = state.errorMotivo != null,
                                    supportingText = {
                                        state.errorMotivo?.let {
                                            Text(it, color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { viewModel.confirmarCancelacion() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                enabled = !state.isCancelando
                            ) {
                                if (state.isCancelando) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text("Confirmar")
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { viewModel.ocultarDialogoCancelar() },
                                enabled = !state.isCancelando
                            ) {
                                Text("Volver")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaDetalle(
    icono: ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}