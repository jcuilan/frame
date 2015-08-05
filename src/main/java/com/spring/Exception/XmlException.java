/**
 * 
 */
package com.spring.Exception;

import org.apache.shiro.authc.AuthenticationException;

/** 
 * 
 * @author jiangcuilan *
 * 2015年6月13日
 */
public class XmlException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public XmlException() {
		super();
	}

	public XmlException(String message, Throwable cause) {
		super(message, cause);
	}

	public XmlException(String message) {
		super(message);
	}

	public XmlException(Throwable cause) {
		super(cause);
	}
}
