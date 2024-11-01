@file:Suppress("unused", "CONTEXT_RECEIVERS_DEPRECATED")

package com.ome.app.utils

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.Context.VIBRATOR_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ome.app.BuildConfig
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.abs
import kotlin.math.roundToInt

/**Activity Extension*/
fun Activity.requireActivity() = this

//Toasty->custom toast library
fun Context.toastySuccess(text: String?) {
    text?.let {
        Toasty.success(this, text, Toast.LENGTH_SHORT, true)
            .show()
    }
}

fun Fragment.toastySuccess(text: String?) {
    text?.let {
        Toasty.success(requireContext(), text, Toast.LENGTH_SHORT, true)
            .show()
    }
}

fun Context.toastyError(text: String?) {
    text?.let {
        Toasty.error(this, text, Toast.LENGTH_SHORT, true)
            .show()
    }
}

fun Fragment.toastyError(text: String?) {
    text?.let {
        Toasty.error(requireContext(), text, Toast.LENGTH_SHORT, true)
            .show()
    }
}

fun Context.toastyInfo(text: String?) {
    text?.let {
        Toasty.info(this, text, Toast.LENGTH_SHORT, true)
            .show()
    }
}

fun Fragment.toastyInfo(text: String?, length: Int = Toasty.LENGTH_SHORT) {
    text?.let {
        Toasty.info(requireContext(), text, length , true)
            .show()
    }
}

fun Context.toastyWarning(text: String?) {
    text?.let {
        Toasty.warning(this, text, Toast.LENGTH_SHORT, true)
            .show()
    }
}

fun Fragment.toastyWarning(text: String?) {
    text?.let {
        Toasty.warning(requireContext(), text, Toast.LENGTH_SHORT, true)
            .show()
    }
}

/**AppCompatActivity, FragmentActivity Extension*/
//default toast
fun AppCompatActivity.toast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    text?.let { Toast.makeText(this, text, duration).show() }
}


fun AppCompatActivity.setActionBarTitle(title: String) {
    this.supportActionBar?.title = title
}

fun FragmentActivity.closeKeyboard() {
    val windowToken = currentFocus?.windowToken ?: window.decorView.rootView.windowToken
    val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(windowToken, 0)
}

fun FragmentActivity.showKeyboard() {
    val view = this.currentFocus
    if (view is EditText) {
        val manager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Fragment.showKeyboard() = activity?.showKeyboard()
fun Fragment.closeKeyboard() = activity?.closeKeyboard()

fun View.closeKeyboard() {
    context?.apply {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}


/**Intent Extension Function*/

inline fun <reified T> Context.navigate(bundle: Bundle? = null) {
    startActivity(Intent(this, T::class.java).apply { bundle?.let { putExtras(it) } })
}

inline fun <reified T> FragmentActivity.navigate(
    bundle: Bundle? = null,
    withFinish: Boolean = true,
    withClear: Boolean = false,
    intent: Intent.() -> Unit = {},
) {
    startActivity(Intent(this, T::class.java).apply {
        bundle?.let { putExtras(it) }
        if (withClear) {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
        }
        intent()
    })
    if (withFinish) finish()
}

inline fun <reified T> Fragment.navigate(
    bundle: Bundle? = null,
    withFinish: Boolean = true,
    withClear: Boolean = false,
    intent: Intent.() -> Unit = {},
) {
    context?.apply {
        startActivity(Intent(this, T::class.java).apply {
            bundle?.let { putExtras(it) }
            if (withClear) {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                )
            }
            intent()
        })
    } ?: toastyWarning("Unable to navigate")
    if (withFinish)
        activity?.finish()
}

inline fun <reified T> Context.clearStackAndStartActivity() {
    startActivity(Intent(this, T::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })
}

fun Context.sendEmail(emails: Array<String>, subject: String?, body: String?) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, emails)
        subject?.let { putExtra(Intent.EXTRA_SUBJECT, subject) }
        body?.let { putExtra(Intent.EXTRA_TEXT, body) }
    }
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
    }
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

fun Context.parseResColor(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}

/** EditText Extension Function*/
val EditText.string: String
    get() = this.text.toString()

val EditText.trimmedString: String
    get() = this.text.toString().trim()


/**Exception Extension Function*/
val Exception.errorMessage: String
    get() {
        return when (this) {
            is ConnectException -> "Unable to connect to the server. Please check your connection"
            is SocketTimeoutException -> "Connection timeout please try again"
            is IOException -> this.message ?: "Server error please try again"
            else -> "Error occurred please try again"
        }
    }

/**Coroutines Extension Function*/
@Suppress("FunctionName")
suspend fun <T, R> T.IO(block: suspend T.() -> R) = withContext(Dispatchers.IO) {
    block()
}

@Suppress("FunctionName")
suspend fun <T, R> T.MAIN(block: suspend T.() -> R) = withContext(Dispatchers.Main) {
    block()
}

