package cm.daccvo.domain

sealed class Endpoint( val path : String){
    data object Root : Endpoint(path = "/")
    data object Login : Endpoint(path = "/auth/login")
    data object Register : Endpoint(path = "/auth/register")
    data object RefreshToken : Endpoint(path = "/auth/refresh")

}