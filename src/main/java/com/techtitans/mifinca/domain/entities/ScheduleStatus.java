package com.techtitans.mifinca.domain.entities;

public class ScheduleStatus {
    private ScheduleStatus(){}
    public static final String REQUESTED = "REQUESTED";
    public static final String APPROVED = "APPROVED"; 
    public static final String DENIED = "DENIED";
    public static final String PAID = "PAID";
    public static final String IN_COURSE = "IN_COURSE";
    public static final String COMPLETED = "COMPLETED";
    public static final String RATED = "RATED";
    public static final String LOST = "LOST";
}
