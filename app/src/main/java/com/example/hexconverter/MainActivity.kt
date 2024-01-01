package com.example.hexconverter

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.example.convertex.R
import com.example.convertex.databinding.ActivityMainBinding
import java.nio.charset.StandardCharsets


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.GradienBottom)
        binding.btnCovert.setOnClickListener()
        {
            val input = binding.txtInput.text.toString()
            if (input.isNotEmpty()) {
                val decodedString = hexToString(input)
                binding.txtResult.text = decodedString
            } else {
                CustomToast.showCustomToast(this, "The input string is empty", false)

            }
        }

        binding.btnCopy.setOnClickListener()
        {
            val strCopy = binding.txtResult.text.toString()
            if (strCopy.isNotEmpty()) {
                copyToClipboard(strCopy)

            } else {
                CustomToast.showCustomToast(this, "The result string is empty", false)
            }
        }
    }

    private fun hexToString(hexString: String): String {
        return try {
            val bytes = hexStringToByteArray(hexString.trimStart().trimEnd())
            String(bytes, StandardCharsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            "Conversion failure: ${e.message}"
        }
    }

    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        var j = 0

        while (i < len) {
            val firstDigit = hexString[i].digitToIntOrNull(16) ?: -1
            val secondDigit = hexString.getOrNull(i + 1)?.digitToIntOrNull(16) ?: -1

            if (firstDigit == -1 || secondDigit == -1) {
                CustomToast.showCustomToast(this, "Conversion failure: Invalid hex string", false)
                throw IllegalArgumentException("Invalid hex string")
            }

            data[j++] = ((firstDigit shl 4) + secondDigit).toByte()
            i += 2
        }

        return data.copyOf(j)
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = ClipData.newPlainText("label", text)

        clipboardManager.setPrimaryClip(clipData)
        CustomToast.showCustomToast(this, "The text was copied successfully", true)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val isAndroid10Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        if (!isAndroid10Plus) return

        val clipboard = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip
        if (clipboard != null) {

            if (clip != null && clip.itemCount > 0) {
                val item = clip.getItemAt(0)
                val strPaste = item.text?.toString()

                if (!strPaste.isNullOrEmpty()) {
                    val editableText: Editable =
                        Editable.Factory.getInstance().newEditable(strPaste)
                    binding.txtInput.text = editableText
                }
            }
        }
        super.onWindowFocusChanged(hasFocus)
    }

}