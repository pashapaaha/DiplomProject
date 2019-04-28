package com.example.paaha.findyourfriend.model

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val pin: String = "",

    var latitude: Double? = null,
    var longitude: Double? = null,
    var lastLocationUpdate: Long? = null
)

//TODO: В рамках ветки feature/safe выполнить:
// +    Добавить для каждого юзера поле пин-код.
//      Пин-код представляет собой пятизначное число, хранящееся в виде строки формата %05d
// +    Для класса FriendInfo добавить поле, хранящее информацию об активности друга
//      то есть, был ли введен пин-код
// +    Добавить окно, в котором у пользователя есть возможность ввести пин-код (если еще не введен)
//      открывается после нажатия на имя друга в окне Friends
// +    Обработать кнопку ввода пина
// -    Добавить окно с личными данными пользователя, на котором будет отобржааться пин-код
// -    Предусмотреть проверку на активность друга при отображении на карте