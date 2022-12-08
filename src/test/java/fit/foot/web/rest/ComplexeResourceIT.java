package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import fit.foot.IntegrationTest;
import fit.foot.domain.Complexe;
import fit.foot.repository.ComplexeRepository;
import fit.foot.repository.EntityManager;
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
 * Integration tests for the {@link ComplexeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ComplexeResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final String ENTITY_API_URL = "/api/complexes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ComplexeRepository complexeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Complexe complexe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Complexe createEntity(EntityManager em) {
        Complexe complexe = new Complexe().nom(DEFAULT_NOM).longitude(DEFAULT_LONGITUDE).latitude(DEFAULT_LATITUDE);
        return complexe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Complexe createUpdatedEntity(EntityManager em) {
        Complexe complexe = new Complexe().nom(UPDATED_NOM).longitude(UPDATED_LONGITUDE).latitude(UPDATED_LATITUDE);
        return complexe;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Complexe.class).block();
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
        complexe = createEntity(em);
    }

    @Test
    void createComplexe() throws Exception {
        int databaseSizeBeforeCreate = complexeRepository.findAll().collectList().block().size();
        // Create the Complexe
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeCreate + 1);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
    }

    @Test
    void createComplexeWithExistingId() throws Exception {
        // Create the Complexe with an existing ID
        complexe.setId(1L);

        int databaseSizeBeforeCreate = complexeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllComplexesAsStream() {
        // Initialize the database
        complexeRepository.save(complexe).block();

        List<Complexe> complexeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Complexe.class)
            .getResponseBody()
            .filter(complexe::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(complexeList).isNotNull();
        assertThat(complexeList).hasSize(1);
        Complexe testComplexe = complexeList.get(0);
        assertThat(testComplexe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
    }

    @Test
    void getAllComplexes() {
        // Initialize the database
        complexeRepository.save(complexe).block();

        // Get all the complexeList
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
            .value(hasItem(complexe.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].longitude")
            .value(hasItem(DEFAULT_LONGITUDE.doubleValue()))
            .jsonPath("$.[*].latitude")
            .value(hasItem(DEFAULT_LATITUDE.doubleValue()));
    }

    @Test
    void getComplexe() {
        // Initialize the database
        complexeRepository.save(complexe).block();

        // Get the complexe
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, complexe.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(complexe.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.longitude")
            .value(is(DEFAULT_LONGITUDE.doubleValue()))
            .jsonPath("$.latitude")
            .value(is(DEFAULT_LATITUDE.doubleValue()));
    }

    @Test
    void getNonExistingComplexe() {
        // Get the complexe
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingComplexe() throws Exception {
        // Initialize the database
        complexeRepository.save(complexe).block();

        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();

        // Update the complexe
        Complexe updatedComplexe = complexeRepository.findById(complexe.getId()).block();
        updatedComplexe.nom(UPDATED_NOM).longitude(UPDATED_LONGITUDE).latitude(UPDATED_LATITUDE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedComplexe.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedComplexe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(UPDATED_LATITUDE);
    }

    @Test
    void putNonExistingComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();
        complexe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, complexe.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateComplexeWithPatch() throws Exception {
        // Initialize the database
        complexeRepository.save(complexe).block();

        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();

        // Update the complexe using partial update
        Complexe partialUpdatedComplexe = new Complexe();
        partialUpdatedComplexe.setId(complexe.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComplexe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComplexe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
    }

    @Test
    void fullUpdateComplexeWithPatch() throws Exception {
        // Initialize the database
        complexeRepository.save(complexe).block();

        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();

        // Update the complexe using partial update
        Complexe partialUpdatedComplexe = new Complexe();
        partialUpdatedComplexe.setId(complexe.getId());

        partialUpdatedComplexe.nom(UPDATED_NOM).longitude(UPDATED_LONGITUDE).latitude(UPDATED_LATITUDE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComplexe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComplexe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(UPDATED_LATITUDE);
    }

    @Test
    void patchNonExistingComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();
        complexe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, complexe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().collectList().block().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(complexe))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteComplexe() {
        // Initialize the database
        complexeRepository.save(complexe).block();

        int databaseSizeBeforeDelete = complexeRepository.findAll().collectList().block().size();

        // Delete the complexe
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, complexe.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Complexe> complexeList = complexeRepository.findAll().collectList().block();
        assertThat(complexeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
