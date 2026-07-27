package com.platform.shared.constant;

public final class RegexConstant {

    public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String MOBILE = "^\\+?[0-9]{7,15}$";
    public static final String UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
    public static final String ALPHANUMERIC = "^[A-Za-z0-9]+$";
    private RegexConstant() {
    }
}