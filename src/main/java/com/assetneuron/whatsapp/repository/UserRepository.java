package com.assetneuron.whatsapp.repository;

import com.assetneuron.whatsapp.dto.UserDTO;
import com.assetneuron.whatsapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.tenantId = :tenantId")
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("tenantId") UUID tenantId);
    
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.tenantId = :tenantId AND u.isDeleted = false AND u.enabled = true")
    Optional<User> findByPhoneNumberAndActive(@Param("phoneNumber") String phoneNumber, @Param("tenantId") UUID tenantId);
    
    @Query("SELECT new com.assetneuron.whatsapp.dto.UserDTO(" +
          "u.firstName, " +
          "u.lastName, " +
          "(SELECT MIN(t.name) FROM Team t JOIN t.users tu WHERE tu.id = u.id AND t.active = true AND t.tenantId = :tenantId)" +
          ") " +
          "FROM User u " +
          "WHERE u.phoneNumber = :phoneNumber " +
          "AND u.tenantId = :tenantId " +
          "AND u.isDeleted = false " +
          "AND u.enabled = true")
    java.util.List<UserDTO> findUserWithTeamByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("tenantId") UUID tenantId);
    
    @Query("SELECT u FROM User u " +
          "WHERE u.shift.id = :shiftId " +
          "AND u.tenantId = :tenantId " +
          "AND u.shift.tenantId = :tenantId " +
          "AND u.isShiftSupervisor = true " +
          "AND u.isDeleted = false " +
          "AND u.enabled = true " +
          "ORDER BY u.createdAt ASC")
    java.util.List<User> findSupervisorsByShiftId(@Param("shiftId") UUID shiftId, @Param("tenantId") UUID tenantId);
}

