package com.ivan.projectmanager.repository.impl;

import com.ivan.projectmanager.model.Report;
import com.ivan.projectmanager.model.Report_;
import com.ivan.projectmanager.model.User;
import com.ivan.projectmanager.repository.AbstractRepository;
import com.ivan.projectmanager.repository.ReportRepository;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReportRepositoryImpl extends AbstractRepository<Report, Long> implements ReportRepository {


    public ReportRepositoryImpl(EntityManager entityManager) {
        super(entityManager, Report.class);
    }

    @Override
    public List<Report> getAll() {
        return super.getAll();
    }

    @Override
    public Optional<Report> getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Optional<Report> update(Long id, Report updatedEntity) {
        Report existingReport = entityManager.find(Report.class, id);
        if (existingReport != null) {
            existingReport.setTitle(updatedEntity.getTitle());
            existingReport.setText(updatedEntity.getText());
            entityManager.merge(existingReport);
            return Optional.of(existingReport);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    public List<Report> getReportsByUserJpql(User user) {
        TypedQuery<Report> query = entityManager.createQuery(
                "SELECT r FROM Report r WHERE r.user = :user", Report.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<Report> getReportsByUserCriteria(User user) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        Root<Report> root = cq.from(Report.class);
        cq.select(root).where(cb.equal(root.get(Report_.USER), user));
        TypedQuery<Report> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    public List<Report> getReportsByUserJpqlFetch(User user) {
        return entityManager.createQuery(
                        "SELECT r FROM Report r JOIN FETCH r.user WHERE r.user = :user", Report.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<Report> getReportsByUserCriteriaFetch(User user) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        Root<Report> root = cq.from(Report.class);
        root.fetch(Report_.USER, JoinType.LEFT);
        cq.select(root).where(cb.equal(root.get(Report_.USER), user));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Report> getReportsByUserEntityGraph(User user) {
        EntityGraph<Report> entityGraph = entityManager.createEntityGraph(Report.class);
        entityGraph.addAttributeNodes(Report_.USER);
        return entityManager.createQuery(
                        "SELECT r FROM Report r WHERE r.user = :user", Report.class)
                .setParameter("user", user)
                .setHint("javax.persistence.fetchgraph", entityGraph)
                .getResultList();
    }
}
