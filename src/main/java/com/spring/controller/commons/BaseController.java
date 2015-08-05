package com.spring.controller.commons;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.common.utils.DateUtils;
import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;



/**
 * 基础控制器 
 * 其他控制器继承此控制器获得日期字段类型转换和防止XSS攻击的功能
 * @description 
 * @author ty
 * @date 2014年3月19日
 */
public class BaseController {
	private PageBounds pageBounds = null; //分页
	int page = 1;//页号 默认1
	int pageSize = 10;//每页数据条数 默认10条
	String sortString = null;//排序
	public PageBounds getPage(HttpServletRequest request){
		if(request.getParameter("page") != null){
			page = Integer.parseInt(request.getParameter("page").toString());
		}
		if(request.getParameter("pageSize") != null){
			pageSize = Integer.parseInt(request.getParameter("pageSize").toString());
		}
		if(request.getParameter("sort") != null){
			sortString = request.getParameter("sort").toString();
		}
		if(request.getParameter("order") != null){
			sortString = request.getParameter("sort").toString() +"."+request.getParameter("order").toString();
		}
		pageBounds = new PageBounds(page, pageSize , Order.formString(sortString));
		return pageBounds;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
			}
			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
		
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
		});
		
		// Timestamp 类型转换
		binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				Date date = DateUtils.parseDate(text);
				setValue(date==null?null:new Timestamp(date.getTime()));
			}
		});
	}
	/**
	 * 参数传递
	 * @param request
	 * @param paramName
	 */
	public void sendParam(HttpServletRequest request,String paramName){
		String paramValue = request.getParameter(paramName);
		request.setAttribute(paramName, paramValue);
	}
}
