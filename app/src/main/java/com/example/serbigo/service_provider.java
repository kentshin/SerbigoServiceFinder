package com.example.serbigo;

public class service_provider {
    private String address;
    private String first_name;
    private String last_name;
    private double rate;
    private String service;
    private long status;



    private service_provider(){

    }


    private service_provider(String address, String first_name, String last_name, double rate, String service, long status){
        this.address = address;
        this.first_name = first_name;
        this.last_name = last_name;
        this.rate = rate;
        this.service = service;
        this.status = status;

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLastname(String last_name) {
        this.last_name = last_name;
    }


    public double getRate() {
        return rate;
    }

    public void setRating(double rating) {
        this.rate = rate;
    }


    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }


}