@Suppress("FunctionName")
suspend fun <T, R> T.DEFAULT(block: suspend T.() -> R) = withContext(Dispatchers.Default) {
    block()
}

val Fragment.viewLifecycleScope
    get() = viewLifecycleOwner.lifecycleScope

val Fragment.viewLifecycle
    get() = viewLifecycleOwner.lifecycle

/**`repeatOnLifecycle` on` lifecycleScope` when `RESUMED` */
fun Fragment.repeatOnLifecycle(
    context: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch(context) {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (isAdded && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                block()
        }
    }
}

/**Flow collect from Fragment with `repeatOnLifecycle` on` lifecycleScope` till `RESUMED` */
context(Fragment)
fun <T> Flow<T?>.collectWithLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: suspend CoroutineScope.(T) -> Unit,
) {
    lifecycleScope.launch(context) {
        repeatOnLifecycle(minActiveState) {
            filterNotNull().collect { value ->
                if (isAdded && lifecycle.currentState.isAtLeast(minActiveState))
                    block(value)
            }
        }
    }
}

/**Flow collect from Activity with `repeatOnLifecycle` on` lifecycleScope` till `RESUMED` */
context(LifecycleOwner)
fun <T> Flow<T?>.collectWithLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: suspend CoroutineScope.(T) -> Unit,
) {
    lifecycleScope.launch(context) {
        repeatOnLifecycle(minActiveState) {
            filterNotNull().collect { value ->
                if (lifecycle.currentState.isAtLeast(minActiveState))
                    block(value)
            }
        }
    }
}

/**Flow collect from Fragment on` lifecycleScope` if `isAdded` and `RESUMED` */
context(Fragment)
fun <T> Flow<T?>.collectWithLifecycleNoRepeat(
    context: CoroutineContext = EmptyCoroutineContext,
    minActiveState: Lifecycle.State = Lifecycle.State.CREATED,
    block: suspend CoroutineScope.(T) -> Unit,
) {
    lifecycleScope.launch(context) {
        filterNotNull().collect { value ->
            if (isAdded && lifecycle.currentState.isAtLeast(minActiveState))
                block(value)
        }
    }
}

inline fun <T, R> Flow<List<T>>.mapList(crossinline transform: suspend T.() -> R): Flow<List<R>> =
    this.map {
        it.map { value ->
            transform(value)
        }
    }

//fun Fragment.navigateUp() {
//    findNavController().navigateUp()
//}
//
//fun Fragment.navigate(@IdRes id: Int, extras: Bundle? = null) {
//    findNavController().navigate(id, extras)
//}

/**
 * Navigate to a destination safely, handling cases where the destination cannot be found.
 *
 * @param resId The resource ID of the destination to navigate to.
 * @param args Arguments to pass to the destination.
 */
