package eki.ekilex.data.transport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import eki.common.data.AbstractDataObject;

public class OrderedMap<K, V> extends AbstractDataObject implements Map<K, V> {

	private static final long serialVersionUID = 1L;

	private KeyValuePair<K, V>[] entries;

	@SuppressWarnings("unchecked")
	public OrderedMap() {
		this.entries = new KeyValuePair[0];
	}

	@Override
	@SuppressWarnings("unchecked")
	public void clear() {
		this.entries = new KeyValuePair[0];
	}

	@Override
	public boolean containsKey(Object key) {
		for (KeyValuePair<K, V> entry : entries) {
			if (key.equals(entry.getKey())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for (KeyValuePair<K, V> entry : entries) {
			if (value.equals(entry.getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> entrySet = new OrderedSet<>();
		for (KeyValuePair<K, V> entry : entries) {
			entrySet.add(entry);
		}
		return entrySet;
	}

	@Override
	public V get(Object key) {
		for (KeyValuePair<K, V> entry : entries) {
			if (key.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return entries.length == 0;
	}

	@Override
	public Set<K> keySet() {
		Set<K> keySet = new OrderedSet<>();
		for (KeyValuePair<K, V> entry : entries) {
			keySet.add(entry.getKey());
		}
		return keySet;
	}

	@Override
	public V put(K key, V value) {
		entries = (KeyValuePair<K, V>[]) ArrayUtils.add(entries, new KeyValuePair<K, V>(key, value));
		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> otherMap) {
		throw new UnsupportedOperationException("Currently not supported. Sorry!");
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException("Currently not supported. Sorry!");
	}

	@Override
	public int size() {
		return entries.length;
	}

	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<>();
		for (KeyValuePair<K, V> entry : entries) {
			values.add(entry.getValue());
		}
		return values;
	}

}
