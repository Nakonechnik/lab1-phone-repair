package com.example.phonerepair.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phonerepair.model.RepairTicket
import com.example.phonerepair.model.TicketStatus
import com.example.phonerepair.ui.theme.DangerRed
import com.example.phonerepair.ui.theme.MutedGray
import com.example.phonerepair.ui.theme.ReadyGreen
import com.example.phonerepair.ui.theme.TextSecondary
import com.example.phonerepair.ui.theme.WarningAmber

@Composable
fun TicketDetailScreen(
    state: TicketDetailUiState,
    onBack: () -> Unit,
    onStartDiagnostics: () -> Unit,
    onFinishDiagnostics: () -> Unit,
    onMarkPartsArrived: () -> Unit,
    onApproveByClient: () -> Unit,
    onPay: () -> Unit,
    onToggleWarranty: () -> Unit,
    onIncreaseSeverity: () -> Unit,
    onDecreaseSeverity: () -> Unit,
    onStartRepair: () -> Unit,
    onCompleteRepair: () -> Unit,
    onIssueToClient: () -> Unit,
    onReject: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ticket = state.ticket
    if (ticket == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Заявка не найдена",
                style = MaterialTheme.typography.headlineSmall
            )
            OutlinedButton(onClick = onBack) {
                Text("Назад к списку")
            }
        }
        return
    }

    val screenBg = screenBackground(ticket)
    val actionsLocked = ticket.isTerminal

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            Column {
                Text(
                    text = "Карточка заявки",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ticket.ticketNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        StatusBanner(ticket)
        Spacer(modifier = Modifier.height(12.dp))
        TicketInfoCard(ticket)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Свойства",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        PropertyRow("Сложность повреждения", "${ticket.severity} / 5")
        PropertyRow("Диагностика", yesNo(ticket.diagnosticsDone))
        PropertyRow("Запчасти на складе", yesNo(ticket.partsInStock))
        PropertyRow("Согласие клиента", yesNo(ticket.clientApproved))
        PropertyRow("Оплачено", yesNo(ticket.isPaid))
        PropertyRow("Гарантийный ремонт", yesNo(ticket.isWarranty))
        PropertyRow("Смета", if (ticket.estimateRub > 0) "${ticket.estimateRub} руб." else "-")

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Действия",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (ticket.status == TicketStatus.NEW) {
            SeverityControls(
                severity = ticket.severity,
                enabled = ticket.canChangeSeverity && !actionsLocked,
                onMinus = onDecreaseSeverity,
                onPlus = onIncreaseSeverity
            )
            Spacer(modifier = Modifier.height(8.dp))
            ActionButton(
                text = if (ticket.isWarranty) "Снять гарантию" else "Гарантийный ремонт",
                enabled = ticket.canToggleWarranty && !actionsLocked,
                onClick = onToggleWarranty
            )
        }

        ActionButton(
            text = "Начать диагностику",
            enabled = ticket.canStartDiagnostics && !actionsLocked,
            onClick = onStartDiagnostics
        )
        ActionButton(
            text = "Завершить диагностику",
            enabled = ticket.canFinishDiagnostics && !actionsLocked,
            onClick = onFinishDiagnostics
        )
        ActionButton(
            text = "Запчасти поступили",
            enabled = ticket.canMarkPartsArrived && !actionsLocked,
            onClick = onMarkPartsArrived
        )
        ActionButton(
            text = "Клиент согласовал смету",
            enabled = ticket.canApprove && !actionsLocked,
            onClick = onApproveByClient
        )
        ActionButton(
            text = "Оплатить ремонт",
            enabled = ticket.canPay && !actionsLocked,
            onClick = onPay
        )
        ActionButton(
            text = "Начать ремонт",
            enabled = ticket.canStartRepair && !actionsLocked,
            onClick = onStartRepair
        )
        ActionButton(
            text = "Завершить ремонт",
            enabled = ticket.canCompleteRepair && !actionsLocked,
            onClick = onCompleteRepair
        )
        ActionButton(
            text = "Выдать устройство",
            enabled = ticket.canIssue && !actionsLocked,
            onClick = onIssueToClient
        )
        ActionButton(
            text = "Отклонить заявку",
            enabled = ticket.canReject && !actionsLocked,
            onClick = onReject,
            containerColor = DangerRed
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить в исходное состояние")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatusBanner(ticket: RepairTicket) {
    val (bg, fg, message) = when {
        ticket.status == TicketStatus.WAITING_PARTS -> Triple(
            WarningAmber,
            Color.White,
            "Внимание: ремонт нельзя начать, пока нет запчастей, согласия клиента и оплаты (или гарантии)."
        )
        ticket.status == TicketStatus.DIAGNOSING && ticket.diagnosticsDone -> Triple(
            PrimaryBlueBanner,
            Color.White,
            "Запчасти уже на складе. Нужны согласие клиента и оплата (или гарантия)."
        )
        ticket.status == TicketStatus.READY -> Triple(
            ReadyGreen,
            Color.White,
            "Устройство отремонтировано и готово к выдаче."
        )
        ticket.status == TicketStatus.REJECTED -> Triple(
            DangerRed,
            Color.White,
            "Заявка отклонена. Доступно только действие сброса."
        )
        ticket.status == TicketStatus.ISSUED -> Triple(
            MutedGray,
            Color.White,
            "Работа по заявке завершена. Устройство выдано клиенту."
        )
        ticket.status == TicketStatus.REPAIRING -> Triple(
            PrimaryBlueBanner,
            Color.White,
            "Идёт ремонт. Изменение условий заявки заблокировано."
        )
        else -> Triple(
            Color(0xFFE2E8F0),
            Color(0xFF0F172A),
            ticket.statusTitle
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = ticket.statusTitle,
                color = fg,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                color = fg,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private val PrimaryBlueBanner = Color(0xFF1D4ED8)

@Composable
private fun TicketInfoCard(ticket: RepairTicket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = ticket.ticketNumber,
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Text(
                text = ticket.deviceName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SeverityControls(
    severity: Int,
    enabled: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Сложность повреждения")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(onClick = onMinus, enabled = enabled && severity > 1) {
                Text("-")
            }
            Text(
                text = "$severity",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onPlus, enabled = enabled && severity < 5) {
                Text("+")
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = Color(0xFFCBD5E1),
            disabledContentColor = Color(0xFF64748B)
        )
    ) {
        Text(text)
    }
}

private fun screenBackground(ticket: RepairTicket): Color {
    return when {
        ticket.status == TicketStatus.REJECTED -> Color(0xFFFEE2E2)
        ticket.status == TicketStatus.READY -> Color(0xFFDCFCE7)
        ticket.status == TicketStatus.WAITING_PARTS -> Color(0xFFFEF3C7)
        ticket.status == TicketStatus.ISSUED -> Color(0xFFE5E7EB)
        ticket.status == TicketStatus.REPAIRING -> Color(0xFFDBEAFE)
        ticket.status == TicketStatus.DIAGNOSING && ticket.diagnosticsDone -> Color(0xFFDBEAFE)
        else -> Color(0xFFF8FAFC)
    }
}

private fun yesNo(value: Boolean): String = if (value) "да" else "нет"