fun Fragment.navigateSafe(@IdRes resId: Int, args: Bundle? = null, navOption: NavOptions? = null): Unit? {
    return try {
        if (lifecycle.currentState == Lifecycle.State.RESUMED)
            findNavController().navigate(resId, args, navOption)
        else null
    } catch (e: IllegalArgumentException) {
        toast(e.message)
        e.printStackTrace()
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Activity?.changeStatusBarColor(@ColorRes colorId: Int, isLight: Boolean = true) {
    this?.window?.apply {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        statusBarColor = ContextCompat.getColor(this@changeStatusBarColor, colorId)
        WindowInsetsControllerCompat(this, decorView).isAppearanceLightStatusBars = isLight
        WindowInsetsControllerCompat(this, decorView).isAppearanceLightNavigationBars = true
    }
}

fun Fragment.changeStatusBarColor(@ColorRes colorId: Int, isLight: Boolean = true) {
    activity?.changeStatusBarColor(colorId, isLight)
}

fun Fragment.getColor(@ColorRes color: Int) = ContextCompat.getColor(requireContext(), color)

fun Fragment.requireApplication() = requireContext().applicationContext!!


//fun Any?.logJson(tag: String = "TAG") {
//    if (BuildConfig.DEBUG) {
//        Log.i("log> '$tag'", "${this?.javaClass?.name}")
//        if (this is String)
//            com.orhanobut.logger.Logger.json(this)
//        else
//            com.orhanobut.logger.Logger.json(Gson().toJson(this))
//    }
//}

fun Any?.log(tag: String = "TAG", hints: String = "") {
    if (BuildConfig.DEBUG)
        Log.i("log> '$tag'", "$hints: ${this?.javaClass?.name}: $this")
}

/**View Extension Function*/
fun View.changeVisibility(isVisible: Boolean, useGone: Boolean = false) {
    visibility = if (isVisible) View.VISIBLE else if (useGone) View.GONE else View.INVISIBLE
}

fun View.visible() {
    if(this.visibility != View.VISIBLE)
        this.visibility = View.VISIBLE
}

fun visible(vararg views: View) {
    views.forEach {
        if(it.visibility != View.VISIBLE)
            it.visibility = View.VISIBLE
    }
}

fun changeFlexBasisPercent(percent: Float, vararg views: View) {
    views.forEach {
        val lp= it.layoutParams as FlexboxLayout.LayoutParams
        lp.flexBasisPercent = percent
        it.layoutParams = lp
    }
}

fun gone(vararg views: View) {
    views.forEach {
        if(it.visibility != View.GONE)
            it.visibility = View.GONE
    }
}

fun View.show() {
    if(this.visibility != View.VISIBLE)
        this.visibility = View.VISIBLE
}


fun View.delayVisible(delay: Long = 200) {
    MainScope().launch {
        delay(delay)
        visibility = View.VISIBLE
    }
}

fun View.shortVisible(delay: Long = 250) {
    MainScope().launch {
        visibility = View.VISIBLE
        delay(delay)
        visibility = View.GONE
    }
}

fun View.shortInvisible(delay: Long = 250) {
    if (isVisible) {
        MainScope().launch {
            visibility = View.GONE
            delay(delay)
            visibility = View.VISIBLE
        }
    }
}

fun View.delayInvisible(delay: Long = 200) {
    MainScope().launch {
        delay(delay)
        this@delayInvisible.visibility = View.INVISIBLE
    }
}

fun View.invisible() {
    if(this.visibility != View.INVISIBLE)
        this.visibility = View.INVISIBLE
}

fun View.gone() {
    if(this.visibility != View.GONE)
        this.visibility = View.GONE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

fun View.disable(lifecycleScope: LifecycleCoroutineScope, timeInMillis: Long) {
    lifecycleScope.launch {
        runWithoutException {
            this@disable.isEnabled = false
            delay(timeInMillis)
            this@disable.isEnabled = true
        }
    }
}

/**Any*/
inline fun runWithoutException(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Log.e("a", "Inside runWithoutException")
        Log.e("a", e.errorMessage)
    }
}

///**Converts DP into pixel */
//val Int.dp: Int
//    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/** Converts pixel into dp */
//val Int.px: Int
//    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

///**Converts DP into pixel */
//val Float.dp: Float
//    get() = (this * Resources.getSystem().displayMetrics.density)

/** Converts pixel into dp */
//val Float.px: Float
//    get() = (this / Resources.getSystem().displayMetrics.density)


infix fun ViewGroup.inflate(@LayoutRes view: Int): View {
    return LayoutInflater.from(context).inflate(view, this, false)
}

fun Int.inflate(viewGroup: ViewGroup): View {
    return LayoutInflater.from(viewGroup.context).inflate(this, viewGroup, false)
}

fun EditText.doAfterTextChangedFlow() = callbackFlow {
    doAfterTextChanged {
        it?.let { trySend(it.toString()) }
    }
    awaitClose()
}

fun Long.toDateTime(): String {
    var date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(this)
    val day = SimpleDateFormat("dd", Locale.getDefault()).format(System.currentTimeMillis())
    if (day.toInt() == date.split(" ")[0].toInt())
        date = date.split(", ")[1]
    return date.toString()
}

fun Long.toDayPassed(): String {
    val today = System.currentTimeMillis()
    val msDiff = today - this
    val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)
    return "$daysDiff Days Ago"
}

typealias DateRange = String
typealias MonthYearName = String

fun Pair<Long, Long>.toDateRange(): DateRange {
    val (startDate, endDate) = this
    return "${startDate.toDateModern()} - ${endDate.toDateModern()}"
}

fun Pair<Long, Long>.toDayPassed(): Int {
    val (startDate, endDate) = this
    val msDiff = endDate - startDate
    return TimeUnit.MILLISECONDS.toDays(msDiff).toInt().plus(1)
}

fun Long.toDuration(): Pair<String, Long> {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutesRaw = TimeUnit.MILLISECONDS.toMinutes(this)
    val minutes = minutesRaw % 60 // Get the remaining minutes after subtracting hours
    return Pair(if (hours > 0) "${hours}h, ${minutes}m" else "${minutes}m", minutesRaw)
}

fun String.toMonthYearName(): MonthYearName {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = formatter.parse(this)
    return SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(date?.time)
}

fun Long.toTimeDate(): String {
    return SimpleDateFormat("hh-mma_dd-MM-yyyy", Locale.getDefault()).format(this)
}

fun Long.toDate(): String = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(this)
fun Long.toDateModern(): String = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(this)
fun Long.toDateServer(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)
fun Long.toDateTimeServer(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)

fun Long.toTime(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this)
fun Long.toTimeServer(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)
fun Long.toTimeHyphen(): String = SimpleDateFormat("hh-mma", Locale.getDefault()).format(this)

fun Long.toFormattedDDMMYYEEDate(): String {
    // Define the date format: "dd-MM-yyyy EEEE"
    val dateFormat = SimpleDateFormat("dd-MM-yyyy EEEE", Locale.getDefault())

    // Convert milliseconds to a formatted date
    val date = Date(this)
    return dateFormat.format(date)
}

fun Long.toFormattedDDYYMMDDDateTime(): String {
    // Define the date format: "dd-MM-yyyy EEEE"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // Convert milliseconds to a formatted date
    val date = Date(this)
    return dateFormat.format(date)
}


fun Long.toClockTime(): Triple<Int, Int, Int> {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toClockTime
    }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    return Triple(hour, minute, second)
}

