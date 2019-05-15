# Installing Dynamic Extensions in Alfresco

Dynamic Extensions (DE) is distributed as an Alfresco Module Package (AMP) extension that can be installed in Alfresco.

## Installing the DE AMP

To support multiple Alfresco versions, different AMP's for each minor Alfresco version update are 
build and distributed. E.g. if you are working with Alfresco 6.0.7-ga, you should
use the `alfresco-dynamic-extensions-repo-60` artifact.

### Maven Central Coordinates
All required artifacts, including the AMP to be installed in Alfresco, are available in Maven Central.

```xml
<dependency>
    <groupId>eu.xenit</groupId>
    <artifactId>alfresco-dynamic-extensions-repo-${alfresco-version}</artifactId>
    <version>${latest-dynamic-extensions-version}</version>
    <type>amp</type>
</dependency>
```

```groovy
alfrescoAmp "eu.xenit:alfresco-dynamic-extensions-repo-${alfrescoVersion}:${latest-dynamic-extensions-version}@amp"

```

These artifacts can be used to automatically install DE in Alfresco using e.g. the Alfresco Maven SDK or 
the [Alfresco Gradle Docker Plugins](https://github.com/xenit-eu/alfresco-docker-gradle-plugin)

### Manual download and install

* Download the latest <a href="https://github.com/xenit-eu/dynamic-extensions-for-alfresco/releases">Dynamic Extensions AMP</a>.
* Use the <a href="https://docs.alfresco.com/6.1/concepts/dev-extensions-modules-management-tool.html">Module Management Tool</a> to install the AMP in the Alfresco repository of your choosing.
* After restarting Alfresco, open the Control Panel: <a href="http://localhost:8080/alfresco/service/dynamic-extensions/">http://localhost:8080/alfresco/service/dynamic-extensions/</a>.
* Accessing the Control Panel requires an admin account.

## Supported Alfresco versions

Dynamic Extensions is systematically integration tested against:

* Alfresco Enterprise 6.1
* Alfresco Community 6.1
* Alfresco Enterprise 6.0
* Alfresco Community 6.0
* Alfresco Enterprise 5.2
* Alfresco Enterprise 5.1
* Alfresco Enterprise 5.0

### Known Alfresco issues that impact Dynamic Extensions

#### Alfresco 6.1 - wrong version of 'Commons annotations' used
When using DE on Alfresco 6.1, it is possible that it fails to startup due to following error:

```
Caused by: java.lang.NoSuchMethodError: javax.annotation.Resource.lookup()Ljava/lang/String;
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor$ResourceElement.<init>(CommonAnnotationBeanPostProcessor.java:621)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.lambda$buildResourceMetadata$0(CommonAnnotationBeanPostProcessor.java:383)
at org.springframework.util.ReflectionUtils.doWithLocalFields(ReflectionUtils.java:719)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.buildResourceMetadata(CommonAnnotationBeanPostProcessor.java:365)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.findResourceMetadata(CommonAnnotationBeanPostProcessor.java:350)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.postProcessMergedBeanDefinition(CommonAnnotationBeanPostProcessor.java:298)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyMergedBeanDefinitionPostProcessors(AbstractAutowireCapableBeanFactory.java:1044)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:550)
```

The root cause is for this problem is that Alfresco has multiple implementations of the 
[JSR 250 specification](https://en.wikipedia.org/wiki/JSR_250), 'Common Annotations' in the `WEB-INF/lib/` folder:

1. javax.annotation:javax.annotation-api
2. javax.annotation:jsr250-api
3. org.apache.geronimo.specs:geronimo-annotation_1.0_spec

Only the first one is up to date and contains the correct implementation of the `Resource` class. The other two versions
contain an old implementation of the `Resource` class, causing the provided error to be thrown by Spring internally.

This is only an issue as of Java 11 (Alfresco 6.1) because earlier versions had an correct implementation 
of the `Resource` class embedded in the distribution, and the 
[`bootstrap` classloader has the highest priority](https://tomcat.apache.org/tomcat-9.0-doc/class-loader-howto.html).

This issue has been reported to Alfresco: [MNT-20557](https://issues.alfresco.com/jira/browse/MNT-20557). 
Waiting for Alfresco to fix the issue, following workarounds can be used to make DE work on Alfresco 6.1:

* Remove the `jsr250-api` and `geronimo-annotation_1.0_spec` jars from the `WEB-INF/lib` folder of the Alfresco webapp.
* Install [this hotfix AMP](https://github.com/xenit-eu/alfresco-hotfix-MNT-20557) in your Alfresco distribution, 
which will overwrite the `jsr250-api` and `geronimo-annotation_1.0_spec` jars with empty jars.