package edu.itesm.pokedex

data class Cameo(
    val id: String, val nombre: String,
    val genero: String, val universo: String, val foto: String){
    constructor():this("","", "", "","")
}
