package com.assetneuron.whatsapp.repository;

import com.assetneuron.whatsapp.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByWorkOrderIdAndTenantId(UUID workOrderId, UUID tenantId);

    @EntityGraph(attributePaths = {"checklistItems"})
    Optional<Task> findByIdAndTenantId(UUID id, UUID tenantId);

    @EntityGraph(attributePaths = {"checklistItems"})
    @Query("SELECT t FROM Task t " +
            "JOIN t.workOrder wo " +
            "WHERE t.id = :taskId " +
            "AND wo.id = :workOrderId " +
            "AND t.tenantId = :tenantId")
    Optional<Task> findByIdAndWorkOrderIdAndTenantId(
            @Param("taskId") UUID taskId,
            @Param("workOrderId") UUID workOrderId,
            @Param("tenantId") UUID tenantId);

}

