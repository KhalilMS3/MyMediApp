package com.example.mymediapp.ui.screens.deit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.mymediapp.model.MealItem
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import org.junit.*
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class DietViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: DietViewModel

    private lateinit var mealsCollection: CollectionReference
    private lateinit var listenerSlot: CapturingSlot<EventListener<QuerySnapshot>>

    @Before
    fun setUp() {
        // Mock android.util.Log
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0

        // Initialize mocks
        db = mockk()
        auth = mockk()

        // Mock FirebaseAuth and FirebaseUser
        val mockUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "testUserId"

        // Mock Firestore chain: db.collection("users").document("testUserId").collection("meals")
        val usersCollection = mockk<CollectionReference>()
        val userDocument = mockk<DocumentReference>()
        mealsCollection = mockk()

        every { db.collection("users") } returns usersCollection
        every { usersCollection.document("testUserId") } returns userDocument
        every { userDocument.collection("meals") } returns mealsCollection

        // Capture the EventListener<QuerySnapshot> passed to addSnapshotListener
        listenerSlot = slot()
        every { mealsCollection.addSnapshotListener(capture(listenerSlot)) } returns mockk<ListenerRegistration>()

        // Initialize ViewModel with mocked dependencies
        viewModel = DietViewModel(db, auth)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> {
            data = it
            latch.countDown()
        }

        this.observeForever(observer)

        try {
            if (!latch.await(time, timeUnit)) {
                throw AssertionError("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }

        return data as T
    }

    @Test
    fun `fetchMealItems updates mealItems LiveData when snapshots are received`() {
        // Arrange
        val mockSnapshot = mockk<QuerySnapshot>()
        val mockDocumentSnapshot = mockk<DocumentSnapshot>()
        val mealItem = MealItem(
            id = "sdf234",
            meal = "ris",
            calories = 300,
            date = "2024-10-10",
            time = "08:00"
        )
        every { mockDocumentSnapshot.toObject(MealItem::class.java) } returns mealItem
        every { mockSnapshot.documents } returns listOf(mockDocumentSnapshot)

        // Act
        // Simulate snapshot received
        listenerSlot.captured.onEvent(mockSnapshot, null)

        // Assert
        val observedMealItems = viewModel.mealItems.getOrAwaitValue()
        assertEquals(1, observedMealItems.size)
        assertEquals(mealItem, observedMealItems[0])
    }

    @Test
    fun `calculateCaloriesPerDay aggregates calories correctly`() {
        // Arrange
        val mockSnapshot = mockk<QuerySnapshot>()
        val mealItem1 = MealItem("asdf23", "Falafel", 300, "2024-10-10", "08:00")
        val mealItem2 = MealItem("sdf34", "Ost", 500, "2024-10-10", "12:00")
        val mealItem3 = MealItem("sdf4we4", "Nudlers", 400, "2024-10-11", "19:00")

        val mockDocumentSnapshot1 = mockk<DocumentSnapshot>()
        val mockDocumentSnapshot2 = mockk<DocumentSnapshot>()
        val mockDocumentSnapshot3 = mockk<DocumentSnapshot>()

        every { mockDocumentSnapshot1.toObject(MealItem::class.java) } returns mealItem1
        every { mockDocumentSnapshot2.toObject(MealItem::class.java) } returns mealItem2
        every { mockDocumentSnapshot3.toObject(MealItem::class.java) } returns mealItem3

        every { mockSnapshot.documents } returns listOf(
            mockDocumentSnapshot1,
            mockDocumentSnapshot2,
            mockDocumentSnapshot3
        )

        // Act
        listenerSlot.captured.onEvent(mockSnapshot, null)

        // Assert
        val observedCaloriesPerDay = viewModel.caloriesPerDay.getOrAwaitValue()
        assertEquals(2, observedCaloriesPerDay.size)
        assertEquals(800, observedCaloriesPerDay["2024-10-10"])
        assertEquals(400, observedCaloriesPerDay["2024-10-11"])
    }

    @Test
    fun `addMealItem adds meal to Firestore and logs success`() {
        // Arrange
        val mealItem = MealItem("124234dswf", "food", 300, "2025-10-10", "08:00")
        val mealDocument = mockk<DocumentReference>()
        val setTask = mockk<Task<Void>>()

        every { mealsCollection.document(mealItem.id) } returns mealDocument
        every { mealDocument.set(mealItem) } returns setTask

        val successListenerSlot = slot<OnSuccessListener<Void>>()
        every { setTask.addOnSuccessListener(capture(successListenerSlot)) } returns setTask
        every { setTask.addOnFailureListener(any()) } returns setTask

        // Act
        viewModel.addMealItem(mealItem)
        successListenerSlot.captured.onSuccess(null)

        // Assert
        verify { mealDocument.set(mealItem) }
    }

    @Test
    fun `deleteMealItem deletes meal from Firestore and logs success`() {
        // Arrange
        val mealId = "124234dswf"
        val mealDocument = mockk<DocumentReference>()
        val deleteTask = mockk<Task<Void>>()

        every { mealsCollection.document(mealId) } returns mealDocument
        every { mealDocument.delete() } returns deleteTask

        val successListenerSlot = slot<OnSuccessListener<Void>>()
        every { deleteTask.addOnSuccessListener(capture(successListenerSlot)) } returns deleteTask
        every { deleteTask.addOnFailureListener(any()) } returns deleteTask

        // Act
        viewModel.deleteMealItem(mealId)
        successListenerSlot.captured.onSuccess(null)

        // Assert
        verify { mealDocument.delete() }
    }




}
