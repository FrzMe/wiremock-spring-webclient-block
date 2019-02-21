# Spring WebClient / WireMock block/freeze

Using WireMock 2.21 (2.18-2.21 seem to be affected) when repeatedely sending http requests which request and decode JSON using the spring WebClient to WireMock the requests sometimes freezes and fail to reply. 
The issue does not occur when using Wiremock 2.17 and it does not occur when requesting Strings (and not decoding JSON).
I'm not entirely certain whether this is a WireMock or a Spring WebClient issue, I'm therefore raising this issue with both projects. 
* WireMock Issue: https://github.com/tomakehurst/wiremock/issues/1084
* Spring issue: https://github.com/spring-projects/spring-framework/issues/22441

The failing test setup can be found here: https://github.com/FrzMe/wiremock-spring-webclient-block/blob/master/src/test/java/webclientblock/WebClientBlockTest.java#L49

Reasons why it could be a spring WebClient issue:

* when requesting "String" via the WebClient the issue does not occur (-> it occurs only when json decoding) (see https://github.com/FrzMe/wiremock-spring-webclient-block/blob/master/src/test/java/webclientblock/WebClientBlockTest.java#L102)
* when using the RestTemplate instead of the WebClient the issue does not occur (see https://github.com/FrzMe/wiremock-spring-webclient-block/blob/master/src/test/java/webclientblock/WebClientBlockTest.java#L80 )

Reasons why it could be a WireMock issue:

* when requesting from other sources (local nginx, other spring service, etc.) the issue does not occur. (see https://github.com/FrzMe/wiremock-spring-webclient-block/blob/master/src/test/java/webclientblock/WebClientBlockTest.java#L127)
* the issue does not occur in WireMock 2.17 (test by changing version in pom https://github.com/FrzMe/wiremock-spring-webclient-block/blob/master/pom.xml#L38 )

The Issue can be reproduced when running the WebClientBlockTest from https://github.com/FrzMe/wiremock-spring-webclient-block - there the test testWiremockServiceGetJsonDecode fails consistently for me (no later than ~8000 iterations - sometimes much earlier) 
The test also shows that the same problem can not be reproduced when making http requests using the spring RestTemplate.

Both WireMock and WebClient are set up completely self contained within the test.
