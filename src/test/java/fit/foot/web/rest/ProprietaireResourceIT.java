package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import fit.foot.IntegrationTest;
import fit.foot.domain.Proprietaire;
import fit.foot.repository.EntityManager;
import fit.foot.repository.ProprietaireRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ProprietaireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProprietaireResourceIT {

    private static final byte[] DEFAULT_AVATAR = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_AVATAR = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_AVATAR_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CIN = "AAAAAAAAAA";
    private static final String UPDATED_CIN = "BBBBBBBBBB";

    private static final String DEFAULT_RIB = "AAAAAAAAAA";
    private static final String UPDATED_RIB = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_TEL = "AAAAAAAAAA";
    private static final String UPDATED_NUM_TEL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/proprietaires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProprietaireRepository proprietaireRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Proprietaire proprietaire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Proprietaire createEntity(EntityManager em) {
        Proprietaire proprietaire = new Proprietaire()
            .avatar(DEFAULT_AVATAR)
            .avatarContentType(DEFAULT_AVATAR_CONTENT_TYPE)
            .cin(DEFAULT_CIN)
            .rib(DEFAULT_RIB)
            .numTel(DEFAULT_NUM_TEL);
        return proprietaire;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Proprietaire createUpdatedEntity(EntityManager em) {
        Proprietaire proprietaire = new Proprietaire()
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .cin(UPDATED_CIN)
            .rib(UPDATED_RIB)
            .numTel(UPDATED_NUM_TEL);
        return proprietaire;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Proprietaire.class).block();
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
        proprietaire = createEntity(em);
    }

    @Test
    void createProprietaire() throws Exception {
        int databaseSizeBeforeCreate = proprietaireRepository.findAll().collectList().block().size();
        // Create the Proprietaire
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeCreate + 1);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(DEFAULT_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(DEFAULT_NUM_TEL);
    }

    @Test
    void createProprietaireWithExistingId() throws Exception {
        // Create the Proprietaire with an existing ID
        proprietaire.setId(1L);

        int databaseSizeBeforeCreate = proprietaireRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProprietairesAsStream() {
        // Initialize the database
        proprietaireRepository.save(proprietaire).block();

        List<Proprietaire> proprietaireList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Proprietaire.class)
            .getResponseBody()
            .filter(proprietaire::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(proprietaireList).isNotNull();
        assertThat(proprietaireList).hasSize(1);
        Proprietaire testProprietaire = proprietaireList.get(0);
        assertThat(testProprietaire.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(DEFAULT_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(DEFAULT_NUM_TEL);
    }

    @Test
    void getAllProprietaires() {
        // Initialize the database
        proprietaireRepository.save(proprietaire).block();

        // Get all the proprietaireList
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
            .value(hasItem(proprietaire.getId().intValue()))
            .jsonPath("$.[*].avatarContentType")
            .value(hasItem(DEFAULT_AVATAR_CONTENT_TYPE))
            .jsonPath("$.[*].avatar")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_AVATAR)))
            .jsonPath("$.[*].cin")
            .value(hasItem(DEFAULT_CIN))
            .jsonPath("$.[*].rib")
            .value(hasItem(DEFAULT_RIB))
            .jsonPath("$.[*].numTel")
            .value(hasItem(DEFAULT_NUM_TEL));
    }

    @Test
    void getProprietaire() {
        // Initialize the database
        proprietaireRepository.save(proprietaire).block();

        // Get the proprietaire
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, proprietaire.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(proprietaire.getId().intValue()))
            .jsonPath("$.avatarContentType")
            .value(is(DEFAULT_AVATAR_CONTENT_TYPE))
            .jsonPath("$.avatar")
            .value(is(Base64Utils.encodeToString(DEFAULT_AVATAR)))
            .jsonPath("$.cin")
            .value(is(DEFAULT_CIN))
            .jsonPath("$.rib")
            .value(is(DEFAULT_RIB))
            .jsonPath("$.numTel")
            .value(is(DEFAULT_NUM_TEL));
    }

    @Test
    void getNonExistingProprietaire() {
        // Get the proprietaire
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProprietaire() throws Exception {
        // Initialize the database
        proprietaireRepository.save(proprietaire).block();

        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();

        // Update the proprietaire
        Proprietaire updatedProprietaire = proprietaireRepository.findById(proprietaire.getId()).block();
        updatedProprietaire
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .cin(UPDATED_CIN)
            .rib(UPDATED_RIB)
            .numTel(UPDATED_NUM_TEL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProprietaire.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProprietaire))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(UPDATED_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(UPDATED_NUM_TEL);
    }

    @Test
    void putNonExistingProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();
        proprietaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, proprietaire.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProprietaireWithPatch() throws Exception {
        // Initialize the database
        proprietaireRepository.save(proprietaire).block();

        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();

        // Update the proprietaire using partial update
        Proprietaire partialUpdatedProprietaire = new Proprietaire();
        partialUpdatedProprietaire.setId(proprietaire.getId());

        partialUpdatedProprietaire.numTel(UPDATED_NUM_TEL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProprietaire.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProprietaire))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(DEFAULT_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(UPDATED_NUM_TEL);
    }

    @Test
    void fullUpdateProprietaireWithPatch() throws Exception {
        // Initialize the database
        proprietaireRepository.save(proprietaire).block();

        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();

        // Update the proprietaire using partial update
        Proprietaire partialUpdatedProprietaire = new Proprietaire();
        partialUpdatedProprietaire.setId(proprietaire.getId());

        partialUpdatedProprietaire
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .cin(UPDATED_CIN)
            .rib(UPDATED_RIB)
            .numTel(UPDATED_NUM_TEL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProprietaire.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProprietaire))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(UPDATED_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(UPDATED_NUM_TEL);
    }

    @Test
    void patchNonExistingProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();
        proprietaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, proprietaire.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().collectList().block().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(proprietaire))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProprietaire() {
        // Initialize the database
        proprietaireRepository.save(proprietaire).block();

        int databaseSizeBeforeDelete = proprietaireRepository.findAll().collectList().block().size();

        // Delete the proprietaire
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, proprietaire.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll().collectList().block();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
