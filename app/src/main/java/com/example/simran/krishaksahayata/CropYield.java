package com.example.simran.krishaksahayata;

public class CropYield {
    private String crop;
    private Double yield;

    public CropYield(String crop, Double yield){
        this.crop = crop;
        this.yield = yield;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public void setYield(Double yield) {
        this.yield = yield;
    }

    public String getCrop() {
        return crop;
    }

    public Double getYield() {
        return yield;
    }
}
