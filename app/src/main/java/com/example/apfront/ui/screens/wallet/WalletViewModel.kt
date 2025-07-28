package com.example.apfront.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.TopUpRequest
import com.example.apfront.data.remote.dto.TransactionDto
import com.example.apfront.data.remote.dto.UserDto
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.data.repository.PaymentRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class WalletUiState(
    val isLoading: Boolean = false,
    val profile: UserDto? = null,
    val transactions: List<TransactionDto> = emptyList(),
    val calculatedBalance: BigDecimal = BigDecimal.ZERO,
    val error: String? = null,
    val topUpSuccess: Boolean = false
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    fun loadWalletData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, topUpSuccess = false) }
            val token = sessionManager.getAuthToken() ?: return@launch

            val profileResult = authRepository.getProfile(token)
            val transactionsResult = paymentRepository.getTransactions(token)

            if (profileResult is Resource.Success && transactionsResult is Resource.Success) {
                val transactions = transactionsResult.data ?: emptyList()
                val balance = calculateBalance(transactions)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = profileResult.data,
                        transactions = transactions,
                        calculatedBalance = balance
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load wallet data.") }
            }
        }
    }

    // --- THIS IS THE FIX ---
    private fun calculateBalance(transactions: List<TransactionDto>): BigDecimal {
        var balance = BigDecimal.ZERO
        for (tx in transactions) {
            // Only include transactions that were successful in the calculation.
            if (tx.status.equals("SUCCESS", ignoreCase = true)) {
                if (tx.type.equals("TOP_UP", ignoreCase = true)) {
                    balance += tx.amount
                } else if (tx.type.equals("PAYMENT", ignoreCase = true)) {
                    // **Crucial Check:** Only subtract from the balance if the payment method was WALLET.
                    if (tx.method.equals("WALLET", ignoreCase = true)) {
                        balance -= tx.amount
                    }
                }
            }
        }
        return balance
    }
    // --- END OF FIX ---

    fun topUp(amount: BigDecimal) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val result = paymentRepository.topUpWallet(token, TopUpRequest(amount))
            if (result is Resource.Success) {
                loadWalletData() // Refresh all data after successful top-up
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun refresh() {
        loadWalletData()
    }
}