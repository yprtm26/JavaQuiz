package com.example.javaquiz.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.javaquiz.data.model.Category

class HomeViewModel : ViewModel() {

    var bestScore by mutableStateOf(1240)
        private set

    var completedQuizzes by mutableStateOf(48)
        private set

    var categories by mutableStateOf(
        listOf(
            Category(
                id = "inheritance",
                name = "Inheritance",
                description = "Menguasai hubungan hierarkis dan penggunaan kembali kode dalam Pemrograman Berorientasi Objek.",
                level = "Pemula",
                iconName = "account_tree"
            ),
            Category(
                id = "inheritance",
                name = "Inheritance",
                description = "Menguasai hubungan hierarkis dan penggunaan kembali kode dalam Pemrograman Berorientasi Objek.",
                level = "Menengah",
                iconName = "account_tree"
            ),
            Category(
                id = "looping",
                name = "Looping",
                description = "Memahami perulangan for, while, dan do-while untuk pemrosesan data yang efisien.",
                level = "Pemula",
                iconName = "refresh"
            ),
            Category(
                id = "looping",
                name = "Looping",
                description = "Memahami perulangan for, while, dan do-while untuk pemrosesan data yang efisien.",
                level = "Menengah",
                iconName = "refresh"
            )
        )
    )
        private set

    var userName by mutableStateOf("Developer")
        private set
}
