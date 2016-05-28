package org.symphodia.studiocity2.repository;

import org.symphodia.studiocity2.domain.ContactInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContactInfo entity.
 */
@SuppressWarnings("unused")
public interface ContactInfoRepository extends JpaRepository<ContactInfo,Long> {

}
