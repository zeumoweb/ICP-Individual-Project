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


/**
 * Model flights accross different cities and countrys. Allow the computation of
 * the shortest number of flights between two cities.
 * 
 * @author Styve Lekane.
 */
public class FlightRoute {
    // Allows us to split csv files on comma while ignoring strings that contains
    // comma as part of it.
    private static final String REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final int AIRLINE_INDEX = 0;
    private static final int SOURCE_AIRPORT_INDEX = 2;
    private static final int DESTINATION_AIRPORT_INDEX = 4;
    private static final int NUMBER_OF_STOPS_INDEX = 7;

    // Maps aiport code to list of connected airports.
    private Map<String, List<AirPort>> routes = new HashMap<>();
    // Maps aiport code to airport info (AirPort Object).
    private Map<String, AirPort> airports;
    // Maps city to all airport within that city.
    private Map<String, List<String>> cities = new HashMap<>();

    public FlightRoute() {
        try {
            loadData("airports.csv", "airlines.csv", "routes.csv");
        } catch (IOException e) {
            System.out.println("An exception occured while reading the files.");
            System.exit(-1);
        }

    }

    /**
     * Loads airport and routes data into the class data structure's fields.
     * 
     * @param airportDataFilePath  Path to airport csv data file.
     * @param airlinesDataFilePath Path to airlines csv data file.
     * @param routesDataFilePath   Path to routes csv data file.
     * @throws IOException
     */
    private void loadData(String airportDataFilePath, String airlinesDataFilePath, String routesDataFilePath)
            throws IOException {
        Stream<String> airports = Files.lines(Paths.get(airportDataFilePath));
        this.airports = airports.filter(this::hasValidIataCode)
                .map(line -> buildAirPortAndCityMap(line.split(FlightRoute.REGEX, -1)))
                .collect(Collectors.toMap(AirPort::getCode, airport -> airport));
        airports.close();

        List<List<String>> tempRoutes = Files.lines(Paths.get(routesDataFilePath))
                .map(route -> List.of(route.split(FlightRoute.REGEX, -1)))
                .collect(Collectors.toList());
        tempRoutes.forEach(this::addPathToRoutes);
    }

    /**
     * Checks if an entry in the airports.csv dataset has a valid IATA code.
     * 
     * @param line Entry/Row in the airport.csv dataset.
     * @return True if valid, and False otherwise.
     */
    private Boolean hasValidIataCode(String line) {
        String[] line_array = line.split(FlightRoute.REGEX, -1);
        return !line_array[AirPort.CODE_INDEX].equals("\\N") && !line_array[AirPort.CODE_INDEX].equals(" ");
    }

    /**
     * Constructs the cities Hashmap by mapping all cities to its list of
     * corresponding connected airports.
     * 
     * @param airport Row in the airport dataset.
     * @return A new AirPort Object build using each row of the airport dataset.
     */
    private AirPort buildAirPortAndCityMap(String[] airport) {
        String cityCode = airport[AirPort.CITY_INDEX] + ", " + airport[AirPort.COUNTRY_INDEX];
        List<String> cityAirports = this.cities.get(cityCode);
        if (cityAirports == null) {
            cityAirports = new ArrayList<>();
            this.cities.put(cityCode, cityAirports);
        }
        cityAirports.add(airport[AirPort.CODE_INDEX]);
        return new AirPort(airport[AirPort.COUNTRY_INDEX], airport[AirPort.CITY_INDEX], airport[AirPort.CODE_INDEX],
                airport[AirPort.ID_INDEX], Double.parseDouble(airport[AirPort.LATITUDE_INDEX]), Double.parseDouble(airport[AirPort.LONGITUDE_INDEX]),
                Double.parseDouble(airport[AirPort.ALTITUDE_INDEX]), cityCode);
    }

