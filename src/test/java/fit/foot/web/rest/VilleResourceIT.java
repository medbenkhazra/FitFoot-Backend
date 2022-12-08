package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import fit.foot.IntegrationTest;
import fit.foot.domain.Ville;
import fit.foot.repository.EntityManager;
import fit.foot.repository.VilleRepository;
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
 * Integration tests for the {@link VilleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VilleResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/villes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Ville ville;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ville createEntity(EntityManager em) {
        Ville ville = new Ville().nom(DEFAULT_NOM);
        return ville;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ville createUpdatedEntity(EntityManager em) {
        Ville ville = new Ville().nom(UPDATED_NOM);
        return ville;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Ville.class).block();
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
        ville = createEntity(em);
    }

    @Test
    void createVille() throws Exception {
        int databaseSizeBeforeCreate = villeRepository.findAll().collectList().block().size();
        // Create the Ville
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeCreate + 1);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(DEFAULT_NOM);
    }

    @Test
    void createVilleWithExistingId() throws Exception {
        // Create the Ville with an existing ID
        ville.setId(1L);

        int databaseSizeBeforeCreate = villeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllVillesAsStream() {
        // Initialize the database
        villeRepository.save(ville).block();

        List<Ville> villeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Ville.class)
            .getResponseBody()
            .filter(ville::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(villeList).isNotNull();
        assertThat(villeList).hasSize(1);
        Ville testVille = villeList.get(0);
        assertThat(testVille.getNom()).isEqualTo(DEFAULT_NOM);
    }

    @Test
    void getAllVilles() {
        // Initialize the database
        villeRepository.save(ville).block();

        // Get all the villeList
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
            .value(hasItem(ville.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM));
    }

    @Test
    void getVille() {
        // Initialize the database
        villeRepository.save(ville).block();

        // Get the ville
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ville.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ville.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM));
    }

    @Test
    void getNonExistingVille() {
        // Get the ville
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingVille() throws Exception {
        // Initialize the database
        villeRepository.save(ville).block();

        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();

        // Update the ville
        Ville updatedVille = villeRepository.findById(ville.getId()).block();
        updatedVille.nom(UPDATED_NOM);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedVille.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedVille))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    void putNonExistingVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();
        ville.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ville.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVilleWithPatch() throws Exception {
        // Initialize the database
        villeRepository.save(ville).block();

        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();

        // Update the ville using partial update
        Ville partialUpdatedVille = new Ville();
        partialUpdatedVille.setId(ville.getId());

        partialUpdatedVille.nom(UPDATED_NOM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVille.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVille))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    void fullUpdateVilleWithPatch() throws Exception {
        // Initialize the database
        villeRepository.save(ville).block();

        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();

        // Update the ville using partial update
        Ville partialUpdatedVille = new Ville();
        partialUpdatedVille.setId(ville.getId());

        partialUpdatedVille.nom(UPDATED_NOM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVille.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVille))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    void patchNonExistingVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();
        ville.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ville.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().collectList().block().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ville))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVille() {
        // Initialize the database
        villeRepository.save(ville).block();

        int databaseSizeBeforeDelete = villeRepository.findAll().collectList().block().size();

        // Delete the ville
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ville.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Ville> villeList = villeRepository.findAll().collectList().block();
        assertThat(villeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