fun Long.toFirstMonthDate(): Long {
    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)
    val re = date.replaceRange(8, 10, "01").split(" ").first() + " 00:00:00"
    return re.toTimeStamp()
}

fun Long.toYear(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(this)

fun String.toTimeStamp(): Long {
    //2024-01-31 15:27:01.367
    val formatter = SimpleDateFormat("yyyy-MM-dd" + if (contains(" ")) " HH:mm:ss" else "", Locale.getDefault())
    return formatter.parse(this)?.time ?: 0L
}

fun Long.toMonthDay(): Int {
    return SimpleDateFormat("dd", Locale.getDefault()).format(this).toInt()
}

fun Long.toMonthCount(): Int {
    return SimpleDateFormat("MM", Locale.getDefault()).format(this).toInt()
}

fun String.to12HourFormat(): String {
    val parts = this.split(":").map { it.toInt() }
    val hours = parts[0]
    val minutes = parts[1]

    return when {
        hours == 0 -> "12:${minutes.toString().padStart(2, '0')}AM"
        hours < 12 -> "${hours}:${minutes.toString().padStart(2, '0')}AM"
        hours == 12 -> "${hours}:${minutes.toString().padStart(2, '0')}PM"
        else -> "${hours - 12}:${minutes.toString().padStart(2, '0')}PM"
    }
}


var time = 120
val sec get() = time % 60
val min get() = time / 60
fun Int.toTimer(): String {
    time = this
    return "$min:${if (sec < 10) "0" else ""}$sec"
}

fun Context.getClipBoardData(): String {
    val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var data = ""
    if (clipBoardManager.primaryClip?.description?.hasMimeType("text/*") == true) {
        clipBoardManager.primaryClip?.itemCount?.let {
            for (i in 0 until it) {
                data += clipBoardManager.primaryClip?.getItemAt(i)?.text ?: ""
            }
        }
    }
    data.log("getClipBoardData")
    return data
}

fun Context.setClipBoardData(data: String?) {
    data?.let {
        val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(data, data)
        clipBoardManager.setPrimaryClip(clip)
        toastySuccess("$data Copied!")
    }
}

fun String.formatDate(): String {
//    val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
//    val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    return this.toTimeStamp().toDate()
}

fun String.toUppercaseType(): String {
    return replaceFirstChar(Char::uppercaseChar)
        .replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
        .split(Regex("[-_ ]"))
        .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
}

fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit,
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}

fun <T> throttleFirst(
    skipMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit,
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skipMs)
            }
        }
    }
}

fun <T> debounce(
    waitMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit,
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

fun debounce(
    coroutineScope: CoroutineScope,
    waitMs: Long = 300L,
    desFun: () -> Unit,
): () -> Unit {
    var debounceJob: Job? = null
    return {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch(Dispatchers.IO) {
            delay(waitMs)
            desFun.invoke()
        }
    }
}

/**
 * Returns 'true' if this string only has numbers else 'false'
 */
fun String.hasNumberOnly() = this.matches("-?\\d+(\\.\\d+)?".toRegex())


fun Context.dpToPx(dp: Float): Float {
    return (dp * resources.displayMetrics.density)
}

fun Context.pxToDp(px: Float): Float {
    return (px / resources.displayMetrics.density)
}

@SuppressLint("MissingPermission")
fun Context.shakeItBaby() {
    val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vib.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vib.vibrate(150)
    }
}

fun String.findParameterValue(parameterName: String): String? {
    val uri = Uri.parse("http://x.com/?$this")
    return uri.query?.split('&')?.map {
        val parts = it.split('=')
        val name = parts.firstOrNull() ?: ""
        val value = parts.drop(1).firstOrNull() ?: ""
        Pair(name, value)
    }?.firstOrNull { it.first == parameterName }?.second
}

fun Map<String, Any?>.toBundle(): Bundle {
    val lst = this.toList().toTypedArray()
//    lst.logJson()
    return bundleOf(*lst)
}

fun Boolean.toYesNo() = if (this) "YES" else "NO"
fun Boolean?.isTrue() = this != null && this
fun Boolean?.isFalse() = this == null || !this

inline fun <R> Boolean?.isTrue(next: () -> R): R? {
    return if (isTrue()) next() else null
}

inline fun <R> Boolean?.isNotTrue(next: () -> R): R? {
    return if (!isTrue()) next() else null
}

inline fun String?.isNotEmpty(next: (String) -> Unit): String? {
    if (!isNullOrEmpty()) next(this)
    return this
}

