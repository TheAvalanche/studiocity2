package org.symphodia.studiocity2.web.rest;

import org.symphodia.studiocity2.Studiocity2App;
import org.symphodia.studiocity2.domain.Equipment;
import org.symphodia.studiocity2.repository.EquipmentRepository;
import org.symphodia.studiocity2.service.EquipmentService;
import org.symphodia.studiocity2.repository.search.EquipmentSearchRepository;

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
 * Test class for the EquipmentResource REST controller.
 *
 * @see EquipmentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Studiocity2App.class)
@WebAppConfiguration
@IntegrationTest
public class EquipmentResourceIntTest {

    private static final String DEFAULT_NAME = "A";
    private static final String UPDATED_NAME = "B";
    private static final String DEFAULT_DESCRIPTION = "A";
    private static final String UPDATED_DESCRIPTION = "B";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Inject
    private EquipmentRepository equipmentRepository;

    @Inject
    private EquipmentService equipmentService;

    @Inject
    private EquipmentSearchRepository equipmentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEquipmentMockMvc;

    private Equipment equipment;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EquipmentResource equipmentResource = new EquipmentResource();
        ReflectionTestUtils.setField(equipmentResource, "equipmentService", equipmentService);
        this.restEquipmentMockMvc = MockMvcBuilders.standaloneSetup(equipmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        equipmentSearchRepository.deleteAll();
        equipment = new Equipment();
        equipment.setName(DEFAULT_NAME);
        equipment.setDescription(DEFAULT_DESCRIPTION);
        equipment.setImage(DEFAULT_IMAGE);
        equipment.setImageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createEquipment() throws Exception {
        int databaseSizeBeforeCreate = equipmentRepository.findAll().size();

        // Create the Equipment

        restEquipmentMockMvc.perform(post("/api/equipment")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(equipment)))
                .andExpect(status().isCreated());

        // Validate the Equipment in the database
        List<Equipment> equipment = equipmentRepository.findAll();
        assertThat(equipment).hasSize(databaseSizeBeforeCreate + 1);
        Equipment testEquipment = equipment.get(equipment.size() - 1);
        assertThat(testEquipment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEquipment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEquipment.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testEquipment.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);

        // Validate the Equipment in ElasticSearch
        Equipment equipmentEs = equipmentSearchRepository.findOne(testEquipment.getId());
        assertThat(equipmentEs).isEqualToComparingFieldByField(testEquipment);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = equipmentRepository.findAll().size();
        // set the field null
        equipment.setName(null);

        // Create the Equipment, which fails.

        restEquipmentMockMvc.perform(post("/api/equipment")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(equipment)))
                .andExpect(status().isBadRequest());

        List<Equipment> equipment = equipmentRepository.findAll();
        assertThat(equipment).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEquipment() throws Exception {
        // Initialize the database
        equipmentRepository.saveAndFlush(equipment);

        // Get all the equipment
        restEquipmentMockMvc.perform(get("/api/equipment?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(equipment.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getEquipment() throws Exception {
        // Initialize the database
        equipmentRepository.saveAndFlush(equipment);

        // Get the equipment
        restEquipmentMockMvc.perform(get("/api/equipment/{id}", equipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(equipment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingEquipment() throws Exception {
        // Get the equipment
        restEquipmentMockMvc.perform(get("/api/equipment/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEquipment() throws Exception {
        // Initialize the database
        equipmentService.save(equipment);

        int databaseSizeBeforeUpdate = equipmentRepository.findAll().size();

        // Update the equipment
        Equipment updatedEquipment = new Equipment();
        updatedEquipment.setId(equipment.getId());
        updatedEquipment.setName(UPDATED_NAME);
        updatedEquipment.setDescription(UPDATED_DESCRIPTION);
        updatedEquipment.setImage(UPDATED_IMAGE);
        updatedEquipment.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restEquipmentMockMvc.perform(put("/api/equipment")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEquipment)))
                .andExpect(status().isOk());

        // Validate the Equipment in the database
        List<Equipment> equipment = equipmentRepository.findAll();
        assertThat(equipment).hasSize(databaseSizeBeforeUpdate);
        Equipment testEquipment = equipment.get(equipment.size() - 1);
        assertThat(testEquipment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEquipment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEquipment.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testEquipment.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);

        // Validate the Equipment in ElasticSearch
        Equipment equipmentEs = equipmentSearchRepository.findOne(testEquipment.getId());
        assertThat(equipmentEs).isEqualToComparingFieldByField(testEquipment);
    }

    @Test
    @Transactional
    public void deleteEquipment() throws Exception {
        // Initialize the database
        equipmentService.save(equipment);

        int databaseSizeBeforeDelete = equipmentRepository.findAll().size();

        // Get the equipment
        restEquipmentMockMvc.perform(delete("/api/equipment/{id}", equipment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean equipmentExistsInEs = equipmentSearchRepository.exists(equipment.getId());
        assertThat(equipmentExistsInEs).isFalse();

        // Validate the database is empty
        List<Equipment> equipment = equipmentRepository.findAll();
        assertThat(equipment).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEquipment() throws Exception {
        // Initialize the database
        equipmentService.save(equipment);

        // Search the equipment
        restEquipmentMockMvc.perform(get("/api/_search/equipment?query=id:" + equipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }
}
