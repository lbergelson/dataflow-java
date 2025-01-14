==============
dataflow-java |Build Status|_ |Build Coverage|_
==============

.. |Build Status| image:: http://img.shields.io/travis/googlegenomics/dataflow-java.svg?style=flat
.. _Build Status: https://travis-ci.org/googlegenomics/dataflow-java

.. |Build Coverage| image:: http://img.shields.io/coveralls/googlegenomics/dataflow-java.svg?style=flat
.. _Build Coverage: https://coveralls.io/r/googlegenomics/dataflow-java?branch=master

Getting started
---------------

* First, follow the Google Cloud Dataflow `getting started instructions
  <https://cloud.google.com/dataflow/getting-started>`_ to set up your environment
  for Dataflow. You will need your Project ID and Google Cloud Storage bucket in the following steps.

* To use this code, build the client using `Apache Maven`_::

    cd dataflow-java
    mvn compile
    mvn bundle:bundle

* Then, follow the Google Genomics `sign up instructions`_ to generate a valid
  ``client_secrets.json`` file.

* Move the ``client_secrets.json`` file into the dataflow-java directory.
  (Authentication will take place the first time you run a pipeline.)

* Then you can run a pipeline locally with the command line, passing in the
  Project ID and Google Cloud Storage bucket you made in the first step.
  This command runs the VariantSimilarity pipeline (which runs PCoA on a dataset)::

    java -cp target/google-genomics-dataflow-v1beta2-0.2-SNAPSHOT.jar \
      com.google.cloud.genomics.dataflow.pipelines.VariantSimilarity \
      --project=my-project-id \
      --output=gs://my-bucket/output/localtest.txt \
      --genomicsSecretsFile=client_secrets.json

  Note: when running locally, you may run into memory issues depending on the
  capacity of your local machine.

* To deploy your pipeline (which runs on Google Compute Engine), some additional
  command line arguments are required::

    java -cp target/google-genomics-dataflow-v1beta2-0.2-SNAPSHOT.jar \
      com.google.cloud.genomics.dataflow.pipelines.VariantSimilarity \
      --runner=BlockingDataflowPipelineRunner \
      --project=my-project-id \
      --stagingLocation=gs://my-bucket/staging \
      --output=gs://my-bucket/output/test.txt \
      --genomicsSecretsFile=client_secrets.json \
      --numWorkers=10

  Note: By default, the max workers you can have without requesting more GCE quota
  is 16. (That's the default limit on VMs)

.. _Apache Maven: http://maven.apache.org/download.cgi
.. _sign up instructions: https://cloud.google.com/genomics

Identity By State (IBS)
-----------------------

* In addition to variant similarity you can run other pipelines by changing the
  first argument provided in the above command lines. For example, to run Identity by State
  change ``VariantSimilarity`` to ``IdentityByState``::

    java -cp target/google-genomics-dataflow-v1beta2-0.2-SNAPSHOT.jar \
      com.google.cloud.genomics.dataflow.pipelines.IdentityByState \
      --project=my-project-id \
      --output=gs://my-bucket/localtest.txt \
      --genomicsSecretsFile=client_secrets.json


Code layout
-----------

The `Main code directory </src/main/java/com/google/cloud/genomics/dataflow>`_
contains several useful utilities:

coders:
  includes ``Coder`` classes that are useful for Genomics pipelines. ``GenericJsonCoder``
  can be used with any of the Java client library classes (like ``Read``, ``Variant``, etc)

functions:
  contains common DoFns that can be reused as part of any pipeline.
  ``OutputPCoAFile`` is an example of a complex ``PTransform`` that provides a useful common analysis.

pipelines:
  contains example pipelines which demonstrate how Google Cloud Dataflow can work with Google Genomics

  * ``VariantSimilarity`` runs a principal coordinates analysis over a dataset containing variants, and
    writes a file of graph results that can be easily displayed by Google Sheets.

  * ``IdentityByState`` runs IBS over a dataset containing variants. See the `results/ibs <results/ibs>`_
    directory for more information on how to use the pipeline's results.

readers:
  contains functions that perform API calls to read data from the genomics API

utils:
  contains utilities for running dataflow workflows against the genomics API

  * ``DataflowWorkarounds``
    contains workarounds needed to use the Google Cloud Dataflow APIs.

  * ``GenomicsOptions.java`` and ``GenomicsDatasetOptions``
    extend these classes for your command line options to take advantage of common command
    line functionality


Maven artifact
--------------
This code is also deployed as a Maven artifact through Sonatype. The
`utils-java readme <https://github.com/googlegenomics/utils-java#releasing-new-versions>`_
has detailed instructions on how to deploy new versions.

To depend on this code, add the following to your ``pom.xml`` file::

  <project>
    <dependencies>
      <dependency>
        <groupId>com.google.cloud.genomics</groupId>
        <artifactId>google-genomics-dataflow</artifactId>
        <version>v1beta2-0.2</version>
      </dependency>
    </dependencies>
  </project>

You can find the latest version in
`Maven's central repository <https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22google-genomics-dataflow%22>`_

We'll soon include an example pipeline that depends on this code in another GitHub repository.

Project status
--------------

Goals
~~~~~
* Provide a Maven artifact which makes it easier to use Google Genomics within Google Cloud Dataflow.
* Provide some example pipelines which demonstrate how Dataflow can be used to analyze Genomics data.

Current status
~~~~~~~~~~~~~~
This code is in active development:

* TODO: Explain all the possible command line args:``zone``, ``allContigs``, etc
* TODO: Refine the transmission probability pipeline
* TODO: Add more tests
