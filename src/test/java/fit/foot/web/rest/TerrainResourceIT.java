package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fit.foot.IntegrationTest;
import fit.foot.domain.Terrain;
import fit.foot.repository.TerrainRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TerrainResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TerrainResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITE_PAR_EQUIPE = 1;
    private static final Integer UPDATED_CAPACITE_PAR_EQUIPE = 2;

    private static final String ENTITY_API_URL = "/api/terrains";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TerrainRepository terrainRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTerrainMockMvc;

    private Terrain terrain;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Terrain createEntity(EntityManager em) {
        Terrain terrain = new Terrain().nom(DEFAULT_NOM).capaciteParEquipe(DEFAULT_CAPACITE_PAR_EQUIPE);
        return terrain;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Terrain createUpdatedEntity(EntityManager em) {
        Terrain terrain = new Terrain().nom(UPDATED_NOM).capaciteParEquipe(UPDATED_CAPACITE_PAR_EQUIPE);
        return terrain;
    }

    @BeforeEach
    public void initTest() {
        terrain = createEntity(em);
    }

    @Test
    @Transactional
    void createTerrain() throws Exception {
        int databaseSizeBeforeCreate = terrainRepository.findAll().size();
        // Create the Terrain
        restTerrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isCreated());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeCreate + 1);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(DEFAULT_CAPACITE_PAR_EQUIPE);
    }

    @Test
    @Transactional
    void createTerrainWithExistingId() throws Exception {
        // Create the Terrain with an existing ID
        terrain.setId(1L);

        int databaseSizeBeforeCreate = terrainRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTerrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTerrains() throws Exception {
        // Initialize the database
        terrainRepository.saveAndFlush(terrain);

        // Get all the terrainList
        restTerrainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(terrain.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].capaciteParEquipe").value(hasItem(DEFAULT_CAPACITE_PAR_EQUIPE)));
    }

    @Test
    @Transactional
    void getTerrain() throws Exception {
        // Initialize the database
        terrainRepository.saveAndFlush(terrain);

        // Get the terrain
        restTerrainMockMvc
            .perform(get(ENTITY_API_URL_ID, terrain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(terrain.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.capaciteParEquipe").value(DEFAULT_CAPACITE_PAR_EQUIPE));
    }

    @Test
    @Transactional
    void getNonExistingTerrain() throws Exception {
        // Get the terrain
        restTerrainMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTerrain() throws Exception {
        // Initialize the database
        terrainRepository.saveAndFlush(terrain);

        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();

        // Update the terrain
        Terrain updatedTerrain = terrainRepository.findById(terrain.getId()).get();
        // Disconnect from session so that the updates on updatedTerrain are not directly saved in db
        em.detach(updatedTerrain);
        updatedTerrain.nom(UPDATED_NOM).capaciteParEquipe(UPDATED_CAPACITE_PAR_EQUIPE);

        restTerrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTerrain.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTerrain))
            )
            .andExpect(status().isOk());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(UPDATED_CAPACITE_PAR_EQUIPE);
    }

    @Test
    @Transactional
    void putNonExistingTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, terrain.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTerrainWithPatch() throws Exception {
        // Initialize the database
        terrainRepository.saveAndFlush(terrain);

        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();

        // Update the terrain using partial update
        Terrain partialUpdatedTerrain = new Terrain();
        partialUpdatedTerrain.setId(terrain.getId());

        partialUpdatedTerrain.capaciteParEquipe(UPDATED_CAPACITE_PAR_EQUIPE);

        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTerrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTerrain))
            )
            .andExpect(status().isOk());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(UPDATED_CAPACITE_PAR_EQUIPE);
    }

    @Test
    @Transactional
    void fullUpdateTerrainWithPatch() throws Exception {
        // Initialize the database
        terrainRepository.saveAndFlush(terrain);

        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();

        // Update the terrain using partial update
        Terrain partialUpdatedTerrain = new Terrain();
        partialUpdatedTerrain.setId(terrain.getId());

        partialUpdatedTerrain.nom(UPDATED_NOM).capaciteParEquipe(UPDATED_CAPACITE_PAR_EQUIPE);

        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTerrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTerrain))
            )
            .andExpect(status().isOk());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(UPDATED_CAPACITE_PAR_EQUIPE);
    }

    @Test
    @Transactional
    void patchNonExistingTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, terrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTerrain() throws Exception {
        // Initialize the database
        terrainRepository.saveAndFlush(terrain);

        int databaseSizeBeforeDelete = terrainRepository.findAll().size();

        // Delete the terrain
        restTerrainMockMvc
            .perform(delete(ENTITY_API_URL_ID, terrain.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