inline fun <T> Collection<T>?.isEmpty(next: () -> Unit): Collection<T>? {
    if (isNullOrEmpty()) next()
    return this
}

inline fun <T> Collection<T>?.isNotEmpty(next: (List<T>) -> Unit): Collection<T>? {
    if (!isNullOrEmpty()) next(toList())
    return this
}

fun String.isValidPhoneBD(): Boolean {
    val phoneNumberRegexBd = "^(?:\\+?88)?01[13-9]\\d{8}\$".toRegex()
    return this.contains(phoneNumberRegexBd)
}

fun String?.isBanglaText() = this == null || matches("[\\u0980-\\u09FF\\s,।_/-]+".toRegex())

fun String?.isValidEmail() =
    this == null || isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean {
    val hasUpper = this.any { it.isUpperCase() }
    val hasLower = this.any { it.isLowerCase() }
    val hasNumber = this.any { it.isDigit() }
    val isGraterThan8 = this.length >= 8
//    val hasSpecial = this.any { "!@#\$%^&*()-_=+[]{};:'\",.<>?/\\|`~".contains(it) }
    return hasUpper && hasLower && hasNumber && isGraterThan8
}

fun String?.isJson(): Boolean {
    if (this == null) return false
    // A regex pattern for valid JSON
    val jsonPattern = """^\s*(\{.*\}|\[.*])\s*$""".toRegex()
    // Check if the string matches the JSON pattern
    return jsonPattern.matches(this)
}

fun <T> T.isNull() = this == null
fun <T> T.isNotNull() = this != null

inline fun <T> T?.isNull(next: () -> Unit): T? {
    if (this == null) next()
    return this
}

inline fun <T> T?.isNotNull(next: (T) -> Unit): T? {
    if (this != null) next(this)
    return this
}

fun Int?.isNullOrZero() = this == null || this == 0
fun Double?.toOneIfZero() = if (this == 0.0 || this == null) 1.0 else this
fun String?.toNAifEmpty() =
    if (isNullOrEmpty() || isBlank() || (this == "0" || this == "0.0")) "N/A" else this

fun String?.toNullifEmpty() =
    if (isNullOrEmpty() || isBlank() || (this == "0" || this == "0.0")) null else this

fun String?.toDoubleOrZero() =
    this?.replace("[^\\d.]".toRegex(), "")?.toDoubleOrNull().orZero().roundTo(2)

fun Int?.orMinusOne() = this ?: -1
fun Int?.orZero() = this ?: 0
fun Long?.orZero() = this ?: 0L
fun Double?.orZero() = this ?: 0.0
fun Float?.orZero() = this ?: 0.0F
fun String?.orZero() = this?.toIntOrNull() ?: 0
fun String?.orZeroD() = this?.toDoubleOrNull() ?: 0.0
fun Boolean?.orFalse() = this ?: false

fun Int?.isZero() = this == null || this == 0
fun Long?.isZero() = this == null || this == 0L
fun Double?.isZero() = this == null || this == 0.0
fun Float?.isZero() = this == null || this == 0.0F
fun Float?.isMinusOne() = this == null || this == -1.0F
fun Float?.isZeroOrMinusOne() = this == null || this == 0.0F || this == -1.0F
fun String?.isZero() = this == null || this.toIntOrNull() == 0
fun String?.isZeroD() = this == null || this.toDoubleOrNull() == 0.0


inline fun <T> tryGet(data: () -> T): T? =
    try {
        data()
    } catch (e: Exception) {
        null
    }

