package de.outlookklon.dao.impl;

import de.outlookklon.dao.DAOException;
import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Hendrik Karwanni
 */
public class StoredMailInfoDAOFilePersistenceTest {

    private static final String ID = "abcd1234";
    private static final StoredMailInfo TEST_MAIL_INFO = new StoredMailInfo(ID);
    private static final String PATH = "aPath/bPath";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private StoredMailInfoDAO dao;

    @Before
    public void init() throws Exception {
        dao = new StoredMailInfoDAOFilePersistence(folder.getRoot());
        dao.saveStoredMailInfo(TEST_MAIL_INFO, PATH);
    }

    @After
    public void clear() throws Exception {
        if (dao.loadStoredMailInfo(ID, PATH) != null) {
            dao.deleteStoredMailInfo(ID, PATH);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateFilePersistence_DirectoryIsNull() throws Exception {
        new StoredMailInfoDAOFilePersistence(null).toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateFilePersistence_DirectoryIsFile() throws Exception {
        File newFile = folder.newFile();

        new StoredMailInfoDAOFilePersistence(newFile).toString();
    }

    @Test
    public void shouldCreateFilePersistence_DirectoryDoesNotExist() throws Exception {
        File newDirectory = new File(folder.getRoot(), "tmpDir");
        assertThat(newDirectory.exists(), is(false));

        new StoredMailInfoDAOFilePersistence(newDirectory).toString();
        assertThat(newDirectory.exists(), is(true));
    }

    @Test
    public void shouldNotLoadStoredMailInfo_FileDoesNotExist() throws Exception {
        String nonExistentID = "ANonExistantID";
        String path = "aPath";

        StoredMailInfo mailInfo = dao.loadStoredMailInfo(nonExistentID, path);
        assertThat(mailInfo, is(nullValue()));
    }

    @Test(expected = DAOException.class)
    public void shouldNotLoadStoredMailInfo_InvalidFile() throws Exception {
        String id = "AInvalidFile";
        String fileName = id + ".json";
        String path = "aPath";

        File tmpFolder = new File(folder.getRoot(), path);
        tmpFolder.mkdirs();

        File file = new File(tmpFolder, fileName);
        FileUtils.writeStringToFile(file, "Invalid data for a StoredMailInfo");

        dao.loadStoredMailInfo(id, path);
    }

    @Test
    public void shouldLoadStoredMailInfo() throws Exception {
        StoredMailInfo expResult = TEST_MAIL_INFO;
        StoredMailInfo result = dao.loadStoredMailInfo(ID, PATH);
        assertThat(result, is(equalTo(expResult)));
    }

    @Test(expected = DAOException.class)
    public void shouldNotSaveStoredMailInfo_FileInUse() throws Exception {
        String id = "anID";
        String fileName = id + ".json";
        String path = "aPath";

        File tmpFolder = new File(folder.getRoot(), path);
        tmpFolder.mkdirs();

        File file = new File(tmpFolder, fileName);
        FileUtils.writeStringToFile(file, "Invalid data for a StoredMailInfo");

        FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
        FileLock lock = channel.lock();

        StoredMailInfo info = new StoredMailInfo(id);
        try {
            dao.saveStoredMailInfo(info, path);
        } finally {
            lock.release();
        }
    }

    @Test
    public void shouldSaveStoredMailInfo() throws Exception {
        dao.deleteStoredMailInfo(ID, PATH);
        StoredMailInfo beforeInfo = dao.loadStoredMailInfo(ID, PATH);
        assertThat(beforeInfo, is(nullValue()));

        dao.saveStoredMailInfo(TEST_MAIL_INFO, PATH);

        StoredMailInfo afterInfo = dao.loadStoredMailInfo(ID, PATH);
        assertThat(afterInfo, is(not(nullValue())));
    }

    @Test(expected = DAOException.class)
    public void shouldNotDeleteStoredMailInfo_InfoDoesNotExist() throws Exception {
        String nonExistentID = "ANonExistantID";
        String path = "aPath";

        dao.deleteStoredMailInfo(nonExistentID, path);
    }

    @Test
    public void shouldDeleteStoredMailInfo() throws Exception {
        StoredMailInfo beforeInfo = dao.loadStoredMailInfo(ID, PATH);
        assertThat(beforeInfo, is(not(nullValue())));

        dao.deleteStoredMailInfo(ID, PATH);

        StoredMailInfo afterInfo = dao.loadStoredMailInfo(ID, PATH);
        assertThat(afterInfo, is(nullValue()));
    }
}
