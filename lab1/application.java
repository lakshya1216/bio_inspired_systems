import java.util.*;

public class TSPGeneticAlgorithm {

    // --- CONFIGURATION ---
    static final int NUM_CITIES = 10;
    static final int POPULATION_SIZE = 100;
    static final int NUM_GENERATIONS = 500;
    static final double MUTATION_RATE = 0.015;
    static final int TOURNAMENT_SIZE = 5;
    static final boolean ELITISM = true;

    // --- CITY CLASS ---
    static class City {
        int x, y;

        City() {
            this.x = (int) (Math.random() * 100);
            this.y = (int) (Math.random() * 100);
        }

        double distanceTo(City city) {
            int dx = this.x - city.x;
            int dy = this.y - city.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    // --- TOUR CLASS (a possible solution) ---
    static class Tour {
        ArrayList<City> cities = new ArrayList<>();
        double fitness = 0;
        double distance = 0;

        Tour(ArrayList<City> cities) {
            this.cities.addAll(cities);
            Collections.shuffle(this.cities);
        }

        Tour(Tour other) {
            this.cities = new ArrayList<>(other.cities);
            this.fitness = other.fitness;
            this.distance = other.distance;
        }

        double getDistance() {
            if (distance == 0) {
                double total = 0;
                for (int i = 0; i < cities.size(); i++) {
                    City from = cities.get(i);
                    City to = cities.get((i + 1) % cities.size());
                    total += from.distanceTo(to);
                }
                distance = total;
            }
            return distance;
        }

        double getFitness() {
            if (fitness == 0) {
                fitness = 1 / getDistance();
            }
            return fitness;
        }

        void mutate() {
            for (int i = 0; i < cities.size(); i++) {
                if (Math.random() < MUTATION_RATE) {
                    int j = (int) (cities.size() * Math.random());
                    Collections.swap(cities, i, j);
                }
            }
            // reset fitness and distance
            fitness = 0;
            distance = 0;
        }

        public String toString() {
            return cities.toString();
        }
    }

    // --- POPULATION CLASS ---
    static class Population {
        Tour[] tours;

        Population(int size, ArrayList<City> allCities) {
            tours = new Tour[size];
            for (int i = 0; i < size; i++) {
                tours[i] = new Tour(allCities);
            }
        }

        Tour getFittest() {
            return Arrays.stream(tours).max(Comparator.comparingDouble(Tour::getFitness)).orElse(null);
        }
    }

    // --- GA OPERATIONS ---
    static Tour crossover(Tour parent1, Tour parent2) {
        int size = parent1.cities.size();
        Tour child = new Tour(new ArrayList<>(Collections.nCopies(size, null)));

        int start = (int) (Math.random() * size);
        int end = (int) (Math.random() * size);

        for (int i = Math.min(start, end); i < Math.max(start, end); i++) {
            child.cities.set(i, parent1.cities.get(i));
        }

        int current = 0;
        for (int i = 0; i < size; i++) {
            City city = parent2.cities.get(i);
            if (!child.cities.contains(city)) {
                while (child.cities.get(current) != null) {
                    current++;
                }
                child.cities.set(current, city);
            }
        }

        return child;
    }

    static Tour tournamentSelection(Population pop) {
        Population tournament = new Population(TOURNAMENT_SIZE, new ArrayList<>());
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int randIdx = (int) (Math.random() * pop.tours.length);
            tournament.tours[i] = pop.tours[randIdx];
        }
        return tournament.getFittest();
    }

    static Population evolvePopulation(Population pop) {
        Population newPop = new Population(pop.tours.length, new ArrayList<>());
        int offset = 0;

        if (ELITISM) {
            newPop.tours[0] = pop.getFittest();
            offset = 1;
        }

        for (int i = offset; i < pop.tours.length; i++) {
            Tour parent1 = tournamentSelection(pop);
            Tour parent2 = tournamentSelection(pop);
            Tour child = crossover(parent1, parent2);
            child.mutate();
            newPop.tours[i] = child;
        }

        return newPop;
    }

    // --- MAIN ---
    public static void main(String[] args) {
        // Create cities
        ArrayList<City> cities = new ArrayList<>();
        for (int i = 0; i < NUM_CITIES; i++) {
            cities.add(new City());
        }

        // Initialize population
        Population pop = new Population(POPULATION_SIZE, cities);
        System.out.println("Initial distance: " + pop.getFittest().getDistance());

        // Evolve
        for (int i = 0; i < NUM_GENERATIONS; i++) {
            pop = evolvePopulation(pop);
            if (i % 50 == 0 || i == NUM_GENERATIONS - 1) {
                System.out.printf("Generation %d: Best distance = %.2f\n", i, pop.getFittest().getDistance());
            }
        }

        // Final result
        Tour best = pop.getFittest();
        System.out.println("\nFinal best distance: " + best.getDistance());
        System.out.println("Best tour: " + best);
    }
}
