package vtsen.hashnode.dev.qrcodescanner.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import vtsen.hashnode.dev.qrcodescanner.AppDatabase
import vtsen.hashnode.dev.qrcodescanner.ScannedItem
import vtsen.hashnode.dev.qrcodescanner.URIList
import vtsen.hashnode.dev.qrcodescanner.ui.screens.ListScreen
import vtsen.hashnode.dev.qrcodescanner.ui.screens.QRCodeScannerScreen

const val URL_KEY: String = "UrlKey"

class MainActivity : ComponentActivity() {
    private var urlText by mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val scope= rememberCoroutineScope()

            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val dao = db.scanDao()

            NavHost(navController = navController, startDestination = Screen.home.route) {
                composable(Screen.home.route){
                    MainScreen(
                        urlText = urlText,
                        onUrlTextUpdate = {
                            urlText = it
                            val uri: Uri = Uri.parse(urlText)
                            for(i in URIList) {
                                if (urlText == i.uriString) return@MainScreen
                            }
                            URIList += ScannedItem(uriString = urlText)
                            scope.launch {
                                dao.insertTaskItem(ScannedItem(uriString = urlText))
                            }
                        },
                        navController
                    )
                }
                composable(Screen.list.route) {
                    ListScreen(navController)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(URL_KEY, urlText)
        super.onSaveInstanceState(outState) // need to be called last
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoreUrlText = savedInstanceState.getString(URL_KEY)

        if(restoreUrlText != null) urlText = restoreUrlText
    }
}

sealed class Screen(val route : String){
    object home : Screen("home")
    object list : Screen("list")
}

@Composable
fun MainScreen(
  //  useSystemUIController: Boolean = true,
    urlText:String,
    onUrlTextUpdate: (String) -> Unit,
    navController: NavController) {

   // QRCodeScannerTheme(useSystemUIController = useSystemUIController) {
        QRCodeScannerScreen(urlText, onUrlTextUpdate,navController)
  //  }
}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    MainScreen(useSystemUIController = false, urlText= "", onUrlTextUpdate = {})
//}