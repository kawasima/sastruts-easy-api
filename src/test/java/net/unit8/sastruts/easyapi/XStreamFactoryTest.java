package net.unit8.sastruts.easyapi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class XStreamFactoryTest {
	@Test
	public void test() {
		XStream xstream = XStreamFactory.getInstance();
		C1 c1 = new C1();
		c1.a_b = "hoge";
		assertThat(xstream.toXML(c1), is("<C1 a_b=\"hoge\"/>"));
	}

	@XStreamAlias("C1")
	static class C1 {
		@XStreamAsAttribute
		String a_b;
	}
}
