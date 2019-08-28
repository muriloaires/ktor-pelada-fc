package web.model.incoming

import com.google.gson.annotations.SerializedName
import dao.tables.LoginType

data class NewUser(
    @SerializedName("name") val _name: String?,
    @SerializedName("username") val _username: String?,
    @SerializedName("email") val _email: String?,
    @SerializedName("isAdvertiser") val _isAdvertiser: Boolean?,
    @SerializedName("password") val _password: String?,
    @SerializedName("loginType") val _loginType: String?
) {

    val name
        get() = _name ?: throw IllegalArgumentException("name is required")

    val username
        get() = _username ?: throw IllegalArgumentException("username is required")

    val email
        get() = _email ?: throw IllegalArgumentException("email is required")

    val isAdvertiser
        get() = _isAdvertiser ?: throw IllegalArgumentException("isAdvertiser is required")

    val loginType
        get() = _loginType ?: throw IllegalArgumentException("loginType is required")

    val password: String?
        get() {
            require(loginType == LoginType.DEFAULT.value && _password != null) {
                "password is required for default loginType"
            }
            return _password
        }


}