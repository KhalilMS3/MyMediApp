import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

data class Pharmacy(val name: String, val location: LatLng)

class PharmacyMapViewModel : ViewModel() {
    fun getNearbyPharmacies(userLocation: LatLng): List<Pharmacy> {
        // Dummy data for pharmacies - replace with real data fetching logic
        return listOf(
            Pharmacy("Pharmacy A", LatLng(userLocation.latitude + 0.01, userLocation.longitude + 0.01)),
            Pharmacy("Pharmacy B", LatLng(userLocation.latitude - 0.01, userLocation.longitude - 0.01)),
            Pharmacy("Pharmacy C", LatLng(userLocation.latitude + 0.02, userLocation.longitude - 0.02))
        )
    }
}
