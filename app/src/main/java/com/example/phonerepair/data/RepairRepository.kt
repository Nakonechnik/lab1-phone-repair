package com.example.phonerepair.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.phonerepair.model.RepairTicket
import com.example.phonerepair.model.TicketStatus

class RepairRepository {
    var tickets by mutableStateOf(sampleTickets())
        private set

    private var nextNumber = 2413

    fun ticket(id: String): RepairTicket? = tickets.find { it.id == id }

    fun addTicket() {
        val number = "SC-$nextNumber"
        nextNumber += 1
        tickets = tickets + RepairTicket(
            id = number,
            ticketNumber = number,
            deviceName = "Новое устройство"
        )
    }

    fun removeTicket(id: String) {
        tickets = tickets.filterNot { it.id == id }
    }

    fun startDiagnostics(id: String) = update(id) { ticket ->
        if (!ticket.canStartDiagnostics) ticket
        else ticket.copy(status = TicketStatus.DIAGNOSING)
    }

    fun finishDiagnostics(id: String) = update(id) { ticket ->
        if (!ticket.canFinishDiagnostics) ticket
        else {
            val estimate = ticket.severity * 2500
            val partsAvailable = ticket.severity <= 2
            ticket.copy(
                diagnosticsDone = true,
                estimateRub = estimate,
                partsInStock = partsAvailable,
                status = if (partsAvailable) {
                    TicketStatus.DIAGNOSING
                } else {
                    TicketStatus.WAITING_PARTS
                }
            )
        }
    }

    fun markPartsArrived(id: String) = update(id) { ticket ->
        if (!ticket.canMarkPartsArrived) ticket
        else ticket.copy(partsInStock = true)
    }

    fun approveByClient(id: String) = update(id) { ticket ->
        if (!ticket.canApprove) ticket
        else ticket.copy(clientApproved = true)
    }

    fun pay(id: String) = update(id) { ticket ->
        if (!ticket.canPay) ticket
        else ticket.copy(isPaid = true)
    }

    fun toggleWarranty(id: String) = update(id) { ticket ->
        if (!ticket.canToggleWarranty) ticket
        else ticket.copy(isWarranty = !ticket.isWarranty)
    }

    fun increaseSeverity(id: String) = update(id) { ticket ->
        if (!ticket.canChangeSeverity || ticket.severity >= 5) ticket
        else ticket.copy(severity = ticket.severity + 1)
    }

    fun decreaseSeverity(id: String) = update(id) { ticket ->
        if (!ticket.canChangeSeverity || ticket.severity <= 1) ticket
        else ticket.copy(severity = ticket.severity - 1)
    }

    fun startRepair(id: String) = update(id) { ticket ->
        if (!ticket.canStartRepair) ticket
        else ticket.copy(status = TicketStatus.REPAIRING)
    }

    fun completeRepair(id: String) = update(id) { ticket ->
        if (!ticket.canCompleteRepair) ticket
        else ticket.copy(status = TicketStatus.READY)
    }

    fun issueToClient(id: String) = update(id) { ticket ->
        if (!ticket.canIssue) ticket
        else ticket.copy(status = TicketStatus.ISSUED)
    }

    fun reject(id: String) = update(id) { ticket ->
        if (!ticket.canReject) ticket
        else ticket.copy(status = TicketStatus.REJECTED)
    }

    fun reset(id: String) = update(id) { ticket ->
        RepairTicket(
            id = ticket.id,
            ticketNumber = ticket.ticketNumber,
            deviceName = ticket.deviceName
        )
    }

    private fun update(id: String, transform: (RepairTicket) -> RepairTicket) {
        tickets = tickets.map { ticket ->
            if (ticket.id == id) transform(ticket) else ticket
        }
    }
}

fun sampleTickets(): List<RepairTicket> = listOf(
    RepairTicket(
        id = "SC-2401",
        ticketNumber = "SC-2401",
        deviceName = "Samsung Galaxy S23",
        status = TicketStatus.NEW,
        severity = 2
    ),
    RepairTicket(
        id = "SC-2402",
        ticketNumber = "SC-2402",
        deviceName = "iPhone 14",
        status = TicketStatus.DIAGNOSING,
        severity = 3
    ),
    RepairTicket(
        id = "SC-2403",
        ticketNumber = "SC-2403",
        deviceName = "Xiaomi 13",
        status = TicketStatus.DIAGNOSING,
        severity = 2,
        diagnosticsDone = true,
        partsInStock = true,
        estimateRub = 5000
    ),
    RepairTicket(
        id = "SC-2404",
        ticketNumber = "SC-2404",
        deviceName = "Pixel 7",
        status = TicketStatus.WAITING_PARTS,
        severity = 4,
        diagnosticsDone = true,
        estimateRub = 10000
    ),
    RepairTicket(
        id = "SC-2405",
        ticketNumber = "SC-2405",
        deviceName = "Huawei P50",
        status = TicketStatus.WAITING_PARTS,
        severity = 5,
        diagnosticsDone = true,
        partsInStock = true,
        clientApproved = true,
        estimateRub = 12500
    ),
    RepairTicket(
        id = "SC-2406",
        ticketNumber = "SC-2406",
        deviceName = "OnePlus 11",
        status = TicketStatus.REPAIRING,
        severity = 3,
        diagnosticsDone = true,
        partsInStock = true,
        clientApproved = true,
        isPaid = true,
        estimateRub = 7500
    ),
    RepairTicket(
        id = "SC-2407",
        ticketNumber = "SC-2407",
        deviceName = "Realme GT",
        status = TicketStatus.READY,
        severity = 2,
        diagnosticsDone = true,
        partsInStock = true,
        clientApproved = true,
        isPaid = true,
        estimateRub = 5000
    ),
    RepairTicket(
        id = "SC-2408",
        ticketNumber = "SC-2408",
        deviceName = "Motorola Edge",
        status = TicketStatus.ISSUED,
        severity = 1,
        diagnosticsDone = true,
        partsInStock = true,
        clientApproved = true,
        isWarranty = true,
        estimateRub = 2500
    ),
    RepairTicket(
        id = "SC-2409",
        ticketNumber = "SC-2409",
        deviceName = "Sony Xperia 5",
        status = TicketStatus.REJECTED,
        severity = 5,
        diagnosticsDone = true,
        estimateRub = 12500
    ),
    RepairTicket(
        id = "SC-2410",
        ticketNumber = "SC-2410",
        deviceName = "Nokia G22",
        status = TicketStatus.NEW,
        severity = 1,
        isWarranty = true
    ),
    RepairTicket(
        id = "SC-2411",
        ticketNumber = "SC-2411",
        deviceName = "Honor Magic 5",
        status = TicketStatus.REPAIRING,
        severity = 2,
        diagnosticsDone = true,
        partsInStock = true,
        clientApproved = true,
        isWarranty = true,
        estimateRub = 5000
    ),
    RepairTicket(
        id = "SC-2412",
        ticketNumber = "SC-2412",
        deviceName = "Asus Zenfone 10",
        status = TicketStatus.READY,
        severity = 3,
        diagnosticsDone = true,
        partsInStock = true,
        clientApproved = true,
        isPaid = true,
        estimateRub = 7500
    )
)
