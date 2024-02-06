
package net.certiv.common.util.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

@SuppressWarnings("rawtypes")
public class ClassSerializer extends StdScalarSerializer<Class> {

	public ClassSerializer() {
		super(Class.class, false);
	}

	@Override
	public void serialize(Class value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.getName());
	}

	@Override
	public void serializeWithType(Class value, JsonGenerator g, SerializerProvider provider,
			TypeSerializer ts) throws IOException {

		// ensure we don't use specific sub-class
		WritableTypeId id = ts.writeTypePrefix(g, ts.typeId(value, Class.class, JsonToken.VALUE_STRING));
		serialize(value, g, provider);
		ts.writeTypeSuffix(g, id);
	}
}
