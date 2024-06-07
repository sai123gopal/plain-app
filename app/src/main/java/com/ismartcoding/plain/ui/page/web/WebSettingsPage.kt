package com.ismartcoding.plain.ui.page.web

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ismartcoding.lib.channel.receiveEventHandler
import com.ismartcoding.lib.channel.sendEvent
import com.ismartcoding.lib.extensions.isTV
import com.ismartcoding.lib.helpers.CoroutinesHelper.coIO
import com.ismartcoding.lib.helpers.CoroutinesHelper.coMain
import com.ismartcoding.lib.helpers.CoroutinesHelper.withIO
import com.ismartcoding.lib.helpers.NetworkHelper
import com.ismartcoding.plain.BuildConfig
import com.ismartcoding.plain.R
import com.ismartcoding.plain.TempData
import com.ismartcoding.plain.preference.ApiPermissionsPreference
import com.ismartcoding.plain.preference.HttpPortPreference
import com.ismartcoding.plain.preference.HttpsPortPreference
import com.ismartcoding.plain.preference.LocalApiPermissions
import com.ismartcoding.plain.preference.LocalKeepAwake
import com.ismartcoding.plain.preference.LocalWeb
import com.ismartcoding.plain.preference.WebSettingsProvider
import com.ismartcoding.plain.features.IgnoreBatteryOptimizationResultEvent
import com.ismartcoding.plain.features.Permission
import com.ismartcoding.plain.features.PermissionItem
import com.ismartcoding.plain.features.Permissions
import com.ismartcoding.plain.features.PermissionsResultEvent
import com.ismartcoding.plain.features.RequestPermissionsEvent
import com.ismartcoding.plain.features.WindowFocusChangedEvent
import com.ismartcoding.plain.helpers.AppHelper
import com.ismartcoding.plain.packageManager
import com.ismartcoding.plain.powerManager
import com.ismartcoding.plain.services.PNotificationListenerService
import com.ismartcoding.plain.ui.base.ActionButtonMoreWithMenu
import com.ismartcoding.plain.ui.base.PAlert
import com.ismartcoding.plain.ui.base.AlertType
import com.ismartcoding.plain.ui.base.BottomSpace
import com.ismartcoding.plain.ui.base.PMiniOutlineButton
import com.ismartcoding.plain.ui.base.PCard
import com.ismartcoding.plain.ui.base.PClickableText
import com.ismartcoding.plain.ui.base.PDropdownMenuItem
import com.ismartcoding.plain.ui.base.PListItem
import com.ismartcoding.plain.ui.base.PMainSwitch
import com.ismartcoding.plain.ui.base.PScaffold
import com.ismartcoding.plain.ui.base.PSwitch
import com.ismartcoding.plain.ui.base.PTopAppBar
import com.ismartcoding.plain.ui.base.Subtitle
import com.ismartcoding.plain.ui.base.Tips
import com.ismartcoding.plain.ui.base.TopSpace
import com.ismartcoding.plain.ui.base.VerticalSpace
import com.ismartcoding.plain.ui.components.WebAddress
import com.ismartcoding.plain.ui.nav.navigate
import com.ismartcoding.plain.ui.helpers.DialogHelper
import com.ismartcoding.plain.ui.models.MainViewModel
import com.ismartcoding.plain.ui.models.VClickText
import com.ismartcoding.plain.ui.models.WebConsoleViewModel
import com.ismartcoding.plain.ui.nav.RouteName
import com.ismartcoding.plain.ui.theme.PlainTheme
import com.ismartcoding.plain.web.HttpServerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WebSettingsPage(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    viewModel: WebConsoleViewModel = viewModel(),
) {
    WebSettingsProvider {
        val context = LocalContext.current
        val webEnabled = LocalWeb.current
        val keepAwake = LocalKeepAwake.current
        val scope = rememberCoroutineScope()
        val enabledPermissions = LocalApiPermissions.current
        var permissionList by remember { mutableStateOf(Permissions.getWebList(context)) }
        var shouldIgnoreOptimize by remember { mutableStateOf(!powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) }
        var systemAlertWindow by remember { mutableStateOf(Permission.SYSTEM_ALERT_WINDOW.can(context)) }
        var isVPNConnected by remember { mutableStateOf(NetworkHelper.isVPNConnected(context)) }
        val events by remember { mutableStateOf<MutableList<Job>>(arrayListOf()) }

        val learnMore = stringResource(id = R.string.learn_more)
        val fullText = (stringResource(id = R.string.web_console_desc) + " " + learnMore)

        LaunchedEffect(Unit) {
            events.add(
                receiveEventHandler<PermissionsResultEvent> { event ->
                    permissionList = Permissions.getWebList(context)
                    systemAlertWindow = Permission.SYSTEM_ALERT_WINDOW.can(context)
                    if (event.map[Permission.NOTIFICATION_LISTENER.toSysPermission()] == true) {
                        PNotificationListenerService.toggle(context, true)
                    }
                }
            )

            events.add(
                receiveEventHandler<WindowFocusChangedEvent> {
                    shouldIgnoreOptimize = !powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)
                    isVPNConnected = NetworkHelper.isVPNConnected(context)
                }
            )

            events.add(receiveEventHandler<IgnoreBatteryOptimizationResultEvent> {
                coIO {
                    DialogHelper.showLoading()
                    delay(1000) // MIUI 12 test 1 second to get the final correct result.
                    DialogHelper.hideLoading()
                    shouldIgnoreOptimize = !powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)
                }
            })
        }

        fun togglePermission(m: PermissionItem, enable: Boolean) {
            scope.launch {
                withIO { ApiPermissionsPreference.putAsync(context, m.permission, enable) }
                if (enable) {
                    val ps = m.permissions.filter { !it.can(context) }
                    if (ps.isNotEmpty()) {
                        sendEvent(RequestPermissionsEvent(*ps.toTypedArray()))
                    } else {
                        if (m.permission == Permission.NOTIFICATION_LISTENER) {
                            PNotificationListenerService.toggle(context, true)
                        }
                    }
                } else {
                    if (m.permission == Permission.NOTIFICATION_LISTENER) {
                        PNotificationListenerService.toggle(context, false)
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                events.forEach { it.cancel() }
                events.clear()
            }
        }

        PScaffold(topBar = {
            PTopAppBar(navController = navController,
                title = stringResource(id = R.string.web_console),
                actions = {
                    PMiniOutlineButton(
                        text = stringResource(R.string.sessions),
                        onClick = {
                            navController.navigate(RouteName.SESSIONS)
                        },
                    )
                    ActionButtonMoreWithMenu { dismiss ->
                        PDropdownMenuItem(leadingIcon = {
                            Icon(
                                Icons.Outlined.Password,
                                contentDescription = stringResource(id = R.string.security)
                            )
                        }, onClick = {
                            dismiss()
                            navController.navigate(RouteName.WEB_SECURITY)
                        }, text = {
                            Text(text = stringResource(R.string.security))
                        })
                        PDropdownMenuItem(leadingIcon = {
                            Icon(
                                Icons.Outlined.DeveloperMode,
                                contentDescription = stringResource(id = R.string.testing_token)
                            )
                        }, onClick = {
                            dismiss()
                            navController.navigate(RouteName.WEB_DEV)
                        }, text = {
                            Text(text = stringResource(R.string.testing_token))
                        })
                    }
                })
        }, content = {
            LazyColumn {
                item {
                    TopSpace()
                    if (webEnabled) {
                        if (mainViewModel.httpServerError.isNotEmpty()) {
                            PAlert(title = stringResource(id = R.string.error), description = mainViewModel.httpServerError, AlertType.ERROR) {
                                if (HttpServerManager.portsInUse.isNotEmpty()) {
                                    PMiniOutlineButton(
                                        text = stringResource(R.string.change_port),
                                        onClick = {
                                            scope.launch(Dispatchers.IO) {
                                                if (HttpServerManager.portsInUse.contains(TempData.httpPort)) {
                                                    HttpPortPreference.putAsync(context, HttpServerManager.httpPorts.filter { it != TempData.httpPort }.random())
                                                }
                                                if (HttpServerManager.portsInUse.contains(TempData.httpsPort)) {
                                                    HttpsPortPreference.putAsync(context, HttpServerManager.httpsPorts.filter { it != TempData.httpsPort }.random())
                                                }
                                                coMain {
                                                    MaterialAlertDialogBuilder(context)
                                                        .setTitle(R.string.restart_app_title)
                                                        .setMessage(R.string.restart_app_message)
                                                        .setPositiveButton(R.string.relaunch_app) { _, _ ->
                                                            AppHelper.relaunch(context)
                                                        }
                                                        .setCancelable(false)
                                                        .create()
                                                        .show()
                                                }
                                            }
                                        },
                                    )
                                }
                                PMiniOutlineButton(
                                    text = stringResource(R.string.relaunch_app),
                                    modifier = Modifier.padding(start = 16.dp),
                                    onClick = {
                                        AppHelper.relaunch(context)
                                    },
                                )
                            }
                        } else {
                            if (isVPNConnected) {
                                PAlert(title = stringResource(id = R.string.attention), description = stringResource(id = R.string.vpn_web_conflict_warning), AlertType.WARNING)
                            }
                            if (!systemAlertWindow) {
                                PAlert(title = stringResource(id = R.string.attention), description = stringResource(id = R.string.system_alert_window_warning), AlertType.WARNING) {
                                    PMiniOutlineButton(
                                        text = stringResource(R.string.grant_permission),
                                        onClick = {
                                            sendEvent(RequestPermissionsEvent(Permission.SYSTEM_ALERT_WINDOW))
                                        },
                                    )
                                }
                            }
                        }
                    }
                    PClickableText(
                        text = fullText,
                        clickTexts = listOf(
                            VClickText(learnMore) {
                                navController.navigate(RouteName.WEB_LEARN_MORE)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    )
                }
                item {
                    VerticalSpace(dp = 8.dp)
                    PMainSwitch(
                        title = stringResource(id = mainViewModel.httpServerState.getTextId()),
                        activated = webEnabled,
                        enable = !mainViewModel.httpServerState.isProcessing()
                    ) {
                        mainViewModel.enableHttpServer(context, it)
                    }
                }
                if (webEnabled) {
                    item {
                        VerticalSpace(dp = 16.dp)
                        PCard {
                            WebAddress(context)
                            VerticalSpace(dp = 16.dp)
                        }
                    }
                }
                item {
                    VerticalSpace(dp = 16.dp)
                    Subtitle(
                        text = stringResource(R.string.permissions),
                    )
                }
                itemsIndexed(permissionList) { index, m ->
                    val permission = m.permission
                    if (permission == Permission.NONE) {
                        VerticalSpace(dp = 16.dp)
                        PCard {
                            PListItem(
                                modifier = Modifier.clickable {
                                    val intent =
                                        Intent(
                                            if (context.isTV()) Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS else Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        )
                                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                                    intent.data = Uri.fromParts("package", context.packageName, null)
                                    if (intent.resolveActivity(packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        DialogHelper.showMessage(R.string.not_supported_error)
                                    }
                                },
                                icon = m.icon,
                                title = permission.getText(),
                                showMore = true,
                            )
                        }
                    } else {
                        PListItem(
                            modifier = PlainTheme
                                .getCardModifier(index = index, size = permissionList.size - 1)
                                .clickable {
                                    togglePermission(m, !enabledPermissions.contains(permission.name))
                                },
                            icon = m.icon,
                            title = permission.getText(),
                            desc =
                            stringResource(
                                if (m.granted) R.string.system_permission_granted else R.string.system_permission_not_granted,
                            ),
                        ) {
                            PSwitch(activated = enabledPermissions.contains(permission.name)) { enable ->
                                togglePermission(m, enable)
                            }
                        }
                    }
                }
                item {
                    VerticalSpace(dp = 16.dp)
                    Subtitle(
                        text = stringResource(id = R.string.performance),
                    )
                    PCard {
                        PListItem(modifier = Modifier.clickable {
                            viewModel.enableKeepAwake(context, !keepAwake)
                        }, title = stringResource(id = R.string.keep_awake)) {
                            PSwitch(activated = keepAwake) { enable ->
                                viewModel.enableKeepAwake(context, enable)
                            }
                        }
                    }
                    Tips(stringResource(id = R.string.keep_awake_tips))
                    VerticalSpace(dp = 16.dp)
                    PCard {
                        PListItem(
                            modifier = Modifier.clickable {
                                if (shouldIgnoreOptimize) {
                                    viewModel.requestIgnoreBatteryOptimization()
                                } else {
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                                    context.startActivity(intent)
                                }
                            },
                            title = stringResource(id = if (shouldIgnoreOptimize) R.string.disable_battery_optimization else R.string.battery_optimization_disabled),
                            showMore = true
                        )
                    }
                }
                item {
                    BottomSpace()
                }
            }
        })
    }
}
