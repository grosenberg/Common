package net.certiv.common.util.json;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class PathDeserializer extends StdScalarDeserializer<Path> {

	private static final String ERR_VAL = "Expecting a string-valued representation of a Path";

	public PathDeserializer() {
		super(Path.class);
	}

	@Override
	public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		if (!p.hasToken(JsonToken.VALUE_STRING)) {
			ctxt.wrongTokenException(p, Path.class, JsonToken.VALUE_STRING, ERR_VAL);
		}

		final String value = p.getText();
		try {
			return Path.of(value);
		} catch (InvalidPathException e) {
			return (Path) ctxt.handleInstantiationProblem(handledType(), value, e);
		}
	}
}
