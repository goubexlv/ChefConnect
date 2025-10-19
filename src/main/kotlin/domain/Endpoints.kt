package cm.daccvo.domain

sealed class Endpoint( val path : String){
    data object Root : Endpoint(path = "/")

    //route auth
    data object Login : Endpoint(path = "/auth/login")
    data object Register : Endpoint(path = "/auth/register")
    data object RefreshToken : Endpoint(path = "/auth/refresh")

    //route recette
    data object Recette : Endpoint(path = "/recette")
    data object UpdateRecette : Endpoint(path = "/recette")
    data object DeleteRecette : Endpoint(path = "/recette")
    data object ConsulteRecette : Endpoint(path = "/recette")
    object AddAvis : Endpoint(path = "/recette/avis")
    data object Search : Endpoint(path = "/search")
    data object FiltreSearch : Endpoint(path = "/search/filter")


}