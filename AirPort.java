/**
 * Models an airport and all the most basic information that can be associated
 * to it.
 * 
 * @author Styve Lekane.
 */
class AirPort {

    // Static Variables
    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int CITY_INDEX = 2;
    public static final int COUNTRY_INDEX = 3;
    public static final int CODE_INDEX = 4; // IATA CODE
    public static final int LATITUDE_INDEX = 6;
    public static final int LONGITUDE_INDEX = 7;
    public static final int ALTITUDE_INDEX = 8;

    /*
     * Two countries can have the same city name, hence the city code is the city
     * unique identifier.
     * It is in the form "City, Country". E.g Accra, Ghana
     */
    private String cityCode;
    private String country;
    private String city;
    private String code;
    private String id;
    private double latitude;
    private double longitude;
    private double altitude;
    private String airline;
    private int stops;

    public AirPort(String country, String city, String code, String id, double latitude, double longitude,
            double altitude, String cityCode) {
        this.city = city;
        this.country = country;
        this.code = code;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.cityCode = cityCode;
    }

    // copy constructor.
    public AirPort(AirPort airport) {
        this.city = airport.getCity();
        this.country = airport.getCountry();
        this.code = airport.getCode();
        this.id = airport.getId();
        this.latitude = airport.getLatitude();
        this.longitude = airport.getLongitude();
        this.altitude = airport.getAltitude();
        this.cityCode = airport.getCityCode();
    }

    public String getCountry() {
        return this.country;
    }

    public String getCity() {
        return this.city;
    }

    public String getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public String getAirLine() {
        return this.airline;
    }

    public int getStops() {
        return this.stops;
    }

    public void setAirLineCode(String airlineCode) {
        this.airline = airlineCode;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    @Override
    public String toString() {
        return "AirPort code is " + this.code + ". located in " + this.country + " , " + this.city + "airline " + this.airline;
    }
}
