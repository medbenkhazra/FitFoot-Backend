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
 * A Quartier.
 */
@Table("quartier")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Quartier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nom")
    private String nom;

    @Transient
    @JsonIgnoreProperties(value = { "user", "annonces", "equipes", "quartier" }, allowSetters = true)
    private Set<Joueur> joueurs = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "terrains", "quartier", "proprietaire" }, allowSetters = true)
    private Set<Complexe> complexes = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "quartiers" }, allowSetters = true)
    private Ville ville;

    @Column("ville_id")
    private Long villeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Quartier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Quartier nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Joueur> getJoueurs() {
        return this.joueurs;
    }

    public void setJoueurs(Set<Joueur> joueurs) {
        if (this.joueurs != null) {
            this.joueurs.forEach(i -> i.setQuartier(null));
        }
        if (joueurs != null) {
            joueurs.forEach(i -> i.setQuartier(this));
        }
        this.joueurs = joueurs;
    }

    public Quartier joueurs(Set<Joueur> joueurs) {
        this.setJoueurs(joueurs);
        return this;
    }

    public Quartier addJoueur(Joueur joueur) {
        this.joueurs.add(joueur);
        joueur.setQuartier(this);
        return this;
    }

    public Quartier removeJoueur(Joueur joueur) {
        this.joueurs.remove(joueur);
        joueur.setQuartier(null);
        return this;
    }

    public Set<Complexe> getComplexes() {
        return this.complexes;
    }

    public void setComplexes(Set<Complexe> complexes) {
        if (this.complexes != null) {
            this.complexes.forEach(i -> i.setQuartier(null));
        }
        if (complexes != null) {
            complexes.forEach(i -> i.setQuartier(this));
        }
        this.complexes = complexes;
    }

    public Quartier complexes(Set<Complexe> complexes) {
        this.setComplexes(complexes);
        return this;
    }

    public Quartier addComplexe(Complexe complexe) {
        this.complexes.add(complexe);
        complexe.setQuartier(this);
        return this;
    }

    public Quartier removeComplexe(Complexe complexe) {
        this.complexes.remove(complexe);
        complexe.setQuartier(null);
        return this;
    }

    public Ville getVille() {
        return this.ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
        this.villeId = ville != null ? ville.getId() : null;
    }

    public Quartier ville(Ville ville) {
        this.setVille(ville);
        return this;
    }

    public Long getVilleId() {
        return this.villeId;
    }

    public void setVilleId(Long ville) {
        this.villeId = ville;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quartier)) {
            return false;
        }
        return id != null && id.equals(((Quartier) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Quartier{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
