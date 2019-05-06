package me.exrates.scheduleservice.repositories;

import me.exrates.scheduleservice.models.dto.ExternalWalletBalancesDto;
import me.exrates.scheduleservice.models.dto.InternalWalletBalancesDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WalletDao {

    List<ExternalWalletBalancesDto> getExternalMainWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    void updateExternalMainWalletBalances(ExternalWalletBalancesDto externalWalletBalancesDto);

    void updateInternalWalletBalances(InternalWalletBalancesDto internalWalletBalancesDto);

    List<InternalWalletBalancesDto> getWalletBalances();

    void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance, LocalDateTime lastReservedBalanceUpdate);
}