package com.argusoft.who.emcare.ui.home

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.pref.EncPref.putString
import com.argusoft.who.emcare.databinding.ListItemSidepaneBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.SidepaneItem
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.toBinding

class SidepaneAdapter(
    val list: ArrayList<SidepaneItem?> = arrayListOf(),
    private val onClickListener: View.OnClickListener,
    val navHostFragment: NavHostFragment
) : BaseAdapter<SidepaneItem>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is ViewHolder -> {
                list[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemSidepaneBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            //on click listener
            itemView.setOnClickListener {
                if(list[bindingAdapterPosition]?.consultationItemData != null){
                    navHostFragment.navController.navigate(R.id.action_patientQuestionnaireFragment_to_patientQuestionnaireFragment, bundleOf(
                        INTENT_EXTRA_QUESTIONNAIRE_ID to list[bindingAdapterPosition]?.consultationItemData?.questionnaireId,
                        INTENT_EXTRA_STRUCTUREMAP_ID to list[bindingAdapterPosition]?.consultationItemData?.structureMapId,
//                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, list[bindingAdapterPosition]?.badgeText)
                        INTENT_EXTRA_QUESTIONNAIRE_HEADER to  list[bindingAdapterPosition]?.consultationItemData?.header, //For testing only replace it with badgeText
                        INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID to  list[bindingAdapterPosition]?.consultationItemData?.consultationFlowItemId,
                        INTENT_EXTRA_PATIENT_ID to list[bindingAdapterPosition]?.consultationItemData?.patientId,
                        INTENT_EXTRA_ENCOUNTER_ID to list[bindingAdapterPosition]?.consultationItemData?.encounterId,
                        INTENT_EXTRA_CONSULTATION_STAGE to list[bindingAdapterPosition]?.consultationItemData?.consultationStage,
                        INTENT_EXTRA_QUESTIONNAIRE_RESPONSE to list[bindingAdapterPosition]?.consultationItemData?.questionnaireResponseText,
                        INTENT_EXTRA_IS_ACTIVE to list[bindingAdapterPosition]?.consultationItemData?.isActive
                    ))

                }

            }
        }
        fun bind(obj: SidepaneItem) = with(obj) {
            binding.iconImageView.setImageResource(iconId!!)
            binding.descriptionTextView.text = description
            if(consultationItemData != null){
                binding.iconImageView.setColorFilter(R.color.black)
            }
        }
    }
}