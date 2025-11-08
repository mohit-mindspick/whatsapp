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

    Optional<User> findByPhoneNumber(String phoneNumber);
    
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.isDeleted = false AND u.enabled = true")
    Optional<User> findByPhoneNumberAndActive(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT new com.assetneuron.whatsapp.dto.UserDTO(" +
          "u.firstName, " +
          "u.lastName, " +
          "(SELECT MIN(t.name) FROM Team t JOIN t.users tu WHERE tu.id = u.id AND t.active = true)" +
          ") " +
          "FROM User u " +
          "WHERE u.phoneNumber = :phoneNumber " +
          "AND u.isDeleted = false " +
          "AND u.enabled = true")
    java.util.List<UserDTO> findUserWithTeamByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}

