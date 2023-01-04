package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Complexe.
 */
@Entity
@Table(name = "complexe")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Complexe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "photo")
    private String photo;

    @OneToMany(mappedBy = "complexe", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "reservations", "annonce", "complexe" }, allowSetters = true)
    private Set<Terrain> terrains = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "complexes", "joueurs", "ville" }, allowSetters = true)
    private Quartier quartier;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "complexes" }, allowSetters = true)
    private Proprietaire proprietaire;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Complexe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Complexe nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Complexe longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Complexe latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPhoto() {
        return this.photo;
    }

    public Complexe photo(String photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Set<Terrain> getTerrains() {
        return this.terrains;
    }

    public void setTerrains(Set<Terrain> terrains) {
        if (this.terrains != null) {
            this.terrains.forEach(i -> i.setComplexe(null));
        }
        if (terrains != null) {
            terrains.forEach(i -> i.setComplexe(this));
        }
        this.terrains = terrains;
    }

    public Complexe terrains(Set<Terrain> terrains) {
        this.setTerrains(terrains);
        return this;
    }

    public Complexe addTerrain(Terrain terrain) {
        this.terrains.add(terrain);
        terrain.setComplexe(this);
        return this;
    }

    public Complexe removeTerrain(Terrain terrain) {
        this.terrains.remove(terrain);
        terrain.setComplexe(null);
        return this;
    }

    public Quartier getQuartier() {
        return this.quartier;
    }

    public void setQuartier(Quartier quartier) {
        this.quartier = quartier;
    }

    public Complexe quartier(Quartier quartier) {
        this.setQuartier(quartier);
        return this;
    }

    public Proprietaire getProprietaire() {
        return this.proprietaire;
    }

    public void setProprietaire(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
    }

    public Complexe proprietaire(Proprietaire proprietaire) {
        this.setProprietaire(proprietaire);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Complexe)) {
            return false;
        }
        return id != null && id.equals(((Complexe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Complexe{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", photo='" + getPhoto() + "'" +
            "}";
    }
}
