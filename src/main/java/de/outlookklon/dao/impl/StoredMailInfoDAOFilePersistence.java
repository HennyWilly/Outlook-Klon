package de.outlookklon.dao.impl;

import de.outlookklon.dao.DAOException;
import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.localization.Localization;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import de.outlookklon.serializers.Serializer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Diese Klasse implementiert die {@link StoredMailInfoDAO}-Schnittstelle für
 * den Zugriff auf die persistierten {@link StoredMailInfo}-Objekte in
 * Dateiform.
 *
 * @author Hendrik Karwanni
 */
public class StoredMailInfoDAOFilePersistence implements StoredMailInfoDAO {

    @Autowired
    private String accountFolderPattern;

    @Autowired
    private Serializer serializer;

    private File getFolderPath(String accountName, String path) {
        String accountFolderPath = String.format(accountFolderPattern, accountName);
        return new File(accountFolderPath, path);
    }

    private File getFilePath(String accountName, String path, String id) {
        String formatedID = id.replace(">", "").replace("<", "");

        File folderPath = getFolderPath(accountName, path);
        folderPath.mkdirs();
        return new File(folderPath, formatedID + ".json");
    }

    @Override
    public StoredMailInfo loadStoredMailInfo(@NonNull String accountName, @NonNull String id, @NonNull String path)
            throws DAOException {
        File filePath = getFilePath(accountName, path, id);

        StoredMailInfo geladen = null;
        if (filePath.exists()) {
            try {
                geladen = serializer.deserializeJson(filePath, StoredMailInfo.class);
            } catch (IOException ex) {
                throw new DAOException(Localization.getString("StoredMailInfoDAO_CouldNotLoadMailInfo"), ex);
            }
        }

        return geladen;
    }

    @Override
    public void saveStoredMailInfo(@NonNull String accountName, @NonNull StoredMailInfo info, @NonNull String path)
            throws DAOException {
        final File filePath = getFilePath(accountName, path, info.getID());

        try {
            serializer.serializeObjectToJson(filePath, info);
        } catch (IOException ex) {
            throw new DAOException(Localization.getString("StoredMailInfoDAO_CouldNotSaveMailInfo"), ex);
        }
    }

    @Override
    public void deleteStoredMailInfo(@NonNull String accountName, @NonNull String id, @NonNull String path)
            throws DAOException {
        final File filePath = getFilePath(accountName, path, id);

        try {
            Files.delete(filePath.toPath());
        } catch (IOException ex) {
            throw new DAOException(Localization.getString("StoredMailInfoDAO_CouldNotDeleteMailInfo"), ex);
        }
    }

}
