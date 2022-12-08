package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import fit.foot.IntegrationTest;
import fit.foot.domain.Joueur;
import fit.foot.domain.enumeration.GENDER;
import fit.foot.repository.EntityManager;
import fit.foot.repository.JoueurRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link JoueurResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class JoueurResourceIT {

    private static final LocalDate DEFAULT_BIRTH_DAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DAY = LocalDate.now(ZoneId.systemDefault());

    private static final GENDER DEFAULT_GENDER = GENDER.MALE;
    private static final GENDER UPDATED_GENDER = GENDER.FEMALE;

    private static final byte[] DEFAULT_AVATAR = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_AVATAR = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_AVATAR_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/joueurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JoueurRepository joueurRepository;

    @Mock
    private JoueurRepository joueurRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Joueur joueur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joueur createEntity(EntityManager em) {
        Joueur joueur = new Joueur()
            .birthDay(DEFAULT_BIRTH_DAY)
            .gender(DEFAULT_GENDER)
            .avatar(DEFAULT_AVATAR)
            .avatarContentType(DEFAULT_AVATAR_CONTENT_TYPE);
        return joueur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joueur createUpdatedEntity(EntityManager em) {
        Joueur joueur = new Joueur()
            .birthDay(UPDATED_BIRTH_DAY)
            .gender(UPDATED_GENDER)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE);
        return joueur;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_joueur__equipe").block();
            em.deleteAll(Joueur.class).block();
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
        joueur = createEntity(em);
    }

    @Test
    void createJoueur() throws Exception {
        int databaseSizeBeforeCreate = joueurRepository.findAll().collectList().block().size();
        // Create the Joueur
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeCreate + 1);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getBirthDay()).isEqualTo(DEFAULT_BIRTH_DAY);
        assertThat(testJoueur.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testJoueur.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testJoueur.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
    }

    @Test
    void createJoueurWithExistingId() throws Exception {
        // Create the Joueur with an existing ID
        joueur.setId(1L);

        int databaseSizeBeforeCreate = joueurRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllJoueursAsStream() {
        // Initialize the database
        joueurRepository.save(joueur).block();

        List<Joueur> joueurList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Joueur.class)
            .getResponseBody()
            .filter(joueur::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(joueurList).isNotNull();
        assertThat(joueurList).hasSize(1);
        Joueur testJoueur = joueurList.get(0);
        assertThat(testJoueur.getBirthDay()).isEqualTo(DEFAULT_BIRTH_DAY);
        assertThat(testJoueur.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testJoueur.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testJoueur.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
    }

    @Test
    void getAllJoueurs() {
        // Initialize the database
        joueurRepository.save(joueur).block();

        // Get all the joueurList
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
            .value(hasItem(joueur.getId().intValue()))
            .jsonPath("$.[*].birthDay")
            .value(hasItem(DEFAULT_BIRTH_DAY.toString()))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].avatarContentType")
            .value(hasItem(DEFAULT_AVATAR_CONTENT_TYPE))
            .jsonPath("$.[*].avatar")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_AVATAR)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJoueursWithEagerRelationshipsIsEnabled() {
        when(joueurRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(joueurRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJoueursWithEagerRelationshipsIsNotEnabled() {
        when(joueurRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(joueurRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getJoueur() {
        // Initialize the database
        joueurRepository.save(joueur).block();

        // Get the joueur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, joueur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(joueur.getId().intValue()))
            .jsonPath("$.birthDay")
            .value(is(DEFAULT_BIRTH_DAY.toString()))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.avatarContentType")
            .value(is(DEFAULT_AVATAR_CONTENT_TYPE))
            .jsonPath("$.avatar")
            .value(is(Base64Utils.encodeToString(DEFAULT_AVATAR)));
    }

    @Test
    void getNonExistingJoueur() {
        // Get the joueur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingJoueur() throws Exception {
        // Initialize the database
        joueurRepository.save(joueur).block();

        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();

        // Update the joueur
        Joueur updatedJoueur = joueurRepository.findById(joueur.getId()).block();
        updatedJoueur
            .birthDay(UPDATED_BIRTH_DAY)
            .gender(UPDATED_GENDER)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedJoueur.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedJoueur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
        assertThat(testJoueur.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testJoueur.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testJoueur.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
    }

    @Test
    void putNonExistingJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();
        joueur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, joueur.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateJoueurWithPatch() throws Exception {
        // Initialize the database
        joueurRepository.save(joueur).block();

        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();

        // Update the joueur using partial update
        Joueur partialUpdatedJoueur = new Joueur();
        partialUpdatedJoueur.setId(joueur.getId());

        partialUpdatedJoueur.birthDay(UPDATED_BIRTH_DAY).gender(UPDATED_GENDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJoueur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedJoueur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
        assertThat(testJoueur.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testJoueur.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testJoueur.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
    }

    @Test
    void fullUpdateJoueurWithPatch() throws Exception {
        // Initialize the database
        joueurRepository.save(joueur).block();

        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();

        // Update the joueur using partial update
        Joueur partialUpdatedJoueur = new Joueur();
        partialUpdatedJoueur.setId(joueur.getId());

        partialUpdatedJoueur
            .birthDay(UPDATED_BIRTH_DAY)
            .gender(UPDATED_GENDER)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJoueur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedJoueur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
        assertThat(testJoueur.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testJoueur.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testJoueur.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();
        joueur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, joueur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().collectList().block().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(joueur))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteJoueur() {
        // Initialize the database
        joueurRepository.save(joueur).block();

        int databaseSizeBeforeDelete = joueurRepository.findAll().collectList().block().size();

        // Delete the joueur
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, joueur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Joueur> joueurList = joueurRepository.findAll().collectList().block();
        assertThat(joueurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
