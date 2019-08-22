package eki.ekilex.data.transport;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class EnumType implements UserType {

	protected static final int[] SQL_TYPES = {Types.VARCHAR};

	private Class<? extends Enum> enumType;

	public EnumType(Class<? extends Enum> enumType) {
		this.enumType = enumType;
	}

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@Override
	public Class<?> returnedClass() {
		return enumType;
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
		if (rs.wasNull()) {
			return null;
		}
		String stringValue = rs.getString(names[0]);
		stringValue = stringValue.trim().toUpperCase();
		return Enum.valueOf(enumType, stringValue);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, SQL_TYPES[0]);
		} else {
			st.setString(index, value.toString());
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
