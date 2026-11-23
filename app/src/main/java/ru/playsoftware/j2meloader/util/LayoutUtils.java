package ru.playsoftware.j2meloader.util;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import javax.microedition.lcdui.keyboard.VirtualKeyboard;

import ru.playsoftware.j2meloader.config.Config;

public class LayoutUtils {

	/**
	 * Makes soft/fire keys the same size as the number keys in every app's saved keyboard layout.
	 * A copy of each changed file is kept as VirtualKeyboardLayout.bak.
	 *
	 * @return number of apps whose layout was changed
	 */
	public static int normalizeAllKeyboardLayouts() {
		File[] dirs = new File(Config.getConfigsDir()).listFiles(File::isDirectory);
		if (dirs == null) {
			return 0;
		}
		int changed = 0;
		for (File dir : dirs) {
			File layout = new File(dir, Config.MIDLET_KEY_LAYOUT_FILE);
			if (!layout.isFile()) {
				continue;
			}
			// Keep the original so the change can be undone by hand
			File backup = new File(layout.getPath() + ".bak");
			try {
				FileUtils.copyFileUsingChannel(layout, backup);
			} catch (IOException e) {
				Log.e("LayoutUtils", "can't back up " + layout, e);
				continue;
			}
			if (VirtualKeyboard.normalizeLayoutFile(layout)) {
				changed++;
			} else {
				//noinspection ResultOfMethodCallIgnored
				backup.delete();
			}
		}
		return changed;
	}
}
