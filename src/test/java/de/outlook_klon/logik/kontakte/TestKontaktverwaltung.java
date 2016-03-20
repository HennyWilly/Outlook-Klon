package de.outlook_klon.logik.kontakte;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TestKontaktverwaltung {
	private static final Kontakt TEST_CONTACT_1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null,
			null, "", "", "");
	private static final Kontakt TEST_CONTACT_2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster",
			null, null, "", "", "");

	private Kontaktverwaltung verwaltung;

	@Before
	public void setUp() {
		verwaltung = new Kontaktverwaltung();
	}

	@Test
	public void shouldAddContact() {
		Kontakt[] expected1 = new Kontakt[] { TEST_CONTACT_1, TEST_CONTACT_2 };
		Kontakt[] expected2 = new Kontakt[] { TEST_CONTACT_2, TEST_CONTACT_1 };

		for (int i = 0; i < 10; i++) {
			verwaltung.addKontakt(TEST_CONTACT_1);
			verwaltung.addKontakt(TEST_CONTACT_2);
		}
		Kontakt[] result = verwaltung.getKontakte(Kontaktverwaltung.DEFAULT);

		if (result[0] == TEST_CONTACT_1)
			assertThat(result, is(expected1));
		else
			assertThat(result, is(expected2));

		verwaltung.löscheKontakt(TEST_CONTACT_1);
		verwaltung.löscheKontakt(TEST_CONTACT_2);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotAddContact_ContactIsNull() {
		verwaltung.addKontakt(null);
	}

	@Test
	public void shouldAddContactToList() {
		String list = "test";
		verwaltung.addListe(list);

		Kontakt[] expected = new Kontakt[] { TEST_CONTACT_2 };

		verwaltung.addKontakt(TEST_CONTACT_1);
		verwaltung.addKontaktZuListe(TEST_CONTACT_2, list);

		Kontakt[] result = verwaltung.getKontakte(list);

		assertThat(result, is(expected));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddContactToList_ListDoesNotExist() {
		String list = "NonExistent";

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotAddContactToList_ListNameEmpty() {
		String list = "";

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddContactToList_ContactAlreadInList() {
		String list = "NonExistent";

		verwaltung.addListe(list);
		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotAddContactToList_ContactIsNull() {
		Kontakt k = null;
		String list = "NonExistent";

		verwaltung.addKontaktZuListe(k, list);
	}

	@Test
	public void shouldAddList() {
		String[] lists = new String[] { "test", "work" };
		String[] expected = new String[] { Kontaktverwaltung.DEFAULT, "test", "work" };

		for (int i = 0; i < lists.length; i++) {
			verwaltung.addListe(lists[i]);
		}
		String[] result = verwaltung.getListen();
		Arrays.sort(result);
		Arrays.sort(expected);

		assertThat(result, is(expected));
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotAddList_ListNameEmpty() {
		String list = "";

		verwaltung.addListe(list);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddList_ListAlreadyExists() {
		String list = "redundant";

		verwaltung.addListe(list);
		verwaltung.addListe(list);
	}

	@Test
	public void shouldDeleteContact() {
		Kontakt[] expected = new Kontakt[] { TEST_CONTACT_1 };

		verwaltung.addKontakt(TEST_CONTACT_1);
		verwaltung.addKontakt(TEST_CONTACT_2);

		verwaltung.löscheKontakt(TEST_CONTACT_2);

		Kontakt[] result = verwaltung.getKontakte(Kontaktverwaltung.DEFAULT);

		assertThat(result, is(expected));
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotDeleteContact_ContactIsNull() {
		verwaltung.löscheKontakt(null);
	}

	@Test
	public void shouldDeleteContactInList() {
		String list = "test";
		verwaltung.addListe(list);

		Kontakt[] expected = new Kontakt[] { TEST_CONTACT_2 };

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
		verwaltung.addKontaktZuListe(TEST_CONTACT_2, list);

		verwaltung.löscheKontakt(TEST_CONTACT_1, list);

		Kontakt[] result = verwaltung.getKontakte(list);

		assertThat(result, is(expected));
	}

	@Test
	public void shouldDeleteContactInDefaultList() {
		String list = Kontaktverwaltung.DEFAULT;
		Kontakt[] expected = new Kontakt[] { TEST_CONTACT_2 };

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
		verwaltung.addKontaktZuListe(TEST_CONTACT_2, list);

		verwaltung.löscheKontakt(TEST_CONTACT_1, list);

		Kontakt[] result = verwaltung.getKontakte(list);

		assertThat(result, is(expected));
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotDeleteContactInList_ContactIsNull() {
		String list = "test";
		verwaltung.addListe(list);

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
		verwaltung.addKontaktZuListe(TEST_CONTACT_2, list);

		verwaltung.löscheKontakt(null, list);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotDeleteContactInList_ListNameEmpty() {
		String list = "test";
		verwaltung.addListe(list);

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list);
		verwaltung.addKontaktZuListe(TEST_CONTACT_2, list);

		verwaltung.löscheKontakt(TEST_CONTACT_1, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotDeleteContactInList_ContactNotInList() {
		String list1 = "test";
		String list2 = "NonExistent";

		verwaltung.addListe(list1);

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list1);
		verwaltung.addKontaktZuListe(TEST_CONTACT_2, list1);

		verwaltung.löscheKontakt(TEST_CONTACT_1, list2);
	}

	@Test
	public void shouldDeleteList() {
		String list1 = "test1";
		String list2 = "test2";

		verwaltung.addListe(list1);
		verwaltung.addListe(list2);

		String[] expected = new String[] { Kontaktverwaltung.DEFAULT };

		verwaltung.löscheListe(list1);
		verwaltung.löscheListe(list2);

		String[] result = verwaltung.getListen();

		assertThat(result, is(expected));
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotDeleteList_ListNameIsEmpty() {
		verwaltung.löscheListe("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotDeleteList_ListIsDefaultList() {
		verwaltung.löscheListe(Kontaktverwaltung.DEFAULT);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotDeleteList_ListNameDoesNotExist() {
		String list1 = "test";
		String list2 = "NonExistent";

		verwaltung.addListe(list1);
		verwaltung.löscheListe(list2);
	}

	@Test
	public void shouldRenameList() {
		String oldName = "test1";
		String newName = "test2";

		String[] expected = new String[] { Kontaktverwaltung.DEFAULT, newName };
		verwaltung.addListe(oldName);
		verwaltung.renameListe(oldName, newName);
		String[] result = verwaltung.getListen();

		assertThat(result, is(expected));
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotRenameList_OldListNameEmpty() {
		verwaltung.renameListe("", "Test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRenameList_OldListNameIsDefault() {
		verwaltung.renameListe(Kontaktverwaltung.DEFAULT, "Test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRenameList_OldListDoesNotExist() {
		verwaltung.renameListe("old list", "new list");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRenameList_NewListAlreadyExists() {
		String list1 = "test1";
		String list2 = "test2";

		verwaltung.addListe(list1);
		verwaltung.addListe(list2);

		verwaltung.renameListe(list1, list2);
	}

	@Test
	public void shouldReturnLists() {
		String list1 = "test1";
		String list2 = "test2";
		String list3 = "test3";

		verwaltung.addListe(list1);
		verwaltung.addListe(list2);
		verwaltung.addListe(list3);

		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list1);
		verwaltung.addKontaktZuListe(TEST_CONTACT_1, list3);

		String[] expected = { list1, list3 };
		String[] actual = verwaltung.getListen(TEST_CONTACT_1);

		assertThat(actual, is(expected));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotReturnContacts_ListDoesNotExist() {
		verwaltung.getKontakte("ANonexistentList");
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotReturnContacts_ListNameIsNull() {
		verwaltung.getKontakte(null);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotReturnContacts_ListNameIsEmpty() {
		verwaltung.getKontakte(" ");
	}
}
