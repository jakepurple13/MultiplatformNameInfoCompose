import androidx.compose.ui.window.Window
import com.programmersbox.shared.UIShow
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    /*renderComposable(rootElementId = "root") {
        UIShow()
    }*/
    onWasmReady {
        Window("NameInfo") {
            UIShow()
        }
    }
}