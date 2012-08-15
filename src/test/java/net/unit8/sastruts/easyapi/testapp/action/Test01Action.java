package net.unit8.sastruts.easyapi.testapp.action;

import net.unit8.sastruts.easyapi.EasyApi;
import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.testapp.dto.UserDto;

import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.Execute;

public class Test01Action {
	public UserDto userDto;

	@Execute(validator=false)
	@EasyApi(responseDto="userDto")
	public String show() {
		userDto = new UserDto();
		userDto.name = "Yoshitaka Kawashima";
		return null;
	}

	@Execute(validator=false)
	@EasyApi(responseDto="userDto")
	public String showJapanese() {
		userDto = new UserDto();
		userDto.name = "川島";
		return null;
	}

	@Execute(validator=false)
	@EasyApi(responseDto="userDto")
	public String showFailure() throws UserDuplicateException {
		userDto = new UserDto();
		userDto.name = "Yoshitaka Kawashima";
		if (StringUtil.equals(userDto.name, "Yoshitaka Kawashima")) {
			throw new UserDuplicateException("0001");
		}
		return null;
	}

	@SuppressWarnings("serial")
	public static class UserDuplicateException extends EasyApiException {
		public UserDuplicateException(String messageCode) {
			super(messageCode);
		}
	}
}
