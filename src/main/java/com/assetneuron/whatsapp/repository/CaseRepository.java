package com.assetneuron.whatsapp.repository;

import com.assetneuron.whatsapp.dto.CaseDTO;
import com.assetneuron.whatsapp.model.Cases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<Cases, UUID> {

    @Query("SELECT new com.assetneuron.whatsapp.dto.CaseDTO(" +
            "c.id, " +
            "c.caseCode, " +
            "c.title, " +
            "c.severity" +
            ") " +
            "FROM Cases c " +
            "WHERE c.assignedTo = :phoneNumber " +
            "AND c.isDeleted = false " +
            "AND c.reportedDate >= :startDate " +
            "AND c.reportedDate <= :endDate " +
            "ORDER BY " +
            "CASE c.severity " +
            "WHEN 'CRITICAL' THEN 1 " +
            "WHEN 'HIGH' THEN 2 " +
            "WHEN 'MEDIUM' THEN 3 " +
            "WHEN 'LOW' THEN 4 " +
            "ELSE 5 END ASC")
    List<CaseDTO> findCasesByUserPhoneNumberAndTimeRange(
            @Param("phoneNumber") String phoneNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    Optional<Cases> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

}

