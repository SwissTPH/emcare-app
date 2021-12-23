/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.argusoft.who.emcare.widget

import android.view.View
import com.argusoft.who.emcare.R
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object CustomEditTextFactory :
    QuestionnaireItemViewHolderFactory(R.layout.custom_edit_text_layout) {
    override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var textInputLayout: TextInputLayout
            private lateinit var textInputEditText: TextInputEditText
            override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

            override fun init(itemView: View) {
                /**
                 * Call the [QuestionnaireItemViewHolderDelegate.onAnswerChanged] function when the widget
                 * is interacted with and answer is changed by user input
                 */
                textInputLayout = itemView.findViewById(R.id.textInputLayout)
                textInputEditText = itemView.findViewById(R.id.textInputEditText)
            }

            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.text
            }

            override fun displayValidationResult(validationResult: ValidationResult) {
                // Custom validation message
                if (validationResult.isValid)
                    textInputLayout.error = validationResult.validationMessages[0] ?: ""
            }
        }
}