package GUI;

import model.Place;

public class GUIUtil {
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;
    private double totalProfit;

    GUIUtil(Place[] places) {
        setExtremeCoordinateVals(places);
        setTotalProfit(places);
    }

    private void setTotalProfit(Place[] places) {
        this.totalProfit = 0.0;
        for(Place place: places) {
            this.totalProfit += place.getFirmProfit();
        }
    }


    private void setExtremeCoordinateVals(Place[] places) {
        this.minLatitude = Double.MAX_VALUE;
        this.maxLatitude = 0.0;
        this.minLongitude = Double.MAX_VALUE;
        this.maxLongitude = 0.0;

        for (Place place : places) {
            double latitude = place.getLatitude();
            double longitude = place.getLongitude();

            if (latitude < this.minLatitude) {
                this.minLatitude = latitude;
            }

            if (latitude > this.maxLatitude) {
                this.maxLatitude = latitude;
            }

            if (longitude < this.minLongitude) {
                this.minLongitude = longitude;
            }

            if (longitude > this.maxLongitude) {
                this.maxLongitude = longitude;
            }
        }
    }

    public int getPlaceXposition(Place place, int minX, int maxX) {
        return (int) (minX + (((place.getLongitude() - minLongitude) * (maxX - minX)) / (maxLongitude - minLongitude)));
    }

    public int getPlaceYposition(Place place, int minY, int maxY) {
        return (int) (maxY - (((place.getLatitude() - minLatitude) * (maxY - minY)) / (maxLatitude - minLatitude)));
    }

    public double getTotalProfit() {
        return totalProfit;
    }
}
