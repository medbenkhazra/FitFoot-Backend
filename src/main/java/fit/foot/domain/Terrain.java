package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Terrain.
 */
@Entity
@Table(name = "terrain")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Terrain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "capacite_par_equipe")
    private Integer capaciteParEquipe;

    @OneToMany(mappedBy = "terrain")
    @JsonIgnoreProperties(value = { "terrain" }, allowSetters = true)
    private Set<Reservation> reservations = new HashSet<>();

    @JsonIgnoreProperties(value = { "terrain", "equipe", "responsable" }, allowSetters = true)
    @OneToOne(mappedBy = "terrain")
    private Annonce annonce;

    @ManyToOne
    @JsonIgnoreProperties(value = { "terrains", "quartier", "proprietaire" }, allowSetters = true)
    private Complexe complexe;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Terrain id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Terrain nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getCapaciteParEquipe() {
        return this.capaciteParEquipe;
    }

    public Terrain capaciteParEquipe(Integer capaciteParEquipe) {
        this.setCapaciteParEquipe(capaciteParEquipe);
        return this;
    }

    public void setCapaciteParEquipe(Integer capaciteParEquipe) {
        this.capaciteParEquipe = capaciteParEquipe;
    }

    public Set<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        if (this.reservations != null) {
            this.reservations.forEach(i -> i.setTerrain(null));
        }
        if (reservations != null) {
            reservations.forEach(i -> i.setTerrain(this));
        }
        this.reservations = reservations;
    }

    public Terrain reservations(Set<Reservation> reservations) {
        this.setReservations(reservations);
        return this;
    }

    public Terrain addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setTerrain(this);
        return this;
    }

    public Terrain removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setTerrain(null);
        return this;
    }

    public Annonce getAnnonce() {
        return this.annonce;
    }

    public void setAnnonce(Annonce annonce) {
        if (this.annonce != null) {
            this.annonce.setTerrain(null);
        }
        if (annonce != null) {
            annonce.setTerrain(this);
        }
        this.annonce = annonce;
    }

    public Terrain annonce(Annonce annonce) {
        this.setAnnonce(annonce);
        return this;
    }

    public Complexe getComplexe() {
        return this.complexe;
    }

    public void setComplexe(Complexe complexe) {
        this.complexe = complexe;
    }

    public Terrain complexe(Complexe complexe) {
        this.setComplexe(complexe);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Terrain)) {
            return false;
        }
        return id != null && id.equals(((Terrain) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Terrain{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", capaciteParEquipe=" + getCapaciteParEquipe() +
            "}";
    }
}
