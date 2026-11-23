package ru.playsoftware.j2meloader.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.ContextWrapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class ExitConfirmationTest {
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	private Context contextWithFilesDir() {
		File dir = tmp.getRoot();
		return new ContextWrapper(null) {
			@Override
			public File getFilesDir() {
				return dir;
			}
		};
	}

	@Test
	public void notSkippedByDefault() {
		assertFalse(ExitConfirmation.isSkipped(contextWithFilesDir()));
	}

	@Test
	public void skipFlagCanBeSetAndCleared() {
		Context context = contextWithFilesDir();
		ExitConfirmation.setSkipped(context, true);
		assertTrue(ExitConfirmation.isSkipped(context));
		ExitConfirmation.setSkipped(context, true); // idempotent
		assertTrue(ExitConfirmation.isSkipped(context));
		ExitConfirmation.setSkipped(context, false);
		assertFalse(ExitConfirmation.isSkipped(context));
		ExitConfirmation.setSkipped(context, false); // clearing twice is harmless
		assertFalse(ExitConfirmation.isSkipped(context));
	}
}
