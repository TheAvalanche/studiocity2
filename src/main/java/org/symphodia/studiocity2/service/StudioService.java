package org.symphodia.studiocity2.service;

import org.symphodia.studiocity2.domain.Studio;
import org.symphodia.studiocity2.repository.StudioRepository;
import org.symphodia.studiocity2.repository.search.StudioSearchRepository;
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
 * Service Implementation for managing Studio.
 */
@Service
@Transactional
public class StudioService {

    private final Logger log = LoggerFactory.getLogger(StudioService.class);
    
    @Inject
    private StudioRepository studioRepository;
    
    @Inject
    private StudioSearchRepository studioSearchRepository;
    
    /**
     * Save a studio.
     * 
     * @param studio the entity to save
     * @return the persisted entity
     */
    public Studio save(Studio studio) {
        log.debug("Request to save Studio : {}", studio);
        Studio result = studioRepository.save(studio);
        studioSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the studios.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Studio> findAll(Pageable pageable) {
        log.debug("Request to get all Studios");
        Page<Studio> result = studioRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one studio by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Studio findOne(Long id) {
        log.debug("Request to get Studio : {}", id);
        Studio studio = studioRepository.findOne(id);
        return studio;
    }

    /**
     *  Delete the  studio by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Studio : {}", id);
        studioRepository.delete(id);
        studioSearchRepository.delete(id);
    }

    /**
     * Search for the studio corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Studio> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Studios for query {}", query);
        return studioSearchRepository.search(queryStringQuery(query), pageable);
    }
}
