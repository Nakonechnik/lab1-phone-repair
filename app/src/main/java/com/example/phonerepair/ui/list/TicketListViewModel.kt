package com.example.phonerepair.ui.list

import androidx.lifecycle.ViewModel
import com.example.phonerepair.data.RepairRepository
import com.example.phonerepair.model.RepairTicket

data class TicketListUiState(
    val tickets: List<RepairTicket> = emptyList()
)

class TicketListViewModel(
    private val repo: RepairRepository
) : ViewModel() {
    val uiState: TicketListUiState
        get() = TicketListUiState(tickets = repo.tickets)

    fun onAdd(deviceName: String) = repo.addTicket(deviceName)

    fun onDelete(id: String) = repo.removeTicket(id)
}
