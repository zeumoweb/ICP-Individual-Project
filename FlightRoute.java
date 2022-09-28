import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AirPort {
    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int CITY_INDEX = 2;
    public static final int COUNTRY_INDEX = 3;
    public static final int CODE_INDEX = 4; // IATA CODE
    public static final int LATITUDE_INDEX = 6;
    public static final int LONGITUDE_INDEX = 7;
    public static final int ALTITUDE_INDEX = 8;
    private String country;
    private String city;
    private String code;
    private String id;
    private String latitude;
    private String longitude;
    private String altitude;
    private String cityCode;
    private String airline;

    public AirPort(String country, String city, String code, String id, String latitude, String longitude,
            String altitude, String cityCode) {
        this.city = city;
        this.country = country;
        this.code = code;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.cityCode = cityCode;
    }

    public AirPort(AirPort airport){
        this.city = airport.getCity();
        this.country = airport.getCountry();
        this.code = airport.getCode();
        this.id = airport.getId();
        this.latitude = airport.getLatitude();
        this.longitude = airport.getLongitude();
        this.altitude = airport.getLatitude();
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

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getAltitude() {
        return this.altitude;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public String getAirLine() {
        return this.airline;
    }

    public void setAirLine(String airlineCode){
        this.airline = airlineCode;
    }

    @Override
    public String toString() {
        return "AirPort code is " + this.code + ". located in " + this.country + " , " + this.city;
    }
}

public class FlightRoute {
    private static final String REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    Map<String, List<AirPort>> routes = new HashMap<>(); // maps aiport code to list of other connected airport
    Map<String, AirPort> airports;  // map aiport code to airport info
    Map<String, List<String>> cities = new HashMap<>(); // map city to all airport within that city
    Map<String, String> airportIataCodeToID = new HashMap<>(); // map airport iata code to airport id
    public FlightRoute() {
        try {
            loadData();
        } catch (IOException e) {
            System.out.println("An exception occured");
        }

    }

    private void loadData() throws IOException {
        Stream<String> airports = Files.lines(Paths.get("airports.csv"));
        this.airports = airports.filter(this::hasValidCode)
                .map(line -> buildAirPortAndCityMap(line.split(FlightRoute.REGEX, -1)))
                .collect(Collectors.toMap(AirPort::getCode, airport -> airport));
        airports.close();
        List<List<String>> tempRoutes = Files.lines(Paths.get("routes.csv"))
                .map(route -> List.of(route.split(FlightRoute.REGEX, -1)))
                .collect(Collectors.toList());
        tempRoutes.forEach(this::addPathToRoutes);
        // System.out.println(this.routes);
    }

    private void addPathToRoutes(List<String> route) {
        // if airport code already in list of routes
        List<AirPort> airport = this.routes.get(route.get(2));
        if (airport == null) {
            airport = new ArrayList<>();
            this.routes.put(route.get(2), airport);
        }
        // String airportId = route.get(5) ? route.get(5) != "\\N" : ""; 
        System.out.println(route.get(4));
        AirPort destinationAirport = this.airports.get(route.get(4));
        if (destinationAirport != null){
            destinationAirport.setAirLine(route.get(0));
        }
        airport.add(destinationAirport); // to ask: some airports don't have code, hence -> route.get(index: 4) is null and hence airport.get(null) is null
    }

    private AirPort buildAirPortAndCityMap(String[] airport) {
        String cityCode = airport[AirPort.CITY_INDEX] + airport[AirPort.COUNTRY_INDEX]; // Each city will be identified by its name followed by the name of it country to avoid clashing names.
        List<String> cityAirports = this.cities.get(cityCode);
        if (cityAirports == null){
            cityAirports = new ArrayList<>();
            this.cities.put(cityCode, cityAirports);
        }
        cityAirports.add(airport[AirPort.CODE_INDEX]);
        return new AirPort(airport[AirPort.COUNTRY_INDEX], airport[AirPort.CITY_INDEX], airport[AirPort.CODE_INDEX],
                airport[AirPort.ID_INDEX], airport[AirPort.LATITUDE_INDEX], airport[AirPort.LONGITUDE_INDEX],
                airport[AirPort.ALTITUDE_INDEX], cityCode);
    }

    private Boolean hasValidCode(String line) {
        String[] line_array = line.split(FlightRoute.REGEX, -1);
        return !line_array[AirPort.CODE_INDEX].equals("\\N") && !line_array[AirPort.CODE_INDEX].equals(" ");
    }

    private List<String> getConnectedCities(String cityCode){
        List<String> cities = new ArrayList<>();
        for (String airportCode : this.cities.get(cityCode)){
            if (airportCode == null || this.routes.get(airportCode) == null) continue;
            for (AirPort airport : this.routes.get(airportCode)){
                if (airport != null){
                    cities.add(airport.getCityCode());
                }
            }
        }
        return cities;
    }

    public void logic(){
        String start = "LondonUnited Kingdom";
        String end = "YaoundeCameroon";
        Queue<String> frontier = new LinkedList<>();
        HashSet<String> explored =  new HashSet<>();
        if (start.equals(end)){
            System.out.println("No path");
            return;
        }
        frontier.add(start);
        while (!frontier.isEmpty()) {
            String current_city = frontier.remove();
            explored.add(current_city);
            List<String> connectedCities = this.getConnectedCities(current_city);
            for(String city: connectedCities){
                if (current_city.equals(end)){
                    System.out.println(connectedCities);
                    System.out.println("Found a path and solution");
                    return;
                }
                if (!explored.contains(city) && !frontier.contains(city)){
                    frontier.add(city);
                }
            }
            
        }
        System.out.println("No solution found");
    }

    public static void main(String[] args) {
        FlightRoute fr = new FlightRoute();
        fr.logic();

    }


    private class Node{
        private String cityCode;
        private Node parent;
        private int totalFlights;

        public Node(String cityCode, Node parent, int totalFlights){
            this.cityCode = cityCode;
            this.parent = parent;
            this.totalFlights = totalFlights;
        }

        public void getFlightPath(){

        }
    }

}