package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class UserTrophyId implements Serializable {
    private static final long serialVersionUID = 3863919604966660496L;
    @Column(name = "trophy_id", nullable = false)
    private Long trophyId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserTrophyId entity = (UserTrophyId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.trophyId, entity.trophyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, trophyId);
    }

}