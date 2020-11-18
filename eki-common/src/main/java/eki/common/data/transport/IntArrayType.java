package eki.common.data.transport;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class IntArrayType implements UserType {

	protected static final int SQL_TYPE = Types.ARRAY;

	@Override
	public int[] sqlTypes() {
		return new int[] {SQL_TYPE};
	}

	@Override
	public Class<?> returnedClass() {
		return int[].class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x == null ? y == null : x.equals(y);
	}

	@Override
	public int hashCode(Object value) throws HibernateException {
		return (value == null) ? 0 : value.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
		Array rsArray = rs.getArray(names[0]);
		if (rsArray == null) {
			return null;
		}
		Integer[] array = (Integer[]) rsArray.getArray();
		return ArrayUtils.toPrimitive(array);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, SQL_TYPE);
		} else {
			int[] castObject = (int[]) value;
			Integer[] integers = ArrayUtils.toObject(castObject);
			Array array = session.connection().createArrayOf("integer", integers);
			st.setArray(index, array);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return (value == null) ? null : ((int[]) value).clone();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}
