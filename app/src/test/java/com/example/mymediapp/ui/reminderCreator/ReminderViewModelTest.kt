package com.example.mymediapp.ui.reminderCreator

import android.app.AlarmManager
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.mymediapp.model.Reminder
import com.example.mymediapp.model.Time
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class ReminderViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Use UnconfinedTestDispatcher for coroutine testing
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ReminderViewModel

    @Before
    fun setUp() {
        // Set Main dispatcher for coroutines
        Dispatchers.setMain(testDispatcher)

        // Mock Context
        val context = mockk<Context>(relaxed = true)

        // Mock FirebaseAuth
        val auth = mockk<FirebaseAuth>(relaxed = true)
        val mockUser = mockk<FirebaseUser>(relaxed = true)
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "testUserId"

        // Mock FirebaseFirestore
        val db = mockk<FirebaseFirestore>(relaxed = true)

        // Mock AlarmManager
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager

        // Mock android.util.Log
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0

        // Initialize ViewModel with required parameters
        viewModel = ReminderViewModel(context, auth, db, alarmManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // Extension function to get LiveData value
    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        try {
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    // Test parseTimeBetweenDoses
    @Test
    fun `parseTimeBetweenDoses parses valid time strings correctly`() {
        val timeString = "2h30m"
        val (hours, minutes) = viewModel.parseTimeBetweenDoses(timeString)
        assertEquals(2, hours)
        assertEquals(30, minutes)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `parseTimeBetweenDoses throws exception for invalid format`() {
        val timeString = "invalid"
        viewModel.parseTimeBetweenDoses(timeString)
    }


    @Test
    fun `addReminder adds reminder to the list`() {
        // Given
        val initialReminders = viewModel.reminders.getOrAwaitValue()
        val initialSize = initialReminders.size

        val reminder = Reminder(
            medicineName = "Paracetamol",
            numberOfDoses = 2,
            timeBetweenDosesString = "4h0m",
            startDate = Date(),
            endDate = Date(),
            startTime = Time(9, 0),
            notes = "After meals"
        )

        // When
        viewModel.addReminder(reminder)

        // Then
        val updatedReminders = viewModel.reminders.getOrAwaitValue()
        assertEquals(initialSize + 1, updatedReminders.size)
        assertTrue(updatedReminders.contains(reminder))
    }

}
