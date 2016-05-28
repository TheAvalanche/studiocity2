package org.symphodia.studiocity2.repository;

import org.symphodia.studiocity2.domain.Studio;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Studio entity.
 */
@SuppressWarnings("unused")
public interface StudioRepository extends JpaRepository<Studio,Long> {

}
