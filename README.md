# Program Structure
The program contains three primary classes:

1.  The AirPort Class that models an airport.
2.  The Node Class that models a state at any given point in time along the path.
3.  The FlightRoute Class which load the data, and contains all the logic of the program.

The main method of the programm is found in the FlightRoute Class.

## How to run the program

To run the program, open an empty text file and write two lines in it, the lines should be in the form below: 

Accra, Ghana
London, United Kingdom

or 

city, country
city, country

create an instance of the FlightRoute object and called the findOptimalFlightPath method while passing the name of your input file. The program will fine an optimal path and write the result in an output text file.