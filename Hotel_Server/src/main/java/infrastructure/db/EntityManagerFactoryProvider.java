package infrastructure.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class EntityManagerFactoryProvider {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("hotelPU");

    private EntityManagerFactoryProvider() {
    }

    public static EntityManagerFactory getInstance() {
        return ENTITY_MANAGER_FACTORY;
    }
}
