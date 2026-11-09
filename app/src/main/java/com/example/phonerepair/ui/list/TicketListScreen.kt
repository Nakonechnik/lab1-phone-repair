package com.example.phonerepair.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phonerepair.model.RepairTicket
import com.example.phonerepair.model.TicketStatus
import com.example.phonerepair.ui.DeviceNameDialog
import com.example.phonerepair.ui.theme.DangerRed
import com.example.phonerepair.ui.theme.MutedGray
import com.example.phonerepair.ui.theme.PrimaryBlue
import com.example.phonerepair.ui.theme.ReadyGreen
import com.example.phonerepair.ui.theme.TextSecondary
import com.example.phonerepair.ui.theme.WarningAmber

@Composable
fun TicketListScreen(
    state: TicketListUiState,
    onOpenTicket: (String) -> Unit,
    onAdd: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Сервисный центр",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Заявки на ремонт: ${state.tickets.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.tickets.isEmpty()) {
                Text(
                    text = "Заявок пока нет. Добавьте новую.",
                    modifier = Modifier.padding(top = 24.dp),
                    color = TextSecondary
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tickets, key = { it.id }) { ticket ->
                        TicketListItem(
                            ticket = ticket,
                            onClick = { onOpenTicket(ticket.id) },
                            onDelete = { onDelete(ticket.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить заявку")
        }
    }

    if (showAddDialog) {
        DeviceNameDialog(
            title = "Новая заявка",
            confirmLabel = "Создать",
            onConfirm = { name ->
                onAdd(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun TicketListItem(
    ticket: RepairTicket,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val accent = statusAccent(ticket)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(88.dp)
                    .background(accent)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = ticket.ticketNumber,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Text(
                    text = ticket.deviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(ticket = ticket, accent = accent)
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = DangerRed
                )
            }
        }
    }
}

@Composable
private fun StatusChip(ticket: RepairTicket, accent: Color) {
    Text(
        text = ticket.statusTitle,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(accent, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

private fun statusAccent(ticket: RepairTicket): Color {
    return when {
        ticket.status == TicketStatus.WAITING_PARTS -> WarningAmber
        ticket.status == TicketStatus.READY -> ReadyGreen
        ticket.status == TicketStatus.REJECTED -> DangerRed
        ticket.status == TicketStatus.ISSUED -> MutedGray
        ticket.status == TicketStatus.REPAIRING -> PrimaryBlue
        ticket.status == TicketStatus.DIAGNOSING && ticket.diagnosticsDone -> PrimaryBlue
        ticket.status == TicketStatus.DIAGNOSING -> PrimaryBlue
        else -> Color(0xFF94A3B8)
    }
}
