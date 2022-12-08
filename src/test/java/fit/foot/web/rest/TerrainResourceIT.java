package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import fit.foot.IntegrationTest;
import fit.foot.domain.Terrain;
import fit.foot.repository.EntityManager;
import fit.foot.repository.TerrainRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TerrainResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Terrain.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        terrain = createEntity(em);
    }

    @Test
    void createTerrain() throws Exception {
        int databaseSizeBeforeCreate = terrainRepository.findAll().collectList().block().size();
        // Create the Terrain
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeCreate + 1);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(DEFAULT_CAPACITE_PAR_EQUIPE);
    }

    @Test
    void createTerrainWithExistingId() throws Exception {
        // Create the Terrain with an existing ID
        terrain.setId(1L);

        int databaseSizeBeforeCreate = terrainRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTerrainsAsStream() {
        // Initialize the database
        terrainRepository.save(terrain).block();

        List<Terrain> terrainList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Terrain.class)
            .getResponseBody()
            .filter(terrain::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(terrainList).isNotNull();
        assertThat(terrainList).hasSize(1);
        Terrain testTerrain = terrainList.get(0);
        assertThat(testTerrain.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(DEFAULT_CAPACITE_PAR_EQUIPE);
    }

    @Test
    void getAllTerrains() {
        // Initialize the database
        terrainRepository.save(terrain).block();

        // Get all the terrainList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(terrain.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].capaciteParEquipe")
            .value(hasItem(DEFAULT_CAPACITE_PAR_EQUIPE));
    }

    @Test
    void getTerrain() {
        // Initialize the database
        terrainRepository.save(terrain).block();

        // Get the terrain
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, terrain.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(terrain.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.capaciteParEquipe")
            .value(is(DEFAULT_CAPACITE_PAR_EQUIPE));
    }

    @Test
    void getNonExistingTerrain() {
        // Get the terrain
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTerrain() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain).block();

        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();

        // Update the terrain
        Terrain updatedTerrain = terrainRepository.findById(terrain.getId()).block();
        updatedTerrain.nom(UPDATED_NOM).capaciteParEquipe(UPDATED_CAPACITE_PAR_EQUIPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTerrain.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTerrain))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(UPDATED_CAPACITE_PAR_EQUIPE);
    }

    @Test
    void putNonExistingTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();
        terrain.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, terrain.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTerrainWithPatch() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain).block();

        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();

        // Update the terrain using partial update
        Terrain partialUpdatedTerrain = new Terrain();
        partialUpdatedTerrain.setId(terrain.getId());

        partialUpdatedTerrain.capaciteParEquipe(UPDATED_CAPACITE_PAR_EQUIPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTerrain.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTerrain))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(UPDATED_CAPACITE_PAR_EQUIPE);
    }

    @Test
    void fullUpdateTerrainWithPatch() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain).block();

        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();

        // Update the terrain using partial update
        Terrain partialUpdatedTerrain = new Terrain();
        partialUpdatedTerrain.setId(terrain.getId());

        partialUpdatedTerrain.nom(UPDATED_NOM).capaciteParEquipe(UPDATED_CAPACITE_PAR_EQUIPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTerrain.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTerrain))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTerrain.getCapaciteParEquipe()).isEqualTo(UPDATED_CAPACITE_PAR_EQUIPE);
    }

    @Test
    void patchNonExistingTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();
        terrain.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, terrain.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().collectList().block().size();
        terrain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(terrain))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTerrain() {
        // Initialize the database
        terrainRepository.save(terrain).block();

        int databaseSizeBeforeDelete = terrainRepository.findAll().collectList().block().size();

        // Delete the terrain
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, terrain.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Terrain> terrainList = terrainRepository.findAll().collectList().block();
        assertThat(terrainList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
