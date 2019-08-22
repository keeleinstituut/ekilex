package eki.ekilex.data.transport;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class CharArrayType implements UserType {

	protected static final int[] SQL_TYPES = {Types.ARRAY};

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@Override
	public Class<?> returnedClass() {
		return Character[].class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return false;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return 0;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
		Array rsArray = rs.getArray(names[0]);
		if (rsArray == null) {
			return null;
		}
		Object arrayObj = rsArray.getArray();
		Character[] chrArray;
		if (arrayObj instanceof String[]) {
			String[] strArray = (String[]) arrayObj;
			int arrayLength = strArray.length;
			chrArray = new Character[arrayLength];
			for (int arrayIndex = 0; arrayIndex < arrayLength; arrayIndex++) {
				chrArray[arrayIndex] = Character.valueOf(strArray[arrayIndex].charAt(0));
			}
		} else {
			chrArray = (Character[]) arrayObj;
		}
		return chrArray;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, SQL_TYPES[0]);
		} else {
			Character[] castObject = (Character[]) value;
			Array array = session.connection().createArrayOf("char", castObject);
			st.setArray(index, array);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
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
