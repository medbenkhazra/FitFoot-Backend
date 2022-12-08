package fit.foot.web.rest;

import static fit.foot.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import fit.foot.IntegrationTest;
import fit.foot.domain.Annonce;
import fit.foot.domain.enumeration.STATUS;
import fit.foot.repository.AnnonceRepository;
import fit.foot.repository.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link AnnonceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AnnonceResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_HEURE_DEBUT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_HEURE_DEBUT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_HEURE_FIN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_HEURE_FIN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_DUREE = 1;
    private static final Integer UPDATED_DUREE = 2;

    private static final Boolean DEFAULT_VALIDATION = false;
    private static final Boolean UPDATED_VALIDATION = true;

    private static final Integer DEFAULT_NOMBRE_PAR_EQUIPE = 1;
    private static final Integer UPDATED_NOMBRE_PAR_EQUIPE = 2;

    private static final STATUS DEFAULT_STATUS = STATUS.OUVERT;
    private static final STATUS UPDATED_STATUS = STATUS.FEREME;

    private static final String ENTITY_API_URL = "/api/annonces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Annonce annonce;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Annonce createEntity(EntityManager em) {
        Annonce annonce = new Annonce()
            .description(DEFAULT_DESCRIPTION)
            .heureDebut(DEFAULT_HEURE_DEBUT)
            .heureFin(DEFAULT_HEURE_FIN)
            .duree(DEFAULT_DUREE)
            .validation(DEFAULT_VALIDATION)
            .nombreParEquipe(DEFAULT_NOMBRE_PAR_EQUIPE)
            .status(DEFAULT_STATUS);
        return annonce;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Annonce createUpdatedEntity(EntityManager em) {
        Annonce annonce = new Annonce()
            .description(UPDATED_DESCRIPTION)
            .heureDebut(UPDATED_HEURE_DEBUT)
            .heureFin(UPDATED_HEURE_FIN)
            .duree(UPDATED_DUREE)
            .validation(UPDATED_VALIDATION)
            .nombreParEquipe(UPDATED_NOMBRE_PAR_EQUIPE)
            .status(UPDATED_STATUS);
        return annonce;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Annonce.class).block();
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
        annonce = createEntity(em);
    }

    @Test
    void createAnnonce() throws Exception {
        int databaseSizeBeforeCreate = annonceRepository.findAll().collectList().block().size();
        // Create the Annonce
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeCreate + 1);
        Annonce testAnnonce = annonceList.get(annonceList.size() - 1);
        assertThat(testAnnonce.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAnnonce.getHeureDebut()).isEqualTo(DEFAULT_HEURE_DEBUT);
        assertThat(testAnnonce.getHeureFin()).isEqualTo(DEFAULT_HEURE_FIN);
        assertThat(testAnnonce.getDuree()).isEqualTo(DEFAULT_DUREE);
        assertThat(testAnnonce.getValidation()).isEqualTo(DEFAULT_VALIDATION);
        assertThat(testAnnonce.getNombreParEquipe()).isEqualTo(DEFAULT_NOMBRE_PAR_EQUIPE);
        assertThat(testAnnonce.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createAnnonceWithExistingId() throws Exception {
        // Create the Annonce with an existing ID
        annonce.setId(1L);

        int databaseSizeBeforeCreate = annonceRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAnnoncesAsStream() {
        // Initialize the database
        annonceRepository.save(annonce).block();

        List<Annonce> annonceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Annonce.class)
            .getResponseBody()
            .filter(annonce::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(annonceList).isNotNull();
        assertThat(annonceList).hasSize(1);
        Annonce testAnnonce = annonceList.get(0);
        assertThat(testAnnonce.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAnnonce.getHeureDebut()).isEqualTo(DEFAULT_HEURE_DEBUT);
        assertThat(testAnnonce.getHeureFin()).isEqualTo(DEFAULT_HEURE_FIN);
        assertThat(testAnnonce.getDuree()).isEqualTo(DEFAULT_DUREE);
        assertThat(testAnnonce.getValidation()).isEqualTo(DEFAULT_VALIDATION);
        assertThat(testAnnonce.getNombreParEquipe()).isEqualTo(DEFAULT_NOMBRE_PAR_EQUIPE);
        assertThat(testAnnonce.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void getAllAnnonces() {
        // Initialize the database
        annonceRepository.save(annonce).block();

        // Get all the annonceList
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
            .value(hasItem(annonce.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].heureDebut")
            .value(hasItem(sameInstant(DEFAULT_HEURE_DEBUT)))
            .jsonPath("$.[*].heureFin")
            .value(hasItem(sameInstant(DEFAULT_HEURE_FIN)))
            .jsonPath("$.[*].duree")
            .value(hasItem(DEFAULT_DUREE))
            .jsonPath("$.[*].validation")
            .value(hasItem(DEFAULT_VALIDATION.booleanValue()))
            .jsonPath("$.[*].nombreParEquipe")
            .value(hasItem(DEFAULT_NOMBRE_PAR_EQUIPE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getAnnonce() {
        // Initialize the database
        annonceRepository.save(annonce).block();

        // Get the annonce
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, annonce.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(annonce.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.heureDebut")
            .value(is(sameInstant(DEFAULT_HEURE_DEBUT)))
            .jsonPath("$.heureFin")
            .value(is(sameInstant(DEFAULT_HEURE_FIN)))
            .jsonPath("$.duree")
            .value(is(DEFAULT_DUREE))
            .jsonPath("$.validation")
            .value(is(DEFAULT_VALIDATION.booleanValue()))
            .jsonPath("$.nombreParEquipe")
            .value(is(DEFAULT_NOMBRE_PAR_EQUIPE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingAnnonce() {
        // Get the annonce
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAnnonce() throws Exception {
        // Initialize the database
        annonceRepository.save(annonce).block();

        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();

        // Update the annonce
        Annonce updatedAnnonce = annonceRepository.findById(annonce.getId()).block();
        updatedAnnonce
            .description(UPDATED_DESCRIPTION)
            .heureDebut(UPDATED_HEURE_DEBUT)
            .heureFin(UPDATED_HEURE_FIN)
            .duree(UPDATED_DUREE)
            .validation(UPDATED_VALIDATION)
            .nombreParEquipe(UPDATED_NOMBRE_PAR_EQUIPE)
            .status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAnnonce.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAnnonce))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
        Annonce testAnnonce = annonceList.get(annonceList.size() - 1);
        assertThat(testAnnonce.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAnnonce.getHeureDebut()).isEqualTo(UPDATED_HEURE_DEBUT);
        assertThat(testAnnonce.getHeureFin()).isEqualTo(UPDATED_HEURE_FIN);
        assertThat(testAnnonce.getDuree()).isEqualTo(UPDATED_DUREE);
        assertThat(testAnnonce.getValidation()).isEqualTo(UPDATED_VALIDATION);
        assertThat(testAnnonce.getNombreParEquipe()).isEqualTo(UPDATED_NOMBRE_PAR_EQUIPE);
        assertThat(testAnnonce.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingAnnonce() throws Exception {
        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();
        annonce.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, annonce.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAnnonce() throws Exception {
        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();
        annonce.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAnnonce() throws Exception {
        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();
        annonce.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAnnonceWithPatch() throws Exception {
        // Initialize the database
        annonceRepository.save(annonce).block();

        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();

        // Update the annonce using partial update
        Annonce partialUpdatedAnnonce = new Annonce();
        partialUpdatedAnnonce.setId(annonce.getId());

        partialUpdatedAnnonce
            .description(UPDATED_DESCRIPTION)
            .heureFin(UPDATED_HEURE_FIN)
            .validation(UPDATED_VALIDATION)
            .nombreParEquipe(UPDATED_NOMBRE_PAR_EQUIPE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnnonce.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnnonce))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
        Annonce testAnnonce = annonceList.get(annonceList.size() - 1);
        assertThat(testAnnonce.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAnnonce.getHeureDebut()).isEqualTo(DEFAULT_HEURE_DEBUT);
        assertThat(testAnnonce.getHeureFin()).isEqualTo(UPDATED_HEURE_FIN);
        assertThat(testAnnonce.getDuree()).isEqualTo(DEFAULT_DUREE);
        assertThat(testAnnonce.getValidation()).isEqualTo(UPDATED_VALIDATION);
        assertThat(testAnnonce.getNombreParEquipe()).isEqualTo(UPDATED_NOMBRE_PAR_EQUIPE);
        assertThat(testAnnonce.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateAnnonceWithPatch() throws Exception {
        // Initialize the database
        annonceRepository.save(annonce).block();

        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();

        // Update the annonce using partial update
        Annonce partialUpdatedAnnonce = new Annonce();
        partialUpdatedAnnonce.setId(annonce.getId());

        partialUpdatedAnnonce
            .description(UPDATED_DESCRIPTION)
            .heureDebut(UPDATED_HEURE_DEBUT)
            .heureFin(UPDATED_HEURE_FIN)
            .duree(UPDATED_DUREE)
            .validation(UPDATED_VALIDATION)
            .nombreParEquipe(UPDATED_NOMBRE_PAR_EQUIPE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnnonce.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnnonce))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
        Annonce testAnnonce = annonceList.get(annonceList.size() - 1);
        assertThat(testAnnonce.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAnnonce.getHeureDebut()).isEqualTo(UPDATED_HEURE_DEBUT);
        assertThat(testAnnonce.getHeureFin()).isEqualTo(UPDATED_HEURE_FIN);
        assertThat(testAnnonce.getDuree()).isEqualTo(UPDATED_DUREE);
        assertThat(testAnnonce.getValidation()).isEqualTo(UPDATED_VALIDATION);
        assertThat(testAnnonce.getNombreParEquipe()).isEqualTo(UPDATED_NOMBRE_PAR_EQUIPE);
        assertThat(testAnnonce.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingAnnonce() throws Exception {
        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();
        annonce.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, annonce.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAnnonce() throws Exception {
        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();
        annonce.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAnnonce() throws Exception {
        int databaseSizeBeforeUpdate = annonceRepository.findAll().collectList().block().size();
        annonce.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(annonce))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Annonce in the database
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAnnonce() {
        // Initialize the database
        annonceRepository.save(annonce).block();

        int databaseSizeBeforeDelete = annonceRepository.findAll().collectList().block().size();

        // Delete the annonce
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, annonce.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Annonce> annonceList = annonceRepository.findAll().collectList().block();
        assertThat(annonceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
