package net.unit8.sastruts.easyapi;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

import com.thoughtworks.xstream.XStream;

@SuppressWarnings("serial")
public class EasyApiInterceptor extends AbstractInterceptor {
	private static XStream xstream = new XStream();

	@Binding(bindingType=BindingType.MAY)
	public String transactionIdName = "X-Transaction-Id";

	public Object invoke(MethodInvocation invocation) throws Throwable {
		EasyApi easyApiAnno = invocation.getMethod().getAnnotation(EasyApi.class);
		Object action = invocation.getThis();
		BeanDesc beanDesc = null;
		String transactionId = null;

		Object ret = null;

		if (easyApiAnno != null) {
			beanDesc = BeanDescFactory.getBeanDesc(action.getClass());

			String requestDtoName = easyApiAnno.requestDto();
			if (requestDtoName != null) {
				PropertyDesc requestPropDesc = beanDesc.getPropertyDesc(requestDtoName);

				HttpServletRequest request = RequestUtil.getRequest();
				transactionId = request.getHeader(transactionIdName);
				InputStream in = request.getInputStream();
				Object requestDto = xstream.fromXML(in);
				requestPropDesc.setValue(action, requestDto);
			}

			try {
				ret = invocation.proceed();
			} catch (EasyApiException e) {
				e.getMessage();
			} catch (Exception e) {

			}

			String responseDtoName = easyApiAnno.responseDto();
			if (responseDtoName != null) {
				PropertyDesc responsePropDesc = beanDesc.getPropertyDesc(responseDtoName);
				Object responseDto = responsePropDesc.getValue(action);
				HttpServletResponse response = ResponseUtil.getResponse();
				response.setHeader(transactionIdName, transactionId);
				String responseXml = xstream.toXML(responseDto);
				response.getWriter().write(responseXml);
			}
		} else {
			ret = invocation.proceed();
		}

		return ret;
	}

}
