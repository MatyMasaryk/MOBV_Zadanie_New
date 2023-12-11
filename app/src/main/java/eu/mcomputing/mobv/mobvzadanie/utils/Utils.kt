package eu.mcomputing.mobv.mobvzadanie.utils

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.mobvzadanie.config.AppConfig
import eu.mcomputing.mobv.mobvzadanie.data.db.entities.UserEntity
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class ItemDiffCallback(
    private val oldList: List<UserEntity>,
    private val newList: List<UserEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].uid == newList[newItemPosition].uid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

@BindingAdapter(
    "showTextToast"
)
fun applyShowTextToast(
    view: View,
    message: Evento<String>?
) {
    message?.getContentIfNotHandled()?.let {
        if (it.isNotBlank()) {
            Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
        }
    }
}

fun generateRandomSalt(): ByteArray {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    return salt
}

fun ByteArray.toHexString(): String = joinToString(separator = "") { eachByte ->
    "%02x".format(eachByte)
}

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256

fun generateHash(password: String, salt: String): String {
    val combinedSalt = "$salt${AppConfig.Hash_SECRET}".toByteArray()

    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
    val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded

    return hash.toHexString()
}
