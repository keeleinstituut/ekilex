package eki.ekilex.data.transport;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

public class OrderedSet<E> implements Set<E> {

	private E[] values;

	@SuppressWarnings("unchecked")
	public OrderedSet() {
		this.values = (E[]) new Object[0];
	}

	@Override
	public boolean add(E e) {
		for (E value : values) {
			if (value.equals(e)) {
				return false;
			}
		}
		values = (E[]) ArrayUtils.add(values, e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("Currently not supported. Sorry!");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void clear() {
		this.values = (E[]) new Object[0];
	}

	@Override
	public boolean contains(Object o) {
		for (E value : values) {
			if (value.equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException("Currently not supported. Sorry!");
	}

	@Override
	public boolean isEmpty() {
		return values.length == 0;
	}

	@Override
	public Iterator<E> iterator() {
		return Arrays.asList(values).iterator();
	}

	@Override
	public boolean remove(Object o) {
		if (contains(o)) {
			values = (E[]) ArrayUtils.removeElement(values, o);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Currently not supported. Sorry!");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Currently not supported. Sorry!");
	}

	@Override
	public int size() {
		return values.length;
	}

	@Override
	public Object[] toArray() {
		return values;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("Currently not supported. Sorry!");
	}

}
