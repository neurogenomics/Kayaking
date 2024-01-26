package gribReader

import ucar.nc2.dataset.NetcdfDataset

class NetCDFGribReader : GribReader {
    private val filePath = "/Users/joelulens/Downloads/Cherbourg_4km_WRF_WAM_240125-12.grb"
    private val uVariableName = "TwoD/LatLon_100X120-49p74N-1p333W/u-component_of_current_surface"
    private val vVariableName = "TwoD/LatLon_100X120-49p74N-1p333W/v-component_of_current_surface"
    private val latName = "TwoD/LatLon_100X120-49p74N-1p333W/lat"
    private val lonName = "TwoD/LatLon_100X120-49p74N-1p333W/lon"


    override fun getSingleVar(
        lat: Float,
        lon: Float,
        variableName: String,
        filePath: String,
        latVarName: String,
        lonVarName: String
    ): Float {

        val file = NetcdfDataset.openFile(filePath, null)
        val variable = file.findVariable(variableName)

        val latVar = file.findVariable(latVarName)
        val latData = latVar.read()

        var latIndex = 0
        while (latData.getFloat(latIndex) < lat) {
            latIndex++
        }

        val lonVar = file.findVariable(lonVarName)
        val lonData = lonVar.read()

        var lonIndex = 0
        while (lonData.getFloat(lonIndex) < lon) {
            lonIndex++
        }

        val origin = intArrayOf(0, 0, latIndex, lonIndex)
        val shape = intArrayOf(1, 1, 1, 1)

        return variable.read(origin, shape).reduce().getFloat(0)
    }
}