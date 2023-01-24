package pro.flown.dgis_maps_flutter

import ru.dgis.sdk.coordinates.GeoPoint

/**
 * Port to Java of Mark McClures Javascript PolylineEncoder :
 * http://facstaff.unca.edu/mcmcclur/GoogleMaps/EncodePolyline/decode.js
 */
class PolylineDecoder {
    companion object {
        private const val DEFAULT_PRECISION = 1E5
    }

    fun decode(encoded: String): List<GeoPoint> {
        return decode(encoded, DEFAULT_PRECISION)
    }

    /**
     * Precision should be something like 1E5 or 1E6. For OSRM routes found precision was 1E6, not the original default
     * 1E5.
     *
     * @param encoded
     * @param precision
     * @return
     */
    fun decode(encoded: String, precision: Double): List<GeoPoint> {
        val track: MutableList<GeoPoint> = ArrayList()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < encoded.length) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = GeoPoint(lat.toDouble() / precision, lng.toDouble() / precision)
            track.add(p)
        }
        return track
    }

}