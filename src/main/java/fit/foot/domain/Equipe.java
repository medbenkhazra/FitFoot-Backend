package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Equipe.
 */
@Entity
@Table(name = "equipe")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties(value = { "equipe", "terrain", "responsable" }, allowSetters = true)
    @OneToOne(mappedBy = "equipe")
    private Annonce annonce;

    @ManyToMany(mappedBy = "equipes")
    @JsonIgnoreProperties(value = { "user", "quartier" }, allowSetters = true)
    private Set<Joueur> joueurs = new HashSet<>();

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
        if (this.annonce != null) {
            this.annonce.setEquipe(null);
        }
        if (annonce != null) {
            annonce.setEquipe(this);
        }
        this.annonce = annonce;
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
