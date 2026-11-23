package javax.microedition.lcdui.keyboard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class VirtualKeyboardLayoutTest {
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	// groups: arrows, soft keys, A-D, numbers, fire, menu (x, y each)
	private static final float[] OVERLAPPING = {
			2, 0.75f, 2.4f, 0.9f, 2, 0.75f, 2, 0.75f, 2.2f, 1.0f, 2, 0.75f};

	private File writeLayout(int version, float[] scales) throws IOException {
		File f = tmp.newFile();
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(f))) {
			out.writeInt(VirtualKeyboard.LAYOUT_SIGNATURE);
			out.writeInt(version);
			out.writeInt(VirtualKeyboard.LAYOUT_TYPE);
			out.writeInt(1);
			out.write(0);
			out.writeInt(VirtualKeyboard.LAYOUT_KEYS);
			out.writeInt(21 + 4);
			out.writeInt(1);
			out.write(new byte[21]);
			out.writeInt(VirtualKeyboard.LAYOUT_SCALES);
			out.writeInt(scales.length * 4 + 4);
			out.writeInt(scales.length);
			for (float v : scales) {
				out.writeFloat(v);
			}
			out.writeInt(VirtualKeyboard.LAYOUT_EOF);
			out.writeInt(0);
		}
		return f;
	}

	/** The scales block is the last one before the 8-byte EOF marker. */
	private float[] readScales(File f) throws IOException {
		byte[] data = Files.readAllBytes(f.toPath());
		int count = 12;
		float[] out = new float[count];
		try (DataInputStream in = new DataInputStream(
				new ByteArrayInputStream(data, data.length - 8 - count * 4, count * 4))) {
			for (int i = 0; i < count; i++) {
				out[i] = in.readFloat();
			}
		}
		return out;
	}

	@Test
	public void matchScales_copiesNumberScaleToSoftAndFire() {
		float[] s = OVERLAPPING.clone();
		assertTrue(VirtualKeyboard.matchScales(s));
		assertEquals(2f, s[2], 0);
		assertEquals(0.75f, s[3], 0);
		assertEquals(2f, s[8], 0);
		assertEquals(0.75f, s[9], 0);
		// other groups untouched
		assertEquals(2f, s[0], 0);
		assertEquals(2f, s[4], 0);
		assertEquals(2f, s[10], 0);
		assertFalse(VirtualKeyboard.matchScales(s));
	}

	@Test
	public void normalizeLayoutFile_rewritesOnlyScales() throws Exception {
		File f = writeLayout(VirtualKeyboard.LAYOUT_VERSION, OVERLAPPING);
		long length = f.length();
		assertTrue(VirtualKeyboard.normalizeLayoutFile(f));
		assertEquals(length, f.length());
		float[] expected = OVERLAPPING.clone();
		VirtualKeyboard.matchScales(expected);
		assertArrayEquals(expected, readScales(f), 0);
		// second run has nothing to do
		assertFalse(VirtualKeyboard.normalizeLayoutFile(f));
	}

	@Test
	public void normalizeLayoutFile_ignoresOldVersionsAndGarbage() throws Exception {
		File old = writeLayout(2, OVERLAPPING);
		byte[] before = Files.readAllBytes(old.toPath());
		assertFalse(VirtualKeyboard.normalizeLayoutFile(old));
		assertArrayEquals(before, Files.readAllBytes(old.toPath()));

		File garbage = tmp.newFile();
		Files.write(garbage.toPath(), new byte[64]);
		assertFalse(VirtualKeyboard.normalizeLayoutFile(garbage));
		assertFalse(VirtualKeyboard.normalizeLayoutFile(new File(tmp.getRoot(), "missing")));
	}

	@Test
	public void normalizeLayoutFile_rejectsWrongScaleCount() throws Exception {
		File f = writeLayout(VirtualKeyboard.LAYOUT_VERSION, new float[]{1, 1, 1, 1});
		byte[] before = Files.readAllBytes(f.toPath());
		assertFalse(VirtualKeyboard.normalizeLayoutFile(f));
		assertArrayEquals(before, Files.readAllBytes(f.toPath()));
	}
}
