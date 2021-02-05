package com.smartwebarts.acrepair.models;

import com.smartwebarts.acrepair.retrofit.DeliveryChargesModel;

import java.util.List;

public class VendorDeliveryChargesModel {

    private List<DeliveryChargesModel> delivery_charge;
    private List<VendorModel> vendors;

    public List<DeliveryChargesModel> getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(List<DeliveryChargesModel> delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public List<VendorModel> getVendors() {
        return vendors;
    }

    public void setVendors(List<VendorModel> vendors) {
        this.vendors = vendors;
    }
}
