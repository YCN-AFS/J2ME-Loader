package ru.playsoftware.j2meloader.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.playsoftware.j2meloader.R;

/**
 * Styles a preference list as "inset grouped" cards: every category becomes one rounded card with
 * hairline separators, preceded by a small grey section header.
 */
final class InsetGroups implements RecyclerView.OnChildAttachStateChangeListener {
	private static final int HEADER = 0;
	private static final int SINGLE = 1;
	private static final int TOP = 2;
	private static final int MIDDLE = 3;
	private static final int BOTTOM = 4;

	private final RecyclerView list;
	private final PreferenceScreen screen;

	private InsetGroups(RecyclerView list, PreferenceScreen screen) {
		this.list = list;
		this.screen = screen;
	}

	static void attach(RecyclerView list, PreferenceScreen screen) {
		float density = list.getResources().getDisplayMetrics().density;
		int side = (int) (16 * density);
		list.setPadding(side, list.getPaddingTop(), side, (int) (32 * density));
		list.setClipToPadding(false);
		list.addOnChildAttachStateChangeListener(new InsetGroups(list, screen));
	}

	@Override
	public void onChildViewAttachedToWindow(View view) {
		int position = list.getChildAdapterPosition(view);
		if (position < 0) {
			return;
		}
		List<Integer> roles = computeRoles();
		if (position >= roles.size()) {
			return;
		}
		int role = roles.get(position);
		Context context = view.getContext();
		if (role == HEADER) {
			view.setBackground(null);
			TextView title = view.findViewById(android.R.id.title);
			if (title != null) {
				title.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				title.setAllCaps(true);
				title.setLetterSpacing(0.04f);
			}
			return;
		}
		int bg;
		switch (role) {
			case TOP: bg = R.drawable.bg_group_top; break;
			case MIDDLE: bg = R.drawable.bg_group_middle; break;
			case BOTTOM: bg = R.drawable.bg_group_bottom; break;
			default: bg = R.drawable.bg_group_single; break;
		}
		Drawable d = ContextCompat.getDrawable(context, bg);
		view.setBackground(d == null ? null : d.mutate());
	}

	@Override
	public void onChildViewDetachedFromWindow(View view) {
	}

	/** One role per visible row of the flattened preference list. */
	private List<Integer> computeRoles() {
		List<Integer> roles = new ArrayList<>();
		for (int i = 0; i < screen.getPreferenceCount(); i++) {
			Preference p = screen.getPreference(i);
			if (!p.isVisible()) {
				continue;
			}
			if (p instanceof PreferenceGroup) {
				PreferenceGroup group = (PreferenceGroup) p;
				roles.add(HEADER);
				int count = 0;
				for (int j = 0; j < group.getPreferenceCount(); j++) {
					if (group.getPreference(j).isVisible()) {
						count++;
					}
				}
				for (int k = 0; k < count; k++) {
					roles.add(count == 1 ? SINGLE : k == 0 ? TOP : k == count - 1 ? BOTTOM : MIDDLE);
				}
			} else {
				roles.add(SINGLE);
			}
		}
		return roles;
	}
}
