package de.outlook_klon.logik.kontakte;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TestKontaktverwaltung {
	private Kontaktverwaltung verwaltung;

	@Before
	public void setUp() {
		verwaltung = new Kontaktverwaltung();
	}

	@Test
	public void testAddKontakt() {
		Kontakt k1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null, null, "", "", "");
		Kontakt k2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster", null, null, "", "", "");
		Kontakt[] expected1 = new Kontakt[] { k1, k2 };
		Kontakt[] expected2 = new Kontakt[] { k2, k1 };

		for (int i = 0; i < 10; i++) {
			verwaltung.addKontakt(k1);
			verwaltung.addKontakt(k2);
		}
		Kontakt[] result = verwaltung.getKontakte(Kontaktverwaltung.DEFAULT);

		if (result[0] == k1)
			assertArrayEquals(expected1, result);
		else
			assertArrayEquals(expected2, result);

		verwaltung.löscheKontakt(k1);
		verwaltung.löscheKontakt(k2);
	}

	@Test(expected = NullPointerException.class)
	public void testAddKontaktException() {
		verwaltung.addKontakt(null);
	}

	@Test
	public void testAddKontaktListe() {
		Kontakt k1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null, null, "", "", "");
		Kontakt k2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster", null, null, "", "", "");

		String liste = "test";
		verwaltung.addListe(liste);

		Kontakt[] expected = new Kontakt[] { k2 };

		verwaltung.addKontakt(k1);
		verwaltung.addKontaktZuListe(k2, liste);

		Kontakt[] result = verwaltung.getKontakte(liste);

		assertArrayEquals(expected, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddKontaktListeException1() {
		Kontakt k = new Kontakt("Mustermann", "Max", "Max Mustermann", "Muster", null, null, "", "", "");
		String liste = "NichtExistent";

		verwaltung.addKontaktZuListe(k, liste);
	}

	@Test(expected = NullPointerException.class)
	public void testAddKontaktListeException2() {
		Kontakt k = new Kontakt("Mustermann", "Max", "Max Mustermann", "Muster", null, null, "", "", "");
		String liste = "";

		verwaltung.addKontaktZuListe(k, liste);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddKontaktListeException3() {
		Kontakt k = new Kontakt("Mustermann", "Max", "Max Mustermann", "Muster", null, null, "", "", "");
		String liste = "NichtExistent";

		verwaltung.addListe(liste);
		verwaltung.addKontaktZuListe(k, liste);
		verwaltung.addKontaktZuListe(k, liste);
	}

	@Test
	public void testAddListe() {
		String[] listen = new String[] { "Test", "Arbeit" };
		String[] expected = new String[] { Kontaktverwaltung.DEFAULT, "Test", "Arbeit" };

		for (int i = 0; i < listen.length; i++) {
			verwaltung.addListe(listen[i]);
		}
		String[] result = verwaltung.getListen();
		Arrays.sort(result);
		Arrays.sort(expected);

		assertArrayEquals(expected, result);
	}

	@Test(expected = NullPointerException.class)
	public void testAddListeException1() {
		String liste = "";

		verwaltung.addListe(liste);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddListeException2() {
		String liste = "Redundant";

		verwaltung.addListe(liste);
		verwaltung.addListe(liste);
	}

	@Test
	public void testLöscheKontakt() {
		Kontakt k1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null, null, "", "", "");
		Kontakt k2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster", null, null, "", "", "");

		Kontakt[] expected = new Kontakt[] { k1 };

		verwaltung.addKontakt(k1);
		verwaltung.addKontakt(k2);

		verwaltung.löscheKontakt(k2);

		Kontakt[] result = verwaltung.getKontakte(Kontaktverwaltung.DEFAULT);

		assertArrayEquals(expected, result);
	}

	@Test(expected = NullPointerException.class)
	public void testLöscheKontaktException() {
		verwaltung.löscheKontakt(null);
	}

	@Test
	public void testLöscheKontaktAusListe() {
		Kontakt k1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null, null, "", "", "");
		Kontakt k2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster", null, null, "", "", "");

		String liste = "test";
		verwaltung.addListe(liste);

		Kontakt[] expected = new Kontakt[] { k2 };

		verwaltung.addKontaktZuListe(k1, liste);
		verwaltung.addKontaktZuListe(k2, liste);

		verwaltung.löscheKontakt(k1, liste);

		Kontakt[] result = verwaltung.getKontakte(liste);

		assertArrayEquals(expected, result);
	}

	@Test(expected = NullPointerException.class)
	public void testLöscheKontaktAusListeException1() {
		Kontakt k1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null, null, "", "", "");
		Kontakt k2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster", null, null, "", "", "");

		String liste = "test";
		verwaltung.addListe(liste);

		verwaltung.addKontaktZuListe(k1, liste);
		verwaltung.addKontaktZuListe(k2, liste);

		verwaltung.löscheKontakt(null, liste);
	}

	@Test(expected = NullPointerException.class)
	public void testLöscheKontaktAusListeException2() {
		Kontakt k1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null, null, "", "", "");
		Kontakt k2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster", null, null, "", "", "");

		String liste = "test";
		verwaltung.addListe(liste);

		verwaltung.addKontaktZuListe(k1, liste);
		verwaltung.addKontaktZuListe(k2, liste);

		verwaltung.löscheKontakt(k1, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLöscheKontaktAusListeException3() {
		Kontakt k1 = new Kontakt("Mustermann", "Max", "Max Mustermann", "MMuster", null, null, "", "", "");
		Kontakt k2 = new Kontakt("Mustermann", "Erika", "Erika Mustermann", "EMuster", null, null, "", "", "");

		String liste1 = "test";
		String liste2 = "NichtExistent";

		verwaltung.addListe(liste1);

		verwaltung.addKontaktZuListe(k1, liste1);
		verwaltung.addKontaktZuListe(k2, liste1);

		verwaltung.löscheKontakt(k1, liste2);
	}

	@Test
	public void testLöscheListe() {
		String liste1 = "test1";
		String liste2 = "test2";

		verwaltung.addListe(liste1);
		verwaltung.addListe(liste2);

		String[] expected = new String[] { Kontaktverwaltung.DEFAULT };

		verwaltung.löscheListe(liste1);
		verwaltung.löscheListe(liste2);

		String[] result = verwaltung.getListen();

		assertArrayEquals(expected, result);
	}

	@Test(expected = NullPointerException.class)
	public void testLöscheListeException1() {
		verwaltung.löscheListe("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLöscheListeException2() {
		verwaltung.löscheListe(Kontaktverwaltung.DEFAULT);
	}

	@Test(expected = NullPointerException.class)
	public void testLöscheListeException3() {
		String liste1 = "test";
		String liste2 = "NichtExistent";

		verwaltung.addListe(liste1);
		verwaltung.löscheListe(liste2);
	}

	@Test
	public void testRenameListe() {
		String alterName = "test1";
		String neuerName = "test2";

		String[] expected = new String[] { Kontaktverwaltung.DEFAULT, neuerName };
		verwaltung.addListe(alterName);
		verwaltung.renameListe(alterName, neuerName);
		String[] result = verwaltung.getListen();

		assertArrayEquals(expected, result);
	}

	@Test(expected = NullPointerException.class)
	public void testRenameListeException1() {
		verwaltung.renameListe("", "Test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRenameListeException2() {
		verwaltung.renameListe(Kontaktverwaltung.DEFAULT, "Test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRenameListeException3() {
		verwaltung.renameListe("Alte Liste", "Neue Liste");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRenameListeException4() {
		String liste1 = "test1";
		String liste2 = "test2";

		verwaltung.addListe(liste1);
		verwaltung.addListe(liste2);

		verwaltung.renameListe(liste1, liste2);
	}
}
