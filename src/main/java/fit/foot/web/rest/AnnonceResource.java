package fit.foot.web.rest;

import fit.foot.domain.Annonce;
import fit.foot.repository.AnnonceRepository;
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
 * REST controller for managing {@link fit.foot.domain.Annonce}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AnnonceResource {

    private final Logger log = LoggerFactory.getLogger(AnnonceResource.class);

    private static final String ENTITY_NAME = "annonce";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnnonceRepository annonceRepository;

    public AnnonceResource(AnnonceRepository annonceRepository) {
        this.annonceRepository = annonceRepository;
    }

    /**
     * {@code POST  /annonces} : Create a new annonce.
     *
     * @param annonce the annonce to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new annonce, or with status {@code 400 (Bad Request)} if the annonce has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/annonces")
    public Mono<ResponseEntity<Annonce>> createAnnonce(@RequestBody Annonce annonce) throws URISyntaxException {
        log.debug("REST request to save Annonce : {}", annonce);
        if (annonce.getId() != null) {
            throw new BadRequestAlertException("A new annonce cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return annonceRepository
            .save(annonce)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/annonces/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /annonces/:id} : Updates an existing annonce.
     *
     * @param id the id of the annonce to save.
     * @param annonce the annonce to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated annonce,
     * or with status {@code 400 (Bad Request)} if the annonce is not valid,
     * or with status {@code 500 (Internal Server Error)} if the annonce couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/annonces/{id}")
    public Mono<ResponseEntity<Annonce>> updateAnnonce(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Annonce annonce
    ) throws URISyntaxException {
        log.debug("REST request to update Annonce : {}, {}", id, annonce);
        if (annonce.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, annonce.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return annonceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return annonceRepository
                    .save(annonce)
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
     * {@code PATCH  /annonces/:id} : Partial updates given fields of an existing annonce, field will ignore if it is null
     *
     * @param id the id of the annonce to save.
     * @param annonce the annonce to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated annonce,
     * or with status {@code 400 (Bad Request)} if the annonce is not valid,
     * or with status {@code 404 (Not Found)} if the annonce is not found,
     * or with status {@code 500 (Internal Server Error)} if the annonce couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/annonces/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Annonce>> partialUpdateAnnonce(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Annonce annonce
    ) throws URISyntaxException {
        log.debug("REST request to partial update Annonce partially : {}, {}", id, annonce);
        if (annonce.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, annonce.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return annonceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Annonce> result = annonceRepository
                    .findById(annonce.getId())
                    .map(existingAnnonce -> {
                        if (annonce.getDescription() != null) {
                            existingAnnonce.setDescription(annonce.getDescription());
                        }
                        if (annonce.getHeureDebut() != null) {
                            existingAnnonce.setHeureDebut(annonce.getHeureDebut());
                        }
                        if (annonce.getHeureFin() != null) {
                            existingAnnonce.setHeureFin(annonce.getHeureFin());
                        }
                        if (annonce.getDuree() != null) {
                            existingAnnonce.setDuree(annonce.getDuree());
                        }
                        if (annonce.getValidation() != null) {
                            existingAnnonce.setValidation(annonce.getValidation());
                        }
                        if (annonce.getNombreParEquipe() != null) {
                            existingAnnonce.setNombreParEquipe(annonce.getNombreParEquipe());
                        }
                        if (annonce.getStatus() != null) {
                            existingAnnonce.setStatus(annonce.getStatus());
                        }

                        return existingAnnonce;
                    })
                    .flatMap(annonceRepository::save);

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
     * {@code GET  /annonces} : get all the annonces.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of annonces in body.
     */
    @GetMapping("/annonces")
    public Mono<List<Annonce>> getAllAnnonces(@RequestParam(required = false) String filter) {
        if ("equipe-is-null".equals(filter)) {
            log.debug("REST request to get all Annonces where equipe is null");
            return annonceRepository.findAllWhereEquipeIsNull().collectList();
        }
        log.debug("REST request to get all Annonces");
        return annonceRepository.findAll().collectList();
    }

    /**
     * {@code GET  /annonces} : get all the annonces as a stream.
     * @return the {@link Flux} of annonces.
     */
    @GetMapping(value = "/annonces", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Annonce> getAllAnnoncesAsStream() {
        log.debug("REST request to get all Annonces as a stream");
        return annonceRepository.findAll();
    }

    /**
     * {@code GET  /annonces/:id} : get the "id" annonce.
     *
     * @param id the id of the annonce to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the annonce, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/annonces/{id}")
    public Mono<ResponseEntity<Annonce>> getAnnonce(@PathVariable Long id) {
        log.debug("REST request to get Annonce : {}", id);
        Mono<Annonce> annonce = annonceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(annonce);
    }

    /**
     * {@code DELETE  /annonces/:id} : delete the "id" annonce.
     *
     * @param id the id of the annonce to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/annonces/{id}")
    public Mono<ResponseEntity<Void>> deleteAnnonce(@PathVariable Long id) {
        log.debug("REST request to delete Annonce : {}", id);
        return annonceRepository
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
