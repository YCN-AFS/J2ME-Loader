package javax.microedition.lcdui.keyboard;

import static org.junit.Assert.assertEquals;

import android.graphics.RectF;

import org.junit.Test;

public class KeyOverlapTest {

	private static RectF rect(float l, float t, float r, float b) {
		// android.jar is stubbed in unit tests, so set the public fields directly
		RectF rect = new RectF();
		rect.left = l;
		rect.top = t;
		rect.right = r;
		rect.bottom = b;
		return rect;
	}

	@Test
	public void apartAndTouchingKeysDoNotOverlap() {
		assertEquals(0f, VirtualKeyboard.overlapRatio(rect(0, 0, 10, 10), rect(20, 0, 30, 10)), 0);
		assertEquals(0f, VirtualKeyboard.overlapRatio(rect(0, 0, 10, 10), rect(10, 0, 20, 10)), 0);
		assertEquals(0f, VirtualKeyboard.overlapRatio(rect(0, 0, 10, 10), rect(0, 10, 10, 20)), 0);
	}

	@Test
	public void partialOverlapIsFractionOfSmallerKey() {
		// 5x10 overlap out of a 10x10 key
		assertEquals(0.5f, VirtualKeyboard.overlapRatio(rect(0, 0, 10, 10), rect(5, 0, 15, 10)), 1e-6f);
		// big key fully covering a small one
		assertEquals(1f, VirtualKeyboard.overlapRatio(rect(0, 0, 100, 100), rect(10, 10, 20, 20)), 1e-6f);
	}

	@Test
	public void overlapIsSymmetric() {
		RectF a = rect(0, 0, 10, 10);
		RectF b = rect(7, 3, 20, 25);
		assertEquals(VirtualKeyboard.overlapRatio(a, b), VirtualKeyboard.overlapRatio(b, a), 0);
	}

	@Test
	public void degenerateRectsGiveZero() {
		assertEquals(0f, VirtualKeyboard.overlapRatio(rect(0, 0, 0, 0), rect(0, 0, 10, 10)), 0);
	}
}
