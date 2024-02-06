package net.certiv.common.util.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

@SuppressWarnings("rawtypes")
public class ClassDeserializer extends StdScalarDeserializer<Class> {

	private static final String ERR_VAL = "Expecting a string-valued representation of a Class";

	public ClassDeserializer() {
		super(Class.class);
	}

	@Override
	public Class deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		if (!p.hasToken(JsonToken.VALUE_STRING)) {
			ctxt.wrongTokenException(p, Class.class, JsonToken.VALUE_STRING, ERR_VAL);
		}

		final String value = p.getText();
		try {
			return Class.forName(value);

		} catch (final ClassNotFoundException e) {
			return (Class) ctxt.handleInstantiationProblem(handledType(), value, e);
		}
	}
}
