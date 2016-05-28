package org.symphodia.studiocity2.repository.search;

import org.symphodia.studiocity2.domain.Studio;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Studio entity.
 */
public interface StudioSearchRepository extends ElasticsearchRepository<Studio, Long> {
}
