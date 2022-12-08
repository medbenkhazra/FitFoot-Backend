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
 * A Proprietaire.
 */
@Table("proprietaire")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Proprietaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("avatar")
    private byte[] avatar;

    @Column("avatar_content_type")
    private String avatarContentType;

    @Column("cin")
    private String cin;

    @Column("rib")
    private String rib;

    @Column("num_tel")
    private String numTel;

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "terrains", "quartier", "proprietaire" }, allowSetters = true)
    private Set<Complexe> complexes = new HashSet<>();

    @Column("user_id")
    private Long userId;

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
        this.userId = user != null ? user.getId() : null;
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

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
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
