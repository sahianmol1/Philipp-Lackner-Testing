package com.androiddevs.shoppinglisttestingyt.ui

import android.os.AsyncTask
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.shoppinglisttestingyt.MainCoroutineRule
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValueTest
import com.androiddevs.shoppinglisttestingyt.others.Constants.MAX_NAME_LENGTH
import com.androiddevs.shoppinglisttestingyt.others.Constants.MAX_PRICE_LENGTH
import com.androiddevs.shoppinglisttestingyt.others.Status
import com.androiddevs.shoppinglisttestingyt.repositories.FakeShoppingRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShoppingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setUp() {
        viewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @Test
    fun `insert shopping item with empty field, return error`() {
        viewModel.insertShoppingItem("name", "", "3.0")

        val value = viewModel.insertShoppingItemsStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, return error`() {
        val string = buildString {
            for (i in 1 .. MAX_NAME_LENGTH + 1) {
                append("a")
            }
        }
        viewModel.insertShoppingItem(string, "5", "3.0")

        val value = viewModel.insertShoppingItemsStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long price, return error`() {
        val string = buildString {
            for (i in 1 .. MAX_PRICE_LENGTH + 1) {
                append("a")
            }
        }
        viewModel.insertShoppingItem("name", "5", string)

        val value = viewModel.insertShoppingItemsStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with valid input, return error`() {

        viewModel.insertShoppingItem("sdaf", "5", "3.0")

        val value = viewModel.insertShoppingItemsStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

}