import numpy as np

# Distance matrix for cities (example: 8 cities)
dist_matrix = np.array([
    [0, 12, 10, 19, 8,  15, 14, 20],
    [12, 0, 3, 7, 2,  8,  6,  10],
    [10, 3, 0, 6, 20,  4,  5,  7],
    [19, 7, 6, 0, 18,  3,  9,  4],
    [8,  2, 20, 18, 0, 11, 13, 9],
    [15, 8, 4, 3, 11, 0, 6,  2],
    [14, 6, 5, 9, 13, 6, 0, 12],
    [20, 10, 7, 4, 9, 2, 12, 0]
])

num_cities = dist_matrix.shape[0]

# Fitness: total distance of route
def route_cost(route):
    cost = 0
    for i in range(len(route)-1):
        cost += dist_matrix[route[i], route[i+1]]
    cost += dist_matrix[route[-1], route[0]]  # return to start
    return cost

# Generate a random route
def random_route():
    return np.random.permutation(num_cities)

# Levy-inspired random swap mutation
def mutate(route):
    new_route = route.copy()
    i, j = np.random.choice(len(route), 2, replace=False)
    new_route[i], new_route[j] = new_route[j], new_route[i]
    return new_route

# Cuckoo Search for TSP
def cuckoo_search_tsp(n=15, pa=0.25, max_iter=10):   # changed to 10
    nests = [random_route() for _ in range(n)]
    fitness = [route_cost(r) for r in nests]
    best_idx = np.argmin(fitness)
    best_route = nests[best_idx].copy()
    best_cost = fitness[best_idx]

    for t in range(1, max_iter+1):
        for i in range(n):
            new_route = mutate(nests[i])
            new_cost = route_cost(new_route)
            if new_cost < fitness[i]:
                nests[i] = new_route
                fitness[i] = new_cost

        for i in range(n):
            if np.random.rand() < pa:
                nests[i] = random_route()
                fitness[i] = route_cost(nests[i])

        current_best_idx = np.argmin(fitness)
        if fitness[current_best_idx] < best_cost:
            best_route = nests[current_best_idx].copy()
            best_cost = fitness[current_best_idx]

        print(f"Iteration {t}/{max_iter} - Best cost so far: {best_cost:.4f}")

    return best_route, best_cost


# Run TSP optimization
np.random.seed(42)
best_route, best_cost = cuckoo_search_tsp(n=20, pa=0.3, max_iter=10)  # set to 10

print("\nBest route found:")
print(best_route)
print("Cost of the best route:", round(best_cost, 4))
