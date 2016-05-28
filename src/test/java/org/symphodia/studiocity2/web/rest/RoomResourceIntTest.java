package org.symphodia.studiocity2.web.rest;

import org.symphodia.studiocity2.Studiocity2App;
import org.symphodia.studiocity2.domain.Room;
import org.symphodia.studiocity2.repository.RoomRepository;
import org.symphodia.studiocity2.repository.search.RoomSearchRepository;

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
 * Test class for the RoomResource REST controller.
 *
 * @see RoomResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Studiocity2App.class)
@WebAppConfiguration
@IntegrationTest
public class RoomResourceIntTest {


    private static final Integer DEFAULT_INDEX = 1;
    private static final Integer UPDATED_INDEX = 2;
    private static final String DEFAULT_DESCRIPTION = "A";
    private static final String UPDATED_DESCRIPTION = "B";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private RoomSearchRepository roomSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRoomMockMvc;

    private Room room;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RoomResource roomResource = new RoomResource();
        ReflectionTestUtils.setField(roomResource, "roomSearchRepository", roomSearchRepository);
        ReflectionTestUtils.setField(roomResource, "roomRepository", roomRepository);
        this.restRoomMockMvc = MockMvcBuilders.standaloneSetup(roomResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        roomSearchRepository.deleteAll();
        room = new Room();
        room.setIndex(DEFAULT_INDEX);
        room.setDescription(DEFAULT_DESCRIPTION);
        room.setImage(DEFAULT_IMAGE);
        room.setImageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createRoom() throws Exception {
        int databaseSizeBeforeCreate = roomRepository.findAll().size();

        // Create the Room

        restRoomMockMvc.perform(post("/api/rooms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(room)))
                .andExpect(status().isCreated());

        // Validate the Room in the database
        List<Room> rooms = roomRepository.findAll();
        assertThat(rooms).hasSize(databaseSizeBeforeCreate + 1);
        Room testRoom = rooms.get(rooms.size() - 1);
        assertThat(testRoom.getIndex()).isEqualTo(DEFAULT_INDEX);
        assertThat(testRoom.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRoom.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testRoom.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);

        // Validate the Room in ElasticSearch
        Room roomEs = roomSearchRepository.findOne(testRoom.getId());
        assertThat(roomEs).isEqualToComparingFieldByField(testRoom);
    }

    @Test
    @Transactional
    public void getAllRooms() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);

        // Get all the rooms
        restRoomMockMvc.perform(get("/api/rooms?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(room.getId().intValue())))
                .andExpect(jsonPath("$.[*].index").value(hasItem(DEFAULT_INDEX)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getRoom() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);

        // Get the room
        restRoomMockMvc.perform(get("/api/rooms/{id}", room.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(room.getId().intValue()))
            .andExpect(jsonPath("$.index").value(DEFAULT_INDEX))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingRoom() throws Exception {
        // Get the room
        restRoomMockMvc.perform(get("/api/rooms/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRoom() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);
        roomSearchRepository.save(room);
        int databaseSizeBeforeUpdate = roomRepository.findAll().size();

        // Update the room
        Room updatedRoom = new Room();
        updatedRoom.setId(room.getId());
        updatedRoom.setIndex(UPDATED_INDEX);
        updatedRoom.setDescription(UPDATED_DESCRIPTION);
        updatedRoom.setImage(UPDATED_IMAGE);
        updatedRoom.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restRoomMockMvc.perform(put("/api/rooms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRoom)))
                .andExpect(status().isOk());

        // Validate the Room in the database
        List<Room> rooms = roomRepository.findAll();
        assertThat(rooms).hasSize(databaseSizeBeforeUpdate);
        Room testRoom = rooms.get(rooms.size() - 1);
        assertThat(testRoom.getIndex()).isEqualTo(UPDATED_INDEX);
        assertThat(testRoom.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRoom.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testRoom.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);

        // Validate the Room in ElasticSearch
        Room roomEs = roomSearchRepository.findOne(testRoom.getId());
        assertThat(roomEs).isEqualToComparingFieldByField(testRoom);
    }

    @Test
    @Transactional
    public void deleteRoom() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);
        roomSearchRepository.save(room);
        int databaseSizeBeforeDelete = roomRepository.findAll().size();

        // Get the room
        restRoomMockMvc.perform(delete("/api/rooms/{id}", room.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean roomExistsInEs = roomSearchRepository.exists(room.getId());
        assertThat(roomExistsInEs).isFalse();

        // Validate the database is empty
        List<Room> rooms = roomRepository.findAll();
        assertThat(rooms).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRoom() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);
        roomSearchRepository.save(room);

        // Search the room
        restRoomMockMvc.perform(get("/api/_search/rooms?query=id:" + room.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(room.getId().intValue())))
            .andExpect(jsonPath("$.[*].index").value(hasItem(DEFAULT_INDEX)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }
}
