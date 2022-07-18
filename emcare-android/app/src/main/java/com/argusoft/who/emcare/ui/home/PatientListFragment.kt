package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientListBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.handleListApiView
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientListFragment: BaseFragment<FragmentPatientListBinding>(), SearchView.OnQueryTextListener {
    private lateinit var glideRequests: GlideRequests
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        homeAdapter = HomeAdapter(onClickListener = this)
        homeViewModel.getPatients("", preference.getLoggedInUser()?.facility?.get(0)?.facilityId, homeAdapter.isNotEmpty())
    }

    override fun initView() {
        setupRecyclerView()
    }

    override fun initListener() {
        binding.searchView.setOnQueryTextListener(this)
        binding.addPatientButton.setOnClickListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        homeViewModel.getPatients(binding.searchView.query.toString(), preference.getLoggedInUser()?.facility?.get(0)?.facilityId, homeAdapter.isNotEmpty())
        return true
    }


    override fun initObserver() {
        observeNotNull(homeViewModel.patients) { apiResponse ->
            apiResponse.handleListApiView(binding.progressLayout, skipIds = listOf(R.id.searchView, R.id.addPatientButton, R.id.swipeRefreshLayout)) {
                it?.let { list ->
                    homeAdapter.clearAllItems()
                    homeAdapter.addAll(list)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.progressLayout.recyclerView = binding.recyclerView
        binding.progressLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.recyclerView.adapter = homeAdapter
        binding.progressLayout.setOnSwipeRefreshLayout {
            homeViewModel.getPatients(binding.searchView.query.toString(), preference.getLoggedInUser()?.facility?.get(0)?.facilityId, true)
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.addPatientButton -> {
                navigate(R.id.action_homeFragment_to_addPatientFragment)
            }
        }
    }
}