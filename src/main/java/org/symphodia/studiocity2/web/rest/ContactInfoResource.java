package org.symphodia.studiocity2.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.symphodia.studiocity2.domain.ContactInfo;
import org.symphodia.studiocity2.service.ContactInfoService;
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
 * REST controller for managing ContactInfo.
 */
@RestController
@RequestMapping("/api")
public class ContactInfoResource {

    private final Logger log = LoggerFactory.getLogger(ContactInfoResource.class);
        
    @Inject
    private ContactInfoService contactInfoService;
    
    /**
     * POST  /contact-infos : Create a new contactInfo.
     *
     * @param contactInfo the contactInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contactInfo, or with status 400 (Bad Request) if the contactInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/contact-infos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ContactInfo> createContactInfo(@Valid @RequestBody ContactInfo contactInfo) throws URISyntaxException {
        log.debug("REST request to save ContactInfo : {}", contactInfo);
        if (contactInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contactInfo", "idexists", "A new contactInfo cannot already have an ID")).body(null);
        }
        ContactInfo result = contactInfoService.save(contactInfo);
        return ResponseEntity.created(new URI("/api/contact-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contactInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contact-infos : Updates an existing contactInfo.
     *
     * @param contactInfo the contactInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contactInfo,
     * or with status 400 (Bad Request) if the contactInfo is not valid,
     * or with status 500 (Internal Server Error) if the contactInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/contact-infos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ContactInfo> updateContactInfo(@Valid @RequestBody ContactInfo contactInfo) throws URISyntaxException {
        log.debug("REST request to update ContactInfo : {}", contactInfo);
        if (contactInfo.getId() == null) {
            return createContactInfo(contactInfo);
        }
        ContactInfo result = contactInfoService.save(contactInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contactInfo", contactInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contact-infos : get all the contactInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contactInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/contact-infos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ContactInfo>> getAllContactInfos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContactInfos");
        Page<ContactInfo> page = contactInfoService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contact-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contact-infos/:id : get the "id" contactInfo.
     *
     * @param id the id of the contactInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contactInfo, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/contact-infos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ContactInfo> getContactInfo(@PathVariable Long id) {
        log.debug("REST request to get ContactInfo : {}", id);
        ContactInfo contactInfo = contactInfoService.findOne(id);
        return Optional.ofNullable(contactInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contact-infos/:id : delete the "id" contactInfo.
     *
     * @param id the id of the contactInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/contact-infos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteContactInfo(@PathVariable Long id) {
        log.debug("REST request to delete ContactInfo : {}", id);
        contactInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contactInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contact-infos?query=:query : search for the contactInfo corresponding
     * to the query.
     *
     * @param query the query of the contactInfo search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/contact-infos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ContactInfo>> searchContactInfos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContactInfos for query {}", query);
        Page<ContactInfo> page = contactInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contact-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
