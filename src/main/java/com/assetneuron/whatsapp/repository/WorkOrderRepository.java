package com.assetneuron.whatsapp.repository;

import com.assetneuron.whatsapp.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    @Query(value = "SELECT id, title, code, importance, type FROM " +
            "(SELECT wo.id, wo.title, wo.code, wo.priority as importance, 'WORKORDER' as type FROM public.wo_work_order wo " +
            "INNER JOIN public.users u ON wo.assigned_to = u.id " +
            "WHERE u.phone_number = :phoneNumber " +
            "AND u.tenant_id = :tenantId " +
            "AND wo.tenant_id = :tenantId " +
            "AND DATE(wo.due_date) <= :dueDate " +
            "AND UPPER(wo.status) NOT IN ('COMPLETED', 'CLOSED', 'CANCELLED') " +
            "UNION ALL " +
            "SELECT c.id, c.title, c.case_code as code, CAST(c.severity AS VARCHAR) as importance, 'CASE' as type FROM public.cases c " +
            "INNER JOIN public.users u ON c.assigned_to = u.id " +
            "INNER JOIN public.case_statuses cs ON c.status_id = cs.id " +
            "WHERE u.phone_number = :phoneNumber " +
            "AND u.tenant_id = :tenantId " +
            "AND c.tenant_id = :tenantId " +
            "AND c.is_deleted = false " +
            "AND UPPER(cs.code) NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED')" +
            "AND DATE(c.due_by) <= :dueDate) AS mywork " +
            "ORDER BY " +
            "  CASE COALESCE(importance, '') " +
            "    WHEN 'CRITICAL' THEN 1" +
            "    WHEN 'HIGH' THEN 2" +
            "    WHEN 'MEDIUM' THEN 3" +
            "    WHEN 'LOW' THEN 4" +
            "    ELSE 5" +
            "  END ASC", nativeQuery = true)
    List<Object[]> findMyWorkByUserPhoneNumberAndDueDate(
            @Param("phoneNumber") String phoneNumber,
            @Param("dueDate") LocalDate dueDate,
            @Param("tenantId") UUID tenantId);

    Optional<WorkOrder> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

}

