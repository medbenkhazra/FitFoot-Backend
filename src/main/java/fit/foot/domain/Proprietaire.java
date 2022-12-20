package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Proprietaire.
 */
@Entity
@Table(name = "proprietaire")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Proprietaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "avatar")
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    @Column(name = "cin")
    private String cin;

    @Column(name = "rib")
    private String rib;

    @Column(name = "num_tel")
    private String numTel;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "proprietaire")
    @JsonIgnoreProperties(value = { "terrains", "quartier", "proprietaire" }, allowSetters = true)
    private Set<Complexe> complexes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Proprietaire id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getAvatar() {
        return this.avatar;
    }

    public Proprietaire avatar(byte[] avatar) {
        this.setAvatar(avatar);
        return this;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAvatarContentType() {
        return this.avatarContentType;
    }

    public Proprietaire avatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
        return this;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }

    public String getCin() {
        return this.cin;
    }

    public Proprietaire cin(String cin) {
        this.setCin(cin);
        return this;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getRib() {
        return this.rib;
    }

    public Proprietaire rib(String rib) {
        this.setRib(rib);
        return this;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }

    public String getNumTel() {
        return this.numTel;
    }

    public Proprietaire numTel(String numTel) {
        this.setNumTel(numTel);
        return this;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Proprietaire user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Complexe> getComplexes() {
        return this.complexes;
    }

    public void setComplexes(Set<Complexe> complexes) {
        if (this.complexes != null) {
            this.complexes.forEach(i -> i.setProprietaire(null));
        }
        if (complexes != null) {
            complexes.forEach(i -> i.setProprietaire(this));
        }
        this.complexes = complexes;
    }

    public Proprietaire complexes(Set<Complexe> complexes) {
        this.setComplexes(complexes);
        return this;
    }

    public Proprietaire addComplexe(Complexe complexe) {
        this.complexes.add(complexe);
        complexe.setProprietaire(this);
        return this;
    }

    public Proprietaire removeComplexe(Complexe complexe) {
        this.complexes.remove(complexe);
        complexe.setProprietaire(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Proprietaire)) {
            return false;
        }
        return id != null && id.equals(((Proprietaire) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Proprietaire{" +
            "id=" + getId() +
            ", avatar='" + getAvatar() + "'" +
            ", avatarContentType='" + getAvatarContentType() + "'" +
            ", cin='" + getCin() + "'" +
            ", rib='" + getRib() + "'" +
            ", numTel='" + getNumTel() + "'" +
            "}";
    }
}
