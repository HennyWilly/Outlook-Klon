package de.outlook_klon.logik.kalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class TestTerminkalender {	
	private Terminkalender kalender;

	@Before
	public void setUp() {
		kalender = new Terminkalender();
	}

	@Test
	public void testUeberschneidung() {
		Termin terminA = new Termin(null, null, new DateTime(2014, 10, 5, 20, 15, 00).toDate(), new DateTime(2014, 10, 10, 20, 15, 00).toDate(),
				null, null, null);
		Termin terminB = new Termin(null, null, new DateTime(2014, 10, 4, 10, 00, 00).toDate(), new DateTime(2014, 10, 6, 10, 00, 00).toDate(),
				null, null, null);

		kalender.addTermin(terminA);
		assertTrue(kalender.ueberschneidung(terminB));
	}

	@Test
	public void testgetOldest() {

		Termin terminA = new Termin(null, null, new DateTime(2014, 10, 4, 20, 15, 00).toDate(), new DateTime(2014, 10, 10, 20, 15, 00).toDate(),
				null, null, null);
		Termin terminB = new Termin(null, null, new DateTime(2014, 10, 5, 10, 00, 00).toDate(), new DateTime(2014, 10, 10, 10, 00, 00).toDate(),
				null, null, null);
		Termin terminC = new Termin(null, null, new DateTime(2014, 10, 6, 20, 15, 00).toDate(), new DateTime(2014, 10, 10, 20, 15, 00).toDate(),
				null, null, null);

		kalender.addTermin(terminC);
		kalender.addTermin(terminA);
		kalender.addTermin(terminB);

		assertEquals(terminA, kalender.getOldest());
	}

	@Test
	public void testgetTermine() {
		Termin terminA = new Termin(null, null, new DateTime(2014, 10, 4, 20, 15, 00).toDate(), new DateTime(2014, 10, 10, 20, 15, 00).toDate(),
				null, null, null);
		Termin terminB = new Termin(null, null, new DateTime(2014, 10, 5, 10, 00, 00).toDate(), new DateTime(2014, 10, 10, 10, 00, 00).toDate(),
				null, null, null);
		Termin terminC = new Termin(null, null, new DateTime(2014, 10, 6, 20, 15, 00).toDate(), new DateTime(2014, 10, 10, 20, 15, 00).toDate(),
				null, null, null);
		Termin terminD = new Termin(null, null, new DateTime(2014, 10, 7, 20, 15, 00).toDate(), new DateTime(2014, 10, 10, 20, 15, 00).toDate(),
				null, null, null);
		Termin terminE = new Termin(null, null, new DateTime(2014, 10, 8, 10, 00, 00).toDate(), new DateTime(2014, 10, 10, 10, 00, 00).toDate(),
				null, null, null);
		Termin terminF = new Termin(null, null, new DateTime(2014, 10, 9, 20, 15, 00).toDate(), new DateTime(2014, 10, 10, 20, 15, 00).toDate(),
				null, null, null);

		kalender.addTermin(terminA);
		kalender.addTermin(terminB);
		kalender.addTermin(terminC);
		kalender.addTermin(terminD);
		kalender.addTermin(terminE);
		kalender.addTermin(terminF);

		Termin[] expected = new Termin[] { terminB, terminC, terminD };

		assertArrayEquals(expected,
				kalender.getTermine(new DateTime(2014, 10, 5, 1, 00, 00).toDate(), new DateTime(2014, 10, 7, 23, 59, 59).toDate()));
	}

}