import androidx.compose.ui.window.Window
import com.programmersbox.shared.UIShow
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("NameInfo") {
            UIShow()
        }
    }
}