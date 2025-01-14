/*
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.dataflow.utils;

import com.google.cloud.dataflow.sdk.options.DataflowPipelineOptions;
import com.google.cloud.dataflow.sdk.options.Default;
import com.google.cloud.dataflow.sdk.options.Description;
import com.google.cloud.genomics.utils.GenomicsFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Contains common genomics pipeline options. Extend this class to add additional command line args.
 *
 *  Note: All methods defined in this class will be called during command line parsing unless it is
 * annotated with a @JsonIgnore annotation.
 */
public interface GenomicsOptions extends DataflowPipelineOptions {

  public static class Methods {
    public static GenomicsFactory.OfflineAuth getGenomicsAuth(GenomicsOptions options)
        throws IOException, GeneralSecurityException {
      String apiKey = options.getApiKey(), appName = options.getAppName();
      return GenomicsFactory.builder(appName).setNumberOfRetries(options.getNumberOfRetries()).build()
          .getOfflineAuth(apiKey, options.getGenomicsSecretsFile());
    }

    public static void validateOptions(GenomicsOptions options) {
      String secretsFile = options.getGenomicsSecretsFile(), apiKey = options.getApiKey();
      if (secretsFile == null && apiKey == null) {
        throw new IllegalArgumentException(
            "Need to specify either --genomicsSecretsFile or --apiKey");
      }
    }
  }

  @Description("If querying a public dataset, provide a Google API key that has access "
      + "to genomics data and no OAuth will be performed.")
  String getApiKey();

  void setApiKey(String apiKey);

  @Description("If querying a private dataset, or performing any write operations, " +
      "you need to provide the path to client_secrets.json. Do not supply an api key.")
  String getGenomicsSecretsFile();

  void setGenomicsSecretsFile(String genomicsSecretsFile);

  @Description("Specifies the maximum number of retries to attempt (if needed) for requests to the Genomics API.")
  @Default.Integer(10)
  int getNumberOfRetries();

  void setNumberOfRetries(int numOfRetries);

  @Description("Specifies number of results to return in a single page of results. "
      + "If unspecified, the default page size for the Genomics API is used.")
  @Default.Integer(0)
  int getPageSize();

  void setPageSize(int pageSize);
}
