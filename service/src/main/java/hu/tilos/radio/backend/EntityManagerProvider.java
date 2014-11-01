package hu.tilos.radio.backend;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProvider {

    @PersistenceContext
    @Produces
    public EntityManager entityManager;
}
