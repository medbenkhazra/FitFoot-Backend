package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fit.foot.domain.enumeration.STATUS;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;

/**
 * A Annonce.
 */
@Entity
@Table(name = "annonce")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Annonce implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "heure_debut")
    private ZonedDateTime heureDebut;

    @Column(name = "heure_fin")
    private ZonedDateTime heureFin;

    @Column(name = "duree")
    private Integer duree;

    @Column(name = "validation")
    private Boolean validation;

    @Column(name = "nombre_par_equipe")
    private Integer nombreParEquipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private STATUS status;

    @JsonIgnoreProperties(value = { "reservations", "annonce", "complexe" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Terrain terrain;

    @JsonIgnoreProperties(value = {}, allowSetters = true)
    @OneToOne(mappedBy = "annonce")
    private Equipe equipe;

    @ManyToOne
    @JsonIgnoreProperties(value = { "avatar", "annonces", "equipes", "quartier" }, allowSetters = true)
    private Joueur responsable;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Annonce id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Annonce description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getHeureDebut() {
        return this.heureDebut;
    }

    public Annonce heureDebut(ZonedDateTime heureDebut) {
        this.setHeureDebut(heureDebut);
        return this;
    }

    public void setHeureDebut(ZonedDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public ZonedDateTime getHeureFin() {
        return this.heureFin;
    }

    public Annonce heureFin(ZonedDateTime heureFin) {
        this.setHeureFin(heureFin);
        return this;
    }

    public void setHeureFin(ZonedDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public Integer getDuree() {
        return this.duree;
    }

    public Annonce duree(Integer duree) {
        this.setDuree(duree);
        return this;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public Boolean getValidation() {
        return this.validation;
    }

    public Annonce validation(Boolean validation) {
        this.setValidation(validation);
        return this;
    }

    public void setValidation(Boolean validation) {
        this.validation = validation;
    }

    public Integer getNombreParEquipe() {
        return this.nombreParEquipe;
    }

    public Annonce nombreParEquipe(Integer nombreParEquipe) {
        this.setNombreParEquipe(nombreParEquipe);
        return this;
    }

    public void setNombreParEquipe(Integer nombreParEquipe) {
        this.nombreParEquipe = nombreParEquipe;
    }

    public STATUS getStatus() {
        return this.status;
    }

    public Annonce status(STATUS status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Annonce terrain(Terrain terrain) {
        this.setTerrain(terrain);
        return this;
    }

    public Equipe getEquipe() {
        return this.equipe;
    }

    public void setEquipe(Equipe equipe) {
        if (this.equipe != null) {
            this.equipe.setAnnonce(null);
        }
        if (equipe != null) {
            equipe.setAnnonce(this);
        }
        this.equipe = equipe;
    }

    public Annonce equipe(Equipe equipe) {
        this.setEquipe(equipe);
        return this;
    }

    public Joueur getResponsable() {
        return this.responsable;
    }

    public void setResponsable(Joueur joueur) {
        this.responsable = joueur;
    }

    public Annonce responsable(Joueur joueur) {
        this.setResponsable(joueur);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Annonce)) {
            return false;
        }
        return id != null && id.equals(((Annonce) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Annonce{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", heureDebut='" + getHeureDebut() + "'" +
            ", heureFin='" + getHeureFin() + "'" +
            ", duree=" + getDuree() +
            ", validation='" + getValidation() + "'" +
            ", nombreParEquipe=" + getNombreParEquipe() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
