package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.Test;

class GsonUtilTest {

	AString str = new AString();
	AInteger idx = new AInteger();
	ADouble dbl = new ADouble();
	AClass cls = new AClass();
	APath path = new APath();
	AUri uri = new AUri();
	AUrl url = new AUrl();

	@Test
	void testDup() {
		assertEquals(GsonUtil.toJson(idx), "{\"idx\":1}");
		assertEquals(GsonUtil.toJson(dbl), "{\"dbl\":5.5}");
		assertEquals(GsonUtil.toJson(cls), "{\"cls\":\"java.lang.String\"}");
		assertEquals(GsonUtil.toJson(path), "{\"path\":\"test\\\\string\"}");
		assertEquals(GsonUtil.toJson(uri),
				"{\"uri\":\"file:///D:/DevFiles/Eclipse/Tools/Certiv/net.certiv.common/test/string\"}");
		assertEquals(GsonUtil.toJson(url),
				"{\"url\":\"file:/D:/DevFiles/Eclipse/Tools/Certiv/net.certiv.common/test/string\"}");
	}

	class AString {
		String str = "test string";

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AString other = (AString) obj;
			return Objects.equals(str, other.str);
		}
	}

	class AInteger {
		Integer idx = 1;

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AInteger other = (AInteger) obj;
			return Objects.equals(idx, other.idx);
		}
	}

	class ADouble {
		Double dbl = 5.5;

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ADouble other = (ADouble) obj;
			return Objects.equals(dbl, other.dbl);
		}
	}

	class AClass {
		Class<String> cls = String.class;

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AClass other = (AClass) obj;
			return Objects.equals(cls, other.cls);
		}
	}

	class APath {
		Path path = Path.of("test", "string");

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			APath other = (APath) obj;
			return Objects.equals(path, other.path);
		}
	}

	class AUri {
		URI uri = Path.of("test", "string").toUri();

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AUri other = (AUri) obj;
			return Objects.equals(uri, other.uri);
		}
	}

	class AUrl {
		URL url;

		public AUrl() {
			try {
				this.url = Path.of("test", "string").toUri().toURL();
			} catch (MalformedURLException e) {}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AUrl other = (AUrl) obj;
			return Objects.equals(url, other.url);
		}
	}
}
