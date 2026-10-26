package com.example.test2

enum class AppDestinations(
    val label: String,
    val icon: Int
) {
    HOME(
        "Próxima actividad",
        R.drawable.ic_home
    ),
    MEASURES(
        "Mediciones",
        R.drawable.ic_favorite
    ),
    SCHEDULING(
        "Planificacion",
        R.drawable.ic_account_box
    ),
    PERFORMANCE(
        "Desempeño",
        R.drawable.ic_bottom_navigation_performance
    )
}
