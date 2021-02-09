package com.gcoolservices.acrepair.models;

public class RateCardModel {
    private String name;
    private String  buingprice;
    private String heading;

    public RateCardModel(String name, String buingprice, String heading) {
        this.name = name;
        this.buingprice = buingprice;
        this.heading=heading;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuingprice() {
        return buingprice;
    }

    public void setBuingprice(String buingprice) {
        this.buingprice = buingprice;
    }
}
