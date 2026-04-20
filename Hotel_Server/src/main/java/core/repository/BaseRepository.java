package core.repository;

import infrastructure.db.EntityManagerFactoryProvider;
import jakarta.persistence.EntityManager;

public abstract class BaseRepository {
    protected EntityManager createEntityManager() {
        return EntityManagerFactoryProvider.getInstance().createEntityManager();
    }
}
