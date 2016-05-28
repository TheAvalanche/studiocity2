package org.symphodia.studiocity2.service;

import org.symphodia.studiocity2.domain.Room;
import org.symphodia.studiocity2.repository.RoomRepository;
import org.symphodia.studiocity2.repository.search.RoomSearchRepository;
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
 * Service Implementation for managing Room.
 */
@Service
@Transactional
public class RoomService {

    private final Logger log = LoggerFactory.getLogger(RoomService.class);
    
    @Inject
    private RoomRepository roomRepository;
    
    @Inject
    private RoomSearchRepository roomSearchRepository;
    
    /**
     * Save a room.
     * 
     * @param room the entity to save
     * @return the persisted entity
     */
    public Room save(Room room) {
        log.debug("Request to save Room : {}", room);
        Room result = roomRepository.save(room);
        roomSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the rooms.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Room> findAll(Pageable pageable) {
        log.debug("Request to get all Rooms");
        Page<Room> result = roomRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one room by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Room findOne(Long id) {
        log.debug("Request to get Room : {}", id);
        Room room = roomRepository.findOne(id);
        return room;
    }

    /**
     *  Delete the  room by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Room : {}", id);
        roomRepository.delete(id);
        roomSearchRepository.delete(id);
    }

    /**
     * Search for the room corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Room> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Rooms for query {}", query);
        return roomSearchRepository.search(queryStringQuery(query), pageable);
    }
}
