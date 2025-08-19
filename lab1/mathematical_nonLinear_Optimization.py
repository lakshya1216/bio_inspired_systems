import numpy as np
import random
import matplotlib.pyplot as plt

def fitness_function(x):
    return x * np.sin(10 * np.pi * x) + 1.0

POP_SIZE = 50          
GENS = 100             
MUTATION_RATE = 0.1    
CROSSOVER_RATE = 0.8   
X_BOUND = [0, 1]       


def create_population(size):
    return np.random.uniform(X_BOUND[0], X_BOUND[1], size)

def evaluate_population(pop):
    return np.array([fitness_function(ind) for ind in pop])

def select(pop, fitness):
    idx = np.random.choice(len(pop), size=len(pop), p=fitness/fitness.sum())
    return pop[idx]

def crossover(parent1, parent2):
    if np.random.rand() < CROSSOVER_RATE:
        alpha = np.random.rand() 
        child1 = alpha * parent1 + (1 - alpha) * parent2
        child2 = alpha * parent2 + (1 - alpha) * parent1
        return child1, child2
    else:
        return parent1, parent2

def mutate(child):
    if np.random.rand() < MUTATION_RATE:
        mutation_value = np.random.uniform(-0.1, 0.1)
        child += mutation_value
        child = np.clip(child, X_BOUND[0], X_BOUND[1])  
    return child

def genetic_algorithm():

    population = create_population(POP_SIZE)
    best_fitness_per_gen = []
    best_solution = None
    best_value = -float("inf")

    for gen in range(GENS):
        fitness = evaluate_population(population)

        max_idx = np.argmax(fitness)
        if fitness[max_idx] > best_value:
            best_value = fitness[max_idx]
            best_solution = population[max_idx]
        
        best_fitness_per_gen.append(best_value)

        selected = select(population, fitness)

        next_population = []
        for i in range(0, POP_SIZE, 2):
            parent1, parent2 = selected[i], selected[i+1]
            child1, child2 = crossover(parent1, parent2)
            child1, child2 = mutate(child1), mutate(child2)
            next_population.extend([child1, child2])

        population = np.array(next_population)

    print(f"Best solution: x = {best_solution:.4f}, f(x) = {best_value:.4f}")

    plt.plot(best_fitness_per_gen)
    plt.xlabel("Generation")
    plt.ylabel("Best Fitness")
    plt.title("GA Optimization Progress")
    plt.show()