suspend inline fun <T> tryInMain(crossinline data: () -> T): T? =
    withContext(Dispatchers.Main) {
        try {
            data()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

inline fun <T> T.applyIf(ifTrue: Boolean, block: T.() -> Unit): T {
    if (ifTrue) block.invoke(this)
    return this
}

/**
 * Formats an integer as a string according to the specified locale.
 *
 * @param locale The locale to use for formatting. Defaults to the default locale.
 * @return The formatted string representation of the integer.
 */
fun Int?.toStringLocale(locale: Locale = Locale.getDefault()): String {
    if (this == null) return "0"
    return NumberFormat.getInstance(locale).format(this)
}

fun Int?.addBracket(locale: Locale = Locale.getDefault()): String {
    if (this == null) return ""
    return "(" + NumberFormat.getInstance(locale).format(this) + ")"
}

fun String?.getPercentage(total: Int): Int {
    if (total == 0 || isNullOrEmpty() || toDoubleOrNull() == null) return 100
    return toDoubleOrZero().getPercentage(total)
}

fun Number.getPercentageFloat(total: Number): Double {
    if (total == 0) return 100.0
    return (toDouble().times(100.0).div(total.toDouble())).roundTo(2)
}

fun Number.getPercentage(total: Number): Int {
    if (total == 0) return 100
    return (toDouble().times(100.0).div(total.toDouble())).toInt()
}

//fun Int.getPercentageTxt(total: Int): String {
//    if (total == 0) return "100%"
//    return "${times(100).div(total)}%"
//}
fun Number.getPercentageTxt(total: Number, asInteger: Boolean = false): String {
    if (total == 0.0) return "100%"
    return toDouble().times(100).div(total.toDouble()).run {
        if (asInteger) roundToInt() else roundTo(2)
    }.toString() + "%"

}

fun Number.getGrowthTxt(lastValue: Number, decimalPoint: Int = 2): String {
    if (lastValue == 0.0) return "100%"
    return "${
        this
            .toDouble()
            .minus(lastValue.toDouble())
            .times(100.0)
            .div(lastValue.toDouble())
            .roundTo(decimalPoint)
    }%"
}

fun Number.roundTo(numFractionDigits: Int = 2): Double =
    "%.${numFractionDigits}f".format(toDouble(), Locale.ENGLISH).toDouble()

//fun Double?.toTK(): String {
//    if (this == null) return "৳0"
//    return (userNullable?.currencySymbol ?: "৳") + when {
//        (this / 100000).toInt() != 0 -> "%,.1f".format(this / 100000) + "Lac"
//        (this / 1000).toInt() != 0 -> "%,.1f".format(this / 1000) + "K"
//        else -> "%,.1f".format(this)
//    }
//}
//
//fun Int.toTK(): String {
//    return (userNullable?.currencySymbol ?: "৳") + when {
//        this / 100000 != 0 -> "%,d".format(this / 100000) + "Lac"
//        this / 1000 != 0 -> "%,d".format(this / 1000) + "K"
//        else -> "%,d".format(this)
//    }
//}

fun Double.addPercentage(decimalPoint: Int = 2): String {
    return "%.${decimalPoint}f".format(this) + "%"
}

//fun Double?.addTK(decimalPoint: Int = 2): String {
//    if (this == null) return "৳0"
//    return (userNullable?.currencySymbol ?: "৳") + "%.${decimalPoint}f".format(this)
//}

fun Int.addPercentage(): String {
    return "$this%"
}

/** Create new instance of a Fragment with `type` as an argument*/
inline fun <reified T> Fragment.newInstance(type: String): T {
    arguments = bundleOf("type" to type)
    return this as T
}

fun enableTooltip(vararg view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        view.forEach {
            it.tooltipText = it.contentDescription
        }
    }
}


fun String.getOptimizedPrintText(totalLength: Int): String {
    val newText: String = if (this.length > totalLength) {
        this.substring(0, totalLength)
    } else {
        this
    }
    return newText
}


