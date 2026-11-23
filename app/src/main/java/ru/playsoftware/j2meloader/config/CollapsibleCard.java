package ru.playsoftware.j2meloader.config;

import android.content.SharedPreferences;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.core.widget.TextViewCompat;

import ru.playsoftware.j2meloader.R;

/**
 * Turns a settings card into an expandable section: tapping its title shows or hides every other
 * control of the card. The state is remembered. The original visibility of each control is saved
 * while collapsed, so controls that were already hidden stay hidden after expanding.
 */
class CollapsibleCard {
	private static final String PREF_PREFIX = "config_card_expanded_";

	private final ViewGroup card;
	private final TextView title;
	private final SharedPreferences prefs;
	private final String key;
	private final SparseIntArray savedVisibility = new SparseIntArray();
	private boolean expanded;
	private boolean collapsedApplied;

	CollapsibleCard(ViewGroup card, TextView title, SharedPreferences prefs, String name,
					boolean defaultExpanded) {
		this.card = card;
		this.title = title;
		this.prefs = prefs;
		this.key = PREF_PREFIX + name;
		this.expanded = prefs.getBoolean(key, defaultExpanded);
		title.setClickable(true);
		title.setFocusable(true);
		title.setOnClickListener(v -> toggle());
		apply();
	}

	void toggle() {
		expanded = !expanded;
		prefs.edit().putBoolean(key, expanded).apply();
		apply();
	}

	/** Re-applies the collapsed state, e.g. after other code changed the visibility of controls. */
	void reapply() {
		if (!expanded) {
			collapsedApplied = false;
		}
		apply();
	}

	private void apply() {
		if (expanded) {
			if (collapsedApplied) {
				for (int i = 0; i < card.getChildCount(); i++) {
					View child = card.getChildAt(i);
					int saved = savedVisibility.get(i, Integer.MIN_VALUE);
					if (saved != Integer.MIN_VALUE) {
						child.setVisibility(saved);
					}
				}
				savedVisibility.clear();
				collapsedApplied = false;
			}
		} else if (!collapsedApplied) {
			savedVisibility.clear();
			for (int i = 0; i < card.getChildCount(); i++) {
				View child = card.getChildAt(i);
				if (child == title || child instanceof ConstraintHelper) {
					continue;
				}
				savedVisibility.put(i, child.getVisibility());
				child.setVisibility(View.GONE);
			}
			collapsedApplied = true;
		}
		title.setContentDescription(null);
		TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(title, 0, 0,
				expanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more, 0);
	}
}
