package ru.playsoftware.j2meloader.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Remembers whether the user chose "don't ask again" for the exit-game confirmation.
 * <p>
 * Games run in a separate process, and SharedPreferences are not reliably shared between
 * processes, so the flag is a marker file in the app's files directory.
 */
public final class ExitConfirmation {
	private static final String MARKER = "skip_exit_confirmation";

	private ExitConfirmation() {
	}

	public static boolean isSkipped(Context context) {
		return new File(context.getFilesDir(), MARKER).exists();
	}

	public static void setSkipped(Context context, boolean skipped) {
		File marker = new File(context.getFilesDir(), MARKER);
		try {
			if (skipped) {
				//noinspection ResultOfMethodCallIgnored
				marker.createNewFile();
			} else {
				//noinspection ResultOfMethodCallIgnored
				marker.delete();
			}
		} catch (IOException e) {
			android.util.Log.e("ExitConfirmation", "can't update " + marker, e);
		}
	}
}
