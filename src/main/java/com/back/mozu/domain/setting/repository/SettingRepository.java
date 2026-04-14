package com.back.mozu.domain.setting.repository;

import com.back.mozu.domain.setting.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Integer> {
}
