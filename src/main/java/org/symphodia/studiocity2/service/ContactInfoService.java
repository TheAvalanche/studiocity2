package org.symphodia.studiocity2.service;

import org.symphodia.studiocity2.domain.ContactInfo;
import org.symphodia.studiocity2.repository.ContactInfoRepository;
import org.symphodia.studiocity2.repository.search.ContactInfoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ContactInfo.
 */
@Service
@Transactional
public class ContactInfoService {

    private final Logger log = LoggerFactory.getLogger(ContactInfoService.class);
    
    @Inject
    private ContactInfoRepository contactInfoRepository;
    
    @Inject
    private ContactInfoSearchRepository contactInfoSearchRepository;
    
    /**
     * Save a contactInfo.
     * 
     * @param contactInfo the entity to save
     * @return the persisted entity
     */
    public ContactInfo save(ContactInfo contactInfo) {
        log.debug("Request to save ContactInfo : {}", contactInfo);
        ContactInfo result = contactInfoRepository.save(contactInfo);
        contactInfoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the contactInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContactInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ContactInfos");
        Page<ContactInfo> result = contactInfoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one contactInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContactInfo findOne(Long id) {
        log.debug("Request to get ContactInfo : {}", id);
        ContactInfo contactInfo = contactInfoRepository.findOne(id);
        return contactInfo;
    }

    /**
     *  Delete the  contactInfo by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContactInfo : {}", id);
        contactInfoRepository.delete(id);
        contactInfoSearchRepository.delete(id);
    }

    /**
     * Search for the contactInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContactInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContactInfos for query {}", query);
        return contactInfoSearchRepository.search(queryStringQuery(query), pageable);
    }
}
