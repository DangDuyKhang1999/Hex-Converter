import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.convertex.R

class CustomToast {
    companion object {
        fun showCustomToast(context: Context, message: String, typeInfo: Boolean) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.toast_custom, null)
            val textView: TextView = view.findViewById(R.id.textView)
            val imageView: ImageView = view.findViewById(R.id.imageView)
            textView.text = message
            if (typeInfo) {
                view.setBackgroundResource(R.drawable.toastbackground)
                imageView.setImageResource(R.drawable.baseline_check_circle_outline_24)
            }
            else
            {
                view.setBackgroundResource(R.drawable.toastbackgrounderror)
                imageView.setImageResource(R.drawable.baseline_error_outline_24)
            }


            val toast = Toast(context)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = view
            toast.show()
        }
    }
}
