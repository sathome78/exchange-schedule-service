package me.exrates.scheduleservice.repositories;

import me.exrates.scheduleservice.models.dto.MerchantCurrencyOptionsDto;

import java.util.List;

public interface MerchantDao {

    List<MerchantCurrencyOptionsDto> getAllMerchantCommissionsLimits();

    void updateMerchantCommissionsLimits(List<MerchantCurrencyOptionsDto> merchantCommissionsLimits);
}
