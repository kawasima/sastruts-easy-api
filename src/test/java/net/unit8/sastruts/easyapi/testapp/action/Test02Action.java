package net.unit8.sastruts.easyapi.testapp.action;

import net.unit8.sastruts.easyapi.EasyApi;
import net.unit8.sastruts.easyapi.testapp.dto.BlogDto;

import org.hamcrest.core.*;
import org.seasar.struts.annotation.Execute;
import static org.junit.Assert.*;

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
		return null;
	}
}
