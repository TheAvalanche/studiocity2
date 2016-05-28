package org.symphodia.studiocity2.web.rest;

import org.symphodia.studiocity2.Studiocity2App;
import org.symphodia.studiocity2.domain.ContactInfo;
import org.symphodia.studiocity2.repository.ContactInfoRepository;
import org.symphodia.studiocity2.repository.search.ContactInfoSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.symphodia.studiocity2.domain.enumeration.ContactInfoType;

/**
 * Test class for the ContactInfoResource REST controller.
 *
 * @see ContactInfoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Studiocity2App.class)
@WebAppConfiguration
@IntegrationTest
public class ContactInfoResourceIntTest {


    private static final ContactInfoType DEFAULT_TYPE = ContactInfoType.PHONE;
    private static final ContactInfoType UPDATED_TYPE = ContactInfoType.WEBSITE;
    private static final String DEFAULT_VALUE = "AA";
    private static final String UPDATED_VALUE = "BB";

    @Inject
    private ContactInfoRepository contactInfoRepository;

    @Inject
    private ContactInfoSearchRepository contactInfoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restContactInfoMockMvc;

    private ContactInfo contactInfo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContactInfoResource contactInfoResource = new ContactInfoResource();
        ReflectionTestUtils.setField(contactInfoResource, "contactInfoSearchRepository", contactInfoSearchRepository);
        ReflectionTestUtils.setField(contactInfoResource, "contactInfoRepository", contactInfoRepository);
        this.restContactInfoMockMvc = MockMvcBuilders.standaloneSetup(contactInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        contactInfoSearchRepository.deleteAll();
        contactInfo = new ContactInfo();
        contactInfo.setType(DEFAULT_TYPE);
        contactInfo.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createContactInfo() throws Exception {
        int databaseSizeBeforeCreate = contactInfoRepository.findAll().size();

        // Create the ContactInfo

        restContactInfoMockMvc.perform(post("/api/contact-infos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contactInfo)))
                .andExpect(status().isCreated());

        // Validate the ContactInfo in the database
        List<ContactInfo> contactInfos = contactInfoRepository.findAll();
        assertThat(contactInfos).hasSize(databaseSizeBeforeCreate + 1);
        ContactInfo testContactInfo = contactInfos.get(contactInfos.size() - 1);
        assertThat(testContactInfo.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testContactInfo.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the ContactInfo in ElasticSearch
        ContactInfo contactInfoEs = contactInfoSearchRepository.findOne(testContactInfo.getId());
        assertThat(contactInfoEs).isEqualToComparingFieldByField(testContactInfo);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = contactInfoRepository.findAll().size();
        // set the field null
        contactInfo.setType(null);

        // Create the ContactInfo, which fails.

        restContactInfoMockMvc.perform(post("/api/contact-infos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contactInfo)))
                .andExpect(status().isBadRequest());

        List<ContactInfo> contactInfos = contactInfoRepository.findAll();
        assertThat(contactInfos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = contactInfoRepository.findAll().size();
        // set the field null
        contactInfo.setValue(null);

        // Create the ContactInfo, which fails.

        restContactInfoMockMvc.perform(post("/api/contact-infos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contactInfo)))
                .andExpect(status().isBadRequest());

        List<ContactInfo> contactInfos = contactInfoRepository.findAll();
        assertThat(contactInfos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllContactInfos() throws Exception {
        // Initialize the database
        contactInfoRepository.saveAndFlush(contactInfo);

        // Get all the contactInfos
        restContactInfoMockMvc.perform(get("/api/contact-infos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(contactInfo.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getContactInfo() throws Exception {
        // Initialize the database
        contactInfoRepository.saveAndFlush(contactInfo);

        // Get the contactInfo
        restContactInfoMockMvc.perform(get("/api/contact-infos/{id}", contactInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(contactInfo.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingContactInfo() throws Exception {
        // Get the contactInfo
        restContactInfoMockMvc.perform(get("/api/contact-infos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContactInfo() throws Exception {
        // Initialize the database
        contactInfoRepository.saveAndFlush(contactInfo);
        contactInfoSearchRepository.save(contactInfo);
        int databaseSizeBeforeUpdate = contactInfoRepository.findAll().size();

        // Update the contactInfo
        ContactInfo updatedContactInfo = new ContactInfo();
        updatedContactInfo.setId(contactInfo.getId());
        updatedContactInfo.setType(UPDATED_TYPE);
        updatedContactInfo.setValue(UPDATED_VALUE);

        restContactInfoMockMvc.perform(put("/api/contact-infos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedContactInfo)))
                .andExpect(status().isOk());

        // Validate the ContactInfo in the database
        List<ContactInfo> contactInfos = contactInfoRepository.findAll();
        assertThat(contactInfos).hasSize(databaseSizeBeforeUpdate);
        ContactInfo testContactInfo = contactInfos.get(contactInfos.size() - 1);
        assertThat(testContactInfo.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testContactInfo.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the ContactInfo in ElasticSearch
        ContactInfo contactInfoEs = contactInfoSearchRepository.findOne(testContactInfo.getId());
        assertThat(contactInfoEs).isEqualToComparingFieldByField(testContactInfo);
    }

    @Test
    @Transactional
    public void deleteContactInfo() throws Exception {
        // Initialize the database
        contactInfoRepository.saveAndFlush(contactInfo);
        contactInfoSearchRepository.save(contactInfo);
        int databaseSizeBeforeDelete = contactInfoRepository.findAll().size();

        // Get the contactInfo
        restContactInfoMockMvc.perform(delete("/api/contact-infos/{id}", contactInfo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean contactInfoExistsInEs = contactInfoSearchRepository.exists(contactInfo.getId());
        assertThat(contactInfoExistsInEs).isFalse();

        // Validate the database is empty
        List<ContactInfo> contactInfos = contactInfoRepository.findAll();
        assertThat(contactInfos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchContactInfo() throws Exception {
        // Initialize the database
        contactInfoRepository.saveAndFlush(contactInfo);
        contactInfoSearchRepository.save(contactInfo);

        // Search the contactInfo
        restContactInfoMockMvc.perform(get("/api/_search/contact-infos?query=id:" + contactInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
}
