package gribReader

interface GribReader {
    fun getSingleVar(lat: Float, lon: Float, variableName: String, filePath: String, latVarName: String, lonVarName: String): Float
}