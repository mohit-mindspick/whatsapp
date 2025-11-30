package com.assetneuron.whatsapp.repository;

import com.assetneuron.whatsapp.model.WorkOrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPart, UUID> {

    List<WorkOrderPart> findByWorkOrderIdAndTenantId(UUID workOrderId, UUID tenantId);

    void deleteByWorkOrderIdAndTenantId(UUID workOrderId, UUID tenantId);

    List<WorkOrderPart> findByWorkOrderIdAndPartCodeAndTenantId(UUID workOrderId, String partCode, UUID tenantId);

}
