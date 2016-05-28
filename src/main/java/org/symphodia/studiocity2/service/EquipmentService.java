package org.symphodia.studiocity2.service;

import org.symphodia.studiocity2.domain.Equipment;
import org.symphodia.studiocity2.repository.EquipmentRepository;
import org.symphodia.studiocity2.repository.search.EquipmentSearchRepository;
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
 * Service Implementation for managing Equipment.
 */
@Service
@Transactional
public class EquipmentService {

    private final Logger log = LoggerFactory.getLogger(EquipmentService.class);
    
    @Inject
    private EquipmentRepository equipmentRepository;
    
    @Inject
    private EquipmentSearchRepository equipmentSearchRepository;
    
    /**
     * Save a equipment.
     * 
     * @param equipment the entity to save
     * @return the persisted entity
     */
    public Equipment save(Equipment equipment) {
        log.debug("Request to save Equipment : {}", equipment);
        Equipment result = equipmentRepository.save(equipment);
        equipmentSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the equipment.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Equipment> findAll(Pageable pageable) {
        log.debug("Request to get all Equipment");
        Page<Equipment> result = equipmentRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one equipment by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Equipment findOne(Long id) {
        log.debug("Request to get Equipment : {}", id);
        Equipment equipment = equipmentRepository.findOne(id);
        return equipment;
    }

    /**
     *  Delete the  equipment by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Equipment : {}", id);
        equipmentRepository.delete(id);
        equipmentSearchRepository.delete(id);
    }

    /**
     * Search for the equipment corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Equipment> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Equipment for query {}", query);
        return equipmentSearchRepository.search(queryStringQuery(query), pageable);
    }
}
