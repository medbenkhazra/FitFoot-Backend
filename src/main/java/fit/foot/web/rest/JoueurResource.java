package fit.foot.web.rest;

import fit.foot.domain.Joueur;
import fit.foot.repository.JoueurRepository;
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
 * REST controller for managing {@link fit.foot.domain.Joueur}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class JoueurResource {

    private final Logger log = LoggerFactory.getLogger(JoueurResource.class);

    private static final String ENTITY_NAME = "joueur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JoueurRepository joueurRepository;

    public JoueurResource(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    /**
     * {@code POST  /joueurs} : Create a new joueur.
     *
     * @param joueur the joueur to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new joueur, or with status {@code 400 (Bad Request)} if the joueur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/joueurs")
    public Mono<ResponseEntity<Joueur>> createJoueur(@RequestBody Joueur joueur) throws URISyntaxException {
        log.debug("REST request to save Joueur : {}", joueur);
        if (joueur.getId() != null) {
            throw new BadRequestAlertException("A new joueur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return joueurRepository
            .save(joueur)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/joueurs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /joueurs/:id} : Updates an existing joueur.
     *
     * @param id the id of the joueur to save.
     * @param joueur the joueur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated joueur,
     * or with status {@code 400 (Bad Request)} if the joueur is not valid,
     * or with status {@code 500 (Internal Server Error)} if the joueur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/joueurs/{id}")
    public Mono<ResponseEntity<Joueur>> updateJoueur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Joueur joueur
    ) throws URISyntaxException {
        log.debug("REST request to update Joueur : {}, {}", id, joueur);
        if (joueur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, joueur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return joueurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return joueurRepository
                    .save(joueur)
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
     * {@code PATCH  /joueurs/:id} : Partial updates given fields of an existing joueur, field will ignore if it is null
     *
     * @param id the id of the joueur to save.
     * @param joueur the joueur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated joueur,
     * or with status {@code 400 (Bad Request)} if the joueur is not valid,
     * or with status {@code 404 (Not Found)} if the joueur is not found,
     * or with status {@code 500 (Internal Server Error)} if the joueur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/joueurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Joueur>> partialUpdateJoueur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Joueur joueur
    ) throws URISyntaxException {
        log.debug("REST request to partial update Joueur partially : {}, {}", id, joueur);
        if (joueur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, joueur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return joueurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Joueur> result = joueurRepository
                    .findById(joueur.getId())
                    .map(existingJoueur -> {
                        if (joueur.getBirthDay() != null) {
                            existingJoueur.setBirthDay(joueur.getBirthDay());
                        }
                        if (joueur.getGender() != null) {
                            existingJoueur.setGender(joueur.getGender());
                        }
                        if (joueur.getAvatar() != null) {
                            existingJoueur.setAvatar(joueur.getAvatar());
                        }
                        if (joueur.getAvatarContentType() != null) {
                            existingJoueur.setAvatarContentType(joueur.getAvatarContentType());
                        }

                        return existingJoueur;
                    })
                    .flatMap(joueurRepository::save);

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
     * {@code GET  /joueurs} : get all the joueurs.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of joueurs in body.
     */
    @GetMapping("/joueurs")
    public Mono<List<Joueur>> getAllJoueurs(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Joueurs");
        if (eagerload) {
            return joueurRepository.findAllWithEagerRelationships().collectList();
        } else {
            return joueurRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /joueurs} : get all the joueurs as a stream.
     * @return the {@link Flux} of joueurs.
     */
    @GetMapping(value = "/joueurs", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Joueur> getAllJoueursAsStream() {
        log.debug("REST request to get all Joueurs as a stream");
        return joueurRepository.findAll();
    }

    /**
     * {@code GET  /joueurs/:id} : get the "id" joueur.
     *
     * @param id the id of the joueur to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the joueur, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/joueurs/{id}")
    public Mono<ResponseEntity<Joueur>> getJoueur(@PathVariable Long id) {
        log.debug("REST request to get Joueur : {}", id);
        Mono<Joueur> joueur = joueurRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(joueur);
    }

    /**
     * {@code DELETE  /joueurs/:id} : delete the "id" joueur.
     *
     * @param id the id of the joueur to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/joueurs/{id}")
    public Mono<ResponseEntity<Void>> deleteJoueur(@PathVariable Long id) {
        log.debug("REST request to delete Joueur : {}", id);
        return joueurRepository
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
