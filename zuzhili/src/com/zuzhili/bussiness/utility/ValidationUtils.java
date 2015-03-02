package com.zuzhili.bussiness.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
	private final static String phoneRegex = "^(13\\d|15[0,1,2,3,5,6,7,8,9]|18\\d)\\d{8}$";
	private final static String emailRegex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    
	public static boolean validationName(String name){
		if(!validationEmail(name)){
			if(!validationPhone(name)){
				return false;
			}
		}
		return true;
	};
	
	// 验证邮箱格式是否正确
	public static boolean validationEmail(String email) {
		Pattern emailPattern = Pattern.compile(emailRegex);
		Matcher emailMatcher = emailPattern.matcher(email);
		if (!emailMatcher.find()) {
			return false;
		}
		return true;
	}
    
	// 验证密码格式是否正确
	public static boolean validationPhone(String phone) {
		Pattern phonePattern = Pattern.compile(phoneRegex);
		Matcher phoneMatcher = phonePattern.matcher(phone);
		if (!phoneMatcher.find()) {
			return false;
		}
		return true;
	}
	
	//验证字符串是否可用
	public static boolean validationString(String str){
		if(null != str && !str.trim().equals("") && !str.trim().equals("null"))
			return true;
		return false;
		
	}
}
