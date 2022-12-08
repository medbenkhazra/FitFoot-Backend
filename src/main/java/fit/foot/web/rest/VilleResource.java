package fit.foot.web.rest;

import fit.foot.domain.Ville;
import fit.foot.repository.VilleRepository;
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
 * REST controller for managing {@link fit.foot.domain.Ville}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VilleResource {

    private final Logger log = LoggerFactory.getLogger(VilleResource.class);

    private static final String ENTITY_NAME = "ville";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VilleRepository villeRepository;

    public VilleResource(VilleRepository villeRepository) {
        this.villeRepository = villeRepository;
    }

    /**
     * {@code POST  /villes} : Create a new ville.
     *
     * @param ville the ville to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ville, or with status {@code 400 (Bad Request)} if the ville has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/villes")
    public Mono<ResponseEntity<Ville>> createVille(@RequestBody Ville ville) throws URISyntaxException {
        log.debug("REST request to save Ville : {}", ville);
        if (ville.getId() != null) {
            throw new BadRequestAlertException("A new ville cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return villeRepository
            .save(ville)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/villes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /villes/:id} : Updates an existing ville.
     *
     * @param id the id of the ville to save.
     * @param ville the ville to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ville,
     * or with status {@code 400 (Bad Request)} if the ville is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ville couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/villes/{id}")
    public Mono<ResponseEntity<Ville>> updateVille(@PathVariable(value = "id", required = false) final Long id, @RequestBody Ville ville)
        throws URISyntaxException {
        log.debug("REST request to update Ville : {}, {}", id, ville);
        if (ville.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ville.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return villeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return villeRepository
                    .save(ville)
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
     * {@code PATCH  /villes/:id} : Partial updates given fields of an existing ville, field will ignore if it is null
     *
     * @param id the id of the ville to save.
     * @param ville the ville to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ville,
     * or with status {@code 400 (Bad Request)} if the ville is not valid,
     * or with status {@code 404 (Not Found)} if the ville is not found,
     * or with status {@code 500 (Internal Server Error)} if the ville couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/villes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Ville>> partialUpdateVille(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Ville ville
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ville partially : {}, {}", id, ville);
        if (ville.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ville.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return villeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Ville> result = villeRepository
                    .findById(ville.getId())
                    .map(existingVille -> {
                        if (ville.getNom() != null) {
                            existingVille.setNom(ville.getNom());
                        }

                        return existingVille;
                    })
                    .flatMap(villeRepository::save);

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
     * {@code GET  /villes} : get all the villes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of villes in body.
     */
    @GetMapping("/villes")
    public Mono<List<Ville>> getAllVilles() {
        log.debug("REST request to get all Villes");
        return villeRepository.findAll().collectList();
    }

    /**
     * {@code GET  /villes} : get all the villes as a stream.
     * @return the {@link Flux} of villes.
     */
    @GetMapping(value = "/villes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Ville> getAllVillesAsStream() {
        log.debug("REST request to get all Villes as a stream");
        return villeRepository.findAll();
    }

    /**
     * {@code GET  /villes/:id} : get the "id" ville.
     *
     * @param id the id of the ville to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ville, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/villes/{id}")
    public Mono<ResponseEntity<Ville>> getVille(@PathVariable Long id) {
        log.debug("REST request to get Ville : {}", id);
        Mono<Ville> ville = villeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ville);
    }

    /**
     * {@code DELETE  /villes/:id} : delete the "id" ville.
     *
     * @param id the id of the ville to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/villes/{id}")
    public Mono<ResponseEntity<Void>> deleteVille(@PathVariable Long id) {
        log.debug("REST request to delete Ville : {}", id);
        return villeRepository
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
