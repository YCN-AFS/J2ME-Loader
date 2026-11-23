package javax.microedition.lcdui.keyboard;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RowCenteringTest {

	// Bom B on a 1080px wide screen: L R / up / left fire right / down / * #
	private static final float[] L = {47, 619, 380, 95, 333, 595, 380, 47, 712};
	private static final float[] T = {1747, 1747, 1858, 1997, 2007, 1997, 2136, 2273, 2273};
	private static final float[] R = {368, 940, 607, 321, 582, 821, 607, 368, 1033};
	private static final float[] B = {1860, 1860, 1997, 2136, 2126, 2136, 2274, 2386, 2386};

	@Test
	public void bomB_rowsAreCenteredAsGroups() {
		float[] s = VirtualKeyboard.computeRowShifts(L, T, R, B, 0, 1080);
		// L and R move together by the same amount
		assertEquals(s[0], s[1], 0);
		assertEquals(46.5f, s[0], 0.01f);
		// up and down arrows end up centred
		assertEquals(540f, (L[2] + s[2] + R[2] + s[2]) / 2, 0.01f);
		assertEquals(540f, (L[6] + s[6] + R[6] + s[6]) / 2, 0.01f);
		// left / fire / right keep their relative positions and are centred as one row
		assertEquals(s[3], s[4], 0);
		assertEquals(s[3], s[5], 0);
		assertEquals(540f, ((L[3] + s[3]) + (R[5] + s[5])) / 2, 0.01f);
		// the already symmetric bottom row is untouched
		assertEquals(0f, s[7], 0);
		assertEquals(0f, s[8], 0);
	}

	@Test
	public void rowDeliberatelyPushedToOneSideIsLeftAlone() {
		// a left-hand d-pad row spanning 0..700 on a 1080px screen is 190px off centre (> 12%)
		float[] s = VirtualKeyboard.computeRowShifts(
				new float[]{0, 250, 500}, new float[]{100, 100, 100},
				new float[]{200, 450, 700}, new float[]{200, 200, 200}, 0, 1080);
		assertEquals(0f, s[0], 0);
		assertEquals(0f, s[1], 0);
		assertEquals(0f, s[2], 0);
	}

	@Test
	public void alreadyCenteredRowStaysAndSmallOffsetShifts() {
		// row 0..1000 centre 500, screen 0..1000: perfectly centred -> no shift
		float[] s = VirtualKeyboard.computeRowShifts(
				new float[]{0}, new float[]{0}, new float[]{1000}, new float[]{100}, 0, 1000);
		assertEquals(0f, s[0], 0);
		// row 100..1000 is 50px right of centre -> shifted left by 50
		float[] s2 = VirtualKeyboard.computeRowShifts(
				new float[]{100}, new float[]{0}, new float[]{1000}, new float[]{100}, 0, 1000);
		assertEquals(-50f, s2[0], 0.001f);
	}

	@Test
	public void keysInDifferentRowsAreIndependent() {
		float[] s = VirtualKeyboard.computeRowShifts(
				new float[]{40, 100}, new float[]{0, 300}, new float[]{240, 300},
				new float[]{100, 400}, 0, 1000);
		// row 1: 40..240 centre 140 -> +360 > 12% (120) -> skipped; row 2: 100..300 centre 200 -> skipped
		assertEquals(0f, s[0], 0);
		assertEquals(0f, s[1], 0);
	}
}
