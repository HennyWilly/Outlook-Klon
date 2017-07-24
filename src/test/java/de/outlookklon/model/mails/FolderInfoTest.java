package de.outlookklon.model.mails;

import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import org.junit.Test;

public class FolderInfoTest {

    @Test
    public void shouldCreateFolderInfo() throws Exception {
        FolderInfo folderInfo = new FolderInfo("Test", "TestPath", 0);
        assertThat(folderInfo.getName(), is("Test"));
        assertThat(folderInfo.getPath(), is("TestPath"));
        assertThat(folderInfo.getNumberUnread(), is(0));
        assertThat(folderInfo, hasToString("Test"));
    }

    @Test
    public void shouldBeEqual() throws Exception {
        FolderInfo folderInfo1 = new FolderInfo("Test", "TestPath", 0);
        FolderInfo folderInfo2 = new FolderInfo("Test", "TestPath", 0);

        assertThat(folderInfo1, is(equalTo(folderInfo2)));
    }

    @Test
    public void shouldBeEqual_SameInstance() throws Exception {
        FolderInfo folderInfo = new FolderInfo("Test", "TestPath", 0);

        assertThat(folderInfo, is(equalTo(folderInfo)));
    }

    @Test
    public void shouldNotBeEqual_Null() throws Exception {
        FolderInfo folderInfo = new FolderInfo("Test", "TestPath", 0);

        assertThat(folderInfo, is(not(equalTo(null))));
    }

    @Test
    public void shouldNotBeEqual_OtherClass() throws Exception {
        FolderInfo folderInfo = new FolderInfo("Test", "TestPath", 0);

        assertThat(folderInfo, is(not(equalTo(new Object()))));
    }

    @Test
    public void shouldTestHashCodeContract() throws Exception {
        Map<FolderInfo, String> map = new HashMap<>();

        map.put(new FolderInfo("Test1", "TestPath", 0), "aaaa");
        map.put(new FolderInfo("Test2", "TestPath", 0), "bbbb");

        assertThat(map, hasEntry(new FolderInfo("Test1", "TestPath", 0), "aaaa"));
        assertThat(map, hasEntry(new FolderInfo("Test2", "TestPath", 0), "bbbb"));
    }

}