fun Fragment.toast(msg: String?) {
    if (msg.isNullOrEmpty()) return
    context?.let {
        Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Context?.toast(msg: String?) {
    if (msg.isNullOrEmpty()) return
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

@Suppress("SpellCheckingInspection")
//fun createObjectMapper(): ObjectMapper {
//    val kotlinModule = KotlinModule.Builder()
//        .withReflectionCacheSize(512)
//        .configure(KotlinFeature.NullToEmptyCollection, false)
//        .configure(KotlinFeature.NullToEmptyMap, false)
//        .configure(KotlinFeature.NullIsSameAsDefault, false)
//        .configure(KotlinFeature.SingletonSupport, true)
//        .configure(KotlinFeature.StrictNullChecks, false)
//        .build()
//
//    val objectMapperBuilder: MapperBuilder<*, *> = JsonMapper.builder()
//        .addModule(kotlinModule)
//        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0 (API level 26) and above
//        objectMapperBuilder
//            .defaultPrettyPrinter(
//                DefaultPrettyPrinter().apply {
//                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
//                    indentObjectsWith(DefaultIndenter("  ", "\n"))
//                }
//            )
//    } else { // Android versions below 8.0
//        objectMapperBuilder
//            .defaultPrettyPrinter(
//                DefaultPrettyPrinter().apply {
//                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
//                    indentObjectsWith(DefaultPrettyPrinter.NopIndenter.instance)
//                }
//            )
//    }
//
//    return objectMapperBuilder
//        .build()
//        .setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))
//}
//
//// Usage
//val objectMapper: ObjectMapper = createObjectMapper()
//

/*// Create a singleton ObjectMapper instance
val objectMapper: ObjectMapper = JsonMapper.builder()
    .addModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, true)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .build()
    .setDefaultPrettyPrinter(
        DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
            indentObjectsWith(DefaultIndenter("  ", "\n"))
        }
    )
    .setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))*/

//inline fun <reified T> String?.fromJson(): T? {
//    return try {
//        objectMapper.readValue<T>(this, objectMapper.constructType(T::class.java))
//    } catch (e: Exception) {
//        Log.e("userData1133", "JsonParseError: Error parsing JSON: ${e.errorMessage}")
//        e.printStackTrace(); null
//    }
//}
//
//fun Any?.toJson(): String {
//    return try {
//        objectMapper.writeValueAsString(this)
//    } catch (e: Exception) {
//        e.printStackTrace(); "{}"
//    }
//}
//fun merge(mainNode: JsonNode, updateNode: JsonNode): JsonNode {
//    val fieldNames = updateNode.fieldNames()
//    while (fieldNames.hasNext()) {
//        val fieldName = fieldNames.next()
//        val jsonNode = mainNode[fieldName]
//        // if field exists and is an embedded object
//        if (jsonNode != null && jsonNode.isObject) {
//            merge(jsonNode, updateNode[fieldName])
//        } else {
//            if (mainNode is ObjectNode) {
//                // Overwrite field
//                val value = updateNode[fieldName]
//                if(!value.asText().isNullOrEmpty())
//                    mainNode.replace(fieldName, value)
//            }
//        }
//    }
//    return mainNode
//}

inline fun <reified T> String?.fromJson(): T {
//    val jsonObject = Gson().toJsonTree(this)
    return Gson().fromJson(this, T::class.java)
}

inline fun <reified T> Any.toObject(): T {
    val jsonObject = Gson().toJsonTree(this)
    val type = object : TypeToken<T>() {}.type
    return Gson().fromJson(jsonObject, type)
}

//inline fun <reified T : Any> T.asMap(): Map<String, Any> {
//    return T::class
//        .memberProperties
//        .associateByTo(mutableMapOf(), keySelector = { it.name }, valueTransform = { it.get(this) })
//        .filterValues { it != null }
//        .mapValues { it.value as Any }
//}

fun Any?.toJson(): String {
    return Gson().toJson(this)
}

fun String?.toUppercaseAllWordRegex(): String? {
    return this?.replace(Regex("\\b\\w")) { it.value.uppercase() }
}


fun main() {
    data class Item(val id: Int = 1)
    data class YK(
        val name: String = "Ma:hd:i",
        val age: Int = 24,
        val list: List<Item> = listOf(Item(1), Item(2), Item(3), Item(4)),
        val list2: List<Item> = listOf(),
    )
}

fun View.animateHeight(targetHeight: Int, duration: Long) {
    val initialHeight = height
    val anim = ValueAnimator.ofInt(initialHeight, targetHeight)
    anim.addUpdateListener { valueAnimator ->
        val value = valueAnimator.animatedValue as Int
        layoutParams.height = value
        requestLayout()
    }
    anim.duration = duration
    anim.interpolator = DecelerateInterpolator() // Add an interpolator for smoother animation
    anim.start()
}


fun Pair<Double, Double>.toLocation(): Location {
    return Location("").apply {
        latitude = first
        longitude = second
    }
}

/*fun Context.getGeoAddress(lat: Double, lon: Double): String? {
    return try {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val address: Address = addresses[0]
            val addressLines = (0..address.maxAddressLineIndex).map { index ->
                address.getAddressLine(index)
            }
            addressLines.joinToString("\n")
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("Geocoder Error", "Error fetching address", e)
        null
    }
}*/

fun Context?.getGeoAddress(location: Location, onSuccess: (String) -> Unit){
    this ?: return
    MainScope().launch {
        getGeoAddress(location.latitude, location.longitude)?.let {
            onSuccess(it)
        }
    }
}

@Suppress("DEPRECATION")
suspend fun Context?.getGeoAddress(lat: Double, lon: Double): String? {
    return withContext(Dispatchers.IO) {
        this@getGeoAddress ?: return@withContext null
        try {
            val geocoder = Geocoder(this@getGeoAddress, Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address: Address = addresses[0]
                val addressLines = (0..address.maxAddressLineIndex).map { index ->
                    address.getAddressLine(index)
                }
                addressLines.joinToString("\n")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Geocoder Error", "Error fetching address", e)
            null
        }
    }
}

fun Spinner.doOnItemSelect(onSelect: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            onSelect(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

fun Context.makeWhatsAppCall(phoneNumber: String) {
    val formattedNumber = phoneNumber.replace("+", "").replace(" ", "")
    val uri = Uri.parse("https://wa.me/$formattedNumber")

    try {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.whatsapp")
        startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
    }
}

fun Context.makePhoneCall(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }

    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request permission if not granted
        if (this is FragmentActivity) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_PHONE_CALL
            )
        }
        return
    }
    startActivity(intent)
}

private const val REQUEST_PHONE_CALL = 1

// To handle the permission result in your activity or fragment
fun handlePermissionResult(
    requestCode: Int,
    grantResults: IntArray,
    onPermissionGranted: () -> Unit
) {
    if (requestCode == REQUEST_PHONE_CALL) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            onPermissionGranted()
        } else {
            // Permission denied, handle the case
        }
    }
}


fun Context.openGoogleMaps(lat: Double, lon: Double) {
    val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(packageManager) != null) {
        startActivity(mapIntent)
    }
}

fun Context?.showCustomTab(url: String?) {
    if (this != null && url != null) {
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(this, Uri.parse(url))
    }
}


