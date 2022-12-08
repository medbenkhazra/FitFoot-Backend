package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Ville.
 */
@Table("ville")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ville implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nom")
    private String nom;

    @Transient
    @JsonIgnoreProperties(value = { "joueurs", "complexes", "ville" }, allowSetters = true)
    private Set<Quartier> quartiers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ville id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Ville nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Quartier> getQuartiers() {
        return this.quartiers;
    }

    public void setQuartiers(Set<Quartier> quartiers) {
        if (this.quartiers != null) {
            this.quartiers.forEach(i -> i.setVille(null));
        }
        if (quartiers != null) {
            quartiers.forEach(i -> i.setVille(this));
        }
        this.quartiers = quartiers;
    }

    public Ville quartiers(Set<Quartier> quartiers) {
        this.setQuartiers(quartiers);
        return this;
    }

    public Ville addQuartier(Quartier quartier) {
        this.quartiers.add(quartier);
        quartier.setVille(this);
        return this;
    }

    public Ville removeQuartier(Quartier quartier) {
        this.quartiers.remove(quartier);
        quartier.setVille(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ville)) {
            return false;
        }
        return id != null && id.equals(((Ville) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ville{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
