package com.example.myapplication1.repository

import com.example.myapplication1.firebase.BookDataSource
import com.example.myapplication1.recycler.Book

class BookRepository(private val dataSource: BookDataSource) {
    suspend fun getBooks(userId: String) = dataSource.getBooks(userId)
    fun addBook(userId: String, book: Book) = dataSource.addBook(userId, book)
    fun updateBook(userId: String, book: Book) = dataSource.updateBook(userId, book)
    suspend fun saveBook(book: Book) = dataSource.saveBook(book)

}



