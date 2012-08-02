package net.unit8.sastruts.easyapi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.unit8.sastruts.easyapi.dto.ErrorDto;
import net.unit8.sastruts.easyapi.dto.FailureDto;
import net.unit8.sastruts.easyapi.dto.RequestDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

import com.thoughtworks.xstream.XStream;

@SuppressWarnings("serial")
public class EasyApiInterceptor extends AbstractInterceptor {
	@Binding(bindingType=BindingType.MAY)
	public String transactionIdName = "X-Transaction-Id";

	public Object invoke(MethodInvocation invocation) throws Throwable {
		EasyApi easyApiAnno = invocation.getMethod().getAnnotation(EasyApi.class);
		Object action = invocation.getThis();
		String transactionId = null;

		Object ret = null;

		if (easyApiAnno != null) {
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(action.getClass());

			String requestDtoName = easyApiAnno.requestDto();
			if (StringUtil.isNotEmpty(requestDtoName)) {
				PropertyDesc requestPropDesc = beanDesc.getPropertyDesc(requestDtoName);

				HttpServletRequest request = RequestUtil.getRequest();
				transactionId = request.getHeader(transactionIdName);
				InputStream in = request.getInputStream();
				XStream xstream = XStreamFactory.getInstance();
				XStreamFactory.setBodyDto(requestPropDesc.getPropertyType());
				RequestDto requestDto = (RequestDto)xstream.fromXML(in);
				requestPropDesc.setValue(action, requestDto.body);
			}

			ResponseDto responseDto = new ResponseDto();
			HttpServletResponse response = ResponseUtil.getResponse();
			try {
				ret = invocation.proceed();
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (EasyApiException cause) {
				Iterator<EasyApiException> iter = cause.iterator();
				responseDto.header.failures = new ArrayList<FailureDto>();
				while(iter.hasNext()) {
					EasyApiException e = iter.next();
					responseDto.header.failures.add(new FailureDto(e.getMessageCode(), e.getMessage()));
				}
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Throwable e) {
				responseDto.header.errors = new ArrayList<ErrorDto>();
				responseDto.header.errors.add(new ErrorDto("9999", e.getMessage()));
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}

			String responseBodyName = easyApiAnno.responseDto();
			if (StringUtil.isNotEmpty(responseBodyName)) {
				PropertyDesc responseBodyPropDesc = beanDesc.getPropertyDesc(responseBodyName);
				responseDto.body = responseBodyPropDesc.getValue(action);
				response.setHeader(transactionIdName, transactionId);
				String responseXml = XStreamFactory.getInstance().toXML(responseDto);
				response.getWriter().write(responseXml);
			}
		} else {
			ret = invocation.proceed();
		}

		return ret;
	}

}
