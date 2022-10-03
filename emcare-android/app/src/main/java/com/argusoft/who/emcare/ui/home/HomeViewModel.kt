package com.argusoft.who.emcare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.URL_CQF_LIBRARY
import com.argusoft.who.emcare.ui.common.URL_INITIAL_EXPRESSION
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.common.stageToBadgeMap
import com.argusoft.who.emcare.ui.common.stageToIconMap
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.createQuestionnaireResponseItem
import com.google.android.fhir.workflow.FhirOperator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val consultationFlowRepository: ConsultationFlowRepository,
    private val fhirEngine: FhirEngine
) : ViewModel() {

    var questionnaireJson: String? = null
    var currentTab: Int = 0
    private val _patients = SingleLiveEvent<ApiResponse<List<PatientItem>>>()
    val patients: LiveData<ApiResponse<List<PatientItem>>> = _patients

    private val _consultations = SingleLiveEvent<ApiResponse<List<ConsultationItemData>>>()
    val consultations: LiveData<ApiResponse<List<ConsultationItemData>>> = _consultations

    private val _questionnaireWithQR = SingleLiveEvent<ApiResponse<Pair<String, String>>>()
    val questionnaireWithQR: LiveData<ApiResponse<Pair<String, String>>> = _questionnaireWithQR

    private val _saveQuestionnaire = MutableLiveData<ApiResponse<ConsultationFlowItem>>()
    val saveQuestionnaire: LiveData<ApiResponse<ConsultationFlowItem>> = _saveQuestionnaire


    fun getPatients(search: String? = null, facilityId: String?, isRefresh: Boolean = false) {
        _patients.value = ApiResponse.Loading(isRefresh)
        viewModelScope.launch {
            patientRepository.getPatients(search, facilityId).collect {
                _patients.value = it
            }
        }
    }

    fun getConsultations(search: String? = null, facilityId: String?, isRefresh: Boolean = false){
        _consultations.value = ApiResponse.Loading(isRefresh)
        val consultationsArrayList = mutableListOf(
            ConsultationItemData(patientId="",
                encounterId="",
                name="Helsenorge Reg.",
                dateOfBirth="10/10/20",
                dateOfConsultation = "10/10/21",
                badgeText = "Registration",
                consultationIcon = R.drawable.closed_consultation_icon_dark,
                questionnaireId = "registration.ideal.q",
                consultationFlowItemId = UUID.randomUUID().toString()),
            ConsultationItemData(patientId="",
                encounterId="",
                name="Signs",
                dateOfBirth="10/10/20",
                dateOfConsultation = "01/01/22",
                badgeText = "Signs",
                consultationIcon = R.drawable.sign_icon,
                questionnaireId = "emcare.b10-16.signs.2m.p",
                consultationFlowItemId = UUID.randomUUID().toString()),

            ConsultationItemData(patientId="",
                name="Measurements",
                encounterId="",
                dateOfBirth="10/10/20",
                dateOfConsultation = "10/10/21",
                badgeText = "Measurements",
                consultationIcon = R.drawable.closed_consultation_icon_dark,
                questionnaireId = "emcare.b6.measurements",
                consultationFlowItemId = UUID.randomUUID().toString()),
        )
        viewModelScope.launch {
            consultationFlowRepository.getAllLatestActiveConsultations().collect {
                it.data?.forEach{ consultationFlowItem ->
                    patientRepository.getPatientById(consultationFlowItem.patientId).collect{ patientResponse ->
                        val patientItem = patientResponse.data
                        if (patientItem != null) {
                            consultationsArrayList.add(
                                ConsultationItemData(
                                    name = patientItem.nameFirstRep.nameAsSingleString.orEmpty { patientItem.identifierFirstRep.value ?:"NA #${patientItem.id?.takeLast(9)}"},
                                    dateOfBirth = patientItem.birthDateElement.valueAsString ?: "Not Provided",
                                    dateOfConsultation = ZonedDateTime.parse(consultationFlowItem.consultationDate.plus("Z[UTC]")).format(DateTimeFormatter.ofPattern("dd/MM/YY")),
                                    badgeText = stageToBadgeMap[consultationFlowItem.consultationStage],
                                    header = consultationFlowItem.questionnaireId, //TODO: For test only, replace it with appropriate header
                                    consultationIcon = stageToIconMap[consultationFlowItem.consultationStage],
                                    consultationFlowItemId = consultationFlowItem.id,
                                    patientId = consultationFlowItem.patientId,
                                    encounterId = consultationFlowItem.encounterId,
                                    questionnaireId = consultationFlowItem.questionnaireId,
                                    structureMapId = consultationFlowItem.structureMapId,
                                    consultationStage = consultationFlowItem.consultationStage,
                                    questionnaireResponseText = consultationFlowItem.questionnaireResponseText,
                                    isActive = consultationFlowItem.isActive
                                )
                            )
                        }
                    }
                }
                _consultations.value = ApiResponse.Success(consultationsArrayList)
            }
        }
    }


    fun getQuestionnaireWithQR(questionnaireId: String, patientId: String, encounterId: String? = null) {
        _questionnaireWithQR.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getQuestionnaire(questionnaireId).collect {
                val parser = FhirContext.forR4().newJsonParser()
                val questionnaireJsonWithQR: Questionnaire = preProcessQuestionnaire(it.data!!, patientId)
                val questionnaireResponse: QuestionnaireResponse = generateQuestionnaireResponseWithPatientIdAndEncounterId(questionnaireJsonWithQR, patientId!!, encounterId!!)

                val questionnaireString = parser.encodeResourceToString(questionnaireJsonWithQR)
                val questionnaireResponseString = parser.encodeResourceToString(questionnaireResponse)
                _questionnaireWithQR.value = ApiResponse.Success(data=questionnaireString to questionnaireResponseString)
            }
        }
    }

    fun saveQuestionnaire(questionnaireResponse: QuestionnaireResponse, questionnaire: String, facilityId: String, structureMapId: String, consultationFlowItemId: String? = null, consultationStage: String? = null) {
        viewModelScope.launch {
            patientRepository.saveQuestionnaire(questionnaireResponse, questionnaire,facilityId, structureMapId, consultationFlowItemId ,consultationStage).collect {
                _saveQuestionnaire.value = it
            }
        }
    }

    private fun generateQuestionnaireResponseWithPatientIdAndEncounterId(questionnaireJson: Questionnaire, patientId: String, encounterId: String) : QuestionnaireResponse {
       //Create empty QR as done in the SDC
        val questionnaireResponse:QuestionnaireResponse = QuestionnaireResponse().apply {
            questionnaire = questionnaireJson.url
        }
        questionnaireJson.item.forEach { it2 ->
            questionnaireResponse.addItem(it2.createQuestionnaireResponseItem())
        }

        //Inject patientId as subject & encounterId as Encounter.
        questionnaireResponse.subject = Reference().apply {
            id = IdType(patientId).id
            type = ResourceType.Patient.name
            identifier = Identifier().apply {
                value = patientId
            }
        }
        questionnaireResponse.encounter = Reference().apply {
            id = encounterId
            type = ResourceType.Encounter.name
            identifier = Identifier().apply {
                value = encounterId
            }
        }

        return questionnaireResponse
    }

    private fun preProcessQuestionnaire(questionnaire: Questionnaire, patientId: String) : Questionnaire {
        var ansQuestionnaire = injectUuid(questionnaire)
        if(questionnaire.hasExtension(URL_CQF_LIBRARY)){
            ansQuestionnaire = injectInitialExpressionCqlValues(questionnaire, patientId)
        }
        return ansQuestionnaire
    }

    private fun injectUuid(questionnaire: Questionnaire) : Questionnaire {
        questionnaire.item.forEach { item ->
            if(!item.initial.isNullOrEmpty()) {
                if(item.initial[0].value.asStringValue() == "uuid()") {
                    item.initial =
                        mutableListOf(Questionnaire.QuestionnaireItemInitialComponent(StringType(
                            UUID.randomUUID().toString())))
                }
            }
        }
        return questionnaire
    }

    private fun injectInitialExpressionCqlValues(questionnaire: Questionnaire, patientId: String): Questionnaire {
        val cqlLibraryURL = questionnaire.getExtensionByUrl(URL_CQF_LIBRARY).value.asStringValue()
        val expressionSet = mutableSetOf<String>()
        //If questionnaire has Cql library then evaluate library and inject the parameters
        if(cqlLibraryURL.isNotEmpty()){
            //Creating ExpressionSet
            questionnaire.item.forEach { item ->
                if(item.hasExtension(URL_INITIAL_EXPRESSION)) {
                    expressionSet.add((item.getExtensionByUrl(URL_INITIAL_EXPRESSION).value as Expression).expression)
                }
            }
            //Evaluating Library
            val parameters = FhirOperator(FhirContext.forR4(), fhirEngine)
                .evaluateLibrary(cqlLibraryURL, patientId, expressionSet)
                as Parameters
            //Inject parameters to appropriate places
            questionnaire.item.forEach { item ->
                if(item.hasExtension(URL_INITIAL_EXPRESSION)) {
                    val value = parameters.getParameter((item.getExtensionByUrl(URL_INITIAL_EXPRESSION).value as Expression).expression) //parameter has same name as linkId
                    //inject value in the QuestionnaireItem as InitialComponent
                    if(value != null)
                        item.initial = mutableListOf(Questionnaire.QuestionnaireItemInitialComponent(value))
                }
            }
            return questionnaire
        } else {
            return questionnaire
        }
    }
}