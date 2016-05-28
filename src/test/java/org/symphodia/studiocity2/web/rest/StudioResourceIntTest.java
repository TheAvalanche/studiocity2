package org.symphodia.studiocity2.web.rest;

import org.symphodia.studiocity2.Studiocity2App;
import org.symphodia.studiocity2.domain.Studio;
import org.symphodia.studiocity2.repository.StudioRepository;
import org.symphodia.studiocity2.repository.search.StudioSearchRepository;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the StudioResource REST controller.
 *
 * @see StudioResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Studiocity2App.class)
@WebAppConfiguration
@IntegrationTest
public class StudioResourceIntTest {

    private static final String DEFAULT_NAME = "A";
    private static final String UPDATED_NAME = "B";
    private static final String DEFAULT_DESCRIPTION = "A";
    private static final String UPDATED_DESCRIPTION = "B";
    private static final String DEFAULT_CITY = "AA";
    private static final String UPDATED_CITY = "BB";
    private static final String DEFAULT_STREET = "AA";
    private static final String UPDATED_STREET = "BB";
    private static final String DEFAULT_HOUSE = "A";
    private static final String UPDATED_HOUSE = "B";
    private static final String DEFAULT_INDEX = "AA";
    private static final String UPDATED_INDEX = "BB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Inject
    private StudioRepository studioRepository;

    @Inject
    private StudioSearchRepository studioSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStudioMockMvc;

