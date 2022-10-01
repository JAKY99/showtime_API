package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Dto.Search.Filters.Permission.PermissionFilterDto;
import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SortingDto;
import com.m2i.showtime.yak.Entity.Permission;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class PermissionRepository {
    EntityManager em;

    public PermissionRepository(EntityManager em) {
        this.em = em;
    }

    public PageListResultDto getPermissionsList(int limit, int offset, SortingDto sort, PermissionFilterDto filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Permission> cq = cb.createQuery(Permission.class);
        Root<Permission> permission = cq.from(Permission.class);

        if (sort.getSortField() != null) {
            if (sort.getSortOrder() == 1) {
                cq.orderBy(cb.asc(permission.get(sort.getSortField())));
            } else {
                cq.orderBy(cb.desc(permission.get(sort.getSortField())));
            }
        }

        //TODO handle filters
        if (filters != null) {
            if (filters.getDisplayName() != null) {
                //cb.like();
            }
        }

        TypedQuery<Permission> queryCount = em.createQuery(cq);
        long totalRecords = queryCount.getResultList()
                                      .size();

        TypedQuery<Permission> query = em.createQuery(cq)
                                         .setFirstResult(offset)
                                         .setMaxResults(limit);
        return new PageListResultDto(query.getResultList(), totalRecords);
    }
}
