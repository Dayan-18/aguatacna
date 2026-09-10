package pe.edu.upt.aguatacna

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform