package com.assetneuron.whatsapp.common.service;

import com.assetneuron.whatsapp.common.model.TenantSettings;
import com.assetneuron.whatsapp.common.repository.TenantSettingsRepository;
import com.assetneuron.whatsapp.config.ReadOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantSettingsService {

    private final TenantSettingsRepository tenantSettingsRepository;

    @ReadOnly
    public TenantSettings getTenantSettingsByTenantId(UUID tenantId) {
        return tenantSettingsRepository.findByTenantIdAndIsDeletedFalse(tenantId)
                .orElse(null);
    }
}

