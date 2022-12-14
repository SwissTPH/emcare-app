package com.argusoft.who.emcare.ui.home.patient.actions

import android.opengl.Visibility
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.remote.executeApiHelper
import com.argusoft.who.emcare.databinding.FragmentPatientQuestionnaireBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.ui.home.HomeViewModel
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientQuestionnaireFragment : BaseFragment<FragmentPatientQuestionnaireBinding>(){

    private val homeViewModel: HomeViewModel by viewModels()
    private val questionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        (activity as? HomeActivity)?.closeSidepane()
//        binding.headerLayout.toolbar.setTitleSidepane(getString(R.string.patient) + " " + requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_HEADER))
        binding.headerLayout.toolbar.setTitleSidepane(requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_HEADER))

        requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_ID)?.let {
            homeViewModel.getQuestionnaireWithQR(it, requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!,requireArguments().getString(INTENT_EXTRA_ENCOUNTER_ID)) }

        childFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.SUBMIT_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            homeViewModel.questionnaireJson?.let {
                homeViewModel.saveQuestionnaire(
                    questionnaireResponse = questionnaireFragment.getQuestionnaireResponse(),
                    questionnaire = it,
                    structureMapId = requireArguments().getString(INTENT_EXTRA_STRUCTUREMAP_ID)!!,
                    facilityId = preference.getLoggedInUser()?.facility?.get(0)?.facilityId!!,
                    consultationFlowItemId = requireArguments().getString(INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID),
                    consultationStage = requireArguments().getString(INTENT_EXTRA_CONSULTATION_STAGE)
                )
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.alertDialog {
                setMessage(R.string.msg_exit_consultation)
                setPositiveButton(R.string.button_yes) { _, _ ->
                    navigate(R.id.action_patientQuestionnaireFragment_to_homeFragment)
                }
                setNegativeButton(R.string.button_no) { _, _ -> }
            }?.show()
        }

        homeViewModel.getPatient(requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!)
        homeViewModel.getSidePaneItems(requireArguments().getString(INTENT_EXTRA_ENCOUNTER_ID)!!,requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!)
    }



    private fun addQuestionnaireFragment(pair: Pair<String, String>) {
        homeViewModel.questionnaireJson = pair.first
        homeViewModel.questionnaireJson?.let {
            questionnaireFragment.arguments =
                bundleOf(
                    QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to pair.first,
                    QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING to pair.second
                )
            childFragmentManager.commit {
                add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
            }
        }
    }

    override fun initListener() {

    }

    override fun initObserver() {
        observeNotNull(homeViewModel.patient) { apiResponse ->
            apiResponse.whenSuccess { patientItem ->
                binding.nameTextView.text = patientItem.nameFirstRep.nameAsSingleString.orEmpty { patientItem.identifierFirstRep.value ?:"NA #${patientItem.id?.takeLast(9)}"}
                binding.dobTextView.text = patientItem.birthDateElement.valueAsString ?: "Not Provided"
                if(!patientItem.hasGender()){
                    if(patientItem.genderElement.valueAsString.equals("male" ,false))
                        binding.childImageView.setImageResource(R.drawable.baby_boy)
                    else
                        binding.childImageView.setImageResource(R.drawable.baby_girl)
                }
                binding.childImageView.visibility = View.VISIBLE
                binding.dobTextViewLabel.visibility = View.VISIBLE
            }
        }

        observeNotNull(homeViewModel.sidepaneItems) { apiResponse ->
            apiResponse.whenSuccess {
                (activity as? HomeActivity)?.setupSidepane()
                (activity as? HomeActivity)?.sidepaneAdapter?.clearAllItems()
                (activity as? HomeActivity)?.sidepaneAdapter?.addAll(it)
            }
        }
        observeNotNull(homeViewModel.saveQuestionnaire) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if (it is ConsultationFlowItem) {
                    navigate(R.id.action_patientQuestionnaireFragment_to_patientQuestionnaireFragment){
                        putString(INTENT_EXTRA_QUESTIONNAIRE_ID, it.questionnaireId)
                        putString(INTENT_EXTRA_STRUCTUREMAP_ID, it.structureMapId)
                        putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, it.questionnaireId)
                        putString(INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID, it.id)
                        putString(INTENT_EXTRA_PATIENT_ID,it.patientId)
                        putString(INTENT_EXTRA_ENCOUNTER_ID,it.encounterId)
                        putString(INTENT_EXTRA_CONSULTATION_STAGE,it.consultationStage)
                        putString(INTENT_EXTRA_QUESTIONNAIRE_RESPONSE,it.questionnaireResponseText)
                    }
                } else {
                    navigate(R.id.action_patientQuestionnaireFragment_to_homeFragment)
                }
            }
        }

        observeNotNull(homeViewModel.questionnaireWithQR) { questionnaire ->
            questionnaire.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if(requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_RESPONSE).isNullOrEmpty())
                    it?.let { addQuestionnaireFragment(it) }
                else
                    it?.let{
                        addQuestionnaireFragment(
                            it.first to
                                requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_RESPONSE, it.second)
                        )
                    }
            }
        }
//        observeNotNull(settingsViewModel.languageApiState) {
//            it.whenSuccess {
//                it.languageData?.convertToMap()?.apply {
//                    binding.headerLayout.toolbar.setTitleSidepane(
//                        getOrElse("Patient") { getString(R.string.patient) } + " "
//                            + requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_HEADER)  )
//                }
//            }
//        }
    }
}