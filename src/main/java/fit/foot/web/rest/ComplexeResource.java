package fit.foot.web.rest;

import fit.foot.domain.Complexe;
import fit.foot.repository.ComplexeRepository;
import fit.foot.security.AuthoritiesConstants;
import fit.foot.security.SecurityUtils;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fit.foot.domain.Complexe}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ComplexeResource {

    private final Logger log = LoggerFactory.getLogger(ComplexeResource.class);

    private static final String ENTITY_NAME = "complexe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComplexeRepository complexeRepository;

    public ComplexeResource(ComplexeRepository complexeRepository) {
        this.complexeRepository = complexeRepository;
    }

    /**
     * {@code POST  /complexes} : Create a new complexe.
     *
     * @param complexe the complexe to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new complexe, or with status {@code 400 (Bad Request)} if
     *         the complexe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.OWNER })
    @PostMapping("/complexes")
    public ResponseEntity<Complexe> createComplexe(@RequestBody Complexe complexe) throws URISyntaxException {
        log.debug("REST request to save Complexe : {}", complexe);
        if (complexe.getId() != null) {
            throw new BadRequestAlertException("A new complexe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Complexe result = complexeRepository.save(complexe);
        return ResponseEntity
            .created(new URI("/api/complexes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /complexes/:id} : Updates an existing complexe.
     *
     * @param id       the id of the complexe to save.
     * @param complexe the complexe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated complexe,
     *         or with status {@code 400 (Bad Request)} if the complexe is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the complexe
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("@complexeRepository.findById(#id).get().owner.user.login == authentication.principal.username or hasRole('ROLE_ADMIN')")
    @PutMapping("/complexes/{id}")
    public ResponseEntity<Complexe> updateComplexe(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Complexe complexe
    ) throws URISyntaxException {
        log.debug("REST request to update Complexe : {}, {}", id, complexe);
        if (complexe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complexe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complexeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Complexe result = complexeRepository.save(complexe);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complexe.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /complexes/:id} : Partial updates given fields of an existing
     * complexe, field will ignore if it is null
     *
     * @param id       the id of the complexe to save.
     * @param complexe the complexe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated complexe,
     *         or with status {@code 400 (Bad Request)} if the complexe is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the complexe is not found,
     *         or with status {@code 500 (Internal Server Error)} if the complexe
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.OWNER })
    @PatchMapping(value = "/complexes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Complexe> partialUpdateComplexe(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Complexe complexe
    ) throws URISyntaxException {
        log.debug("REST request to partial update Complexe partially : {}, {}", id, complexe);
        if (complexe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, complexe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!complexeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Complexe> result = complexeRepository
            .findById(complexe.getId())
            .map(existingComplexe -> {
                if (complexe.getNom() != null) {
                    existingComplexe.setNom(complexe.getNom());
                }
                if (complexe.getLongitude() != null) {
                    existingComplexe.setLongitude(complexe.getLongitude());
                }
                if (complexe.getLatitude() != null) {
                    existingComplexe.setLatitude(complexe.getLatitude());
                }

                return existingComplexe;
            })
            .map(complexeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, complexe.getId().toString())
        );
    }

    /**
     * {@code GET  /complexes} : get all the complexes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of complexes in body.
     */
    @GetMapping("/complexes")
    public List<Complexe> getAllComplexes() {
        log.debug("REST request to get all Complexes");
        return complexeRepository.findAll();
    }

    /**
     * {@code GET  /complexes/:id} : get the "id" complexe.
     *
     * @param id the id of the complexe to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the complexe, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/complexes/{id}")
    public ResponseEntity<Complexe> getComplexe(@PathVariable Long id) {
        log.debug("REST request to get Complexe : {}", id);
        Optional<Complexe> complexe = complexeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(complexe);
    }

    @GetMapping("/complexes/nomComplexe/{nomComplexe}")
    public List<Complexe> getComplexe(@PathVariable String nomComplexe) {
        log.debug("REST request to get Complexe : {}", nomComplexe);
        return complexeRepository.findByNomIgnoreCaseIsContaining(nomComplexe);
    }

    @Secured({ AuthoritiesConstants.OWNER })
    @GetMapping("/complexes/owner")
    public List<Complexe> getComplexeByOwner() {
        log.debug("REST request to get Complexe by owner");
        //
        return complexeRepository.findByProprietaireUserLogin(SecurityUtils.getCurrentUserLogin().get());
    }

    /**
     * {@code DELETE  /complexes/:id} : delete the "id" complexe.
     *
     * @param id the id of the complexe to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/complexes/{id}")
    public ResponseEntity<Void> deleteComplexe(@PathVariable Long id) {
        log.debug("REST request to delete Complexe : {}", id);
        complexeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
