package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fit.foot.IntegrationTest;
import fit.foot.domain.Proprietaire;
import fit.foot.repository.ProprietaireRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ProprietaireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restProprietaireMockMvc;

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

    @BeforeEach
    public void initTest() {
        proprietaire = createEntity(em);
    }

    @Test
    @Transactional
    void createProprietaire() throws Exception {
        int databaseSizeBeforeCreate = proprietaireRepository.findAll().size();
        // Create the Proprietaire
        restProprietaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(proprietaire)))
            .andExpect(status().isCreated());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeCreate + 1);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(DEFAULT_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(DEFAULT_NUM_TEL);
    }

    @Test
    @Transactional
    void createProprietaireWithExistingId() throws Exception {
        // Create the Proprietaire with an existing ID
        proprietaire.setId(1L);

        int databaseSizeBeforeCreate = proprietaireRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProprietaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(proprietaire)))
            .andExpect(status().isBadRequest());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProprietaires() throws Exception {
        // Initialize the database
        proprietaireRepository.saveAndFlush(proprietaire);

        // Get all the proprietaireList
        restProprietaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proprietaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].avatarContentType").value(hasItem(DEFAULT_AVATAR_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].avatar").value(hasItem(Base64Utils.encodeToString(DEFAULT_AVATAR))))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN)))
            .andExpect(jsonPath("$.[*].rib").value(hasItem(DEFAULT_RIB)))
            .andExpect(jsonPath("$.[*].numTel").value(hasItem(DEFAULT_NUM_TEL)));
    }

    @Test
    @Transactional
    void getProprietaire() throws Exception {
        // Initialize the database
        proprietaireRepository.saveAndFlush(proprietaire);

        // Get the proprietaire
        restProprietaireMockMvc
            .perform(get(ENTITY_API_URL_ID, proprietaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(proprietaire.getId().intValue()))
            .andExpect(jsonPath("$.avatarContentType").value(DEFAULT_AVATAR_CONTENT_TYPE))
            .andExpect(jsonPath("$.avatar").value(Base64Utils.encodeToString(DEFAULT_AVATAR)))
            .andExpect(jsonPath("$.cin").value(DEFAULT_CIN))
            .andExpect(jsonPath("$.rib").value(DEFAULT_RIB))
            .andExpect(jsonPath("$.numTel").value(DEFAULT_NUM_TEL));
    }

    @Test
    @Transactional
    void getNonExistingProprietaire() throws Exception {
        // Get the proprietaire
        restProprietaireMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProprietaire() throws Exception {
        // Initialize the database
        proprietaireRepository.saveAndFlush(proprietaire);

        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();

        // Update the proprietaire
        Proprietaire updatedProprietaire = proprietaireRepository.findById(proprietaire.getId()).get();
        // Disconnect from session so that the updates on updatedProprietaire are not directly saved in db
        em.detach(updatedProprietaire);
        updatedProprietaire
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .cin(UPDATED_CIN)
            .rib(UPDATED_RIB)
            .numTel(UPDATED_NUM_TEL);

        restProprietaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProprietaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProprietaire))
            )
            .andExpect(status().isOk());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(UPDATED_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(UPDATED_NUM_TEL);
    }

    @Test
    @Transactional
    void putNonExistingProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();
        proprietaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProprietaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, proprietaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(proprietaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProprietaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(proprietaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProprietaireMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(proprietaire)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProprietaireWithPatch() throws Exception {
        // Initialize the database
        proprietaireRepository.saveAndFlush(proprietaire);

        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();

        // Update the proprietaire using partial update
        Proprietaire partialUpdatedProprietaire = new Proprietaire();
        partialUpdatedProprietaire.setId(proprietaire.getId());

        partialUpdatedProprietaire.numTel(UPDATED_NUM_TEL);

        restProprietaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProprietaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProprietaire))
            )
            .andExpect(status().isOk());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(DEFAULT_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(UPDATED_NUM_TEL);
    }

    @Test
    @Transactional
    void fullUpdateProprietaireWithPatch() throws Exception {
        // Initialize the database
        proprietaireRepository.saveAndFlush(proprietaire);

        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();

        // Update the proprietaire using partial update
        Proprietaire partialUpdatedProprietaire = new Proprietaire();
        partialUpdatedProprietaire.setId(proprietaire.getId());

        partialUpdatedProprietaire
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .cin(UPDATED_CIN)
            .rib(UPDATED_RIB)
            .numTel(UPDATED_NUM_TEL);

        restProprietaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProprietaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProprietaire))
            )
            .andExpect(status().isOk());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
        Proprietaire testProprietaire = proprietaireList.get(proprietaireList.size() - 1);
        assertThat(testProprietaire.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testProprietaire.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testProprietaire.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testProprietaire.getRib()).isEqualTo(UPDATED_RIB);
        assertThat(testProprietaire.getNumTel()).isEqualTo(UPDATED_NUM_TEL);
    }

    @Test
    @Transactional
    void patchNonExistingProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();
        proprietaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProprietaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, proprietaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(proprietaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProprietaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(proprietaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProprietaire() throws Exception {
        int databaseSizeBeforeUpdate = proprietaireRepository.findAll().size();
        proprietaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProprietaireMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(proprietaire))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Proprietaire in the database
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProprietaire() throws Exception {
        // Initialize the database
        proprietaireRepository.saveAndFlush(proprietaire);

        int databaseSizeBeforeDelete = proprietaireRepository.findAll().size();

        // Delete the proprietaire
        restProprietaireMockMvc
            .perform(delete(ENTITY_API_URL_ID, proprietaire.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Proprietaire> proprietaireList = proprietaireRepository.findAll();
        assertThat(proprietaireList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
