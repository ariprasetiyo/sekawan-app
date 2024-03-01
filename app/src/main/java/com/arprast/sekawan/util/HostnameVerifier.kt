package com.arprast.sekawan.util
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class HostnameVerifier (private val domainList: List<String>) : HostnameVerifier {

    override fun verify(fqdn: String, sslSession: SSLSession): Boolean {
        for (domain in domainList) {
            if (fqdn.endsWith(domain)) {
                return true
            }
        }
        return false
    }
}