import java.util.Random;

public class PSOSphereOptimization {

    // --- CONFIGURATION ---
    static final int DIMENSIONS = 5;
    static final int SWARM_SIZE = 30;
    static final int MAX_ITERATIONS = 100;
    static final double MIN_POSITION = -10;
    static final double MAX_POSITION = 10;
    static final double VELOCITY_MIN = -1;
    static final double VELOCITY_MAX = 1;
    static final double C1 = 2.0; // cognitive component
    static final double C2 = 2.0; // social component
    static final double INERTIA = 0.7;

    static Random rand = new Random();

    // --- PARTICLE CLASS ---
    static class Particle {
        double[] position = new double[DIMENSIONS];
        double[] velocity = new double[DIMENSIONS];
        double[] bestPosition = new double[DIMENSIONS];
        double bestValue = Double.MAX_VALUE;

        Particle() {
            for (int i = 0; i < DIMENSIONS; i++) {
                position[i] = MIN_POSITION + (MAX_POSITION - MIN_POSITION) * rand.nextDouble();
                velocity[i] = VELOCITY_MIN + (VELOCITY_MAX - VELOCITY_MIN) * rand.nextDouble();
                bestPosition[i] = position[i];
            }
            bestValue = evaluate(position);
        }

        void updateVelocity(double[] globalBestPosition) {
            for (int i = 0; i < DIMENSIONS; i++) {
                double r1 = rand.nextDouble();
                double r2 = rand.nextDouble();

                velocity[i] = INERTIA * velocity[i]
                        + C1 * r1 * (bestPosition[i] - position[i])
                        + C2 * r2 * (globalBestPosition[i] - position[i]);

                // Clamp velocity
                if (velocity[i] > VELOCITY_MAX) velocity[i] = VELOCITY_MAX;
                if (velocity[i] < VELOCITY_MIN) velocity[i] = VELOCITY_MIN;
            }
        }

        void updatePosition() {
            for (int i = 0; i < DIMENSIONS; i++) {
                position[i] += velocity[i];

                // Clamp position
                if (position[i] > MAX_POSITION) position[i] = MAX_POSITION;
                if (position[i] < MIN_POSITION) position[i] = MIN_POSITION;
            }

            double value = evaluate(position);
            if (value < bestValue) {
                bestValue = value;
                System.arraycopy(position, 0, bestPosition, 0, DIMENSIONS);
            }
        }
    }

    // --- SPHERE FUNCTION ---
    static double evaluate(double[] position) {
        double sum = 0.0;
        for (double x : position) {
            sum += x * x;
        }
        return sum;
    }

    // --- MAIN OPTIMIZATION ---
    public static void main(String[] args) {
        Particle[] swarm = new Particle[SWARM_SIZE];
        double[] globalBestPosition = new double[DIMENSIONS];
        double globalBestValue = Double.MAX_VALUE;

        // Initialize swarm
        for (int i = 0; i < SWARM_SIZE; i++) {
            swarm[i] = new Particle();
            if (swarm[i].bestValue < globalBestValue) {
                globalBestValue = swarm[i].bestValue;
                System.arraycopy(swarm[i].bestPosition, 0, globalBestPosition, 0, DIMENSIONS);
            }
        }

        // PSO loop
        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            for (Particle particle : swarm) {
                particle.updateVelocity(globalBestPosition);
                particle.updatePosition();

                if (particle.bestValue < globalBestValue) {
                    globalBestValue = particle.bestValue;
                    System.arraycopy(particle.bestPosition, 0, globalBestPosition, 0, DIMENSIONS);
                }
            }

            if (iter % 10 == 0 || iter == MAX_ITERATIONS - 1) {
                System.out.printf("Iteration %d: Best Value = %.6f\n", iter, globalBestValue);
            }
        }

        // Final output
        System.out.println("\nBest solution found:");
        for (int i = 0; i < DIMENSIONS; i++) {
            System.out.printf("x[%d] = %.4f\n", i, globalBestPosition[i]);
        }
        System.out.printf("Final minimized value: %.6f\n", globalBestValue);
    }
}
