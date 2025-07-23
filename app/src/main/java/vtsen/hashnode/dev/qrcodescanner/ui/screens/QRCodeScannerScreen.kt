package vtsen.hashnode.dev.qrcodescanner.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Entity
import vtsen.hashnode.dev.qrcodescanner.URIList
import vtsen.hashnode.dev.qrcodescanner.ui.Screen
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import vtsen.hashnode.dev.qrcodescanner.AppDatabase
import vtsen.hashnode.dev.qrcodescanner.ScannedItem
import androidx.core.net.toUri


@Composable
fun QRCodeScannerScreen(urlText: String, onUrlTextUpdate: (String) -> Unit,navController: NavController) {

    var statusText by remember { mutableStateOf("") }
    val context = LocalContext.current

    PermissionRequestDialog(
        permission = Manifest.permission.CAMERA,
        onResult = { isGranted ->
            statusText = if (isGranted) {
                "Scan QR code now!"
            } else {
                "No camera permission!"
            }
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text=statusText, fontWeight = FontWeight.SemiBold, fontSize = 30.sp)

        Spacer(modifier = Modifier.height(5.dp))
        CameraPreview { url ->
            onUrlTextUpdate(url)
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = urlText,
            onValueChange = {},
            label = {Text("Detected URL")},
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(5.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                launchUrl(context, urlText)
            }
        ) {
            Text(text="Launch", fontWeight = FontWeight.SemiBold, fontSize = 30.sp)
        }
    }
    Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        BottomBar(navController)
    }
}

@Composable
fun BottomBar(navController: NavController){
   // Box(modifier=Modifier.fillMaxSize()){
        Row {
            Box(modifier = Modifier.weight(1f).height(50.dp).align(Alignment.Bottom)
                .clickable{
                    navController.navigate(Screen.home.route) {
                        popUpTo(0) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }, contentAlignment = Alignment.Center){
                Text("Home")
            }
            Box(modifier = Modifier.weight(1f).height(50.dp).align(Alignment.Bottom)
                .clickable{
                    navController.navigate(Screen.list.route) {
                        popUpTo(Screen.home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }, contentAlignment = Alignment.Center){
                Text("List")
            }
        }
  //  }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListScreen(navController: NavController){

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.scanDao()

    val scannedItemsFlow = remember { dao.allTaskItems() }
    val scannedItems by scannedItemsFlow.collectAsState(initial = emptyList())

    val formatter = remember {
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
            .withZone(ZoneId.systemDefault())
    }
  //  URIList = scannedItems
    LazyColumn {
        items(scannedItems){
            Column {
              //  Box(modifier=Modifier.fillMaxWidth().padding(5.dp), contentAlignment = Alignment.CenterStart) {
                    Text(it.uriString.toString())

                   // Text(it.scannedAt.toString())
               // }
                Spacer(modifier=Modifier.height(30.dp))
              //  Box(modifier=Modifier.fillMaxWidth().padding(5.dp), contentAlignment = Alignment.CenterEnd) {

                    Text("time:" + formatter.format(Instant.ofEpochMilli(it.scannedAt)))
               // }
            }
            Spacer(modifier=Modifier.height(30.dp))
            Box(modifier= Modifier.fillMaxWidth().height(1.dp).background(Color.Black)){}
            Spacer(modifier=Modifier.height(30.dp))
        }
    }
    Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        BottomBar(navController)
    }
}

private fun launchUrl(context: Context, urlText: String) {
    val uri: Uri = urlText.toUri()

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.android.chrome")
    }

    try {
        context.startActivity(intent)

    } catch (e: ActivityNotFoundException) {
        intent.setPackage(null)

        try {
            context.startActivity(intent)

        } catch (e: ActivityNotFoundException) {

        }
    }
}