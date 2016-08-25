package de.outlookklon.config;

import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.dao.impl.StoredMailInfoDAOFilePersistence;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DAOs {

    @Bean(name = "storedMailInfoDAO")
    public StoredMailInfoDAO getStoredMailInfoDAO() throws IOException {
        return new StoredMailInfoDAOFilePersistence();
    }
}
