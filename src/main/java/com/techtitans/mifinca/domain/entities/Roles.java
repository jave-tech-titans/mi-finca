package com.techtitans.mifinca.domain.entities;

public class Roles {
    private static String LANDLORD_ROLE = "LANDLORD";
    private static String USER_ROLE = "USER";

    public static String landlordRole(){
        return LANDLORD_ROLE;
    }

    public static String userRole(){
        return USER_ROLE;
    }

}
