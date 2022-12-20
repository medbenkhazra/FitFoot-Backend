package fit.foot.web.rest;

import fit.foot.domain.Terrain;
import fit.foot.repository.TerrainRepository;
import fit.foot.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fit.foot.domain.Terrain}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TerrainResource {

    private final Logger log = LoggerFactory.getLogger(TerrainResource.class);

    private static final String ENTITY_NAME = "terrain";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TerrainRepository terrainRepository;

    public TerrainResource(TerrainRepository terrainRepository) {
        this.terrainRepository = terrainRepository;
    }

    /**
     * {@code POST  /terrains} : Create a new terrain.
     *
     * @param terrain the terrain to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new terrain, or with status {@code 400 (Bad Request)} if the terrain has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/terrains")
    public ResponseEntity<Terrain> createTerrain(@RequestBody Terrain terrain) throws URISyntaxException {
        log.debug("REST request to save Terrain : {}", terrain);
        if (terrain.getId() != null) {
            throw new BadRequestAlertException("A new terrain cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Terrain result = terrainRepository.save(terrain);
        return ResponseEntity
            .created(new URI("/api/terrains/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /terrains/:id} : Updates an existing terrain.
     *
     * @param id the id of the terrain to save.
     * @param terrain the terrain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated terrain,
     * or with status {@code 400 (Bad Request)} if the terrain is not valid,
     * or with status {@code 500 (Internal Server Error)} if the terrain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/terrains/{id}")
    public ResponseEntity<Terrain> updateTerrain(@PathVariable(value = "id", required = false) final Long id, @RequestBody Terrain terrain)
        throws URISyntaxException {
        log.debug("REST request to update Terrain : {}, {}", id, terrain);
        if (terrain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, terrain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!terrainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Terrain result = terrainRepository.save(terrain);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, terrain.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /terrains/:id} : Partial updates given fields of an existing terrain, field will ignore if it is null
     *
     * @param id the id of the terrain to save.
     * @param terrain the terrain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated terrain,
     * or with status {@code 400 (Bad Request)} if the terrain is not valid,
     * or with status {@code 404 (Not Found)} if the terrain is not found,
     * or with status {@code 500 (Internal Server Error)} if the terrain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/terrains/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Terrain> partialUpdateTerrain(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Terrain terrain
    ) throws URISyntaxException {
        log.debug("REST request to partial update Terrain partially : {}, {}", id, terrain);
        if (terrain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, terrain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!terrainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Terrain> result = terrainRepository
            .findById(terrain.getId())
            .map(existingTerrain -> {
                if (terrain.getNom() != null) {
                    existingTerrain.setNom(terrain.getNom());
                }
                if (terrain.getCapaciteParEquipe() != null) {
                    existingTerrain.setCapaciteParEquipe(terrain.getCapaciteParEquipe());
                }

                return existingTerrain;
            })
            .map(terrainRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, terrain.getId().toString())
        );
    }

    /**
     * {@code GET  /terrains} : get all the terrains.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of terrains in body.
     */
    @GetMapping("/terrains")
    public List<Terrain> getAllTerrains(@RequestParam(required = false) String filter) {
        if ("annonce-is-null".equals(filter)) {
            log.debug("REST request to get all Terrains where annonce is null");
            return StreamSupport
                .stream(terrainRepository.findAll().spliterator(), false)
                .filter(terrain -> terrain.getAnnonce() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all Terrains");
        return terrainRepository.findAll();
    }

    /**
     * {@code GET  /terrains/:id} : get the "id" terrain.
     *
     * @param id the id of the terrain to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the terrain, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/terrains/{id}")
    public ResponseEntity<Terrain> getTerrain(@PathVariable Long id) {
        log.debug("REST request to get Terrain : {}", id);
        Optional<Terrain> terrain = terrainRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(terrain);
    }

    /**
     * {@code DELETE  /terrains/:id} : delete the "id" terrain.
     *
     * @param id the id of the terrain to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/terrains/{id}")
    public ResponseEntity<Void> deleteTerrain(@PathVariable Long id) {
        log.debug("REST request to delete Terrain : {}", id);
        terrainRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
