package com.kayak_backend.services.route

class CombinedGenerator<T>(private vararg val generators: Iterator<T>) : Iterator<List<T>> {
    private var currentGeneratorIndex = 0

    override fun hasNext(): Boolean {
        return generators.any { it.hasNext() }
    }

    override fun next(): List<T> {
        val combinedValues = mutableListOf<T>()
        var currentGenerator = generators[currentGeneratorIndex]

        while (!currentGenerator.hasNext()) {
            currentGeneratorIndex = (currentGeneratorIndex + 1) % generators.size
            currentGenerator = generators[currentGeneratorIndex]
        }

        repeat(generators.size) {
            if (currentGenerator.hasNext()) {
                combinedValues.add(currentGenerator.next())
            }
            currentGeneratorIndex = (currentGeneratorIndex + 1) % generators.size
            currentGenerator = generators[currentGeneratorIndex]
        }
        return combinedValues
    }
}
