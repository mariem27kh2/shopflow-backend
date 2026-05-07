package com.shopflow.shopflow.repository;

import com.shopflow.shopflow.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository; // interface de repository pour l'entité Address, permettant d'effectuer des opérations CRUD sur la table des adresses dans la base de données
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}