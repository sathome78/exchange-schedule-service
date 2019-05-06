package me.exrates.scheduleservice.services;

import me.exrates.scheduleservice.models.dto.ExternalWalletBalancesDto;
import me.exrates.scheduleservice.models.dto.InternalWalletBalancesDto;

import java.util.List;

public interface WalletService {

    List<ExternalWalletBalancesDto> getExternalWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    void updateExternalMainWalletBalances();

    void updateExternalReservedWalletBalances();

    void updateInternalWalletBalances();

    List<InternalWalletBalancesDto> getWalletBalances();
}