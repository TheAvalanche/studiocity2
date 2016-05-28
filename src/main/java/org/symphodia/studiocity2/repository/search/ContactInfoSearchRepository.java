package org.symphodia.studiocity2.repository.search;

import org.symphodia.studiocity2.domain.ContactInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ContactInfo entity.
 */
public interface ContactInfoSearchRepository extends ElasticsearchRepository<ContactInfo, Long> {
}
