package fit.foot.web.rest;

import fit.foot.domain.Quartier;
import fit.foot.repository.QuartierRepository;
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
 * REST controller for managing {@link fit.foot.domain.Quartier}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class QuartierResource {

    private final Logger log = LoggerFactory.getLogger(QuartierResource.class);

    private static final String ENTITY_NAME = "quartier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuartierRepository quartierRepository;

    public QuartierResource(QuartierRepository quartierRepository) {
        this.quartierRepository = quartierRepository;
    }

    /**
     * {@code POST  /quartiers} : Create a new quartier.
     *
     * @param quartier the quartier to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quartier, or with status {@code 400 (Bad Request)} if the quartier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/quartiers")
    public ResponseEntity<Quartier> createQuartier(@RequestBody Quartier quartier) throws URISyntaxException {
        log.debug("REST request to save Quartier : {}", quartier);
        if (quartier.getId() != null) {
            throw new BadRequestAlertException("A new quartier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Quartier result = quartierRepository.save(quartier);
        return ResponseEntity
            .created(new URI("/api/quartiers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /quartiers/:id} : Updates an existing quartier.
     *
     * @param id the id of the quartier to save.
     * @param quartier the quartier to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quartier,
     * or with status {@code 400 (Bad Request)} if the quartier is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quartier couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/quartiers/{id}")
    public ResponseEntity<Quartier> updateQuartier(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Quartier quartier
    ) throws URISyntaxException {
        log.debug("REST request to update Quartier : {}, {}", id, quartier);
        if (quartier.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quartier.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quartierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Quartier result = quartierRepository.save(quartier);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quartier.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /quartiers/:id} : Partial updates given fields of an existing quartier, field will ignore if it is null
     *
     * @param id the id of the quartier to save.
     * @param quartier the quartier to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quartier,
     * or with status {@code 400 (Bad Request)} if the quartier is not valid,
     * or with status {@code 404 (Not Found)} if the quartier is not found,
     * or with status {@code 500 (Internal Server Error)} if the quartier couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/quartiers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Quartier> partialUpdateQuartier(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Quartier quartier
    ) throws URISyntaxException {
        log.debug("REST request to partial update Quartier partially : {}, {}", id, quartier);
        if (quartier.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quartier.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quartierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Quartier> result = quartierRepository
            .findById(quartier.getId())
            .map(existingQuartier -> {
                if (quartier.getNom() != null) {
                    existingQuartier.setNom(quartier.getNom());
                }

                return existingQuartier;
            })
            .map(quartierRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quartier.getId().toString())
        );
    }

    /**
     * {@code GET  /quartiers} : get all the quartiers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quartiers in body.
     */
    @GetMapping("/quartiers")
    public List<Quartier> getAllQuartiers() {
        log.debug("REST request to get all Quartiers");
        return quartierRepository.findAll();
    }

    /**
     * {@code GET  /quartiers/:id} : get the "id" quartier.
     *
     * @param id the id of the quartier to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quartier, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/quartiers/{id}")
    public ResponseEntity<Quartier> getQuartier(@PathVariable Long id) {
        log.debug("REST request to get Quartier : {}", id);
        Optional<Quartier> quartier = quartierRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(quartier);
    }

    /**
     * {@code DELETE  /quartiers/:id} : delete the "id" quartier.
     *
     * @param id the id of the quartier to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/quartiers/{id}")
    public ResponseEntity<Void> deleteQuartier(@PathVariable Long id) {
        log.debug("REST request to delete Quartier : {}", id);
        quartierRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
