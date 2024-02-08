import com.kayak_backend.services.slipways.BeachesService
import io.ktor.server.testing.*
import kotlin.test.*

class BeachesServiceTest {
    private val beachesService = BeachesService()

    @Test
    fun returnsAllBeaches() =
        testApplication {
            val beaches = beachesService.getAllBeaches()
            val numberOfBeaches = 211
            assertEquals(numberOfBeaches, beaches.size)
        }
}
