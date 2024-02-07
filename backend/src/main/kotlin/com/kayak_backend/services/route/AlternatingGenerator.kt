package com.kayak_backend.services.route

class AlternatingGenerator<T>(private val generators: List<Iterator<T>>) : Iterator<T> {
    private var currentGeneratorIndex = 0

    override fun hasNext(): Boolean {
        return generators.any { it.hasNext() }
    }

    override fun next(): T {
        var currentGenerator = generators[currentGeneratorIndex]
        while (!currentGenerator.hasNext()) {
            currentGeneratorIndex = (currentGeneratorIndex + 1) % generators.size
            currentGenerator = generators[currentGeneratorIndex]
        }
        currentGeneratorIndex = (currentGeneratorIndex + 1) % generators.size
        return currentGenerator.next()
    }
}
