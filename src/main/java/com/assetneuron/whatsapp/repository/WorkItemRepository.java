package com.assetneuron.whatsapp.repository;

import com.assetneuron.whatsapp.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkOrder, UUID> {

    @Query(value = "SELECT id, title, code, importance, type FROM " +
            "(SELECT wo.id, wo.title, wo.code, wo.priority as importance, 'WORKORDER' as type FROM public.wo_work_order wo " +
            "INNER JOIN public.users u ON wo.assigned_to = u.id " +
            "WHERE u.phone_number = :phoneNumber " +
            "AND wo.created_at >= :startDate " +
            "AND wo.created_at <= :endDate " +
            "UNION ALL " +
            "SELECT c.id, c.title, c.case_code as code, CAST(c.severity AS VARCHAR) as importance, 'CASE' as type FROM public.cases c " +
            "WHERE c.assigned_to = :phoneNumber " +
            "AND c.is_deleted = false " +
            "AND c.reported_date >= :startDate " +
            "AND c.reported_date <= :endDate) AS mywork " +
            "ORDER BY " +
            "  CASE COALESCE(importance, '') " +
            "    WHEN 'CRITICAL' THEN 1" +
            "    WHEN 'HIGH' THEN 2" +
            "    WHEN 'MEDIUM' THEN 3" +
            "    WHEN 'LOW' THEN 4" +
            "    ELSE 5" +
            "  END ASC", nativeQuery = true)
    List<Object[]> findMyWorkByUserPhoneNumberAndTimeRange(
            @Param("phoneNumber") String phoneNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

