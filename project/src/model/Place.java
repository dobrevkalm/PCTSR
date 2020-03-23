package model;

public class Place implements Cloneable {
    private int id;
    private String companyName;
    private String address;
    private double latitude;
    private double longitude;
    private double firmProfit;

    public Place(int id, String companyName, String address, double firmProfit, double latitude, double longitude) {
        this.id = id;
        this.companyName = companyName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.firmProfit = firmProfit;
    }

    /**
     * GETTERS
     */

    public int getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getFirmProfit() {
        return firmProfit;
    }
}
