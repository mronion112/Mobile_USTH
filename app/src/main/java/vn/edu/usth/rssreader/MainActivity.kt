package vn.edu.usth.rssreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import vn.edu.usth.rssreader.ui.navigation.AppNavHost
import vn.edu.usth.rssreader.ui.theme.MobileUSTHTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileUSTHTheme {
                AppNavHost()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppShellPreview() {
    MobileUSTHTheme {
        AppNavHost()
    }
}
