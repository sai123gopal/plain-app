package com.ismartcoding.plain.ui.page.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ismartcoding.lib.helpers.CoroutinesHelper.withIO
import com.ismartcoding.plain.R
import com.ismartcoding.plain.enums.DarkTheme
import com.ismartcoding.plain.preference.AmoledDarkThemePreference
import com.ismartcoding.plain.preference.DarkThemePreference
import com.ismartcoding.plain.preference.LocalAmoledDarkTheme
import com.ismartcoding.plain.preference.LocalDarkTheme
import com.ismartcoding.plain.ui.base.BottomSpace
import com.ismartcoding.plain.ui.base.PCard
import com.ismartcoding.plain.ui.base.PListItem
import com.ismartcoding.plain.ui.base.PScaffold
import com.ismartcoding.plain.ui.base.PSwitch
import com.ismartcoding.plain.ui.base.Subtitle
import com.ismartcoding.plain.ui.base.TopSpace
import com.ismartcoding.plain.ui.base.VerticalSpace
import com.ismartcoding.plain.ui.theme.PlainTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkThemePage(navController: NavHostController) {
    val context = LocalContext.current
    val darkTheme = LocalDarkTheme.current
    val amoledDarkTheme = LocalAmoledDarkTheme.current
    val scope = rememberCoroutineScope()

    PScaffold(
        navController,
        topBarTitle = stringResource(R.string.dark_theme),
        content = {
            LazyColumn {
                item {
                    TopSpace()
                }
                item {
                    PCard {
                        DarkTheme.entries.map {
                            PListItem(
                                title = it.getText(context),
                                onClick = {
                                    scope.launch {
                                        withIO {
                                            DarkThemePreference.putAsync(context, it)
                                        }
                                    }
                                },
                            ) {
                                RadioButton(selected = it.value == darkTheme, onClick = {
                                    scope.launch {
                                        withIO {
                                            DarkThemePreference.putAsync(context, it)
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
                item {
                    VerticalSpace(dp = 16.dp)
                    Subtitle(
                        text = stringResource(R.string.other),
                    )
                    PCard {
                        PListItem(
                            title = stringResource(R.string.amoled_dark_theme),
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    AmoledDarkThemePreference.putAsync(context, !amoledDarkTheme)
                                }
                            },
                        ) {
                            PSwitch(activated = amoledDarkTheme) {
                                scope.launch(Dispatchers.IO) {
                                    AmoledDarkThemePreference.putAsync(context, !amoledDarkTheme)
                                }
                            }
                        }
                    }
                    BottomSpace()
                }
            }
        },
    )
}
