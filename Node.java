import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Models the state of an traveller at any given point along the path between the source city and the destination city.
 * @author Styve Lekane
 */
public class Node {
    public String cityCode;
    public String airline;
    public String airport;
    public Node parent;
    public int totalFlights;
    public int stops;
    public double totalDistance;

    public Node(String cityCode, Node parent, String airport, String airline, int totalFlights, int stops,
            double totalDistance) {
        this.cityCode = cityCode;
        this.parent = parent;
        this.totalFlights = totalFlights;
        this.stops = stops;
        this.airline = airline;
        this.airport = airport;
        this.totalDistance = totalDistance;
    }

    /**
     * Writes the output path to the output file.
     * @param outputPath Name of output file.
     */
    public void writeFlightPathToFile(String outputPath) {
        Node curr = this;
        int totalStops = 0;
        List<String> path = new ArrayList<>();
        List<String> airlinePath = new ArrayList<>();
        List<String> airportPath = new ArrayList<>();
        List<Integer> stops = new ArrayList<>();
        while (curr != null) {
            path.add(curr.cityCode);
            airlinePath.add(curr.airline);
            airportPath.add(curr.airport);
            stops.add(curr.stops);
            totalStops += curr.stops;
            curr = curr.parent;
        }
        Collections.reverse(path);
        Collections.reverse(airlinePath);
        Collections.reverse(airportPath);
        Collections.reverse(stops);

        writeToFile(outputPath, airlinePath, airportPath, stops, totalStops, totalFlights, totalDistance);
    }

    private void writeToFile(String outputPath, List<String> airlinePath, List<String> airportPath, List<Integer> stops, int totalStops, int totalFlights, double totalDistance){
        try {
            FileWriter fstream = new FileWriter(outputPath + "_output.txt");
            for (int i = 0; i < airportPath.size() - 1; i++) {
                String line = new StringBuilder(airlinePath.get(i + 1)).append(" From ")
                        .append(airportPath.get(i)).append(" To ").append(airportPath.get(i + 1))
                        .append(" ")
                        .append(String.valueOf(stops.get(i + 1)))
                        .append(" ")
                        .append(" Stops.\n").toString();
                fstream.write(line);
            }
            String line = new StringBuilder().append("Total flights: ")
                    .append(String.valueOf(totalFlights) + "\n")
                    .append("Total additional stops: " + String.valueOf(totalStops) + "\n")
                    .append("Total distance: ")
                    .append(String.valueOf(Math.round(this.totalDistance)) + " Km\n")
                    .append("Optimality criteria: flights")
                    .toString();
            fstream.write(line);
            fstream.close();
        } catch (IOException e) {
            System.out.println("Failed to write to file");
            System.exit(-1);
        }
    }

    @Override
    public boolean equals(Object node) {
        final Node other = (Node) node;
        return other.cityCode == this.cityCode && other.airline == this.airline;
    }
}
