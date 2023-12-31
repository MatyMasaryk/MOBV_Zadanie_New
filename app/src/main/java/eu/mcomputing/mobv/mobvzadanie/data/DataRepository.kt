package eu.mcomputing.mobv.mobvzadanie.data

import android.content.Context
import eu.mcomputing.mobv.mobvzadanie.data.api.ApiService
import eu.mcomputing.mobv.mobvzadanie.data.api.model.ChangePasswordRequest
import eu.mcomputing.mobv.mobvzadanie.data.api.model.GeofenceUpdateRequest
import eu.mcomputing.mobv.mobvzadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.mobvzadanie.data.api.model.UserRegistrationRequest
import eu.mcomputing.mobv.mobvzadanie.data.db.AppRoomDatabase
import eu.mcomputing.mobv.mobvzadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.mobvzadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.mobvzadanie.data.model.User
import eu.mcomputing.mobv.mobvzadanie.data.db.LocalCache
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                        ApiService.create(context),
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
                    ).also { INSTANCE = it }
            }
    }

    suspend fun apiRegisterUser(
        username: String,
        email: String,
        password: String,
        repeatPassword: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        if (repeatPassword.isEmpty()) {
            return Pair("Repeat the password", null)
        }
        if (password != repeatPassword) {
            return Pair("Passwords don't match", null)
        }
        try {
//            val hashedPassword = hash(password)
//            val hashedString = hashedPassword.toString()

            val response = service.registerUser(UserRegistrationRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    println(json_response)
                    if (json_response.uid == "-1") {
                        return Pair("Username already taken.", null)
                    }
                    if (json_response.uid == "-2") {
                        return Pair("Email already taken.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            email,
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to create user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to create user.", null)
    }

    suspend fun apiLoginUser(
        username: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
//            val hashedPassword = hash(password)
//            val hashedString = hashedPassword.toString()

            val response = service.loginUser(UserLoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("Wrong password or username.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            "",
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }

    suspend fun apiGetUser(
        uid: String
    ): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair("", User(it.name, "", it.id, "", "", it.photo))
                }
            }

            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }

    suspend fun apiChangePassword(
        oldPassword: String,
        newPassword: String,
        repeatPassword: String
    ): String {
        if (oldPassword.isEmpty()) {
            return "Password can not be empty"
        }
        if (newPassword.isEmpty()) {
            return "Password can not be empty"
        }
        if (repeatPassword.isEmpty()) {
            return "Repeat the new password"
        }
        if (newPassword != repeatPassword) {
            return "Passwords don't match"
        }
        try {
//            val hashedPassword = hash(oldPassword)
//            val hashedString = hashedPassword.toString()
//
//            val hashedNew = hash(newPassword)
//            val hashedStringNew = hashedNew.toString()

            val response = service.changePassword(ChangePasswordRequest(oldPassword, newPassword))

            if (response.isSuccessful) {
                response.body()?.let {
                    return ""
                }
            }
            return "Failed to change password."
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to change password."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to change password."
    }

    suspend fun apiListGeofence(): String {
        try {
            val response = service.listGeofence()

            if (response.isSuccessful) {
                response.body()?.list?.let { it ->
                    val users = it.map {
                        UserEntity(
                            it.uid, it.name, it.updated,
                            0.0, 0.0, it.radius, it.photo
                        )
                    }
                    cache.insertUserItems(users)
                    return ""
                }
            }

            return "Failed to load users"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to load users."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to load users."
    }

    fun getUsers() = cache.getUsers()

    suspend fun getUsersList() = cache.getUsersList()

    suspend fun insertGeofence(item: GeofenceEntity) {
        cache.insertGeofence(item)
        try {
            val response =
                service.updateGeofence(GeofenceUpdateRequest(item.lat, item.lon, item.radius))

            if (response.isSuccessful) {
                response.body()?.let {

                    item.uploaded = true
                    cache.insertGeofence(item)
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun removeGeofence() {
        try {
            val response = service.deleteGeofence()

            if (response.isSuccessful) {
                response.body()?.let {
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
