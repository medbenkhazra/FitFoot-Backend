package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fit.foot.domain.enumeration.GENDER;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Joueur.
 */
@Entity
@Table(name = "joueur")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Joueur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "birth_day")
    private LocalDate birthDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GENDER gender;

    @Lob
    @Column(name = "avatar")
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "responsable")
    @JsonIgnoreProperties(value = { "terrain", "equipe", "responsable" }, allowSetters = true)
    private Set<Annonce> annonces = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_joueur__equipe",
        joinColumns = @JoinColumn(name = "joueur_id"),
        inverseJoinColumns = @JoinColumn(name = "equipe_id")
    )
    @JsonIgnoreProperties(value = { "annonce", "joueurs" }, allowSetters = true)
    private Set<Equipe> equipes = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "complexes", "joueurs", "ville" }, allowSetters = true)
    private Quartier quartier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Joueur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBirthDay() {
        return this.birthDay;
    }

    public Joueur birthDay(LocalDate birthDay) {
        this.setBirthDay(birthDay);
        return this;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public GENDER getGender() {
        return this.gender;
    }

    public Joueur gender(GENDER gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(GENDER gender) {
        this.gender = gender;
    }

    public byte[] getAvatar() {
        return this.avatar;
    }

    public Joueur avatar(byte[] avatar) {
        this.setAvatar(avatar);
        return this;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAvatarContentType() {
        return this.avatarContentType;
    }

    public Joueur avatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
        return this;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Joueur user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Annonce> getAnnonces() {
        return this.annonces;
    }

    public void setAnnonces(Set<Annonce> annonces) {
        if (this.annonces != null) {
            this.annonces.forEach(i -> i.setResponsable(null));
        }
        if (annonces != null) {
            annonces.forEach(i -> i.setResponsable(this));
        }
        this.annonces = annonces;
    }

    public Joueur annonces(Set<Annonce> annonces) {
        this.setAnnonces(annonces);
        return this;
    }

    public Joueur addAnnonces(Annonce annonce) {
        this.annonces.add(annonce);
        annonce.setResponsable(this);
        return this;
    }

    public Joueur removeAnnonces(Annonce annonce) {
        this.annonces.remove(annonce);
        annonce.setResponsable(null);
        return this;
    }

    public Set<Equipe> getEquipes() {
        return this.equipes;
    }

    public void setEquipes(Set<Equipe> equipes) {
        this.equipes = equipes;
    }

    public Joueur equipes(Set<Equipe> equipes) {
        this.setEquipes(equipes);
        return this;
    }

    public Joueur addEquipe(Equipe equipe) {
        this.equipes.add(equipe);
        equipe.getJoueurs().add(this);
        return this;
    }

    public Joueur removeEquipe(Equipe equipe) {
        this.equipes.remove(equipe);
        equipe.getJoueurs().remove(this);
        return this;
    }

    public Quartier getQuartier() {
        return this.quartier;
    }

    public void setQuartier(Quartier quartier) {
        this.quartier = quartier;
    }

    public Joueur quartier(Quartier quartier) {
        this.setQuartier(quartier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Joueur)) {
            return false;
        }
        return id != null && id.equals(((Joueur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Joueur{" +
            "id=" + getId() +
            ", birthDay='" + getBirthDay() + "'" +
            ", gender='" + getGender() + "'" +
            ", avatar='" + getAvatar() + "'" +
            ", avatarContentType='" + getAvatarContentType() + "'" +
            "}";
    }
}
