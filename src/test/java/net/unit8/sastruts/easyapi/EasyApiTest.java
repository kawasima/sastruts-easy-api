package net.unit8.sastruts.easyapi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import net.unit8.sastruts.easyapi.testapp.action.Test01Action;
import net.unit8.sastruts.easyapi.testapp.action.Test02Action;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.mock.servlet.MockHttpServletResponse;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.TestContext;
import org.seasar.framework.unit.impl.SimpleInternalTestContext;
import org.seasar.struts.util.ResponseUtil;
import org.seasar.struts.util.ServletContextUtil;

@RunWith(Seasar2.class)
public class EasyApiTest {
	private TestContext ctx;

	public static void dummy() {
		new EasyApiTest().ctx = new SimpleInternalTestContext();
	}

	@Test
	public void test() {
		Test01Action action = ctx.getComponent(Test01Action.class);
		action.show();
		MockHttpServletResponse response = (MockHttpServletResponse)ResponseUtil.getResponse();
		System.out.println(response.getResponseString());
	}

	@Test
	public void testJapanese() {
		Test01Action action = ctx.getComponent(Test01Action.class);
		action.showJapanese();
		MockHttpServletResponse response = (MockHttpServletResponse)ResponseUtil.getResponse();
		System.out.println(response.getCharacterEncoding());
		System.out.println(response.getResponseString());
	}

	@Test
	public void testFailure() throws Exception {
		Test01Action action = ctx.getComponent(Test01Action.class);
		action.showFailure();
		MockHttpServletResponse response = (MockHttpServletResponse)ResponseUtil.getResponse();
		System.out.println(response.getResponseString());
	}

	@Test
	public void testPost() {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		ServletContext context = ServletContextUtil.getServletContext();
		MockPostHttpServletRequest request = new MockPostHttpServletRequest(context, "/blog/post");
		request.setRequestBody("<request><head/><body>"
				+ "<title>Blog</title>"
				+ "<description>This is body.</description></body></request>");
		container.getExternalContext().setRequest(request);

		Test02Action action = ctx.getComponent(Test02Action.class);
		action.postArticle();
		MockHttpServletResponse response = (MockHttpServletResponse)ResponseUtil.getResponse();
		assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
		System.out.println(response.getResponseString());
	}

	@Test
	public void testEmptyRequest() {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		ServletContext context = ServletContextUtil.getServletContext();
		MockPostHttpServletRequest request = new MockPostHttpServletRequest(context, "/blog/post");
		request.setRequestBody("");
		container.getExternalContext().setRequest(request);

		Test02Action action = ctx.getComponent(Test02Action.class);
		action.postArticle();
		MockHttpServletResponse response = (MockHttpServletResponse)ResponseUtil.getResponse();
		assertThat(response.getStatus(), is(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
		System.out.println(response.getResponseString());
	}

	@Test
	public void testPostFailure() {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		ServletContext context = ServletContextUtil.getServletContext();
		MockPostHttpServletRequest request = new MockPostHttpServletRequest(context, "/blog/post");
		request.setRequestBody("<request><head/><body>"
				+ "<title>Blog</title>"
				+ "<description>This is body.</description></body></request>");
		request.addHeader("X-Transaction-Id", "transactionId");
		container.getExternalContext().setRequest(request);

		Test02Action action = ctx.getComponent(Test02Action.class);
		try {
			action.postFailure();
		} catch (EasyApiException e) {

		}
		MockHttpServletResponse response = (MockHttpServletResponse)ResponseUtil.getResponse();
		assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
		System.out.println(response.getResponseString());

	}
}
