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
 * A Equipe.
 */
@Table("equipe")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    private Annonce annonce;

    @Transient
    @JsonIgnoreProperties(value = { "user", "annonces", "equipes", "quartier" }, allowSetters = true)
    private Set<Joueur> joueurs = new HashSet<>();

    @Column("annonce_id")
    private Long annonceId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Annonce getAnnonce() {
        return this.annonce;
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
        this.annonceId = annonce != null ? annonce.getId() : null;
    }

    public Equipe annonce(Annonce annonce) {
        this.setAnnonce(annonce);
        return this;
    }

    public Set<Joueur> getJoueurs() {
        return this.joueurs;
    }

    public void setJoueurs(Set<Joueur> joueurs) {
        if (this.joueurs != null) {
            this.joueurs.forEach(i -> i.removeEquipe(this));
        }
        if (joueurs != null) {
            joueurs.forEach(i -> i.addEquipe(this));
        }
        this.joueurs = joueurs;
    }

    public Equipe joueurs(Set<Joueur> joueurs) {
        this.setJoueurs(joueurs);
        return this;
    }

    public Equipe addJoueur(Joueur joueur) {
        this.joueurs.add(joueur);
        joueur.getEquipes().add(this);
        return this;
    }

    public Equipe removeJoueur(Joueur joueur) {
        this.joueurs.remove(joueur);
        joueur.getEquipes().remove(this);
        return this;
    }

    public Long getAnnonceId() {
        return this.annonceId;
    }

    public void setAnnonceId(Long annonce) {
        this.annonceId = annonce;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipe)) {
            return false;
        }
        return id != null && id.equals(((Equipe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipe{" +
            "id=" + getId() +
            "}";
    }
}