fun Double.formatAmount(): String {
    return when {
        this >= 1_000_000 -> "%.1fM".format(this / 1_000_000)
        this >= 1_000 -> "%.1fK".format(this / 1_000)
        else -> this.toString()
    }
}

private fun Context.isWhatsAppInstalled(): Boolean {
    val packageManager = packageManager
    return try {
        packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun EditText.onSearchClick(action: (String) -> Unit) {
    // Ensure IME action is set to Search
    this.imeOptions = EditorInfo.IME_ACTION_SEARCH

    // Set the action listener for the IME Search or Enter key
    this.setOnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
            (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {

            val searchText = v.text.toString()
            action(searchText) // Call the passed action with the search text

            // Hide the keyboard after the action is triggered
            val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)

            true // Indicate that the action has been handled
        } else {
            false
        }
    }
}



fun generateTagId(empId: Int, countryId: Int): String {
    val combined = empId.toString() + System.currentTimeMillis().toString() + countryId.toString()
    val hashCode = combined.hashCode()
    val tagId = abs(hashCode % 10000000000L) // Ensures the tag ID is not more than 10 digits
    return tagId.toString()
}

val FragmentActivity?.keyboardState: StateFlow<Triple<Boolean, Int, Insets>>
    get() {
        val keyboardState = MutableStateFlow(Triple(false, 0, Insets.NONE))
        this ?: return keyboardState
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom - insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            ).bottom
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            keyboardState.value = Triple(imeVisible, if (imeVisible) bottom else 0, systemBars)
            keyboardState.value.log("rememberKeyboardState")
            WindowInsetsCompat.CONSUMED
        }


        return keyboardState
    }

val Any.TAG: String get() = this::class.java.simpleName


//fun isConnectedToInternet(): Boolean {
//    return ConnectivityObserver.isNetworkAvailable
//}

// Extension function to handle long click with delay
@SuppressLint("ClickableViewAccessibility")
fun View.setOnLongClickListenerWithDelay(
    duration: Long = 3000L,
    onLongClick: (View) -> Unit,
    onCancel: (View) -> Unit
) {
    var job: Job? = null
    var isLongClickStarted = false
    var isLongClickDone = false

    setOnLongClickListener { view ->
        isLongClickStarted = true
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(duration)
            if (isActive && isLongClickStarted) {
                isLongClickDone = true
                onLongClick(view)
            }
        }
        true
    }

    setOnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (isLongClickStarted && !isLongClickDone) { // Check if long click was started but not completed
                    isLongClickStarted = false
                    isLongClickDone = false
                    job?.cancel()
                    onCancel(view)
                } else if (isLongClickStarted) {
                    isLongClickStarted = false
                    isLongClickDone = false
                }
            }
        }
        false
    }
}

fun Fragment.dpToPx(dp: Float): Int {
    val density = resources.displayMetrics.density
    return (dp * density).toInt()
}

fun Any.loge(tag: String = "SPRO") {
    if (BuildConfig.DEBUG) {
        val stackTraceElement = Throwable().stackTrace[1]
        val fullClassName = stackTraceElement.className
        val fileName = stackTraceElement.fileName
        val lineNumber = stackTraceElement.lineNumber
        val methodName = stackTraceElement.methodName

        val customTag = "$tag: " + fullClassName.substringAfterLast('.')
        val logMessage = "($fileName:$lineNumber) #$methodName: $this"

        Log.e(customTag, logMessage)
    }
}

fun Long.formatTime(): String {
    return String.format(
        Locale.US, "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )
}

context(Fragment)
fun OnBackPressedCallback.onBackPressedIgnoreCallback() {
    isEnabled = false
    activity?.onBackPressedDispatcher?.onBackPressed()
    isEnabled = true
}

context(FragmentActivity)
fun OnBackPressedCallback.onBackPressedIgnoreCallback() {
    isEnabled = false
    onBackPressedDispatcher.onBackPressed()
    isEnabled = true
}

@Suppress("UNUSED_PARAMETER")
fun Fragment.onBackPressed(view: View? = null) {
    activity?.onBackPressedDispatcher?.onBackPressed()
}

fun convertListToFlow(mutableList: MutableList<String>): Flow<List<String>> {
    return flow {
        emit(mutableList.toList())  // Emit the list as a Flow
    }
}



fun TextView.setHtml(html: String) {
    val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
    this.text = spanned
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun RecyclerView.setFullHeight() {
    val adapter = this.adapter ?: return
    var totalHeight = 0
    for (i in 0 until adapter.itemCount) {
        val viewHolder = adapter.createViewHolder(this, adapter.getItemViewType(i))
        adapter.onBindViewHolder(viewHolder, i)
        viewHolder.itemView.measure(
            View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED
        )
        totalHeight += viewHolder.itemView.measuredHeight
    }
    val layoutParams = this.layoutParams
    layoutParams.height = totalHeight
    this.layoutParams = layoutParams
}
