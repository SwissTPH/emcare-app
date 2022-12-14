package com.argusoft.who.emcare.data.remote

import ca.uhn.fhir.parser.IParser
import okhttp3.ResponseBody
import org.hl7.fhir.r4.model.Resource
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class FhirConverterFactory(private val parser: IParser) : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *> {
    return FhirConverter(parser)
  }
}

/** Retrofit converter that allows us to parse FHIR resources */
private class FhirConverter(private val parser: IParser) : Converter<ResponseBody, Resource> {
  override fun convert(value: ResponseBody): Resource {
    return parser.parseResource(value.string()) as Resource
  }
}
