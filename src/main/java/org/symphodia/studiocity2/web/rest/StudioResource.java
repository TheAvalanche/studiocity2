package org.symphodia.studiocity2.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.symphodia.studiocity2.domain.Studio;
import org.symphodia.studiocity2.service.StudioService;
import org.symphodia.studiocity2.web.rest.util.HeaderUtil;
import org.symphodia.studiocity2.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing Studio.
 */
@RestController
@RequestMapping("/api")
public class StudioResource {

    private final Logger log = LoggerFactory.getLogger(StudioResource.class);
        
    @Inject
    private StudioService studioService;
    
    /**
     * POST  /studios : Create a new studio.
     *
     * @param studio the studio to create
     * @return the ResponseEntity with status 201 (Created) and with body the new studio, or with status 400 (Bad Request) if the studio has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/studios",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Studio> createStudio(@Valid @RequestBody Studio studio) throws URISyntaxException {
        log.debug("REST request to save Studio : {}", studio);
        if (studio.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("studio", "idexists", "A new studio cannot already have an ID")).body(null);
        }
        Studio result = studioService.save(studio);
        return ResponseEntity.created(new URI("/api/studios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("studio", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /studios : Updates an existing studio.
     *
     * @param studio the studio to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated studio,
     * or with status 400 (Bad Request) if the studio is not valid,
     * or with status 500 (Internal Server Error) if the studio couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/studios",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Studio> updateStudio(@Valid @RequestBody Studio studio) throws URISyntaxException {
        log.debug("REST request to update Studio : {}", studio);
        if (studio.getId() == null) {
            return createStudio(studio);
        }
        Studio result = studioService.save(studio);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("studio", studio.getId().toString()))
            .body(result);
    }

    /**
     * GET  /studios : get all the studios.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of studios in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/studios",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Studio>> getAllStudios(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Studios");
        Page<Studio> page = studioService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/studios");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /studios/:id : get the "id" studio.
     *
     * @param id the id of the studio to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the studio, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/studios/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Studio> getStudio(@PathVariable Long id) {
        log.debug("REST request to get Studio : {}", id);
        Studio studio = studioService.findOne(id);
        return Optional.ofNullable(studio)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /studios/:id : delete the "id" studio.
     *
     * @param id the id of the studio to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/studios/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStudio(@PathVariable Long id) {
        log.debug("REST request to delete Studio : {}", id);
        studioService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("studio", id.toString())).build();
    }

    /**
     * SEARCH  /_search/studios?query=:query : search for the studio corresponding
     * to the query.
     *
     * @param query the query of the studio search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/studios",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Studio>> searchStudios(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Studios for query {}", query);
        Page<Studio> page = studioService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/studios");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
