package fit.foot.web.rest;

import fit.foot.domain.Annonce;
import fit.foot.domain.Equipe;
import fit.foot.domain.Joueur;
import fit.foot.domain.Reservation;
import fit.foot.domain.enumeration.STATUS;
import fit.foot.repository.AnnonceRepository;
import fit.foot.repository.EquipeRepository;
import fit.foot.repository.JoueurRepository;
import fit.foot.repository.ReservationRepository;
import fit.foot.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

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
    private final EquipeRepository equipeRepository;
    private final JoueurRepository joueurRepository;
    private final ReservationResource reservationResource;

    public AnnonceResource(
        AnnonceRepository annonceRepository,
        EquipeRepository equipeRepository,
        JoueurRepository joueurRepository,
        ReservationResource reservationResource
    ) {
        this.annonceRepository = annonceRepository;
        this.equipeRepository = equipeRepository;
        this.joueurRepository = joueurRepository;
        this.reservationResource = reservationResource;
    }

    /**
     * {@code POST  /annonces} : Create a new annonce.
     *
     * @param annonce the annonce to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new annonce, or with status {@code 400 (Bad Request)} if the
     *         annonce has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/annonces")
    public ResponseEntity<Annonce> createAnnonce(@RequestBody Annonce annonce) throws URISyntaxException {
        log.debug("REST request to save Annonce : {}", annonce);
        if (annonce.getId() != null) {
            throw new BadRequestAlertException("A new annonce cannot already have an ID", ENTITY_NAME, "idexists");
        }
        // check if annonce heureDebut corresponds to one of the open time slots from
        // reservationResource
        if (!reservationResource.isTimeSlotAvailable(annonce.getHeureDebut(), annonce.getHeureFin())) {
            throw new BadRequestAlertException(
                "Annonce heureDebut does not correspond to one of the open time slots",
                ENTITY_NAME,
                "idexists"
            );
        }
        Equipe equipe = new Equipe();
        if (annonce.getResponsable().getEquipes() == null) {
            annonce.getResponsable().setEquipes(new HashSet<>());
        }
        annonce.getResponsable().addEquipe(equipe);
        equipe.addJoueur(annonce.getResponsable());
        annonce.setEquipe(equipe);
        annonce.setStatus(STATUS.ENCOURS);
        // saving many to many relationship
        equipeRepository.save(equipe);
        joueurRepository.save(annonce.getResponsable());
        // saving reservation
        Reservation reservation = new Reservation();
        reservation.setTerrain(annonce.getTerrain());
        reservation.setDate(annonce.getHeureDebut().toLocalDate());
        reservation.setHeureDebut(annonce.getHeureDebut());
        reservation.setHeureFin(annonce.getHeureFin());
        reservationResource.createReservation(reservation);
        Annonce result = annonceRepository.save(annonce);
        return ResponseEntity
            .created(new URI("/api/annonces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/annonces/join/{id}/{userId}")
    public ResponseEntity<Annonce> join(
        @PathVariable(value = "id", required = false) final Long id,
        @PathVariable(value = "userId", required = false) final Long userId
    ) {
        log.debug("REST request to join Annonce : {}, {}", id, userId);

        // Vérifier que l'ID de l'annonce et de l'utilisateur sont présents
        if (id == null || userId == null) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idnull");
        }

        // Récupérer l'annonce et le joueur à partir de leurs ID respectifs
        Optional<Annonce> annonceOptional = annonceRepository.findById(id);
        Optional<Joueur> joueurOptional = joueurRepository.findByUserId(userId);

        // Vérifier que l'annonce et le joueur existent
        if (!annonceOptional.isPresent() || !joueurOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Annonce annonce = annonceOptional.get();
        Joueur joueur = joueurOptional.get();

        // Vérifier que l'équipe existe et qu'elle n'est pas déjà complète
        Equipe equipe = annonce.getEquipe();
        // if annoce is full book a reservation in the terrain of the annonce
        if (equipe.getJoueurs().size() == 2 * annonce.getNombreParEquipe() - 1) {
            annonce.setStatus(STATUS.FEREME);
            annonceRepository.save(annonce);
        }
        if (equipe.getJoueurs().size() >= 2 * annonce.getNombreParEquipe()) {
            return ResponseEntity.badRequest().build();
        }

        // Mettre à jour l'équipe et l'annonce
        joueur.addEquipe(equipe);
        equipe.addJoueur(joueur);
        annonce.setEquipe(equipe);
        joueurRepository.save(joueur);
        annonceRepository.save(annonce);
        equipeRepository.save(equipe);
        return ResponseEntity.ok(annonce);
    }

    /**
     * {@code PUT  /annonces/:id} : Updates an existing annonce.
     *
     * @param id      the id of the annonce to save.
     * @param annonce the annonce to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated annonce,
     *         or with status {@code 400 (Bad Request)} if the annonce is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the annonce
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/annonces/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable(value = "id", required = false) final Long id, @RequestBody Annonce annonce)
        throws URISyntaxException {
        log.debug("REST request to update Annonce : {}, {}", id, annonce);
        if (annonce.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, annonce.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!annonceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Annonce result = annonceRepository.save(annonce);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, annonce.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /annonces/:id} : Partial updates given fields of an existing
     * annonce, field will ignore if it is null
     *
     * @param id      the id of the annonce to save.
     * @param annonce the annonce to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated annonce,
     *         or with status {@code 400 (Bad Request)} if the annonce is not valid,
     *         or with status {@code 404 (Not Found)} if the annonce is not found,
     *         or with status {@code 500 (Internal Server Error)} if the annonce
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/annonces/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Annonce> partialUpdateAnnonce(
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

        if (!annonceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Annonce> result = annonceRepository
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
            .map(annonceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, annonce.getId().toString())
        );
    }

    /**
     * {@code GET  /annonces} : get all the annonces.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of annonces in body.
     */
    @GetMapping("/annonces")
    public List<Annonce> getAllAnnonces() {
        log.debug("REST request to get all Annonces");
        return annonceRepository.findAll();
    }

    @GetMapping("/annonces/responsable")
    public List<Annonce> getByResponsable() {
        // get user from security context
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // using the user id to get the joueur
        Optional<Joueur> joueur = joueurRepository.findByUserLogin(user.getUsername());
        return annonceRepository.findByResponsableIdOrderByHeureDebutDesc(joueur.get().getId());
    }

    @GetMapping("/annonces/participation")
    public Set<Annonce> getByParticipation() {
        // get user from security context
        Set<Annonce> annonces = new HashSet<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // using the user id to get the joueur
        Optional<Joueur> joueur = joueurRepository.findByUserLogin(user.getUsername());
        joueur
            .get()
            .getEquipes()
            .forEach(p -> {
                annonces.add(p.getAnnonce());
            });
        log.debug("REST request to get all Annonces {}", annonces);
        return annonces;
    }

    /**
     * {@code GET  /annonces/:id} : get the "id" annonce.
     *
     * @param id the id of the annonce to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the annonce, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/annonces/{id}")
    public ResponseEntity<Annonce> getAnnonce(@PathVariable Long id) {
        log.debug("REST request to get Annonce : {}", id);
        Optional<Annonce> annonce = annonceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(annonce);
    }

    /**
     * {@code DELETE  /annonces/:id} : delete the "id" annonce.
     *
     * @param id the id of the annonce to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/annonces/{id}")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        log.debug("REST request to delete Annonce : {}", id);
        annonceRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
