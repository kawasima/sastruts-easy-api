package net.unit8.sastruts.easyapi.testapp.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import net.unit8.sastruts.easyapi.EasyApi;
import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.testapp.dto.BlogDto;

import org.seasar.struts.annotation.Execute;

/**
 *
 * @author kawasima
 */
public class Test02Action {
	public BlogDto blogDto;

	@Execute(validator=false)
	@EasyApi(requestDto="blogDto")
	public String postArticle() {
		assertNotNull(blogDto);
		assertThat(blogDto.title, is("Blog"));
		return null;
	}

	@Execute(validator=false)
	@EasyApi(requestDto="blogDto")
	public String postFailure() throws EasyApiException {
		throw new EasyApiException("001");
	}
}
