package fit.foot.web.rest;

import fit.foot.domain.Complexe;
import fit.foot.repository.ComplexeRepository;
import fit.foot.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

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
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new complexe, or with status {@code 400 (Bad Request)} if the complexe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/complexes")
    public Mono<ResponseEntity<Complexe>> createComplexe(@RequestBody Complexe complexe) throws URISyntaxException {
        log.debug("REST request to save Complexe : {}", complexe);
        if (complexe.getId() != null) {
            throw new BadRequestAlertException("A new complexe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return complexeRepository
            .save(complexe)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/complexes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /complexes/:id} : Updates an existing complexe.
     *
     * @param id the id of the complexe to save.
     * @param complexe the complexe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complexe,
     * or with status {@code 400 (Bad Request)} if the complexe is not valid,
     * or with status {@code 500 (Internal Server Error)} if the complexe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/complexes/{id}")
    public Mono<ResponseEntity<Complexe>> updateComplexe(
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

        return complexeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return complexeRepository
                    .save(complexe)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /complexes/:id} : Partial updates given fields of an existing complexe, field will ignore if it is null
     *
     * @param id the id of the complexe to save.
     * @param complexe the complexe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated complexe,
     * or with status {@code 400 (Bad Request)} if the complexe is not valid,
     * or with status {@code 404 (Not Found)} if the complexe is not found,
     * or with status {@code 500 (Internal Server Error)} if the complexe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/complexes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Complexe>> partialUpdateComplexe(
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

        return complexeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Complexe> result = complexeRepository
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
                    .flatMap(complexeRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /complexes} : get all the complexes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of complexes in body.
     */
    @GetMapping("/complexes")
    public Mono<List<Complexe>> getAllComplexes() {
        log.debug("REST request to get all Complexes");
        return complexeRepository.findAll().collectList();
    }

    /**
     * {@code GET  /complexes} : get all the complexes as a stream.
     * @return the {@link Flux} of complexes.
     */
    @GetMapping(value = "/complexes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Complexe> getAllComplexesAsStream() {
        log.debug("REST request to get all Complexes as a stream");
        return complexeRepository.findAll();
    }

    /**
     * {@code GET  /complexes/:id} : get the "id" complexe.
     *
     * @param id the id of the complexe to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the complexe, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/complexes/{id}")
    public Mono<ResponseEntity<Complexe>> getComplexe(@PathVariable Long id) {
        log.debug("REST request to get Complexe : {}", id);
        Mono<Complexe> complexe = complexeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(complexe);
    }

    /**
     * {@code DELETE  /complexes/:id} : delete the "id" complexe.
     *
     * @param id the id of the complexe to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/complexes/{id}")
    public Mono<ResponseEntity<Void>> deleteComplexe(@PathVariable Long id) {
        log.debug("REST request to delete Complexe : {}", id);
        return complexeRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