    /**
     * Fill the routes field of the class by matching every aiport to its list of
     * corresponding destinations.
     * 
     * @param route Row in the routes.csv dataset.
     */
    private void addPathToRoutes(List<String> route) {
        List<AirPort> destinationAirports = this.routes.get(route.get(FlightRoute.SOURCE_AIRPORT_INDEX));
        if (destinationAirports == null) {
            destinationAirports = new ArrayList<>();
            this.routes.put(route.get(FlightRoute.SOURCE_AIRPORT_INDEX), destinationAirports);
        }
        AirPort destinationAirport = this.airports.get(route.get(FlightRoute.DESTINATION_AIRPORT_INDEX));
        if (destinationAirport != null) {
            destinationAirport = new AirPort(destinationAirport);
            destinationAirport.setAirLineCode(route.get(FlightRoute.AIRLINE_INDEX));
            destinationAirport.setStops(Integer.parseInt(route.get(FlightRoute.NUMBER_OF_STOPS_INDEX)));
        }
        destinationAirports.add(destinationAirport);
    }

    /**
     * Returns all the list of connected Airports to a given city.
     * 
     * @param cityCode The unique identifier city code.
     * @return A list of AirPort objects.
     */
    private List<AirPort> getConnectedAirPorts(String cityCode) {
        List<AirPort> connectedAirPorts = new ArrayList<>();
        for (String airportCode : this.cities.get(cityCode)) {
            if (airportCode == null || this.routes.get(airportCode) == null)
                continue;
            for (AirPort airport : this.routes.get(airportCode)) {
                if (airport != null && routes.containsKey(airport.getCode())) {
                    connectedAirPorts.add(airport);
                }
            }
        }
        return connectedAirPorts;
    }

    private double calculateHaversineDistance(double latSource, double lonSource,
            double latDestination, double lonDestination) {
        // get the distance between source and destination latitudes and longitudes
        double diffLat = Math.toRadians(latDestination - latSource);
        double diffLon = Math.toRadians(lonDestination - lonSource);

        latSource = Math.toRadians(latSource);
        latDestination = Math.toRadians(latDestination);

        // Compute Distance
        double dist = Math.pow(Math.sin(diffLat / 2), 2) +
                Math.pow(Math.sin(diffLon / 2), 2) *
                        Math.cos(latSource) *
                        Math.cos(latSource);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(dist));
        return rad * c;
    }

    private List<String> readLocationFile(String path) {
        try {
            return Files.lines(Paths.get(path)).collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("An Errro occured while reading the input file");
            System.exit(-1);
        }
        return null;
    }

    /**
     * Find the optimal flight path between two cities and output the result into a
     * text file.
     * The optimal flight path is considered to be the path with the lowest number
     * of flights.
     * 
     * @param locationFilePath
     */
    public void findOptimalFlightPath(String locationFilePath) {
        List<String> locations = this.readLocationFile(locationFilePath + ".txt");
        String sourceCity = locations.get(0);
        String destinationCity = locations.get(1);
        Queue<Node> frontier = new LinkedList<>();
        for (String airport : this.cities.get(sourceCity)) {
            if (!this.routes.containsKey(airport)){
                continue;
            }
            frontier.add(new Node(sourceCity, null, airport, null, 0, 0, 0));
        }

        HashSet<String> explored = new HashSet<>();
        while (!frontier.isEmpty()) {
            Node currentCity = frontier.remove();
            AirPort currentAirPort = airports.get(currentCity.airport);
            explored.add(currentCity.cityCode);
            List<AirPort> connectedAirPorts = this.getConnectedAirPorts(currentCity.cityCode);
            for (AirPort airport : connectedAirPorts) {
                double distance = this.calculateHaversineDistance(currentAirPort.getLatitude(), currentAirPort.getLongitude(), airport.getLatitude(), airport.getLongitude());
                Node nextCity = new Node(airport.getCityCode(), currentCity, airport.getCode(), airport.getAirLine(),
                        currentCity.totalFlights + 1, airport.getStops(), currentCity.totalDistance + distance);
                if (airport.getCityCode().equals(destinationCity)) {
                    System.out.println("Found a path and solution");
                    nextCity.writeFlightPathToFile(locationFilePath);
                    return;
                }
                if (!explored.contains(airport.getCityCode()) && !frontier.contains(nextCity)) {
                    frontier.add(nextCity);
                }
            }

        }
        System.out.println("No solution found");
    }

    public static void main(String[] args) {

        FlightRoute fr = new FlightRoute();
        fr.findOptimalFlightPath("accra_london");
    }
}
