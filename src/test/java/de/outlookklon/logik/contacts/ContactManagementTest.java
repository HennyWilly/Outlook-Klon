package de.outlookklon.logik.contacts;

import java.util.Arrays;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class ContactManagementTest {

    private static final Contact TEST_CONTACT_1 = new Contact("Mustermann", "Max", "Max Mustermann", "MMuster", null,
            null, "", "", "");
    private static final Contact TEST_CONTACT_2 = new Contact("Mustermann", "Erika", "Erika Mustermann", "EMuster",
            null, null, "", "", "");

    private ContactManagement management;

    @Before
    public void setUp() {
        management = new ContactManagement();
    }

    @Test
    public void shouldAddContact() {
        Contact[] expected1 = new Contact[]{TEST_CONTACT_1, TEST_CONTACT_2};
        Contact[] expected2 = new Contact[]{TEST_CONTACT_2, TEST_CONTACT_1};

        for (int i = 0; i < 10; i++) {
            management.addContact(TEST_CONTACT_1);
            management.addContact(TEST_CONTACT_2);
        }
        Contact[] result = management.getContacts(ContactManagement.DEFAULT);

        if (result[0] == TEST_CONTACT_1) {
            assertThat(result, is(expected1));
        } else {
            assertThat(result, is(expected2));
        }

        management.deleteContact(TEST_CONTACT_1);
        management.deleteContact(TEST_CONTACT_2);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddContact_ContactIsNull() {
        management.addContact(null);
    }

    @Test
    public void shouldAddContactToList() {
        String list = "test";
        management.addList(list);

        Contact[] expected = new Contact[]{TEST_CONTACT_2};

        management.addContact(TEST_CONTACT_1);
        management.addToContactList(TEST_CONTACT_2, list);

        Contact[] result = management.getContacts(list);

        assertThat(result, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddContactToList_ListDoesNotExist() {
        String list = "NonExistent";

        management.addToContactList(TEST_CONTACT_1, list);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddContactToList_ListNameEmpty() {
        String list = "";

        management.addToContactList(TEST_CONTACT_1, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddContactToList_ContactAlreadInList() {
        String list = "NonExistent";

        management.addList(list);
        management.addToContactList(TEST_CONTACT_1, list);
        management.addToContactList(TEST_CONTACT_1, list);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddContactToList_ContactIsNull() {
        Contact k = null;
        String list = "NonExistent";

        management.addToContactList(k, list);
    }

    @Test
    public void shouldAddList() {
        String[] lists = new String[]{"test", "work"};
        String[] expected = new String[]{ContactManagement.DEFAULT, "test", "work"};

        for (String list : lists) {
            management.addList(list);
        }
        String[] result = management.getLists();
        Arrays.sort(result);
        Arrays.sort(expected);

        assertThat(result, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddList_ListNameEmpty() {
        String list = "";

        management.addList(list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddList_ListAlreadyExists() {
        String list = "redundant";

        management.addList(list);
        management.addList(list);
    }

    @Test
    public void shouldDeleteContact() {
        Contact[] expected = new Contact[]{TEST_CONTACT_1};

        management.addContact(TEST_CONTACT_1);
        management.addContact(TEST_CONTACT_2);

        management.deleteContact(TEST_CONTACT_2);

        Contact[] result = management.getContacts(ContactManagement.DEFAULT);

        assertThat(result, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotDeleteContact_ContactIsNull() {
        management.deleteContact(null);
    }

    @Test
    public void shouldDeleteContactInList() {
        String list = "test";
        management.addList(list);

        Contact[] expected = new Contact[]{TEST_CONTACT_2};

        management.addToContactList(TEST_CONTACT_1, list);
        management.addToContactList(TEST_CONTACT_2, list);

        management.deleteContact(TEST_CONTACT_1, list);

        Contact[] result = management.getContacts(list);

        assertThat(result, is(expected));
    }

    @Test
    public void shouldDeleteContactInDefaultList() {
        String list = ContactManagement.DEFAULT;
        Contact[] expected = new Contact[]{TEST_CONTACT_2};

        management.addToContactList(TEST_CONTACT_1, list);
        management.addToContactList(TEST_CONTACT_2, list);

        management.deleteContact(TEST_CONTACT_1, list);

        Contact[] result = management.getContacts(list);

        assertThat(result, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotDeleteContactInList_ContactIsNull() {
        String list = "test";
        management.addList(list);

        management.addToContactList(TEST_CONTACT_1, list);
        management.addToContactList(TEST_CONTACT_2, list);

        management.deleteContact(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotDeleteContactInList_ListNameEmpty() {
        String list = "test";
        management.addList(list);

        management.addToContactList(TEST_CONTACT_1, list);
        management.addToContactList(TEST_CONTACT_2, list);

        management.deleteContact(TEST_CONTACT_1, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDeleteContactInList_ContactNotInList() {
        String list1 = "test";
        String list2 = "NonExistent";

        management.addList(list1);

        management.addToContactList(TEST_CONTACT_1, list1);
        management.addToContactList(TEST_CONTACT_2, list1);

        management.deleteContact(TEST_CONTACT_1, list2);
    }

    @Test
    public void shouldDeleteList() {
        String list1 = "test1";
        String list2 = "test2";

        management.addList(list1);
        management.addList(list2);

        String[] expected = new String[]{ContactManagement.DEFAULT};

        management.deleteList(list1);
        management.deleteList(list2);

        String[] result = management.getLists();

        assertThat(result, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotDeleteList_ListNameIsEmpty() {
        management.deleteList("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDeleteList_ListIsDefaultList() {
        management.deleteList(ContactManagement.DEFAULT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDeleteList_ListNameDoesNotExist() {
        String list1 = "test";
        String list2 = "NonExistent";

        management.addList(list1);
        management.deleteList(list2);
    }

    @Test
    public void shouldRenameList() {
        String oldName = "test1";
        String newName = "test2";

        String[] expected = new String[]{ContactManagement.DEFAULT, newName};
        management.addList(oldName);
        management.renameList(oldName, newName);
        String[] result = management.getLists();

        assertThat(result, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotRenameList_OldListNameEmpty() {
        management.renameList("", "Test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotRenameList_OldListNameIsDefault() {
        management.renameList(ContactManagement.DEFAULT, "Test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotRenameList_OldListDoesNotExist() {
        management.renameList("old list", "new list");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotRenameList_NewListAlreadyExists() {
        String list1 = "test1";
        String list2 = "test2";

        management.addList(list1);
        management.addList(list2);

        management.renameList(list1, list2);
    }

    @Test
    public void shouldReturnLists() {
        String list1 = "test1";
        String list2 = "test2";
        String list3 = "test3";

        management.addList(list1);
        management.addList(list2);
        management.addList(list3);

        management.addToContactList(TEST_CONTACT_1, list1);
        management.addToContactList(TEST_CONTACT_1, list3);

        String[] expected = {list1, list3};
        String[] actual = management.getLists(TEST_CONTACT_1);

        assertThat(actual, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotReturnContacts_ListDoesNotExist() {
        management.getContacts("ANonexistentList");
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotReturnContacts_ListNameIsNull() {
        management.getContacts(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotReturnContacts_ListNameIsEmpty() {
        management.getContacts(" ");
    }
}
