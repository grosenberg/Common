
package net.certiv.common.util.json;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import net.certiv.common.util.Chars;

public class PathSerializer extends StdScalarSerializer<Path> {

	public PathSerializer() {
		super(Path.class);
	}

	@Override
	public void serialize(Path value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.toString().replace(Chars.ESC, Chars.SLASH));
	}

	@Override
	public void serializeWithType(Path value, JsonGenerator g, SerializerProvider provider, TypeSerializer ts)
			throws IOException {

		// ensure we don't use specific sub-classes:
		WritableTypeId id = ts.writeTypePrefix(g, ts.typeId(value, Path.class, JsonToken.VALUE_STRING));
		serialize(value, g, provider);
		ts.writeTypeSuffix(g, id);
	}
}
