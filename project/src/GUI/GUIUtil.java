package GUI;

import model.Place;

class GUIUtil {
    //map's latitude and longitude values
    private final double MIN_LATITUDE = 54.554316;
    private final double MAX_LATITUDE = 57.751806;
    private final double MIN_LONGITUDE = 8.072245;
    private final double MAX_LONGITUDE = 12.793928;
    private double totalProfit;

    GUIUtil(Place[] places) {
        setTotalProfit(places);
    }

    private void setTotalProfit(Place[] places) {
        this.totalProfit = 0.0;
        for(Place place: places) {
            this.totalProfit += place.getFirmProfit();
        }
    }

    int getPlaceXposition(Place place, int maxX) {
        return (int) ((((place.getLongitude() - MIN_LONGITUDE) * (maxX)) / (MAX_LONGITUDE - MIN_LONGITUDE)));
    }

    int getPlaceYposition(Place place, int maxY) {
        return (int) (maxY - (((place.getLatitude() - MIN_LATITUDE) * (maxY)) / (MAX_LATITUDE - MIN_LATITUDE)));
    }

    double getTotalProfit() {
        return totalProfit;
    }
}
