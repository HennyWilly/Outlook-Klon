package de.outlookklon.dao.impl;

import de.outlookklon.dao.DAOException;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import de.outlookklon.serializers.Serializer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import de.outlookklon.dao.StoredMailInfoDAO;

/**
 * Diese Klasse implementiert die {@link StoredMailInfoDAO}-Schnittstelle für den
 * Zugriff auf die persistierten {@link StoredMailInfo}-Objekte in Dateiform.
 *
 * @author Hendrik Karwanni
 */
public class StoredMailInfoDAOFilePersistence implements StoredMailInfoDAO {

    private File folder;

    public StoredMailInfoDAOFilePersistence(File folder) throws IOException {
        if (folder == null) {
            throw new NullPointerException("folder is null");
        }
        if (folder.isFile()) {
            throw new IllegalArgumentException("folder is a file");
        }
        if (!folder.exists()) {
            Files.createDirectories(folder.toPath());
        }

        this.folder = folder;
    }

    private File getFolderPath(String path) {
        return new File(folder, path);
    }

    private File getFilePath(String path, String id) {
        String formatedID = id.replace(">", "").replace("<", "");

        File folderPath = getFolderPath(path);
        folderPath.mkdirs();
        return new File(folderPath, formatedID + ".json");
    }

    @Override
    public StoredMailInfo loadMailInfo(String id, String path) throws DAOException {
        File filePath = getFilePath(path, id);

        StoredMailInfo geladen = null;
        if (filePath.exists()) {
            try {
                geladen = Serializer.deserializeJson(filePath, StoredMailInfo.class);
            } catch (IOException ex) {
                throw new DAOException("Could not load MailInfo", ex);
            }
        }

        return geladen;
    }

    @Override
    public void saveMailInfo(StoredMailInfo info, String path) throws DAOException {
        final File filePath = getFilePath(path, info.getID());

        try {
            Serializer.serializeObjectToJson(filePath, info);
        } catch (IOException ex) {
            throw new DAOException("Could not save MailInfo", ex);
        }
    }

    @Override
    public void deleteMailInfo(String id, String path) throws DAOException {
        final File filePath = getFilePath(path, id);

        try {
            Files.delete(filePath.toPath());
        } catch (IOException ex) {
            throw new DAOException("Could not delete MailInfo", ex);
        }
    }

}
