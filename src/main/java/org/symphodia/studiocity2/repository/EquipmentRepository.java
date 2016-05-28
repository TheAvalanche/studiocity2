package org.symphodia.studiocity2.repository;

import org.symphodia.studiocity2.domain.Equipment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Equipment entity.
 */
@SuppressWarnings("unused")
public interface EquipmentRepository extends JpaRepository<Equipment,Long> {

}
