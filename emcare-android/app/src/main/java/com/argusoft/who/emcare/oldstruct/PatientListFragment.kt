package com.argusoft.who.emcare.oldstruct

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.EmCareApplication
import com.google.android.fhir.FhirEngine
import com.argusoft.who.emcare.oldstruct.PatientListViewModel.PatientListViewModelFactory
import com.argusoft.who.emcare.databinding.FragmentPatientListBinding

class PatientListFragment : Fragment() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientListViewModel: PatientListViewModel
  private lateinit var searchView: SearchView
  private var _binding: FragmentPatientListBinding? = null
  private val binding
    get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentPatientListBinding.inflate(inflater, container, false)
    val view = binding.root
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = requireActivity().title
      setDisplayHomeAsUpEnabled(true)
    }
    fhirEngine = EmCareApplication.fhirEngine(requireContext())
    patientListViewModel =
      ViewModelProvider(
          this,
          PatientListViewModelFactory(requireActivity().application, fhirEngine)
        )
        .get(PatientListViewModel::class.java)
    val recyclerView: RecyclerView = binding.patientListContainer.patientList
    val adapter = PatientItemRecyclerViewAdapter(this::onPatientItemClicked)
    recyclerView.adapter = adapter
    recyclerView.addItemDecoration(
      DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
        setDrawable(ColorDrawable(Color.LTGRAY))
      }
    )

    patientListViewModel.liveSearchedPatients.observe(
      viewLifecycleOwner,
      {
        Log.d("PatientListActivity", "Submitting ${it.count()} patient records")
        adapter.submitList(it)
      }
    )
    patientListViewModel.patientCount.observe(
      viewLifecycleOwner,
      { Log.d("PatientListActivity", "$it Patient") }
    )

    patientListViewModel.patientCount.observe(
      viewLifecycleOwner,
      { binding.patientListContainer.patientCount.text = "$it Patient(s)" }
    )
    searchView = binding.search
    searchView.setOnQueryTextListener(
      object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String): Boolean {
          patientListViewModel.searchPatientsByName(newText)
          return true
        }

        override fun onQueryTextSubmit(query: String): Boolean {
          patientListViewModel.searchPatientsByName(query)
          return true
        }
      }
    )
    requireActivity()
      .onBackPressedDispatcher
      .addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
          override fun handleOnBackPressed() {
            if (searchView.query.isNotEmpty()) {
              searchView.setQuery("", true)
            } else {
              isEnabled = false
              activity?.onBackPressed()
            }
          }
        }
      )

    binding.apply {
      addPatient.setOnClickListener { onAddPatientClick() }
      addPatient.setColorFilter(Color.WHITE)
    }
    setHasOptionsMenu(true)
    (activity as MainActivity).setDrawerEnabled(true)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        (activity as MainActivity).openNavigationDrawer()
        true
      }
      else -> false
    }
  }

  private fun onPatientItemClicked(patientItem: PatientListViewModel.PatientItem) {
    findNavController()
      .navigate(PatientListFragmentDirections.navigateToProductDetail(patientItem.resourceId))
  }

  private fun onAddPatientClick() {
    findNavController()
      .navigate(PatientListFragmentDirections.actionPatientListToAddPatientFragment())
  }
}