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
    val hexArray = "0123456789ABCDEF".toCharArray()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.HexToStringBottom)
        // setOnClickListener for Text Manipulation
        setOnClickTextManipulation()
        binding.btnConvert.setOnClickListener()
        {
            if (binding.btnSource.text.toString() == getString(R.string.txtHex)) {
                val input = binding.txtInput.text.toString()
                if (input.isNotEmpty()) {
                    val decodedString = hexToString(input)
                    binding.txtResult.text = decodedString
                } else {
                    CustomToast.showCustomToast(
                        this,
                        getString(R.string.logErrorInputEmpty),
                        CustomToastType.ERROR
                    )
                }
            } else {
                val input = binding.txtInput.text.toString()
                if (input.isNotEmpty()) {
                    val decodedString = stringToHex(input)
                    binding.txtResult.text = decodedString
                } else {
                    CustomToast.showCustomToast(
                        this,
                        getString(R.string.logErrorInputEmpty),
                        CustomToastType.ERROR
                    )
                }
            }
        }

        binding.btnSwap.setOnClickListener()
        {
            if (binding.btnSource.text.toString() == getString(R.string.txtHex)) {
                binding.btnSource.text = getString(R.string.txtText)
                binding.btnDestination.text = getString(R.string.txtHex)
            } else {
                binding.btnSource.text = getString(R.string.txtHex)
                binding.btnDestination.text = getString(R.string.txtText)
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
        val cleanedHexString = hexString.replace("\\s".toRegex(), "")
        val len = cleanedHexString.length
        val data = ByteArray(len / 2)
        var i = 0
        var j = 0
        while (i < len) {
            val firstDigit = cleanedHexString[i].digitToIntOrNull(16) ?: -1
            val secondDigit = cleanedHexString.getOrNull(i + 1)?.digitToIntOrNull(16) ?: -1

            if (firstDigit == -1 || secondDigit == -1) {
                throw IllegalArgumentException("Invalid hex string")
            }

            data[j++] = ((firstDigit shl 4) + secondDigit).toByte()
            i += 2
        }
        return data.copyOf(j)
    }

    fun stringToHex(inputString: String): String {
        val bytes = inputString.toByteArray(Charsets.UTF_8)
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    /**
     * Copy the specified text to the clipboard.
     *
     * @param text The text to be copied.
     */
    private fun copyToClipboard(text: String) {
        // Get the system clipboard service
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        // Create a new clip data with the specified label and text
        val clipData = ClipData.newPlainText("label", text)
        // Set the clip data to the clipboard
        clipboardManager.setPrimaryClip(clipData)
    }

    /**
     * Called when the window has gained or lost focus.
     *
     * @param hasFocus True if the window has gained focus, false otherwise.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        // Check if the device is running Android 10 or later
        val isAndroid10Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        if (!isAndroid10Plus) return
        // Get the system clipboard service
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip
        // Check if the clipboard is not null
        if (clipboard != null) {
            // Check if the clipboard contains any data
            if (clip != null && clip.itemCount > 0) {
                // Get the first item from the clipboard
                val item = clip.getItemAt(0)
                val strPaste = item.text?.toString()

                // Check if the pasted string is not null or empty
                if (!strPaste.isNullOrEmpty()) {
                    // Create an editable text from the pasted string
                    val editableText: Editable =
                        Editable.Factory.getInstance().newEditable(strPaste)

                    // Set the editable text to the text input view
                    binding.txtInput.text = editableText
                }
            }
        }
        super.onWindowFocusChanged(hasFocus)
    }

    private fun setOnClickTextManipulation() {
        // Copy the text from the input textview
        binding.btnInputCopy.setOnClickListener()
        {
            if (binding.txtInput.text.toString().isEmpty()) {
                CustomToast.showCustomToast(
                    this,
                    getString(R.string.logErrorInputEmpty),
                    CustomToastType.ERROR
                )
            } else {
                copyToClipboard(binding.txtInput.text.toString())
                CustomToast.showCustomToast(
                    this,
                    getString(R.string.logInfoCopySuccess),
                    CustomToastType.INFORMATION
                )
            }
        }
        // Paste the text from clipboard to input textview
        binding.btnInputPaste.setOnClickListener() {
            val strPaste = getClipboardText()
            if (strPaste.isNullOrEmpty()) {
                CustomToast.showCustomToast(
                    this,
                    getString(R.string.logErroPaste),
                    CustomToastType.ERROR
                )
            } else {
                val editableText: Editable =
                    Editable.Factory.getInstance().newEditable(strPaste)
                binding.txtInput.text = editableText
            }

        }

        // Copy the text from the result textview
        binding.btnResultCopy.setOnClickListener()
        {
            if (binding.txtResult.text.isEmpty()) {
                CustomToast.showCustomToast(
                    this,
                    getString(R.string.logErrorResultEmpty),
                    CustomToastType.ERROR
                )
            } else {
                copyToClipboard(binding.txtResult.text.toString())
                CustomToast.showCustomToast(
                    this,
                    getString(R.string.logInfoCopySuccess),
                    CustomToastType.INFORMATION
                )
            }
        }

        // Paste the text from clipboard to result textview
        binding.btnResultPaste.setOnClickListener() {
            val strPaste = getClipboardText()
            if (strPaste.isNullOrEmpty()) {
                CustomToast.showCustomToast(
                    this,
                    getString(R.string.logErroPaste),
                    CustomToastType.ERROR
                )
            } else {
                val editableText: Editable =
                    Editable.Factory.getInstance().newEditable(strPaste)
                binding.txtResult.text = editableText
            }

        }
    }

    private fun getClipboardText(): String? {
        // Get the system clipboard service
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip

        // Check if the clipboard contains any data
        if (clip != null && clip.itemCount > 0) {
            // Get the first item from the clipboard
            val item = clip.getItemAt(0)
            // Get the text from the clipboard item
            return item.text?.toString()
        }

        return null
    }

}