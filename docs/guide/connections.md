---
layout: default
title: Ldaptive - connections
redirect_from: "/docs/guide/connections/"
---

# Connections

LDAP connections are stateful and persistent, which means they must be opened before operations are performed and then closed when no longer needed. Connections are created using a ConnectionFactory. Ldaptive provides two implementations of ConnectionFactory: DefaultConnectionFactory and PooledConnectionFactory.

A DefaultConnectionFactory can be used statically or as an instance variable:

{% highlight java %}
{% include source/connections/1.java %}
{% endhighlight %}

{% highlight java %}
{% include source/connections/2.java %}
{% endhighlight %}

See the [pooling guide](docs/guide/connections/pooling.html) for details on how to use a PooledConnectionFactory.

## startTLS / SSL

When transmitting sensitive data to or from an LDAP it's important to use a secure connection. To use SSL:

{% highlight java %}
{% include source/connections/3.java %}
{% endhighlight %}

startTLS allows the client to upgrade and downgrade the security of the connection as needed. To use startTLS:

{% highlight java %}
{% include source/connections/4.java %}
{% endhighlight %}

In practice it is not advisable to downgrade a TLS connection, after all, you've already done the hard work to establish a TLS connection. In fact, many LDAP servers don't even support the operation. The server will simply close the connection if a stopTLS operation is received. Consequently ldaptive doesn't have functions for starting and stopping TLS on an open connection. You must decide whether you wish to use startTLS before the connection is opened.

### Trust Issues

When using SSL or startTLS trust errors are very common. The client must be configured to trust the server and when performing client authentication, the server must be configured to trust the client. This sections deals with how to configure your LDAP client with the proper trust and authentication material.

#### Java cacerts file

You can add either the server certificate or the server certificate's CA to the cacerts file included with your Java installation. This is the simplest solution, but be aware that it impacts the trust of all secure connections made by the JVM.

{% highlight bash %}
keytool -import -file $PATH_TO_CERT -keystore $JAVA_HOME/jre/lib/security/cacerts -alias my_server_cert
{% endhighlight %}

#### Command line options

Java supports command line options for designating both the truststore and keystore to be used for secure connections. Note that this impacts the trust of all secure connections made by the JVM.

{% highlight bash %}
java -Djavax.net.ssl.keyStore=$PATH_TO/my.keystore -Djavax.net.ssl.trustStore=$PATH_TO/my.truststore
{% endhighlight %}

When performing client authentication the JVM will select the first certificate in my.keystore that matches the allowed CAs supplied by the server. 

#### Trust Managers

If you have a implementation of javax.net.ssl.TrustManager it can be added directory to SslConfig. Ldaptive provides several implementations which may be helpful:

- AggregateTrustManager - combines multiple trust managers allowing the use of either all trust managers or any trust manager to pass validation
- DefaultTrustManager - the default JVM trust managers
- HostnameVerifyingTrustManager - custom trust manager based on certificate hostname
- AllowAnyTrustManager - trusts any client or server

Note that if you provide both trust managers and a credential config to the SslConfig, all trust managers will be required.

#### Credential Configuration

Ldaptive includes several classes to make the use of keystores and X509 credentials easier. CredentialConfig implementations support loading key and trust material from both the classpath and the file system.

Use a custom truststore for startTLS connections that is located on the classpath:

{% highlight java %}
{% include source/connections/5.java %}
{% endhighlight %}

Use X509 certificates for both authentication and trust that are located on the file system:

{% highlight java %}
{% include source/connections/6.java %}
{% endhighlight %}

Supported certificate formats include:

- PEM
- DER
- PKCS7

Supported private key formats include:

- PKCS8

### Hostname Validation

[RFC 2830](http://www.ietf.org/rfc/rfc2830.txt) section 3.6 specifies how hostnames should be validated for startTLS. No such RFC exists for LDAPS and neither JNDI nor any of the other Ldaptive providers perform any hostname checks after the SSL handshake. Ldaptive remedies this issue by injecting hostname validation as a trust manager when an LDAPS connection is detected. The validation rules are as follows:

- if hostname is an IP address, then certificate must have exact match IP subjAltName
- hostname must match any DNS subjAltName if any exist
- hostname must match the first CN
- if certificate begins with a wildcard, domains are used for matching

## Operation Retry

Since LDAP connections are persistent they can be disrupted in a variety of ways: server restarts, miss-behaving load balancers, network blips, etc. Because of the myriad number of ways in which an LDAP connection may suddenly stop working ldaptive provides operation retry functionality. This means that for certain LDAP error codes the library will simply attempt to close, reopen the connection, and then try the operation again. This behavior is controlled by the following properties on the ReopenOperationExceptionHandler:

 Name | Default Value | Description
 retry        | 0 | number of times to retry; set to -1 to retry indefinitely
 retryWait    | 0 | time in milliseconds to wait between retries
 retryBackoff | 0 | factor by which to delay successive retries

Note that retry controls the number of retries after the first reopen. A single reopen is always attempted.

For example:

{% highlight java %}
{% include source/connections/7.java %}
{% endhighlight %}

- Retry #1: sleep 3 seconds
- Retry #2: sleep 6 seconds
- Retry #3: sleep 12 seconds
- Retry #4: sleep 18 seconds
- Retry #5: sleep 24 seconds

### Operation Exception Result Codes

So what causes an operation to be retried? Each provider has a default list of result codes that when encountered by an operation will cause it to be retried.

*JNDI Provider*

- ResultCode.PROTOCOL_ERROR (2)  (javax.naming.CommunicationException)
- ResultCode.BUSY (51)                     (javax.naming.ServiceUnavailableException)
- ResultCode.UNAVAILABLE (52)        (javax.naming.ServiceUnavailableException)

See How LDAP Error Codes Map to JNDI Exceptions for details on which naming exceptions map to which result codes.

*JLDAP Provider*

- ResultCode.LDAP_TIMEOUT (85)
- ResultCode.CONNECT_ERROR (91)

*Apache LDAP Provider*
None set

*UnboundID Provider*
- ResultCode.LDAP_TIMEOUT (85)
- ResultCode.CONNECT_ERROR (91)

If you need to modify the default setting you can do so by changing the provider configuration:

{% highlight java %}
{% include source/connections/8.java %}
{% endhighlight %}

## URLs & Connection Strategies

Ldaptive does not require URLs to be of any certain form, but it is recommended to use the form:

{% highlight text %}
scheme://host:port -> ldap://directory.ldaptive.org:389
{% endhighlight %}

All the providers in ldaptive will attempt to parse this form correctly. If multiple URLs are provided you can specify a client side connection strategy to control the behavior if a connection cannot be established.

*Connection Strategies*

Name | Behavior
DEFAULT | no action, behavior dictated by the provider
ACTIVE_PASSIVE | attempt each URL in the order provided for each connection; the first URL is always used unless it fails
ROUND_ROBIN | attempt the next URL in the order provided for each connection; URLs are rotated regardless of connection success or failure
RANDOM | attempt a random URL; useful for stateless implementations

{% highlight java %}
{% include source/connections/9.java %}
{% endhighlight %}

Note that if multiple URLs are provided with a DEFAULT strategy to the JNDI provider then you will get the JNDI active/passive behavior. No other ldaptive provider currently supports multiple URLS in the DEFAULT strategy. If multiple URLs are supplied, those providers typically select the last URL in the list.

## Provider Properties

Some providers support arbitrary string based properties to control certain connection behavior. To set those properties:

{% highlight java %}
{% include source/connections/10.java %}
{% endhighlight %}

See the [providers guide](docs/guide/providers.html) for more information.
