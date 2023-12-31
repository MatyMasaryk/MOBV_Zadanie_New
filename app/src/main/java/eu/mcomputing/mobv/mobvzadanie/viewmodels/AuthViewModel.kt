package eu.mcomputing.mobv.mobvzadanie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.mobvzadanie.data.DataRepository
import eu.mcomputing.mobv.mobvzadanie.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeat_password = MutableLiveData<String>()

    private val _passwordResult = MutableLiveData<String>()
    val passwordResult: LiveData<String> get() = _passwordResult

    val oldPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val repeatPasswordChange = MutableLiveData<String>()

    val passResultMessage: String = "Create a new password"

    fun registerUser() {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(
                username.value ?: "",
                email.value ?: "",
                password.value ?: "",
                repeat_password.value ?: ""
            )
            _registrationResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(username.value ?: "", password.value ?: "")
            _loginResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun changePassword() {
        viewModelScope.launch {
            val result = dataRepository.apiChangePassword(oldPassword.value ?: "",
                newPassword.value ?: "", repeatPasswordChange.value ?: "")
            _passwordResult.postValue(result ?: "")
        }
    }

    fun clearModel() {
        _userResult.value = null
        password.value = ""
        repeat_password.value = ""
        clearPassResult()
    }

    fun clearPassResult() {
        _passwordResult.value = passResultMessage
        oldPassword.value = ""
        newPassword.value = ""
        repeatPasswordChange.value = ""
    }
}