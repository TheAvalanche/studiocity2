package org.symphodia.studiocity2.repository.search;

import org.symphodia.studiocity2.domain.Room;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Room entity.
 */
public interface RoomSearchRepository extends ElasticsearchRepository<Room, Long> {
}
