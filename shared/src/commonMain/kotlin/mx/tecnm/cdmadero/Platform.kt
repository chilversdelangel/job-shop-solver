package mx.tecnm.cdmadero

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform