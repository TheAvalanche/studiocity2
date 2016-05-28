package org.symphodia.studiocity2.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.symphodia.studiocity2.domain.Equipment;
import org.symphodia.studiocity2.repository.EquipmentRepository;
import org.symphodia.studiocity2.repository.search.EquipmentSearchRepository;
import org.symphodia.studiocity2.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Equipment.
 */
@RestController
@RequestMapping("/api")
public class EquipmentResource {

    private final Logger log = LoggerFactory.getLogger(EquipmentResource.class);
        
    @Inject
    private EquipmentRepository equipmentRepository;
    
    @Inject
    private EquipmentSearchRepository equipmentSearchRepository;
    
    /**
     * POST  /equipment : Create a new equipment.
     *
     * @param equipment the equipment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new equipment, or with status 400 (Bad Request) if the equipment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/equipment",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Equipment> createEquipment(@Valid @RequestBody Equipment equipment) throws URISyntaxException {
        log.debug("REST request to save Equipment : {}", equipment);
        if (equipment.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("equipment", "idexists", "A new equipment cannot already have an ID")).body(null);
        }
        Equipment result = equipmentRepository.save(equipment);
        equipmentSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/equipment/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("equipment", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /equipment : Updates an existing equipment.
     *
     * @param equipment the equipment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated equipment,
     * or with status 400 (Bad Request) if the equipment is not valid,
     * or with status 500 (Internal Server Error) if the equipment couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/equipment",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Equipment> updateEquipment(@Valid @RequestBody Equipment equipment) throws URISyntaxException {
        log.debug("REST request to update Equipment : {}", equipment);
        if (equipment.getId() == null) {
            return createEquipment(equipment);
        }
        Equipment result = equipmentRepository.save(equipment);
        equipmentSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("equipment", equipment.getId().toString()))
            .body(result);
    }

    /**
     * GET  /equipment : get all the equipment.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of equipment in body
     */
    @RequestMapping(value = "/equipment",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Equipment> getAllEquipment() {
        log.debug("REST request to get all Equipment");
        List<Equipment> equipment = equipmentRepository.findAll();
        return equipment;
    }

    /**
     * GET  /equipment/:id : get the "id" equipment.
     *
     * @param id the id of the equipment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the equipment, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/equipment/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Equipment> getEquipment(@PathVariable Long id) {
        log.debug("REST request to get Equipment : {}", id);
        Equipment equipment = equipmentRepository.findOne(id);
        return Optional.ofNullable(equipment)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /equipment/:id : delete the "id" equipment.
     *
     * @param id the id of the equipment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/equipment/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        log.debug("REST request to delete Equipment : {}", id);
        equipmentRepository.delete(id);
        equipmentSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("equipment", id.toString())).build();
    }

    /**
     * SEARCH  /_search/equipment?query=:query : search for the equipment corresponding
     * to the query.
     *
     * @param query the query of the equipment search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/equipment",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Equipment> searchEquipment(@RequestParam String query) {
        log.debug("REST request to search Equipment for query {}", query);
        return StreamSupport
            .stream(equipmentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
