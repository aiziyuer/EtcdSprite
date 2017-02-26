package com.aiziyuer.app.ui.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.WritableList;

public class AdvanceWritableList extends WritableList {

	@SuppressWarnings("unchecked")
	public <T> boolean replaceAll(Collection<T> c) {

		checkRealm();

		ListDiffEntry[] entries = new ListDiffEntry[wrappedList.size() + c.size()];
		int entryIndex = 0;
		for (ListIterator<T> it = wrappedList.listIterator(wrappedList.size()); it.hasPrevious();) {
			int elementIndex = it.previousIndex();
			Object element = it.previous();
			entries[entryIndex++] = Diffs.createListDiffEntry(elementIndex, false, element);
		}
		wrappedList.clear();
		int addIndex = wrappedList.size();
		for (Iterator<T> it = c.iterator(); it.hasNext();) {
			Object element = it.next();
			entries[entryIndex++] = Diffs.createListDiffEntry(addIndex++, true, element);
		}
		boolean added = wrappedList.addAll(c);
		fireListChange(Diffs.createListDiff(entries));

		return added;
	}

}
