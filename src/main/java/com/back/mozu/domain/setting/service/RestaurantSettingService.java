package com.back.mozu.domain.setting.service;

import com.back.mozu.domain.setting.dto.SettingUpdateRequestDto;
import com.back.mozu.domain.setting.dto.SettingUpdateResponseDto;
import com.back.mozu.domain.setting.entity.RestaurantSettings;
import com.back.mozu.domain.setting.repository.RestaurantSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class RestaurantSettingService {

    private final RestaurantSettingRepository restaurantSettingRepository;

    @Transactional
    public SettingUpdateResponseDto updateSettings(SettingUpdateRequestDto dto) {
        RestaurantSettings settings = restaurantSettingRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("설정값을 찾을 수 없습니다"));
        settings.update(dto.getTotalTables(), dto.getOpeningTime(), dto.getClosingTime());
        restaurantSettingRepository.save(settings);
        return SettingUpdateResponseDto.builder()
                .totalTables(settings.getTotalTables())
                .openingTime(settings.getOpeningTime())
                .closingTime(settings.getClosingTime())
                .build();
    }

    @Transactional
    public void setup(Integer totalTables, LocalTime openingTime, LocalTime closingTime) {
        if (restaurantSettingRepository.count() > 0) return;
        restaurantSettingRepository.save(
                RestaurantSettings.builder()
                        .totalTables(totalTables)
                        .openingTime(openingTime)
                        .closingTime(closingTime)
                        .build()
        );
    }
}
