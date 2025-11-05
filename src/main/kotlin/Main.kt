package ru.netology

import kotlinx.coroutines.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// ---------- Модели ----------
data class Post(
    val id: Long,
    val authorId: Long,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var fullAuthor: Author? = null
)

data class Author(
    val id: Long,
    val name: String,
    val avatar: String
)

// ---------- API ----------
interface ApiService {
    @GET("api/posts")
    suspend fun getPosts(): List<Post>

    @GET("api/authors/{id}")
    suspend fun getAuthorById(@Path("id") id: Long): Author
}

// ---------- MAIN ----------
fun main() = runBlocking {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:9999/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api = retrofit.create(ApiService::class.java)

    println("Загружаем посты...")
    val posts = api.getPosts()

    println("Загружаем авторов для постов...")
    val postsWithAuthors = posts.map { post ->
        async {
            val author = api.getAuthorById(post.authorId)
            post.copy(fullAuthor = author)
        }
    }.awaitAll()

    println("\n Результат:")
    postsWithAuthors.forEach { println("${it.fullAuthor?.name}: ${it.content}") }
}