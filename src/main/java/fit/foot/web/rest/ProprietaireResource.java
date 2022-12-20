package fit.foot.web.rest;

import fit.foot.domain.Proprietaire;
import fit.foot.repository.ProprietaireRepository;
import fit.foot.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fit.foot.domain.Proprietaire}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProprietaireResource {

    private final Logger log = LoggerFactory.getLogger(ProprietaireResource.class);

    private static final String ENTITY_NAME = "proprietaire";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProprietaireRepository proprietaireRepository;

    public ProprietaireResource(ProprietaireRepository proprietaireRepository) {
        this.proprietaireRepository = proprietaireRepository;
    }

    /**
     * {@code POST  /proprietaires} : Create a new proprietaire.
     *
     * @param proprietaire the proprietaire to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new proprietaire, or with status {@code 400 (Bad Request)} if the proprietaire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/proprietaires")
    public ResponseEntity<Proprietaire> createProprietaire(@RequestBody Proprietaire proprietaire) throws URISyntaxException {
        log.debug("REST request to save Proprietaire : {}", proprietaire);
        if (proprietaire.getId() != null) {
            throw new BadRequestAlertException("A new proprietaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Proprietaire result = proprietaireRepository.save(proprietaire);
        return ResponseEntity
            .created(new URI("/api/proprietaires/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /proprietaires/:id} : Updates an existing proprietaire.
     *
     * @param id the id of the proprietaire to save.
     * @param proprietaire the proprietaire to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proprietaire,
     * or with status {@code 400 (Bad Request)} if the proprietaire is not valid,
     * or with status {@code 500 (Internal Server Error)} if the proprietaire couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/proprietaires/{id}")
    public ResponseEntity<Proprietaire> updateProprietaire(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Proprietaire proprietaire
    ) throws URISyntaxException {
        log.debug("REST request to update Proprietaire : {}, {}", id, proprietaire);
        if (proprietaire.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proprietaire.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proprietaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Proprietaire result = proprietaireRepository.save(proprietaire);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proprietaire.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /proprietaires/:id} : Partial updates given fields of an existing proprietaire, field will ignore if it is null
     *
     * @param id the id of the proprietaire to save.
     * @param proprietaire the proprietaire to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proprietaire,
     * or with status {@code 400 (Bad Request)} if the proprietaire is not valid,
     * or with status {@code 404 (Not Found)} if the proprietaire is not found,
     * or with status {@code 500 (Internal Server Error)} if the proprietaire couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/proprietaires/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Proprietaire> partialUpdateProprietaire(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Proprietaire proprietaire
    ) throws URISyntaxException {
        log.debug("REST request to partial update Proprietaire partially : {}, {}", id, proprietaire);
        if (proprietaire.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proprietaire.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proprietaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Proprietaire> result = proprietaireRepository
            .findById(proprietaire.getId())
            .map(existingProprietaire -> {
                if (proprietaire.getAvatar() != null) {
                    existingProprietaire.setAvatar(proprietaire.getAvatar());
                }
                if (proprietaire.getAvatarContentType() != null) {
                    existingProprietaire.setAvatarContentType(proprietaire.getAvatarContentType());
                }
                if (proprietaire.getCin() != null) {
                    existingProprietaire.setCin(proprietaire.getCin());
                }
                if (proprietaire.getRib() != null) {
                    existingProprietaire.setRib(proprietaire.getRib());
                }
                if (proprietaire.getNumTel() != null) {
                    existingProprietaire.setNumTel(proprietaire.getNumTel());
                }

                return existingProprietaire;
            })
            .map(proprietaireRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proprietaire.getId().toString())
        );
    }

    /**
     * {@code GET  /proprietaires} : get all the proprietaires.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of proprietaires in body.
     */
    @GetMapping("/proprietaires")
    public List<Proprietaire> getAllProprietaires() {
        log.debug("REST request to get all Proprietaires");
        return proprietaireRepository.findAll();
    }

    /**
     * {@code GET  /proprietaires/:id} : get the "id" proprietaire.
     *
     * @param id the id of the proprietaire to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the proprietaire, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/proprietaires/{id}")
    public ResponseEntity<Proprietaire> getProprietaire(@PathVariable Long id) {
        log.debug("REST request to get Proprietaire : {}", id);
        Optional<Proprietaire> proprietaire = proprietaireRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(proprietaire);
    }

    /**
     * {@code DELETE  /proprietaires/:id} : delete the "id" proprietaire.
     *
     * @param id the id of the proprietaire to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/proprietaires/{id}")
    public ResponseEntity<Void> deleteProprietaire(@PathVariable Long id) {
        log.debug("REST request to delete Proprietaire : {}", id);
        proprietaireRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
