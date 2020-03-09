package com.aescolar.marvelsuperheroes.characters.ui

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aescolar.apiclient.ApiClient
import com.aescolar.marvelsuperheroes.R
import com.aescolar.marvelsuperheroes.characterdetail.ui.CharacterDetailActivity
import com.aescolar.marvelsuperheroes.domain.model.Character
import com.paginate.Paginate
import kotlinx.android.synthetic.main.activity_characters_list.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.android.viewmodel.ext.android.viewModel

class CharactersListActivity : AppCompatActivity() {

    private val viewModel: CharactersViewModel by viewModel()

    private val screenStatusObserver =
        Observer<CharactersViewModel.CharactersListScreenStatus> { status ->
            if (status != CharactersViewModel.CharactersListScreenStatus.ShowLoading) {
                paginationController.contentsLoading = false
            }
            when (status) {
                CharactersViewModel.CharactersListScreenStatus.ShowLoading -> {
                    showLoading()
                }
                CharactersViewModel.CharactersListScreenStatus.ShowNoData -> {
                    showNoData()
                }
                is CharactersViewModel.CharactersListScreenStatus.ShowNetworkError -> {
                    showErrorScreen(status.error)
                }
                is CharactersViewModel.CharactersListScreenStatus.CharactersLoaded -> {
                    paginationController.hasLoadedAllItems = !status.moreToLoad
                    showCharactersList(status.characters)
                }
            }
        }

    private lateinit var charactersListAdapter: CharactersListAdapter
    private lateinit var paginationController: PaginationController
    private var currentFilter: String? = null

    // Most people find annoying that apps close with a single back.
    private var lastPressMs: Long = 0
    private var exitToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_list)

        setupRecyclerView()
        setupPagination()
        setupSearchBar()

        viewModel.charactersListScreenStatus.observe(this, screenStatusObserver)
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()

        // First, clear search filter if there is any, then handle back-exit.
        if (search_filter.text.isNotEmpty()) {
            search_filter.text.clear()
        } else {
            if (currentTime - lastPressMs > EXIT_DELAY_MS) {
                exitToast =
                    Toast.makeText(baseContext, R.string.exit_alert_toast, Toast.LENGTH_SHORT).also {
                        it.show()
                    }
                lastPressMs = currentTime
            } else {
                if (exitToast != null) exitToast!!.cancel()
                super.onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() {
        charactersListAdapter =
            CharactersListAdapter {
                CharacterDetailActivity.open(this, it)
            }
        characters_recycler_view.apply {
            setHasFixedSize(true)
            adapter = charactersListAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(this.context, RecyclerView.VERTICAL))
        }

    }

    private fun setupPagination() {
        paginationController = PaginationController {
            paginationController.contentsLoading = true
            viewModel.fetchPage(currentFilter)
        }

        Paginate.with(characters_recycler_view, paginationController)
            .setLoadingTriggerThreshold(8)
            .addLoadingListItem(true)
            .build()
    }

    private fun setupSearchBar() {
        with(search_filter) {
            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (v.text.isNotEmpty()) {
                        currentFilter = search_filter.text.toString()
                        viewModel.fetchPage(currentFilter)
                    } else {
                        if (currentFilter != null) {
                            currentFilter = null
                            viewModel.fetchPage()
                        }
                    }

                    val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                    true
                } else {
                    false
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isEmpty()) {
                        currentFilter = null
                        viewModel.fetchPage()
                    }
                }
            })
        }
    }

    private fun showLoading() {
        characters_recycler_view.visibility = View.GONE
        search_filter.visibility = View.GONE
        error_text.visibility = View.GONE
        error_refresh.visibility = View.GONE
        no_characters.visibility = View.GONE

        loading_progress.visibility = View.VISIBLE
    }

    private fun showCharactersList(characters: List<Character>) {
        loading_progress.visibility = View.GONE
        error_refresh.visibility = View.GONE
        error_text.visibility = View.GONE
        no_characters.visibility = View.GONE

        search_filter.visibility = View.VISIBLE
        characters_recycler_view.visibility = View.VISIBLE

        charactersListAdapter.submitList(characters)
        charactersListAdapter.notifyDataSetChanged()
    }

    private fun showNoData() {
        characters_recycler_view.visibility = View.GONE
        loading_progress.visibility = View.GONE
        error_text.visibility = View.GONE
        error_refresh.visibility = View.GONE

        no_characters.visibility = View.VISIBLE
    }

    private fun showErrorScreen(error: ApiClient.ApiError) {
        characters_recycler_view.visibility = View.GONE
        search_filter.visibility = View.GONE
        loading_progress.visibility = View.GONE

        val errorMessage = when (error) {
            is ApiClient.ApiError.UnknownError -> resources.getString(
                R.string.network_error_unknown,
                error.code.toString()
            )
            ApiClient.ApiError.NotFoundError -> resources.getString(R.string.network_error_not_found)
            ApiClient.ApiError.NetworkError -> resources.getString(R.string.network_error_generic)
        }

        error_text.apply {
            visibility = View.VISIBLE
            text = errorMessage
        }
        error_refresh.apply {
            setOnClickListener { viewModel.fetchPage(forceRefresh = true) }
            visibility = View.VISIBLE
        }
    }

    companion object {
        private const val EXIT_DELAY_MS = 2500L
    }
}
