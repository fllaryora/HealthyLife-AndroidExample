package com.example.test2.features.dailyactivity.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.ui.viewmodel.ActivityViewModel
import com.example.test2.features.dailyactivity.ui.components.FieldForAddNewActivity
import com.example.test2.features.dailyactivity.ui.components.SingleActivityItem

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    activityTakenAddPressed: (DailyActivityEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    val activities: List<DailyActivityEntity>  by viewModel.activities.collectAsState()
    val loading: Boolean  by viewModel.loading.collectAsState()

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    /**
     *  It can not do
     * Column(verticalScroll)
     *     └── LazyColumn
     *
     * Process: com.example.test2, PID: 32038
     *  java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.
    *       at androidx.compose.foundation.internal.InlineClassHelperKt.throwIllegalStateException(InlineClassHelper.kt:26)
    *       at androidx.compose.foundation.CheckScrollableContainerConstraintsKt.checkScrollableContainerConstraints-K40F9xA(CheckScrollableContainerConstraints.kt:59)
    *       at androidx.compose.foundation.lazy.LazyListKt$rememb
    * */

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {

        item{
            FieldForAddNewActivity({
                    activityName,
                    activityHour,
                    activityMinute,
                    activityDaysOfWeek,
                    activityTypeOfRecorder,
                    activityIsAlarmEnabled  ->

                viewModel.addActivity( activityName,
                    activityHour,
                    activityMinute,
                    activityDaysOfWeek,
                    activityTypeOfRecorder ,
                    activityIsAlarmEnabled)
            })
        }

        activityFunctionList(activities = activities, activityDeletePressed ={ dailyActivityEntity ->
            viewModel.deleteActivity(dailyActivityEntity)
        }, activityTakenAddPressed = activityTakenAddPressed)

    }

}

fun LazyListScope.activityFunctionList(
    activities: List<DailyActivityEntity>,
    activityDeletePressed: (DailyActivityEntity) -> Unit,
    activityTakenAddPressed: (DailyActivityEntity) -> Unit,
) {
    if (activities.isEmpty()) {
        item {
            Text(
                text = "Empty View",

                style = MaterialTheme.typography.headlineSmall
            )
        }
    } else {
        item {
            Text(
                text = "Activities (${activities.size})",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        items(activities) { activity: DailyActivityEntity ->
            SingleActivityItem(activity, activityDeletePressed, activityTakenAddPressed)
        }

    }
}


