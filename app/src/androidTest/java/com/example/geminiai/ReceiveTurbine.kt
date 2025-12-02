package com.example.geminiai

import app.cash.turbine.ReceiveTurbine

suspend fun <T> ReceiveTurbine<List<T>>.awaitNotEmpty(): List<T> {
    var list: List<T>
    do {
        list = this.awaitItem()
    } while (list.isEmpty())
    return list
}

suspend fun <T> ReceiveTurbine<T?>.awaitNotNull(): T {
    var item: T?
    do {
        item = this.awaitItem()
    } while (item == null)
    return item
}
