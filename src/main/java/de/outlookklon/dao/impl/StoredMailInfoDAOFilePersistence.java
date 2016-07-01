package de.outlookklon.dao.impl;

import de.outlookklon.Program;
import de.outlookklon.dao.DAOException;
import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import de.outlookklon.serializers.Serializer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.NonNull;

/**
 * Diese Klasse implementiert die {@link StoredMailInfoDAO}-Schnittstelle f�r
 * den Zugriff auf die persistierten {@link StoredMailInfo}-Objekte in
 * Dateiform.
 *
 * @author Hendrik Karwanni
 */
public class StoredMailInfoDAOFilePersistence implements StoredMailInfoDAO {

    private File folder;

    public StoredMailInfoDAOFilePersistence(@NonNull File folder) throws IOException {
        if (folder.isFile()) {
            throw new IllegalArgumentException(Program.STRINGS.getString("StoredMailInfoDAO_NotAFolder"));
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
    public StoredMailInfo loadStoredMailInfo(@NonNull String id, @NonNull String path)
            throws DAOException {
        File filePath = getFilePath(path, id);

        StoredMailInfo geladen = null;
        if (filePath.exists()) {
            try {
                geladen = Serializer.deserializeJson(filePath, StoredMailInfo.class);
            } catch (IOException ex) {
                throw new DAOException(Program.STRINGS.getString("StoredMailInfoDAO_CouldNotLoadMailInfo"), ex);
            }
        }

        return geladen;
    }

    @Override
    public void saveStoredMailInfo(@NonNull StoredMailInfo info, @NonNull String path)
            throws DAOException {
        final File filePath = getFilePath(path, info.getID());

        try {
            Serializer.serializeObjectToJson(filePath, info);
        } catch (IOException ex) {
            throw new DAOException(Program.STRINGS.getString("StoredMailInfoDAO_CouldNotSaveMailInfo"), ex);
        }
    }

    @Override
    public void deleteStoredMailInfo(@NonNull String id, @NonNull String path)
            throws DAOException {
        final File filePath = getFilePath(path, id);

        try {
            Files.delete(filePath.toPath());
        } catch (IOException ex) {
            throw new DAOException(Program.STRINGS.getString("StoredMailInfoDAO_CouldNotDeleteMailInfo"), ex);
        }
    }

}
