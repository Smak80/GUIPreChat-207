import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.awaitApplication
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import viewmodels.MainViewModel
import kotlin.reflect.KType

@Composable
@Preview
fun App(
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    MaterialTheme {
        Column (modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                userScrollEnabled = true,
                verticalArrangement = Arrangement.Bottom,
            ){
                items(viewModel.messages){
                    Card(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    ){
                        Text(
                            it,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            InputRow(
                viewModel.inputText,
                modifier = Modifier.fillMaxWidth(),
                { viewModel.inputText = it }
            ){
                viewModel.messages.add(viewModel.inputText)
                viewModel.inputText = ""
            }
        }
    }
}

@Composable
fun InputRow(
    inputText: String,
    modifier: Modifier = Modifier,
    onInputChanged: (String) -> Unit = {},
    onEnter: (String) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = {
                onInputChanged(it)
            },
            modifier = Modifier.weight(1f).onKeyEvent {
                if (it.type == KeyEventType.KeyUp && it.isShiftPressed && it.key == Key.Enter){
                    onEnter(inputText)
                    true
                } else false
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onEnter(inputText)
                    },
                    //shape = RoundedCornerShape(100),
                    modifier = Modifier
                ) {
                    Icon(Icons.Default.Email, null)
                }
            }
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
