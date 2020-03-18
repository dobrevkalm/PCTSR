import java.io.*;
import java.util.*;

public class DataReader {
    private final int NUM_COMPANIES = 91;
    // a list with all the companies
    private final Place[] COMPANIES = new Place[NUM_COMPANIES];
    // a matrix with distances between all the companies
    private final double[][] DISTANCE_MATRIX = new double[91][91];

    public DataReader() {
        try {
            readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() throws IOException {
        File data = new File("../../data/data.txt");
        BufferedReader br = new BufferedReader(new FileReader(data));
        for(int i = 0; i < NUM_COMPANIES; i++) {
            // parse company data 0 - name, 1 - address, 2 - profit, 3 - latitude, 4 - longitude, 5 - distance array
            String[] company = br.readLine().split(";");
            this.COMPANIES[i] = new Place(i, company[0], company[1], Double.parseDouble(company[2]), Double.parseDouble(company[3]), Double.parseDouble(company[4]));
            this.DISTANCE_MATRIX[i] = getDistanceArray(company[5]);
        }
    }

    private double[] getDistanceArray(String distances) {
        // all the distances are comma separated
        String[] distanceString = distances.split(",");
        double[] distanceArray = new double[distanceString.length];
        for(int i = 0; i < distanceString.length; i++) {
            distanceArray[i] = Double.parseDouble(distanceString[i]);
        }
        return distanceArray;
    }

    /**
     * GETTERS
     */
    public double[][] getDistanceMatrix() {
        return this.DISTANCE_MATRIX;
    }

    public double getTwoCompanyDistance(int companyIdx1, int companyIdx2) {
        return this.DISTANCE_MATRIX[companyIdx1][companyIdx2];
    }

    public Place[] getAllCompanies() {
        return this.COMPANIES;
    }

    public Place getSingeCompany(int companyIdx) {
        return this.COMPANIES[companyIdx];
    }

    /**
     * TEST METHODS
     */
    private void testDistanceMatrix() {
        for(int i = 0; i < this.NUM_COMPANIES; i++) {
            double[] companyDistance = this.DISTANCE_MATRIX[i];
            // distance to itself should be 0
            assert companyDistance[i] == 0;
            // distance to another vertex should be positive
            if (i > 0) assert companyDistance[i-1] > 0;
            else assert companyDistance[i+1] > 0;
        }
    }

    private void testPlaces() {
        for (int i = 0; i < this.NUM_COMPANIES; i++) {
            Place place = this.COMPANIES[i];
            assert place.getId() >= 0;
            assert place.getCompanyName() != null && place.getCompanyName().length() > 0;
            assert place.getAddress() != null && place.getCompanyName().length() > 0;
            assert place.getFirmProfit() > 0;
            assert place.getLatitude() > 0;
            assert place.getLongitude() > 0;
        }
    }

    /**
     * MAIN
     */
    public static void main(String[] args) throws IOException {
        DataReader dr = new DataReader();
        dr.testDistanceMatrix();
        dr.testPlaces();
    }
}