    private Studio studio;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StudioResource studioResource = new StudioResource();
        ReflectionTestUtils.setField(studioResource, "studioSearchRepository", studioSearchRepository);
        ReflectionTestUtils.setField(studioResource, "studioRepository", studioRepository);
        this.restStudioMockMvc = MockMvcBuilders.standaloneSetup(studioResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        studioSearchRepository.deleteAll();
        studio = new Studio();
        studio.setName(DEFAULT_NAME);
        studio.setDescription(DEFAULT_DESCRIPTION);
        studio.setCity(DEFAULT_CITY);
        studio.setStreet(DEFAULT_STREET);
        studio.setHouse(DEFAULT_HOUSE);
        studio.setIndex(DEFAULT_INDEX);
        studio.setImage(DEFAULT_IMAGE);
        studio.setImageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createStudio() throws Exception {
        int databaseSizeBeforeCreate = studioRepository.findAll().size();

        // Create the Studio

        restStudioMockMvc.perform(post("/api/studios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(studio)))
                .andExpect(status().isCreated());

        // Validate the Studio in the database
        List<Studio> studios = studioRepository.findAll();
        assertThat(studios).hasSize(databaseSizeBeforeCreate + 1);
        Studio testStudio = studios.get(studios.size() - 1);
        assertThat(testStudio.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStudio.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStudio.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testStudio.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(testStudio.getHouse()).isEqualTo(DEFAULT_HOUSE);
        assertThat(testStudio.getIndex()).isEqualTo(DEFAULT_INDEX);
        assertThat(testStudio.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testStudio.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);

        // Validate the Studio in ElasticSearch
        Studio studioEs = studioSearchRepository.findOne(testStudio.getId());
        assertThat(studioEs).isEqualToComparingFieldByField(testStudio);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = studioRepository.findAll().size();
        // set the field null
        studio.setName(null);

        // Create the Studio, which fails.

        restStudioMockMvc.perform(post("/api/studios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(studio)))
                .andExpect(status().isBadRequest());

        List<Studio> studios = studioRepository.findAll();
        assertThat(studios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = studioRepository.findAll().size();
        // set the field null
        studio.setCity(null);

        // Create the Studio, which fails.

        restStudioMockMvc.perform(post("/api/studios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(studio)))
                .andExpect(status().isBadRequest());

        List<Studio> studios = studioRepository.findAll();
        assertThat(studios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStreetIsRequired() throws Exception {
        int databaseSizeBeforeTest = studioRepository.findAll().size();
        // set the field null
        studio.setStreet(null);

        // Create the Studio, which fails.

        restStudioMockMvc.perform(post("/api/studios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(studio)))
                .andExpect(status().isBadRequest());

        List<Studio> studios = studioRepository.findAll();
        assertThat(studios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHouseIsRequired() throws Exception {
        int databaseSizeBeforeTest = studioRepository.findAll().size();
        // set the field null
        studio.setHouse(null);

        // Create the Studio, which fails.

        restStudioMockMvc.perform(post("/api/studios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(studio)))
                .andExpect(status().isBadRequest());

        List<Studio> studios = studioRepository.findAll();
        assertThat(studios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStudios() throws Exception {
        // Initialize the database
        studioRepository.saveAndFlush(studio);

        // Get all the studios
        restStudioMockMvc.perform(get("/api/studios?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(studio.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
                .andExpect(jsonPath("$.[*].street").value(hasItem(DEFAULT_STREET.toString())))
                .andExpect(jsonPath("$.[*].house").value(hasItem(DEFAULT_HOUSE.toString())))
                .andExpect(jsonPath("$.[*].index").value(hasItem(DEFAULT_INDEX.toString())))
                .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getStudio() throws Exception {
        // Initialize the database
        studioRepository.saveAndFlush(studio);

        // Get the studio
        restStudioMockMvc.perform(get("/api/studios/{id}", studio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(studio.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.street").value(DEFAULT_STREET.toString()))
            .andExpect(jsonPath("$.house").value(DEFAULT_HOUSE.toString()))
            .andExpect(jsonPath("$.index").value(DEFAULT_INDEX.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingStudio() throws Exception {
        // Get the studio
        restStudioMockMvc.perform(get("/api/studios/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStudio() throws Exception {
        // Initialize the database
        studioRepository.saveAndFlush(studio);
        studioSearchRepository.save(studio);
        int databaseSizeBeforeUpdate = studioRepository.findAll().size();

        // Update the studio
        Studio updatedStudio = new Studio();
        updatedStudio.setId(studio.getId());
        updatedStudio.setName(UPDATED_NAME);
        updatedStudio.setDescription(UPDATED_DESCRIPTION);
        updatedStudio.setCity(UPDATED_CITY);
        updatedStudio.setStreet(UPDATED_STREET);
        updatedStudio.setHouse(UPDATED_HOUSE);
        updatedStudio.setIndex(UPDATED_INDEX);
        updatedStudio.setImage(UPDATED_IMAGE);
        updatedStudio.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restStudioMockMvc.perform(put("/api/studios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStudio)))
                .andExpect(status().isOk());

        // Validate the Studio in the database
        List<Studio> studios = studioRepository.findAll();
        assertThat(studios).hasSize(databaseSizeBeforeUpdate);
        Studio testStudio = studios.get(studios.size() - 1);
        assertThat(testStudio.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStudio.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStudio.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testStudio.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testStudio.getHouse()).isEqualTo(UPDATED_HOUSE);
        assertThat(testStudio.getIndex()).isEqualTo(UPDATED_INDEX);
        assertThat(testStudio.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testStudio.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);

        // Validate the Studio in ElasticSearch
        Studio studioEs = studioSearchRepository.findOne(testStudio.getId());
        assertThat(studioEs).isEqualToComparingFieldByField(testStudio);
    }

    @Test
    @Transactional
    public void deleteStudio() throws Exception {
        // Initialize the database
        studioRepository.saveAndFlush(studio);
        studioSearchRepository.save(studio);
        int databaseSizeBeforeDelete = studioRepository.findAll().size();

        // Get the studio
        restStudioMockMvc.perform(delete("/api/studios/{id}", studio.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean studioExistsInEs = studioSearchRepository.exists(studio.getId());
        assertThat(studioExistsInEs).isFalse();

        // Validate the database is empty
        List<Studio> studios = studioRepository.findAll();
        assertThat(studios).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStudio() throws Exception {
        // Initialize the database
        studioRepository.saveAndFlush(studio);
        studioSearchRepository.save(studio);

        // Search the studio
        restStudioMockMvc.perform(get("/api/_search/studios?query=id:" + studio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studio.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].street").value(hasItem(DEFAULT_STREET.toString())))
            .andExpect(jsonPath("$.[*].house").value(hasItem(DEFAULT_HOUSE.toString())))
            .andExpect(jsonPath("$.[*].index").value(hasItem(DEFAULT_INDEX.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }
}
