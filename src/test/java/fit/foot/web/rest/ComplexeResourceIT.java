package fit.foot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fit.foot.IntegrationTest;
import fit.foot.domain.Complexe;
import fit.foot.repository.ComplexeRepository;
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
 * Integration tests for the {@link ComplexeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restComplexeMockMvc;

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

    @BeforeEach
    public void initTest() {
        complexe = createEntity(em);
    }

    @Test
    @Transactional
    void createComplexe() throws Exception {
        int databaseSizeBeforeCreate = complexeRepository.findAll().size();
        // Create the Complexe
        restComplexeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(complexe)))
            .andExpect(status().isCreated());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeCreate + 1);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void createComplexeWithExistingId() throws Exception {
        // Create the Complexe with an existing ID
        complexe.setId(1L);

        int databaseSizeBeforeCreate = complexeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComplexeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(complexe)))
            .andExpect(status().isBadRequest());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllComplexes() throws Exception {
        // Initialize the database
        complexeRepository.saveAndFlush(complexe);

        // Get all the complexeList
        restComplexeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(complexe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())));
    }

    @Test
    @Transactional
    void getComplexe() throws Exception {
        // Initialize the database
        complexeRepository.saveAndFlush(complexe);

        // Get the complexe
        restComplexeMockMvc
            .perform(get(ENTITY_API_URL_ID, complexe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(complexe.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingComplexe() throws Exception {
        // Get the complexe
        restComplexeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComplexe() throws Exception {
        // Initialize the database
        complexeRepository.saveAndFlush(complexe);

        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();

        // Update the complexe
        Complexe updatedComplexe = complexeRepository.findById(complexe.getId()).get();
        // Disconnect from session so that the updates on updatedComplexe are not directly saved in db
        em.detach(updatedComplexe);
        updatedComplexe.nom(UPDATED_NOM).longitude(UPDATED_LONGITUDE).latitude(UPDATED_LATITUDE);

        restComplexeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedComplexe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedComplexe))
            )
            .andExpect(status().isOk());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void putNonExistingComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();
        complexe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplexeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, complexe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(complexe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplexeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(complexe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplexeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(complexe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComplexeWithPatch() throws Exception {
        // Initialize the database
        complexeRepository.saveAndFlush(complexe);

        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();

        // Update the complexe using partial update
        Complexe partialUpdatedComplexe = new Complexe();
        partialUpdatedComplexe.setId(complexe.getId());

        restComplexeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplexe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComplexe))
            )
            .andExpect(status().isOk());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void fullUpdateComplexeWithPatch() throws Exception {
        // Initialize the database
        complexeRepository.saveAndFlush(complexe);

        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();

        // Update the complexe using partial update
        Complexe partialUpdatedComplexe = new Complexe();
        partialUpdatedComplexe.setId(complexe.getId());

        partialUpdatedComplexe.nom(UPDATED_NOM).longitude(UPDATED_LONGITUDE).latitude(UPDATED_LATITUDE);

        restComplexeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComplexe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComplexe))
            )
            .andExpect(status().isOk());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
        Complexe testComplexe = complexeList.get(complexeList.size() - 1);
        assertThat(testComplexe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testComplexe.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testComplexe.getLatitude()).isEqualTo(UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void patchNonExistingComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();
        complexe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComplexeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, complexe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(complexe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplexeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(complexe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComplexe() throws Exception {
        int databaseSizeBeforeUpdate = complexeRepository.findAll().size();
        complexe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComplexeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(complexe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Complexe in the database
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComplexe() throws Exception {
        // Initialize the database
        complexeRepository.saveAndFlush(complexe);

        int databaseSizeBeforeDelete = complexeRepository.findAll().size();

        // Delete the complexe
        restComplexeMockMvc
            .perform(delete(ENTITY_API_URL_ID, complexe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Complexe> complexeList = complexeRepository.findAll();
        assertThat(complexeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
